package scc

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.Test
import org.scalatest.matchers.must.Matchers

class SpamCCTest extends Matchers {

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }

  def assertSame(expected: List[String], actual: List[String]) = {

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

    val actual: List[String] = compile(lines)

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
  def return2: Unit = {

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
  def returnVar: Unit = {

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
        |  var d = c
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_main_a: EQU 0
        |root_main_b: EQU 1
        |root_main_c: EQU 2
        |root_main_d: EQU 3
        |[:root_main_a] = 3
        |REGA = [:root_main_a]
        |REGA = REGA + 3
        |[:root_main_b] = REGA
        |REGA = 4
        |REGB = [:root_main_b]
        |REGA = REGA + REGB
        |[:root_main_c] = REGA
        |REGA = [:root_main_c]
        |[:root_main_d] = REGA
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }


  @Test
  def varEqExpressionTwoVars: Unit = {

    val lines =
      """def main(): void = {
        |  var a = 1 + 2
        |  var b = a + 3
        |  var c = a + b
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_main_a: EQU 0
        |root_main_b: EQU 1
        |root_main_c: EQU 2
        |[:root_main_a] = 3
        |REGA = [:root_main_a]
        |REGA = REGA + 3
        |[:root_main_b] = REGA
        |REGA = [:root_main_a]
        |REGB = [:root_main_b]
        |[:root_main_c] = REGA + REGB
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }
  @Test
  def varEqExpressionThreeExpr: Unit = {

    val lines =
      """def main(): void = {
        |  var a = 0
        |  var a = a +  1
        |  var b = 4 + (a + 5)
        |  putchar(b)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_main_a: EQU 0
        |root_main_b: EQU 1
        |root_main_c: EQU 2
        |[:root_main_a] = 3
        |REGA = [:root_main_a]
        |REGA = REGA + 3
        |[:root_main_b] = REGA
        |REGA = [:root_main_a]
        |REGB = [:root_main_b]
        |[:root_main_c] = REGA + REGB
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def output: Unit = {

    val lines =
      """
        |def main(): void = {
        |  putchar('A')
        |  putchar('B')
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_main_putchar_wait_1:
        |PCHITMP = <:root_main_putchar_transmit_2
        |PC = >:root_main_putchar_transmit_2 _DO
        |PCHITMP = <:root_main_putchar_wait_1
        |PC = <:root_main_putchar_wait_1
        |root_main_putchar_transmit_2:
        |UART = 65
        |root_main_putchar_wait_3:
        |PCHITMP = <:root_main_putchar_transmit_4
        |PC = >:root_main_putchar_transmit_4 _DO
        |PCHITMP = <:root_main_putchar_wait_3
        |PC = <:root_main_putchar_wait_3
        |root_main_putchar_transmit_4:
        |UART = 66
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

  @Test
  def whileLoopTrue: Unit = {

    val lines =
      """
        |def main(): void = {
        | while(true) {
        |   var a = 1
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_main_while_1_a: EQU 0
        |root_main_while_1_top:
        |[:root_main_while_1_a] = 1
        |PCHITMP = <:root_main_while_1_top
        |PC = >:root_main_while_1_top
        |root_main_while_1_bot:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

//  @Test
//  def whileLoopCond: Unit = {
//
//    val lines =
//      """
//        |def main(): void = {
//        | var a=2
//        | while(a>0) {
//        |   a=a-1
//        | }
//        |}
//        |""".stripMargin
//
//    val actual: List[String] = compile(lines)
//
//    val expected = split(
//      """
//        |root_main_while_1_a: EQU 0
//        |[:root_main_a] = 1
//        |root_main_while_1_top:
//        |PCHITMP = <:root_main_while_1_top
//        |PC = >:root_main_while_1_top
//        |root_main_while_1_bot:
//        |PCHITMP = <:root_end
//        |PC = >:root_end
//        |root_end:
//        |END
//        |""")
//
//    assertSame(expected, actual)
//  }


  private def compile(lines: String, quiet: Boolean = true) = {
    val scc = new SpamCC
    val actual: List[String] = scc.compile(lines)

    val asm = new Assembler
    val str = actual.mkString("\n")
    println("ASSEMBLING:\n" + str)

    asm.assemble(str, quiet = quiet)

    // ditch comments
    val filtered = actual.filter{ l =>
      ((!quiet) || !l.matches("^\\s*;.*"))
    }

    filtered
  }
}
