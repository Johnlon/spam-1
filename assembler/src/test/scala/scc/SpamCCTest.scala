package scc

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SpamCCTest extends AnyFlatSpec with Matchers {

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

  it should "function variables" in {

    val lines =
      """
        |def main(): void = {
        | var a=1
        | var b=2
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

  private def compile(lines: String) = {
    val scc = new SpamCC
    val actual: List[String] = scc.compile(lines)

    val asm = new Assembler
    val str = actual.mkString("\n")
    println("ASSEMBLING:\n" + str)
    asm.assemble(str)

    actual
  }

  it should "two functions with variables" in {

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

  it should "return 2" in {

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

  it should "return var" in {

    val lines =
      """
        |def main(): void = {
        |  var a = 1
        |  return a
        |}
        |""".stripMargin

    val actual: scala.List[_root_.scala.Predef.String] = compile(lines)

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
}
