package scc

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.Test
import org.scalatest.matchers.must.Matchers

class SpamCCTest extends Matchers {

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def assertSame( expected: List[String], actual: List[String]) = {

    if (expected != actual) {
      println("Expected: " + expected)
      println("Actual  : " + actual)

      assertEquals(
        expected.mkString("\n").stripTrailing().stripLeading(),
        actual.mkString("\n").stripTrailing().stripLeading()
      )
    }
  }

  @Test
  def functionVariables: Unit = {

    val lines =
      """
        |def main(): void = {
        | var a=1
        | var b=1+1
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_main_a: EQU 0
        |root_main_b: EQU 1
        |[:root_main_a] = 1
        |[:root_main_b] = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }


  @Test
  def twoFunctions: Unit = {

    val lines =
      """
        |def main(): void = {
        | var a=1
        | var b=2
        |}
        |def other(): void = {
        | var a=1
        | var b=2
        |}
        |""".stripMargin

    val actual : List[String] = compile(lines)

    val expected = split(
      """
        |root_main_a: EQU 0
        |root_main_b: EQU 1
        |root_other_a: EQU 2
        |root_other_b: EQU 3
        |[:root_main_a] = 1
        |[:root_main_b] = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |[:root_other_a] = 1
        |[:root_other_b] = 2
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

  @Test
  def return2 : Unit ={

    val lines =
      """
        |def main(): void = {
        |  return 2
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |REGD = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

  @Test
  def returnVar : Unit ={

    val lines =
      """
        |def main(): void = {
        |  var a = 1
        |  return a
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_main_a: EQU 0
        |[:root_main_a] = 1
        |REGD = [:root_main_a]
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

  @Test
  def varEqExpression: Unit = {

    val lines =
      """
        |def main(): void = {
        |  var a = 1 + 2
        |  var b = a + 3
        |  var c = 4 + b
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_main_a: EQU 0
        |root_main_b: EQU 1
        |root_main_c: EQU 2
        |[:root_main_a] = 3
        |REGA = [:root_main_a]
        |[:root_main_b] = REGA + 3
        |REGA = [:root_main_b]
        |[:root_main_c] = REGA + 4
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

        assertSame(expected, actual)
  }


    private def compile(lines: String) = {
      val scc = new SpamCC
      val actual: List[String] = scc.compile(lines)

      val asm = new Assembler
      val str = actual.mkString("\n")
      println("ASSEMBLING:\n" + str)
      asm.assemble(str)

      actual
    }
}
