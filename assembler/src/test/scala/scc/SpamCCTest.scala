package scc

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicBoolean

import asm.Assembler
import org.junit.Assert.{assertEquals, fail}
import org.junit.runners.MethodSorters
import org.junit.{FixMethodOrder, Test}
import scc.Checks._
import terminal.Terminal

import scala.collection.mutable.ListBuffer

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SpamCCTest {

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
        |root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 1
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

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
        |root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: EQU   3
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
        |END""".stripMargin)

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
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: EQU   3
        |root_function_main___VAR_b: BYTES [0]
        |root_function_other___VAR_RETURN_HI: EQU   4
        |root_function_other___VAR_RETURN_HI: BYTES [0]
        |root_function_other___VAR_RETURN_LO: EQU   5
        |root_function_other___VAR_RETURN_LO: BYTES [0]
        |root_function_other___VAR_a: EQU   6
        |root_function_other___VAR_a: BYTES [0]
        |root_function_other___VAR_b: EQU   7
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
        |END""".stripMargin)

    // not using Ex function as it assumes main is last function
    assertSame(expected, actual)
  }

  @Test
  def varEqSimpleTwoArgExpr(): Unit = {

    val lines =
      """
        |fun main() {
        |  // a = 63 + 2 = 'A'
        |  var a = 63 + 2;
        |
        |  // b = a + 1 = 'B'
        |  var b = a + 1;
        |
        |  // c = 1 + b = 'C'
        |  var c = 1 + b;
        |
        |  // d = c the d++
        |  var d = c;
        |  let d = d + 1;
        |
        |  // e = a + (b/2) = 'b'
        |  var e = a + (b/2);
        |
        |  // should print 'A'
        |  putchar(a)
        |  // should print 'B'
        |  putchar(b)
        |  // should print 'C'
        |  putchar(c)
        |  // should print 'D'
        |  putchar(d)
        |  // should print 'b'
        |  putchar(e)
        |
        |  // should shift left twice to become the '@' char
        |  let a = %00010000;
        |  let b = 2;
        |  var at = a A_LSL_B b;
        |  // should print '@'
        |  putchar(at)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
      checkTransmittedChar(str, 'b')
      checkTransmittedChar(str, '@')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_b: EQU   3
        |root_function_main___VAR_b: BYTES [0]
        |root_function_main___VAR_c: EQU   4
        |root_function_main___VAR_c: BYTES [0]
        |root_function_main___VAR_d: EQU   5
        |root_function_main___VAR_d: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: EQU   6
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr3: EQU   7
        |root_function_main___VAR_compoundBlkExpr3: BYTES [0]
        |root_function_main___VAR_e: EQU   8
        |root_function_main___VAR_e: BYTES [0]
        |root_function_main___VAR_at: EQU   9
        |root_function_main___VAR_at: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_a] = 65
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA + 1
        |[:root_function_main___VAR_b] = REGA
        |REGA = 1
        |REGA = REGA + [:root_function_main___VAR_b]
        |[:root_function_main___VAR_c] = REGA
        |REGA = [:root_function_main___VAR_c]
        |[:root_function_main___VAR_d] = REGA
        |REGA = [:root_function_main___VAR_d]
        |REGA = REGA + 1
        |[:root_function_main___VAR_d] = REGA
        |REGA = [:root_function_main___VAR_a]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_b]
        |[:root_function_main___VAR_compoundBlkExpr3] = REGA
        |REGA = 2
        |REGC = [:root_function_main___VAR_compoundBlkExpr3]
        |[:root_function_main___VAR_compoundBlkExpr3] = REGC / REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr3]
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_e] = REGA
        |root_function_main_putcharVar_a____LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharVar_a____LABEL_transmit_2
        |PC = >:root_function_main_putcharVar_a____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharVar_a____LABEL_wait_1
        |PC = <:root_function_main_putcharVar_a____LABEL_wait_1
        |root_function_main_putcharVar_a____LABEL_transmit_2:
        |UART = [:root_function_main___VAR_a]
        |root_function_main_putcharVar_b____LABEL_wait_3:
        |PCHITMP = <:root_function_main_putcharVar_b____LABEL_transmit_4
        |PC = >:root_function_main_putcharVar_b____LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharVar_b____LABEL_wait_3
        |PC = <:root_function_main_putcharVar_b____LABEL_wait_3
        |root_function_main_putcharVar_b____LABEL_transmit_4:
        |UART = [:root_function_main___VAR_b]
        |root_function_main_putcharVar_c____LABEL_wait_5:
        |PCHITMP = <:root_function_main_putcharVar_c____LABEL_transmit_6
        |PC = >:root_function_main_putcharVar_c____LABEL_transmit_6 _DO
        |PCHITMP = <:root_function_main_putcharVar_c____LABEL_wait_5
        |PC = <:root_function_main_putcharVar_c____LABEL_wait_5
        |root_function_main_putcharVar_c____LABEL_transmit_6:
        |UART = [:root_function_main___VAR_c]
        |root_function_main_putcharVar_d____LABEL_wait_7:
        |PCHITMP = <:root_function_main_putcharVar_d____LABEL_transmit_8
        |PC = >:root_function_main_putcharVar_d____LABEL_transmit_8 _DO
        |PCHITMP = <:root_function_main_putcharVar_d____LABEL_wait_7
        |PC = <:root_function_main_putcharVar_d____LABEL_wait_7
        |root_function_main_putcharVar_d____LABEL_transmit_8:
        |UART = [:root_function_main___VAR_d]
        |root_function_main_putcharVar_e____LABEL_wait_9:
        |PCHITMP = <:root_function_main_putcharVar_e____LABEL_transmit_10
        |PC = >:root_function_main_putcharVar_e____LABEL_transmit_10 _DO
        |PCHITMP = <:root_function_main_putcharVar_e____LABEL_wait_9
        |PC = <:root_function_main_putcharVar_e____LABEL_wait_9
        |root_function_main_putcharVar_e____LABEL_transmit_10:
        |UART = [:root_function_main___VAR_e]
        |[:root_function_main___VAR_a] = 16
        |[:root_function_main___VAR_b] = 2
        |REGA = [:root_function_main___VAR_a]
        |REGB = [:root_function_main___VAR_b]
        |[:root_function_main___VAR_at] = REGA << REGB
        |root_function_main_putcharVar_at____LABEL_wait_11:
        |PCHITMP = <:root_function_main_putcharVar_at____LABEL_transmit_12
        |PC = >:root_function_main_putcharVar_at____LABEL_transmit_12 _DO
        |PCHITMP = <:root_function_main_putcharVar_at____LABEL_wait_11
        |PC = <:root_function_main_putcharVar_at____LABEL_wait_11
        |root_function_main_putcharVar_at____LABEL_transmit_12:
        |UART = [:root_function_main___VAR_at]
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    //assertSame(expected, actual)
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
      checkTransmittedChar(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
        |root_function_main___VAR_a: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: EQU   3
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr3: EQU   4
        |root_function_main___VAR_compoundBlkExpr3: BYTES [0]
        |root_function_main___VAR_b: EQU   5
        |root_function_main___VAR_b: BYTES [0]
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

    // assertSame(expected, actual)
  }

  @Test
  def putchar(): Unit = {

    val lines =
      """
        |fun main() {
        |  putchar(65)
        |  putchar('B')
        |  var c=67;
        |  putchar(c)
        |  putchar(c+1)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_c: EQU   2
        |root_function_main___VAR_c: BYTES [0]
        |root_function_main_putcharGeneral___VAR_compoundBlkExpr2: EQU   3
        |root_function_main_putcharGeneral___VAR_compoundBlkExpr2: BYTES [0]
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
        |[:root_function_main___VAR_c] = 67
        |root_function_main_putcharVar_c____LABEL_wait_5:
        |PCHITMP = <:root_function_main_putcharVar_c____LABEL_transmit_6
        |PC = >:root_function_main_putcharVar_c____LABEL_transmit_6 _DO
        |PCHITMP = <:root_function_main_putcharVar_c____LABEL_wait_5
        |PC = <:root_function_main_putcharVar_c____LABEL_wait_5
        |root_function_main_putcharVar_c____LABEL_transmit_6:
        |UART = [:root_function_main___VAR_c]
        |REGA = [:root_function_main___VAR_c]
        |[:root_function_main_putcharGeneral___VAR_compoundBlkExpr2] = REGA
        |REGA = 1
        |REGC = [:root_function_main_putcharGeneral___VAR_compoundBlkExpr2]
        |[:root_function_main_putcharGeneral___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main_putcharGeneral___VAR_compoundBlkExpr2]
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

    // assertSame(expected, actual)
  }

  @Test
  def getchar(): Unit = {

    val lines =
      """
        |fun main() {
        |  var o =1;
        |  while (true) {
        |    var g = getchar();
        |    putchar(g)
        |  }
        |}
        |""".stripMargin

    compile(lines, verbose = true, timeout = 1000)
  }

  @Test
  def valEqLogical(): Unit = {

    val lines =
      """
        |fun main() {
        | var a=1>0;
        | putchar(a)
        |
        | let a=0>1;
        | putchar(a)
        |
        | let a=0==1;
        | putchar(a)
        |
        | let a=1==1;
        | putchar(a)
        |
        | let a=%1010 & %1100;
        | putchar(a)

        | let a=%1010 | %1100;
        | putchar(a)
        |}
        |""".stripMargin

    compile(lines, verbose = true, outputCheck = {
      str =>
        checkTransmittedL('h', str, List("01", "00", "00", "01", "08", "0e"))
    })
  }

  @Test
  def whileLoopCond(): Unit = {

    val lines =
      """
        |fun main() {
        | var a=10;
        | while(a>0) {
        |   let a=a-1;
        |   putchar(a)
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, outputCheck = {
      lines =>
        checkTransmittedDec(lines, List(9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
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

    //assertSame(expected, actual)
  }


  @Test
  def whileLoopTrueIfBreak(): Unit = {

    val lines =
      """
        |fun main() {
        | var a = 1;
        | while(true) {
        |   let a = a + 1;
        |
        |   if (a>10) {
        |     break
        |   }
        |   putchar(a)
        |
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, outputCheck = {
      lines =>
        checkTransmittedDec(lines, List(2, 3, 4, 5, 6, 7, 8, 9, 10))
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_a: EQU   2
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

    //assertSame(expected, actual)
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
        | //let d = a2;
        | putchar(d)
        | putchar(a2)
        | putchar(a3)
        | putchar(a4)
        |
        | // ascii 33 dec
        | let a1 = '!';
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

    val actual: List[String] = compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, '?')
      checkTransmittedChar(str, 'E')
      checkTransmittedChar(str, '!')
    })

    val expected = split(
      """root_function_print___VAR_RETURN_HI: EQU   0
        |root_function_print___VAR_RETURN_HI: BYTES [0]
        |root_function_print___VAR_RETURN_LO: EQU   1
        |root_function_print___VAR_RETURN_LO: BYTES [0]
        |root_function_print___VAR_a1: EQU   2
        |root_function_print___VAR_a1: BYTES [0]
        |root_function_print___VAR_a2: EQU   3
        |root_function_print___VAR_a2: BYTES [0]
        |root_function_print___VAR_a3: EQU   4
        |root_function_print___VAR_a3: BYTES [0]
        |root_function_print___VAR_a4: EQU   5
        |root_function_print___VAR_a4: BYTES [0]
        |root_function_print___VAR_d: EQU   6
        |root_function_print___VAR_d: BYTES [0]
        |root_function_main___VAR_RETURN_HI: EQU   7
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   8
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_arg1: EQU   9
        |root_function_main___VAR_arg1: BYTES [0]
        |root_function_main___VAR_arg2: EQU   10
        |root_function_main___VAR_arg2: BYTES [0]
        |root_function_main___VAR_compoundBlkExpr2: EQU   11
        |root_function_main___VAR_compoundBlkExpr2: BYTES [0]
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
        |[:root_function_print___VAR_a1] = REGA
        |REGA = [:root_function_main___VAR_arg2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = [:root_function_main___VAR_arg1]
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a2] = REGA
        |REGA = 63
        |[:root_function_print___VAR_a3] = REGA
        |REGA = [:root_function_main___VAR_arg1]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGA
        |REGA = 4
        |REGC = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_main___VAR_compoundBlkExpr2] = REGC + REGA
        |REGA = [:root_function_main___VAR_compoundBlkExpr2]
        |[:root_function_print___VAR_a4] = REGA
        |[:root_function_print___VAR_RETURN_HI] = < :root_function_main___LABEL_RETURN_LOCATION_9
        |[:root_function_print___VAR_RETURN_LO] = > :root_function_main___LABEL_RETURN_LOCATION_9
        |PCHITMP = < :root_function_print___LABEL_START
        |PC = > :root_function_print___LABEL_START
        |root_function_main___LABEL_RETURN_LOCATION_9:
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

    //assertSame(expected, actual)
  }

  @Test
  def functionCalls2Deep(): Unit = {

    val lines =
      """
        |fun depth2(b1 out) {
        | let b1 = b1 + 1;
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

    val actual: List[String] = compile(lines, quiet = true, outputCheck = {
      lines =>
        checkTransmittedChars(lines, List("B"))
    })

    val expected = split(
      """root_function_depth2___VAR_RETURN_HI: EQU   0
        |root_function_depth2___VAR_RETURN_HI: BYTES [0]
        |root_function_depth2___VAR_RETURN_LO: EQU   1
        |root_function_depth2___VAR_RETURN_LO: BYTES [0]
        |root_function_depth2___VAR_b1: EQU   2
        |root_function_depth2___VAR_b1: BYTES [0]
        |root_function_depth1___VAR_RETURN_HI: EQU   3
        |root_function_depth1___VAR_RETURN_HI: BYTES [0]
        |root_function_depth1___VAR_RETURN_LO: EQU   4
        |root_function_depth1___VAR_RETURN_LO: BYTES [0]
        |root_function_depth1___VAR_a1: EQU   5
        |root_function_depth1___VAR_a1: BYTES [0]
        |root_function_main___VAR_RETURN_HI: EQU   6
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   7
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_arg1: EQU   8
        |root_function_main___VAR_arg1: BYTES [0]
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
        |[:root_function_depth2___VAR_b1] = REGA
        |[:root_function_depth2___VAR_RETURN_HI] = < :root_function_depth1___LABEL_RETURN_LOCATION_1
        |[:root_function_depth2___VAR_RETURN_LO] = > :root_function_depth1___LABEL_RETURN_LOCATION_1
        |PCHITMP = < :root_function_depth2___LABEL_START
        |PC = > :root_function_depth2___LABEL_START
        |root_function_depth1___LABEL_RETURN_LOCATION_1:
        |REGA = [:root_function_depth2___VAR_b1]
        |[:root_function_depth1___VAR_a1] = REGA
        |PCHITMP = [:root_function_depth1___VAR_RETURN_HI]
        |PC = [:root_function_depth1___VAR_RETURN_LO]
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_arg1] = 65
        |REGA = [:root_function_main___VAR_arg1]
        |[:root_function_depth1___VAR_a1] = REGA
        |[:root_function_depth1___VAR_RETURN_HI] = < :root_function_main___LABEL_RETURN_LOCATION_2
        |[:root_function_depth1___VAR_RETURN_LO] = > :root_function_main___LABEL_RETURN_LOCATION_2
        |PCHITMP = < :root_function_depth1___LABEL_START
        |PC = > :root_function_depth1___LABEL_START
        |root_function_main___LABEL_RETURN_LOCATION_2:
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

    //assertSame(expected, actual)
  }

  @Test
  def references(): Unit = {

    val lines =
      """
        |fun main() {
        |
        | // define string
        | var even = "Even\0";
        | var odd = "Odd\0";
        |
        | // value at 16 bit var ptr becomes address of array odd
        | ref ptr = odd;
        |
        | var i = 10;
        | while (i>0) {
        |   let i = i - 1;
        |   var c = i % 2;
        |   if (c == 0) {
        |       // set pointer to point at even
        |       let ptr = even;
        |   }
        |   if (c != 0) {
        |      let ptr = odd;
        |   }
        |   puts(ptr)
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, quiet = true, outputCheck = str => {
      val value: List[String] = "OddEvenOddEvenOddEvenOddEvenOddEven".toList.map(_.toString)
      checkTransmittedL('c', str, value)
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_even: EQU   2
        |root_function_main___VAR_even: BYTES [69, 118, 101, 110, 0]
        |root_function_main___VAR_odd: EQU   7
        |root_function_main___VAR_odd: BYTES [79, 100, 100, 0]
        |root_function_main___VAR_ptr: EQU   11
        |root_function_main___VAR_ptr: BYTES [0, 7]
        |root_function_main___VAR_i: EQU   13
        |root_function_main___VAR_i: BYTES [0]
        |root_function_main_whileCond3___VAR_c: EQU   14
        |root_function_main_whileCond3___VAR_c: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_i] = 10
        |root_function_main_whileCond3___LABEL_CHECK:
        |REGA = [:root_function_main___VAR_i]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond3___LABEL_BODY
        |PC = >:root_function_main_whileCond3___LABEL_BODY _GT
        |PCHITMP = <:root_function_main_whileCond3___LABEL_AFTER
        |PC = >:root_function_main_whileCond3___LABEL_AFTER
        |root_function_main_whileCond3___LABEL_BODY:
        |REGA = [:root_function_main___VAR_i]
        |REGA = REGA - 1
        |[:root_function_main___VAR_i] = REGA
        |REGA = [:root_function_main___VAR_i]
        |REGA = REGA % 2
        |[:root_function_main_whileCond3___VAR_c] = REGA
        |root_function_main_whileCond3_ifCond1___LABEL_CHECK:
        |REGA = [:root_function_main_whileCond3___VAR_c]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond3_ifCond1___LABEL_BODY
        |PC = >:root_function_main_whileCond3_ifCond1___LABEL_BODY _EQ
        |PCHITMP = <:root_function_main_whileCond3_ifCond1___LABEL_AFTER
        |PC = >:root_function_main_whileCond3_ifCond1___LABEL_AFTER
        |root_function_main_whileCond3_ifCond1___LABEL_BODY:
        |REGA = <:root_function_main___VAR_even
        |[:root_function_main___VAR_ptr] = REGA
        |REGA = >:root_function_main___VAR_even
        |[:root_function_main___VAR_ptr + 1] = REGA
        |root_function_main_whileCond3_ifCond1___LABEL_AFTER:
        |root_function_main_whileCond3_ifCond2___LABEL_CHECK:
        |REGA = [:root_function_main_whileCond3___VAR_c]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond3_ifCond2___LABEL_BODY
        |PC = >:root_function_main_whileCond3_ifCond2___LABEL_BODY _NE
        |PCHITMP = <:root_function_main_whileCond3_ifCond2___LABEL_AFTER
        |PC = >:root_function_main_whileCond3_ifCond2___LABEL_AFTER
        |root_function_main_whileCond3_ifCond2___LABEL_BODY:
        |REGA = <:root_function_main___VAR_odd
        |[:root_function_main___VAR_ptr] = REGA
        |REGA = >:root_function_main___VAR_odd
        |[:root_function_main___VAR_ptr + 1] = REGA
        |root_function_main_whileCond3_ifCond2___LABEL_AFTER:
        |MARHI = [:root_function_main___VAR_ptr]
        |MARLO = [:root_function_main___VAR_ptr+1]
        |root_function_main_whileCond3_puts___LABEL_startloop_4:
        |NOOP = RAM _S
        |PCHITMP = <:root_function_main_whileCond3_puts___LABEL_endloop_7
        |PC = >:root_function_main_whileCond3_puts___LABEL_endloop_7 _Z
        |root_function_main_whileCond3_puts___LABEL_wait_5:
        |PCHITMP = <:root_function_main_whileCond3_puts___LABEL_transmit_6
        |PC = >:root_function_main_whileCond3_puts___LABEL_transmit_6 _DO
        |PCHITMP = <:root_function_main_whileCond3_puts___LABEL_wait_5
        |PC = <:root_function_main_whileCond3_puts___LABEL_wait_5
        |root_function_main_whileCond3_puts___LABEL_transmit_6:
        |UART = RAM
        |MARLO = MARLO + 1 _S
        |MARHI = MARHI + 1 _C
        |PCHITMP = <:root_function_main_whileCond3_puts___LABEL_startloop_4
        |PC = >:root_function_main_whileCond3_puts___LABEL_startloop_4
        |root_function_main_whileCond3_puts___LABEL_endloop_7:
        |PCHITMP = <:root_function_main_whileCond3___LABEL_CHECK
        |PC = >:root_function_main_whileCond3___LABEL_CHECK
        |root_function_main_whileCond3___LABEL_AFTER:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    //assertSame(expected, actual)
  }

  @Test
  def stringIndexing(): Unit = {

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
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_string: EQU   2
        |root_function_main___VAR_string: BYTES [65, 66, 67, 68, 0]
        |root_function_main___VAR_ac: EQU   7
        |root_function_main___VAR_ac: BYTES [0]
        |root_function_main___VAR_b: EQU   8
        |root_function_main___VAR_b: BYTES [0]
        |root_function_main___VAR_bc: EQU   9
        |root_function_main___VAR_bc: BYTES [0]
        |root_function_main___VAR_d: EQU   10
        |root_function_main___VAR_d: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |REGA = 0
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |[:root_function_main___VAR_ac] = REGA
        |[:root_function_main___VAR_b] = 1
        |REGA = [:root_function_main___VAR_b]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
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

    //assertSame(expected, actual)
  }

  @Test
  def stringIteration(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        |
        | var idx = 0;
        | var c = string[idx];
        | while (c != 0) {
        |   putchar(c)
        |   let idx = idx + 1;
        |   let c = string[idx];
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_string: EQU   2
        |root_function_main___VAR_string: BYTES [65, 66, 67, 68, 0]
        |root_function_main___VAR_idx: EQU   7
        |root_function_main___VAR_idx: BYTES [0]
        |root_function_main___VAR_c: EQU   8
        |root_function_main___VAR_c: BYTES [0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |[:root_function_main___VAR_idx] = 0
        |REGA = [:root_function_main___VAR_idx]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |[:root_function_main___VAR_c] = REGA
        |root_function_main_whileCond1___LABEL_CHECK:
        |REGA = [:root_function_main___VAR_c]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond1___LABEL_BODY
        |PC = >:root_function_main_whileCond1___LABEL_BODY _NE
        |PCHITMP = <:root_function_main_whileCond1___LABEL_AFTER
        |PC = >:root_function_main_whileCond1___LABEL_AFTER
        |root_function_main_whileCond1___LABEL_BODY:
        |root_function_main_whileCond1_putcharVar_c____LABEL_wait_2:
        |PCHITMP = <:root_function_main_whileCond1_putcharVar_c____LABEL_transmit_3
        |PC = >:root_function_main_whileCond1_putcharVar_c____LABEL_transmit_3 _DO
        |PCHITMP = <:root_function_main_whileCond1_putcharVar_c____LABEL_wait_2
        |PC = <:root_function_main_whileCond1_putcharVar_c____LABEL_wait_2
        |root_function_main_whileCond1_putcharVar_c____LABEL_transmit_3:
        |UART = [:root_function_main___VAR_c]
        |REGA = [:root_function_main___VAR_idx]
        |REGA = REGA + 1
        |[:root_function_main___VAR_idx] = REGA
        |REGA = [:root_function_main___VAR_idx]
        |MARLO = REGA + (>:root_function_main___VAR_string) _S
        |MARHI = <:root_function_main___VAR_string
        |MARHI = NU B_PLUS_1 <:root_function_main___VAR_string _C
        |REGA = RAM
        |[:root_function_main___VAR_c] = REGA
        |PCHITMP = <:root_function_main_whileCond1___LABEL_CHECK
        |PC = >:root_function_main_whileCond1___LABEL_CHECK
        |root_function_main_whileCond1___LABEL_AFTER:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    // assertSame(expected, actual)
  }

  @Test
  def snake(): Unit = {
    import terminal.Terminal._

    val lines =
      s"""fun main() {
         | var a = 33;
         | var b = 0;
         | var l = 0;
         | putchar(${ORIGIN.toInt})
         | while ( a < 255 ) {
         |  let b = 10;
         |
         |  while ( b > 0 ) {
         |   putchar(${RIGHT.toInt})
         |   putchar( a )
         |   //putchar( '#' )
         |   //putchar( '.' )
         |   let b = b - 1;
         |  }
         |  let b = 10;
         |  while ( b > 0 ) {
         |   putchar(${DOWN.toInt})
         |   putchar( a )
         |   //putchar( '#' )
         |   //putchar( '.' )
         |   let b = b - 1;
         |  }
         |  let b = 10;
         |  while ( b > 0 ) {
         |   putchar(${LEFT.toInt})
         |   putchar( a )
         |   //putchar( '#' )
         |   //putchar( '.' )
         |   let b = b - 1;
         |  }
         |
         |  let b = 10;
         |  while ( b > 0 ) {
         |   putchar(${UP.toInt})
         |   putchar( a )
         |   //putchar( '#' )
         |   //putchar( '.' )
         |   let b = b - 1;
         |  }
         |  putchar(${RIGHT.toInt})
         |  putchar(${DOWN.toInt})
         |
         |   let a = a + 1;
         | }
         |
         |}
         |
         |// END  COMMAND
         |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, '?')
      checkTransmittedChar(str, 'E')
      checkTransmittedChar(str, '!')
    })

  }

  @Test
  def puts(): Unit = {

    val lines =
      """
        |fun main() {
        | // define string
        | var string = "ABCD\0";
        | puts(string)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines, verbose = true, quiet = true, outputCheck = str => {
      checkTransmittedChar(str, 'A')
      checkTransmittedChar(str, 'B')
      checkTransmittedChar(str, 'C')
      checkTransmittedChar(str, 'D')
    })

    val expected = split(
      """root_function_main___VAR_RETURN_HI: EQU   0
        |root_function_main___VAR_RETURN_HI: BYTES [0]
        |root_function_main___VAR_RETURN_LO: EQU   1
        |root_function_main___VAR_RETURN_LO: BYTES [0]
        |root_function_main___VAR_string: EQU   2
        |root_function_main___VAR_string: BYTES [65, 66, 67, 68, 0]
        |PCHITMP = < :ROOT________main_start
        |PC = > :ROOT________main_start
        |ROOT________main_start:
        |root_function_main___LABEL_START:
        |MARLO = >:root_function_main___VAR_string
        |MARHI = <:root_function_main___VAR_string
        |root_function_main_puts___LABEL_startloop_1:
        |NOOP = RAM _S
        |PCHITMP = <:root_function_main_puts___LABEL_endloop_4
        |PC = >:root_function_main_puts___LABEL_endloop_4 _Z
        |root_function_main_puts___LABEL_wait_2:
        |PCHITMP = <:root_function_main_puts___LABEL_transmit_3
        |PC = >:root_function_main_puts___LABEL_transmit_3 _DO
        |PCHITMP = <:root_function_main_puts___LABEL_wait_2
        |PC = <:root_function_main_puts___LABEL_wait_2
        |root_function_main_puts___LABEL_transmit_3:
        |UART = RAM
        |MARLO = MARLO + 1 _S
        |MARHI = MARHI + 1 _C
        |PCHITMP = <:root_function_main_puts___LABEL_startloop_1
        |PC = >:root_function_main_puts___LABEL_startloop_1
        |root_function_main_puts___LABEL_endloop_4:
        |PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END""".stripMargin)

    //  assertSame(expected, actual)
  }


  def writeFile(roms: List[List[String]], tmpFileRom: File): Unit = {
    val pw = new PrintWriter(tmpFileRom)

    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }

    pw.close()
  }

  def writeUartControlFile(tmpFileRom: File): Unit = {
    val pw = new PrintWriter(tmpFileRom)

    // permit a big transmit
    pw.write("t1000000")
    pw.write("#100000000")

    pw.close()
  }

  def exec(romsPath: File, tmpUartControl: File, verbose: Boolean, outputCheck: List[String] => Unit, timeout: Int): Unit = {
    import scala.language.postfixOps
    import scala.sys.process._
    val romFileUnix = romsPath.getPath.replaceAll("\\\\", "/")
    val controlFileUnix = tmpUartControl.getPath.replaceAll("\\\\", "/")

    println("RUNNING :\n" + romFileUnix)

    //    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../verilog/spamcc_sim.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$romFileUnix  +uart_control_file=`pwd`/$controlFileUnix"""))
    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../verilog/spamcc_sim.sh '$timeout' ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$romFileUnix"""))

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

  def compile(linesRaw: String, verbose: Boolean = false, quiet: Boolean = true, outputCheck: List[String] => Unit = _ => {}, timeout: Int = 20): List[String] = {
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
    val IsBytes = """^\s*[a-zA-Z0-9_]+:\s*BYTES\s.*$""".r
    val IsString = """^\s*[a-zA-Z0-9_]+:\s*STR\s.*$""".r

    actual.foreach { l =>
      if (IsComment.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsEqu.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsLabel.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      } else if (IsBytes.matches(l)) {
        val withoutBrackets = l.replaceAll(""".*\[""", "").replaceAll("""\].*$""", "")

        val bytes = withoutBrackets.split(",").length

        println(s"${pc.formatted("%5d")}  $l")
        pc += bytes

      } else if (IsString.matches(l)) {
        val withoutQuotes = l.replaceAll("""^[^"]+"""", "").replaceAll(""""[^"]*$""", "")
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
    val tmpUartControl = new File(Terminal.uartControl)

    println("WRITING ROM TO :\n" + tmpFileRom)
    writeFile(roms, tmpFileRom)
    writeUartControlFile(tmpUartControl)
    exec(tmpFileRom, tmpUartControl, verbose, outputCheck, timeout)

    print("ASM RAN OK\n" + filtered.map(_.stripLeading()).mkString("\n"))
    filtered
  }

}
