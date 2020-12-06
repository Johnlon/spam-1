package scc

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicBoolean

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.runners.MethodSorters
import org.junit.{FixMethodOrder, Test}
import org.scalatest.matchers.must.Matchers

import scala.collection.mutable.ListBuffer

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SpamCCTest extends Matchers {

  def split(s: String): List[String] = {
    val strings = s.split("\n")
    strings
      .map(_.stripTrailing().stripLeading())
      .filterNot(_.isBlank).toList
  }

  def assertSame(expected: List[String], actual: List[String]): Unit = {
    println("\nCOMPARING ASM:")
    if (expected != actual) {
      println("Expected: " + expected)
      println("Actual  : " + actual)

      val e = expected.map(_.stripTrailing().stripLeading()).mkString("\n")
      val a = actual.map(_.stripTrailing().stripLeading()).mkString("\n")
      assertEquals(e, a)
    }
  }

  @Test
  def varEq1(): Unit = {

    val lines =
      """fun main() {
        | var a=1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 1
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        """.stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varEqConstExpr(): Unit = {

    val lines =
      """
        |fun main() {
        | var a=1;
        | var b=64+1;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 1
        |[:root_function_main___VAR_b] = 65
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""".stripMargin)

    assertSame(expected, actual)
  }


  @Test
  def twoFunctions(): Unit = {

    val lines =
      """
        |fun main() {
        | var a=1;
        | var b=2;
        |}
        |fun other() {
        | var a=1;
        | var b=2;
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: BYTES [0]
        |root_function_other___VAR_RETURN_HI: BYTES [0]
        |root_function_other___VAR_RETURN_LO: BYTES [0]
        |root_function_other___VAR_a: BYTES [0]
        |root_function_other___VAR_b: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 1
        |[:root_function_main___VAR_b] = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_function_other___LABEL_START:
        |[:root_function_other___VAR_a] = 1
        |[:root_function_other___VAR_b] = 2
        |PCHITMP = [:root_function_other___VAR_RETURN_HI]
        |PC = [:root_function_other___VAR_RETURN_LO]
        |root_end:
        |END
        |""".stripMargin)

    // not using Ex function as it assumes main is last function
    assertSame(expected, actual)
  }

  @Test
  def varEqSimpleTwoArgExpr(): Unit = {

    val lines =
      """
        |fun main() {
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
      """
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: BYTES [0]
        |root_function_main___VAR_c: BYTES [0]
        |root_function_main___VAR_d: BYTES [0]
        |root_function_main___VAR_e: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 3
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA + 3
        |[:root_function_main___VAR_b] = REGA
        |REGA = 4
        |REGA = REGA + [:root_function_main___VAR_b]
        |[:root_function_main___VAR_c] = REGA
        |REGA = [:root_function_main___VAR_c]
        |[:root_function_main___VAR_d] = REGA
        |REGA = [:root_function_main___VAR_a]
        |REGA = [:root_function_main___VAR_b]
        |[:root_function_main___VAR_e] = REGA + REGB
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
        |""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def varEqNestedExpr(): Unit = {

    val lines =
      """fun main() {
        |  var a = 64;
        |  var b = 1 + (a + 3);
        |  putchar(b)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, outputCheck = str => {
      checkTransmitted(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr3: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 64
        |REGA = 1
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_a]
        |[:root_function_main___VAR_compoundBlkExpr3] = REGA
        |REGA = 3
        |REGC = [:root_function_main___VAR_compoundBlkExpr3]
        |[:root_function_main___VAR_compoundBlkExpr3] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr3]
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_b] = REGA
        |root_function_main_putcharVar_b____LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharVar_b____LABEL_transmit_2
        |PC = >:root_function_main_putcharVar_b____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharVar_b____LABEL_wait_1
        |PC = <:root_function_main_putcharVar_b____LABEL_wait_1
        |root_function_main_putcharVar_b____LABEL_transmit_2:
        |UART = [:root_function_main___VAR_b]
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def putchar(): Unit = {

    val lines =
      """
        |fun main() {
        |  putchar('A')
        |  putchar('B')
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, outputCheck = str => {
      checkTransmitted(str, 'A')
      checkTransmitted(str, 'B')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |root_function_main_putcharI_65____LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharI_65____LABEL_transmit_2
        |PC = >:root_function_main_putcharI_65____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharI_65____LABEL_wait_1
        |PC = <:root_function_main_putcharI_65____LABEL_wait_1
        |root_function_main_putcharI_65____LABEL_transmit_2:
        |UART = 65
        |root_function_main_putcharI_66____LABEL_wait_3:
        |PCHITMP = <:root_function_main_putcharI_66____LABEL_transmit_4
        |PC = >:root_function_main_putcharI_66____LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharI_66____LABEL_wait_3
        |PC = <:root_function_main_putcharI_66____LABEL_wait_3
        |root_function_main_putcharI_66____LABEL_transmit_4:
        |UART = 66
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }


  @Test
  def whileLoopCond(): Unit = {

    val lines =
      """
        |fun main() {
        | var a=10;
        | while(a>0) {
        |   var a=a-1;
        |   putchar(a)
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 10
        |root_function_main_whileCond1___LABEL_CHECK:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond1___LABEL_BODY
        |PC = >:root_function_main_whileCond1___LABEL_BODY _GT
        |PCHITMP = <:root_function_main_whileCond1___LABEL_AFTER
        |PC = >:root_function_main_whileCond1___LABEL_AFTER
        |root_function_main_whileCond1___LABEL_BODY:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA - 1
        |[:root_function_main___VAR_a] = REGA
        |root_function_main_whileCond1_putcharVar_a____LABEL_wait_2:
        |PCHITMP = <:root_function_main_whileCond1_putcharVar_a____LABEL_transmit_3
        |PC = >:root_function_main_whileCond1_putcharVar_a____LABEL_transmit_3 _DO
        |PCHITMP = <:root_function_main_whileCond1_putcharVar_a____LABEL_wait_2
        |PC = <:root_function_main_whileCond1_putcharVar_a____LABEL_wait_2
        |root_function_main_whileCond1_putcharVar_a____LABEL_transmit_3:
        |UART = [:root_function_main___VAR_a]
        |PCHITMP = <:root_function_main_whileCond1___LABEL_CHECK
        |PC = >:root_function_main_whileCond1___LABEL_CHECK
        |root_function_main_whileCond1___LABEL_AFTER:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }


  @Test
  def whileLoopTrueIfBreak(): Unit = {

    val lines =
      """
        |fun main() {
        | var a = 1;
        | while(true) {
        |   var a = a + 1;
        |
        |   if (a>10) {
        |     break
        |   }
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 1
        |root_function_main_whileTrue2___LABEL_BODY:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA + 1
        |[:root_function_main___VAR_a] = REGA
        |root_function_main_whileTrue2_ifCond1___LABEL_CHECK:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA PASS_A 10 _S
        |PCHITMP = <:root_function_main_whileTrue2_ifCond1___LABEL_BODY
        |PC = >:root_function_main_whileTrue2_ifCond1___LABEL_BODY _GT
        |PCHITMP = <:root_function_main_whileTrue2_ifCond1___LABEL_AFTER
        |PC = >:root_function_main_whileTrue2_ifCond1___LABEL_AFTER
        |root_function_main_whileTrue2_ifCond1___LABEL_BODY:
        |PCHITMP = <:root_function_main_whileTrue2___LABEL_AFTER
        |PC = >:root_function_main_whileTrue2___LABEL_AFTER
        |root_function_main_whileTrue2_ifCond1___LABEL_AFTER:
        |PCHITMP = <:root_function_main_whileTrue2___LABEL_BODY
        |PC = >:root_function_main_whileTrue2___LABEL_BODY
        |root_function_main_whileTrue2___LABEL_AFTER:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def functionCalls(): Unit = {

    val lines =
      """
        |// START FN COMMAND
        |
        |fun print(a1 out, a2, a3, a4) {
        | // FN COMMENT
        | var d = a1;
        | putchar(d)
        | putchar(a2)
        | putchar(a3)
        | putchar(a4)
        |
        | // ascii 33 dec
        | var a1 = '!';
        | // END FN COMMENT
        |}
        |
        |fun main() {
        | var arg1 = 'A';
        | var arg2 = 1;
        |
        | // CALLING PRINT - 63 is '?'
        | print(arg1, arg2+arg1, 63, arg1+4)
        |
        | // CALLING PUT CHAR OF OUT VALUE
        | putchar(arg1)
        |
        |}
        |
        |// END  COMMAND
        |""".stripMargin

    val actual: List[String] = compile(lines, quiet = true, outputCheck = str => {
      checkTransmitted(str, 'A')
      checkTransmitted(str, 'B')
      checkTransmitted(str, '?')
      checkTransmitted(str, 'E')
      checkTransmitted(str, '!')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_arg1: BYTES [0]
        |root_function_main___VAR_arg2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_print___VAR_RETURN_HI: BYTES [0]
        |root_function_print___VAR_RETURN_LO: BYTES [0]
        |root_function_print___VAR_a1: BYTES [0]
        |root_function_print___VAR_a2: BYTES [0]
        |root_function_print___VAR_a3: BYTES [0]
        |root_function_print___VAR_a4: BYTES [0]
        |root_function_print___VAR_d: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |root_function_print___LABEL_START:
        |REGA = [:root_function_print___VAR_a1]
        |[:root_function_print___VAR_d] = REGA
        |root_function_print_putcharVar_d____LABEL_wait_1:
        |PCHITMP = <:root_function_print_putcharVar_d____LABEL_transmit_2
        |PC = >:root_function_print_putcharVar_d____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_print_putcharVar_d____LABEL_wait_1
        |PC = <:root_function_print_putcharVar_d____LABEL_wait_1
        |root_function_print_putcharVar_d____LABEL_transmit_2:
        |UART = [:root_function_print___VAR_d]
        |root_function_print_putcharVar_a2____LABEL_wait_3:
        |PCHITMP = <:root_function_print_putcharVar_a2____LABEL_transmit_4
        |PC = >:root_function_print_putcharVar_a2____LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_print_putcharVar_a2____LABEL_wait_3
        |PC = <:root_function_print_putcharVar_a2____LABEL_wait_3
        |root_function_print_putcharVar_a2____LABEL_transmit_4:
        |UART = [:root_function_print___VAR_a2]
        |root_function_print_putcharVar_a3____LABEL_wait_5:
        |PCHITMP = <:root_function_print_putcharVar_a3____LABEL_transmit_6
        |PC = >:root_function_print_putcharVar_a3____LABEL_transmit_6 _DO
        |PCHITMP = <:root_function_print_putcharVar_a3____LABEL_wait_5
        |PC = <:root_function_print_putcharVar_a3____LABEL_wait_5
        |root_function_print_putcharVar_a3____LABEL_transmit_6:
        |UART = [:root_function_print___VAR_a3]
        |root_function_print_putcharVar_a4____LABEL_wait_7:
        |PCHITMP = <:root_function_print_putcharVar_a4____LABEL_transmit_8
        |PC = >:root_function_print_putcharVar_a4____LABEL_transmit_8 _DO
        |PCHITMP = <:root_function_print_putcharVar_a4____LABEL_wait_7
        |PC = <:root_function_print_putcharVar_a4____LABEL_wait_7
        |root_function_print_putcharVar_a4____LABEL_transmit_8:
        |UART = [:root_function_print___VAR_a4]
        |[:root_function_print___VAR_a1] = 33
        |PCHITMP = [:root_function_print___VAR_RETURN_HI]
        |PC = [:root_function_print___VAR_RETURN_LO]
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_arg1] = 65
        |[:root_function_main___VAR_arg2] = 1
        |REGA = [:root_function_main___VAR_arg1]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a1] = REGA
        |REGA = [:root_function_main___VAR_arg2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_arg1]
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a2] = REGA
        |REGA = 63
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a3] = REGA
        |REGA = [:root_function_main___VAR_arg1]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = 4
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a4] = REGA
        |[:root_function_print___VAR_RETURN_HI] = < :root_function_main___LABEL_RETURN_9
        |[:root_function_print___VAR_RETURN_LO] = > :root_function_main___LABEL_RETURN_9
        |PCHITMP = < :root_function_print___LABEL_START
        |PC = > :root_function_print___LABEL_START
        |root_function_main___LABEL_RETURN_9:
        |REGA = [:root_function_print___VAR_a1]
        |[:root_function_main___VAR_arg1] = REGA
        |root_function_main_putcharVar_arg1____LABEL_wait_10:
        |PCHITMP = <:root_function_main_putcharVar_arg1____LABEL_transmit_11
        |PC = >:root_function_main_putcharVar_arg1____LABEL_transmit_11 _DO
        |PCHITMP = <:root_function_main_putcharVar_arg1____LABEL_wait_10
        |PC = <:root_function_main_putcharVar_arg1____LABEL_wait_10
        |root_function_main_putcharVar_arg1____LABEL_transmit_11:
        |UART = [:root_function_main___VAR_arg1]
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def functionCalls2Deep(): Unit = {

    val lines =
      """
        |fun depth2(b1 out) {
        | var b1 = b1 + 1;
        |}
        |
        |fun depth1(a1 out) {
        | depth2(a1)
        |}
        |
        |fun main() {
        | var arg1 = 'A';
        | depth1(arg1)
        | putchar(arg1)
        |}
        |
        |// END  COMMAND
        |""".stripMargin

    val actual: List[String] = compile(lines, quiet = true, outputCheck = str => {
      checkTransmitted(str, 'B')
    })

    val expected = split(
      """root_function_depth1___VAR_RETURN_HI: BYTES [0]
        |root_function_depth1___VAR_RETURN_LO: BYTES [0]
        |root_function_depth1___VAR_a1: BYTES [0]
        |root_function_depth1___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_depth2___VAR_RETURN_HI: BYTES [0]
        |root_function_depth2___VAR_RETURN_LO: BYTES [0]
        |root_function_depth2___VAR_b1: BYTES [0]
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_arg1: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |root_function_depth2___LABEL_START:
        |REGA = [:root_function_depth2___VAR_b1]
        |REGA = REGA + 1
        |[:root_function_depth2___VAR_b1] = REGA
        |PCHITMP = [:root_function_depth2___VAR_RETURN_HI]
        |PC = [:root_function_depth2___VAR_RETURN_LO]
        |root_function_depth1___LABEL_START:
        |REGA = [:root_function_depth1___VAR_a1]
        |[:root_function_depth1___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_depth1___VAR_compoundBlkExpr2]
        |[:root_function_depth2___VAR_b1] = REGA
        |[:root_function_depth2___VAR_RETURN_HI] = < :root_function_depth1___LABEL_RETURN_1
        |[:root_function_depth2___VAR_RETURN_LO] = > :root_function_depth1___LABEL_RETURN_1
        |PCHITMP = < :root_function_depth2___LABEL_START
        |PC = > :root_function_depth2___LABEL_START
        |root_function_depth1___LABEL_RETURN_1:
        |REGA = [:root_function_depth2___VAR_b1]
        |[:root_function_depth1___VAR_a1] = REGA
        |PCHITMP = [:root_function_depth1___VAR_RETURN_HI]
        |PC = [:root_function_depth1___VAR_RETURN_LO]
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_arg1] = 65
        |REGA = [:root_function_main___VAR_arg1]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_depth1___VAR_a1] = REGA
        |[:root_function_depth1___VAR_RETURN_HI] = < :root_function_main___LABEL_RETURN_2
        |[:root_function_depth1___VAR_RETURN_LO] = > :root_function_main___LABEL_RETURN_2
        |PCHITMP = < :root_function_depth1___LABEL_START
        |PC = > :root_function_depth1___LABEL_START
        |root_function_main___LABEL_RETURN_2:
        |REGA = [:root_function_depth1___VAR_a1]
        |[:root_function_main___VAR_arg1] = REGA
        |root_function_main_putcharVar_arg1____LABEL_wait_3:
        |PCHITMP = <:root_function_main_putcharVar_arg1____LABEL_transmit_4
        |PC = >:root_function_main_putcharVar_arg1____LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharVar_arg1____LABEL_wait_3
        |PC = <:root_function_main_putcharVar_arg1____LABEL_wait_3
        |root_function_main_putcharVar_arg1____LABEL_transmit_4:
        |UART = [:root_function_main___VAR_arg1]
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  @Test
  def aStringIteration(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        |
        | // index by literal
        | var ac = string[0];
        |
        | // index by variable
        | var b = 1;
        | var bc = string[b];
        |
        | // print values so we can test correct values selected
        | var d = 3;
        | putchar(ac)
        | putchar(bc)
        | putchar(string[2])
        | putchar(string[d])
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = false, quiet = true, outputCheck = str => {
      checkTransmitted(str, 'A')
      checkTransmitted(str, 'B')
      checkTransmitted(str, 'C')
      checkTransmitted(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_ac: BYTES [0]
        |root_function_main___VAR_b: BYTES [0]
        |root_function_main___VAR_bc: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr4: BYTES [0]
        |root_function_main___VAR_d: BYTES [0]
        |root_function_main___VAR_string: BYTES [65, 66, 67, 68, 0]
        |root_function_main_putcharGeneral___VAR_compoundBlkExpr3: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |REGA = 0
        |[:root_function_main___VAR_compoundBlkExpr4] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr4]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_ac] = REGA
        |[:root_function_main___VAR_b] = 1
        |REGA = [:root_function_main___VAR_b]
        |[:root_function_main___VAR_compoundBlkExpr4] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr4]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_bc] = REGA
        |[:root_function_main___VAR_d] = 3
        |root_function_main_putcharVar_ac____LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharVar_ac____LABEL_transmit_2
        |PC = >:root_function_main_putcharVar_ac____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharVar_ac____LABEL_wait_1
        |PC = <:root_function_main_putcharVar_ac____LABEL_wait_1
        |root_function_main_putcharVar_ac____LABEL_transmit_2:
        |UART = [:root_function_main___VAR_ac]
        |root_function_main_putcharVar_bc____LABEL_wait_3:
        |PCHITMP = <:root_function_main_putcharVar_bc____LABEL_transmit_4
        |PC = >:root_function_main_putcharVar_bc____LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharVar_bc____LABEL_wait_3
        |PC = <:root_function_main_putcharVar_bc____LABEL_wait_3
        |root_function_main_putcharVar_bc____LABEL_transmit_4:
        |UART = [:root_function_main___VAR_bc]
        |REGA = 2
        |[:root_function_main_putcharGeneral___VAR_compoundBlkExpr3] = REGA
        |REGA = [:root_function_main_putcharGeneral___VAR_compoundBlkExpr3]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |root_function_main_putcharGeneral___LABEL_wait_5:
        |PCHITMP = <:root_function_main_putcharGeneral___LABEL_transmit_6
        |PC = >:root_function_main_putcharGeneral___LABEL_transmit_6 _DO
        |PCHITMP = <:root_function_main_putcharGeneral___LABEL_wait_5
        |PC = <:root_function_main_putcharGeneral___LABEL_wait_5
        |root_function_main_putcharGeneral___LABEL_transmit_6:
        |UART = REGA
        |REGA = [:root_function_main___VAR_d]
        |[:root_function_main_putcharGeneral___VAR_compoundBlkExpr3] = REGA
        |REGA = [:root_function_main_putcharGeneral___VAR_compoundBlkExpr3]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |root_function_main_putcharGeneral___LABEL_wait_7:
        |PCHITMP = <:root_function_main_putcharGeneral___LABEL_transmit_8
        |PC = >:root_function_main_putcharGeneral___LABEL_transmit_8 _DO
        |PCHITMP = <:root_function_main_putcharGeneral___LABEL_wait_7
        |PC = <:root_function_main_putcharGeneral___LABEL_wait_7
        |root_function_main_putcharGeneral___LABEL_transmit_8:
        |UART = REGA
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    assertSame(expected, actual)
  }

  private def checkTransmitted(str: List[String], c: Char) = {
    val hex = c.charValue().toHexString
    if (!str.find(_.contains(s"TRANSMITTING h$hex")).isDefined) {
      fail(s"did not transmit '$c' char")
    }
  }

  private def compile(linesRaw: String, verbose: Boolean = false, quiet: Boolean = true, outputCheck: List[String] => Unit = _ => {}): List[String] = {
    val scc = new SpamCC

    val lines = "program {\n" + linesRaw + "\n}"
    val actual: List[String] = scc.compile(lines)

    val endRemoved: List[String] = actual.filter(!_.equals("END"))
    val successfulTerminationLocation = List("PCHITMP = <$BEAF", "PC = >$BEAF", "END")
    val patched = endRemoved ++ successfulTerminationLocation

    // jump to signaling location - verilog program monitors this locn
    val str = patched.mkString("\n")
    println("ASSEMBLING:\n")
    var pc = 0

    val IsEqu = "^\\s*[a-zA-Z0-9_]+:\\s*EQU.*$".r
    val IsLabel = "^\\s*[a-zA-Z0-9_]+:\\s*$".r
    val IsComment = "^\\s*;.*$".r
    val IsString = """^\s*[a-zA-Z0-9_]+:\s*STR\s*"[^"]*"\s*$""".r

    actual.foreach { l =>
      if (IsComment.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsEqu.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsLabel.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      } else if (IsString.matches(l)) {
        val withoutQuotes = l.replaceAll("""^[^"]+"""","").replaceAll(""""[^"]*$""","")
        val lstr = org.apache.commons.text.StringEscapeUtils.unescapeJava(withoutQuotes)

        val bytes = lstr.getBytes("UTF-8").toSeq

        println(s"${pc.formatted("%5d")}  $l")
        pc += bytes.length

      } else {
        println(s"${pc.formatted("%5d")}  $l")
        pc += 1
      }
    }

    val asm = new Assembler
    val roms = asm.assemble(str, quiet = quiet)

    // ditch comments
    val filtered = actual.filter { l =>
      (!quiet) || !l.matches("^\\s*;.*")
    }

    val tmpFileRom = new File("build", "spammcc-test.rom")

    println("WRITING ROM TO :\n" + tmpFileRom)
    writeFile(roms, tmpFileRom)

    exec(tmpFileRom, verbose, outputCheck)

    print("ASM RAN OK\n" + filtered.map(_.stripLeading()).mkString("\n"))
    filtered
  }

  private def writeFile(roms: List[List[String]], tmpFileRom: File): Unit = {
    val pw = new PrintWriter(tmpFileRom)

    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }

    pw.close()
  }

  def exec(romsPath: File, verbose: Boolean, outputCheck: List[String] => Unit): Unit = {
    import scala.language.postfixOps

    import scala.sys.process._
    val abs = romsPath.getPath.replaceAll("\\\\", "/")

    println("RUNNING :\n" + abs)

    //    val pb: ProcessBuilder = Process(Seq("cmd", "/c", "bash", "-c", s"""../verilog/simulate_one.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$abs"""))
    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../verilog/spamcc_sim.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$abs"""))

    val success = new AtomicBoolean()
    val lines = ListBuffer.empty[String]

    val logger = ProcessLogger.apply(
      fout = output => {
        lines.append(output)
        if (output.contains("SUCCESS - AT EXPECTED END OF PROGRAM")) success.set(true)
        if (verbose) println("\t   \t: " + output)
      },
      ferr = output => {
        lines.append(output)
        if (verbose) println("\tERR\t: " + output)
      }
    )

    // process has builtin timeout
    val process = pb.run(logger)
    val ex = process.exitValue()

    println("EXIT CODE " + ex)

    outputCheck(lines.toList)

    if (success.get())
      println("SUCCESSFUL SIMULATION")
    else
      fail("SIMULATION - DID NOT REACH END")

  }
}
