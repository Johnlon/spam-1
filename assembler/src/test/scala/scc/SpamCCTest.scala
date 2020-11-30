package scc

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.{FixMethodOrder, Test}
import org.junit.runners.MethodSorters
import org.scalatest.matchers.must.Matchers

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SpamCCTest extends Matchers {

  def split(s: String): List[String] = {
    s.split("\\|")
      .map(_.stripTrailing()
        .stripLeading())
      .filterNot(_.isBlank).toList
  }

  def assertSameEx(expected: List[String], actual: List[String]) = {
    var end = split(
      """PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
                  """.stripMargin)

    assertSame(expected ++ end, actual)
  }

  def assertSame(expected: List[String], actual: List[String]) = {

    if (expected != actual) {
      println("Expected: " + expected)
      println("Actual  : " + actual)

      val e = expected.map( _.stripTrailing().stripLeading()).mkString("\n")
      val a = actual.map( _.stripTrailing().stripLeading()).mkString("\n")
      assertEquals(e, a)
    }
  }

  @Test
  def varEq1: Unit = {

    val lines =
      """def main(): void = {
        | var a=1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main_a: EQU 0
        |[:root_function_main_a] = 1
        |""")

    assertSameEx(expected, actual)
  }

  @Test
  def varEq1AndVarEq1Plus1: Unit = {

    val lines =
      """
        |def main(): void = {
        | var a=1;
        | var b=1+1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main_a: EQU 0
        |root_function_main_b: EQU 1
        |[:root_function_main_a] = 1
        |[:root_function_main_b] = 2
        |""")

    assertSameEx(expected, actual)
  }


  @Test
  def twoFunctions: Unit = {

    val lines =
      """
        |def main(): void = {
        | var a=1;
        | var b=2;
        |}
        |def other(): void = {
        | var a=1;
        | var b=2;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main_a: EQU 0
        |root_function_main_b: EQU 1
        |root_function_other_a: EQU 2
        |root_function_other_b: EQU 3
        |[:root_function_main_a] = 1
        |[:root_function_main_b] = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |[:root_function_other_a] = 1
        |[:root_function_other_b] = 2
        |root_end:
        |END
        |""")

    assertSame(expected, actual)
  }

  @Test
  def returnLiteralConst: Unit = {

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
        |""")

    assertSameEx(expected, actual)
  }

  @Test
  def returnVar: Unit = {

    val lines =
      """
        |def main(): void = {
        |  var a = 1;
        |  return a
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main_a: EQU 0
        |[:root_function_main_a] = 1
        |REGD = [:root_function_main_a]
        |""")

    assertSameEx(expected, actual)
  }

  @Test
  def varEqSimpleTwoArgExpr: Unit = {

    val lines =
      """
        |def main(): void = {
        |  var a = 1 + 2;
        |  var b = a + 3;
        |  var c = 4 + b;
        |  var d = c;
        |  var e = a + b;
        |
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main_a: EQU 0
        |root_function_main_b: EQU 1
        |root_function_main_c: EQU 2
        |root_function_main_d: EQU 3
        |root_function_main_e: EQU 4
        |[:root_function_main_a] = 3
        |REGA = [:root_function_main_a]
        |REGA = REGA + 3
        |[:root_function_main_b] = REGA
        |REGA = 4
        |REGA = REGA + [:root_function_main_b]
        |[:root_function_main_c] = REGA
        |REGA = [:root_function_main_c]
        |[:root_function_main_d] = REGA
        |REGA = [:root_function_main_a]
        |REGA = [:root_function_main_b]
        |[:root_function_main_e] = REGA + REGB
        |
        """.stripMargin)

    assertSameEx(expected, actual)
  }

  @Test
  def varEqNestedExpr: Unit = {

    val lines =
      """def main(): void = {
        |  var a = 1;
        |  var b = 2 + (a + 3);
        |  putchar(b)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main_a: EQU 0
        |root_function_main_varExprs_d2: EQU 1
        |root_function_main_varExprs_d3: EQU 2
        |root_function_main_b: EQU 3
        |[:root_function_main_a] = 1
        |REGA = 2
        |[:root_function_main_varExprs_d2] = REGA
        |REGA = [:root_function_main_a]
        |[:root_function_main_varExprs_d3] = REGA
        |REGA = 3
        |REGB = [:root_function_main_varExprs_d3]
        |[:root_function_main_varExprs_d3] = REGB + REGA
        |REGA = [:root_function_main_varExprs_d3]
        |REGB = [:root_function_main_varExprs_d2]
        |[:root_function_main_varExprs_d2] = REGB + REGA
        |REGA = [:root_function_main_varExprs_d2]
        |[:root_function_main_b] = REGA
        |root_function_main_putcharN__wait_1:
        |PCHITMP = <:root_function_main_putcharN__transmit_2
        |PC = >:root_function_main_putcharN__transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharN__wait_1
        |PC = <:root_function_main_putcharN__wait_1
        |root_function_main_putcharN__transmit_2:
        |UART = [:root_function_main_b]
        |""".stripMargin)

    assertSameEx(expected, actual)
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
        |root_function_main_putcharI__wait_1:
        |PCHITMP = <:root_function_main_putcharI__transmit_2
        |PC = >:root_function_main_putcharI__transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharI__wait_1
        |PC = <:root_function_main_putcharI__wait_1
        |root_function_main_putcharI__transmit_2:
        |UART = 65
        |root_function_main_putcharI__wait_3:
        |PCHITMP = <:root_function_main_putcharI__transmit_4
        |PC = >:root_function_main_putcharI__transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharI__wait_3
        |PC = <:root_function_main_putcharI__wait_3
        |root_function_main_putcharI__transmit_4:
        |UART = 66
        |""".stripMargin)

    assertSameEx(expected, actual)
  }

  @Test
  def whileLoopTrue: Unit = {

    val lines =
      """
        |def main(): void = {
        | while(true) {
        |   var a = 1;
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main_whileTrue1_a: EQU 0
        |root_function_main_whileTrue1_block_2__top:
        |[:root_function_main_whileTrue1_a] = 1
        |PCHITMP = <:root_function_main_whileTrue1_block_2__top
        |PC = >:root_function_main_whileTrue1_block_2__top
        |root_function_main_whileTrue1_block_2__bot:
        |""".stripMargin)

    assertSameEx(expected, actual)
  }

  @Test
  def whileLoopCond: Unit = {

    val lines =
      """
        |def main(): void = {
        | var a=10;
        | while(a>0) {
        |   var a=a-1;
        |   putchar(a)
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main_a: EQU 0
        |[:root_function_main_a] = 10
        |root_function_main_whileCond1__2__check:
        |REGA = [:root_function_main_a]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond1__2__top
        |PC = >:root_function_main_whileCond1__2__top _GT
        |PCHITMP = <:root_function_main_whileCond1__2__bot
        |PC = >:root_function_main_whileCond1__2__bot
        |root_function_main_whileCond1__2__top:
        |REGA = [:root_function_main_a]
        |REGA = REGA - 1
        |[:root_function_main_a] = REGA
        |root_function_main_whileCond1_putcharN__wait_3:
        |PCHITMP = <:root_function_main_whileCond1_putcharN__transmit_4
        |PC = >:root_function_main_whileCond1_putcharN__transmit_4 _DO
        |PCHITMP = <:root_function_main_whileCond1_putcharN__wait_3
        |PC = <:root_function_main_whileCond1_putcharN__wait_3
        |root_function_main_whileCond1_putcharN__transmit_4:
        |UART = [:root_function_main_a]
        |PCHITMP = <:root_function_main_whileCond1__2__check
        |PC = >:root_function_main_whileCond1__2__check
        |root_function_main_whileCond1__2__bot:
        |""".stripMargin)

    assertSameEx(expected, actual)
  }


  private def compile(lines: String, quiet: Boolean = true) = {
    val scc = new SpamCC
    val actual: List[String] = scc.compile(lines)

    val asm = new Assembler
    val str = actual.mkString("\n")
    println("ASSEMBLING:\n" + str)

    asm.assemble(str, quiet = quiet)

    // ditch comments
    val filtered = actual.filter { l =>
      ((!quiet) || !l.matches("^\\s*;.*"))
    }

    filtered
  }
}
