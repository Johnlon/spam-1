package scc

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicBoolean

import asm.Assembler
import org.junit.Assert.assertEquals
import org.junit.runners.MethodSorters
import org.junit.{FixMethodOrder, Test}
import org.scalatest.matchers.must.Matchers

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SpamCCTest extends Matchers {

  def split(s: String): List[String] = {
    val strings = s.split("\n")
    strings
      .map(_.stripTrailing().stripLeading())
      .filterNot(_.isBlank).toList
  }

  def assertSameEx(expected: List[String], actual: List[String]) = {
    val end = split(
      """PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
                  """.stripMargin)

    println("\nCOMPARING ASM:")
    assertSame(expected ++ end, actual)
  }

  def assertSame(expected: List[String], actual: List[String]) = {

    if (expected != actual) {
      println("Expected: " + expected)
      println("Actual  : " + actual)

      val e = expected.map(_.stripTrailing().stripLeading()).mkString("\n")
      val a = actual.map(_.stripTrailing().stripLeading()).mkString("\n")
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
        |root_function_main___VAR_a: EQU 0
        |[:root_function_main___VAR_a] = 1
        """.stripMargin)

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
        |root_function_main___VAR_a: EQU 0
        |root_function_main___VAR_b: EQU 1
        |[:root_function_main___VAR_a] = 1
        |[:root_function_main___VAR_b] = 2
        |""".stripMargin)

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
        |root_function_main___VAR_a: EQU 0
        |root_function_main___VAR_b: EQU 1
        |root_function_other___VAR_a: EQU 2
        |root_function_other___VAR_b: EQU 3
        |[:root_function_main___VAR_a] = 1
        |[:root_function_main___VAR_b] = 2
        |PCHITMP = <:root_end
        |PC = >:root_end
        |[:root_function_other___VAR_a] = 1
        |[:root_function_other___VAR_b] = 2
        |root_end:
        |END
        |""".stripMargin)
        
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
        |""".stripMargin)

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
        |root_function_main___VAR_a: EQU 0
        |[:root_function_main___VAR_a] = 1
        |REGD = [:root_function_main___VAR_a]
        |""".stripMargin)

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
      """root_function_main___VAR_a: EQU 0
        |root_function_main___VAR_b: EQU 1
        |root_function_main___VAR_c: EQU 2
        |root_function_main___VAR_d: EQU 3
        |root_function_main___VAR_e: EQU 4
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
      """root_function_main___VAR_a: EQU 0
        |root_function_main___VAR_varExprs_d2: EQU 1
        |root_function_main___VAR_varExprs_d3: EQU 2
        |root_function_main___VAR_b: EQU 3
        |[:root_function_main___VAR_a] = 1
        |REGA = 2
        |[:root_function_main___VAR_varExprs_d2] = REGA
        |REGA = [:root_function_main___VAR_a]
        |[:root_function_main___VAR_varExprs_d3] = REGA
        |REGA = 3
        |REGB = [:root_function_main___VAR_varExprs_d3]
        |[:root_function_main___VAR_varExprs_d3] = REGB + REGA
        |REGA = [:root_function_main___VAR_varExprs_d3]
        |REGB = [:root_function_main___VAR_varExprs_d2]
        |[:root_function_main___VAR_varExprs_d2] = REGB + REGA
        |REGA = [:root_function_main___VAR_varExprs_d2]
        |[:root_function_main___VAR_b] = REGA
        |root_function_main_putcharN___LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharN___LABEL_transmit_2
        |PC = >:root_function_main_putcharN___LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharN___LABEL_wait_1
        |PC = <:root_function_main_putcharN___LABEL_wait_1
        |root_function_main_putcharN___LABEL_transmit_2:
        |UART = [:root_function_main___VAR_b]
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
        |root_function_main_putcharI___LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharI___LABEL_transmit_2
        |PC = >:root_function_main_putcharI___LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharI___LABEL_wait_1
        |PC = <:root_function_main_putcharI___LABEL_wait_1
        |root_function_main_putcharI___LABEL_transmit_2:
        |UART = 65
        |root_function_main_putcharI___LABEL_wait_3:
        |PCHITMP = <:root_function_main_putcharI___LABEL_transmit_4
        |PC = >:root_function_main_putcharI___LABEL_transmit_4 _DO
        |PCHITMP = <:root_function_main_putcharI___LABEL_wait_3
        |PC = <:root_function_main_putcharI___LABEL_wait_3
        |root_function_main_putcharI___LABEL_transmit_4:
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
        |   var a = 1
        |   var a = a + 1
        |   TODO  HOW TO DETECT SUCCESS?
        |   TODO USE AN IF STATEMENT TO EXIT?
        |    TODO OR DO A PUTCHAR AND ASSERT THAT IT COUNTS UP
        |   if (a>10) {
        |     break // have to find end tag of enclosing block somehow  - perhaps make the end tag a property of all blocks?
        |     // goto end_label // thi requires forward references perhaps??
        |   }
        |
        | }
        | label: end_label
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
      """
        |root_function_main___VAR_a: EQU 0
        |[:root_function_main___VAR_a] = 10
        |root_function_main_whileCond1___LABEL_check:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond1___LABEL_top
        |PC = >:root_function_main_whileCond1___LABEL_top _GT
        |PCHITMP = <:root_function_main_whileCond1___LABEL_bot
        |PC = >:root_function_main_whileCond1___LABEL_bot
        |root_function_main_whileCond1___LABEL_top:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA - 1
        |[:root_function_main___VAR_a] = REGA
        |root_function_main_whileCond1_putcharN___LABEL_wait_2:
        |PCHITMP = <:root_function_main_whileCond1_putcharN___LABEL_transmit_3
        |PC = >:root_function_main_whileCond1_putcharN___LABEL_transmit_3 _DO
        |PCHITMP = <:root_function_main_whileCond1_putcharN___LABEL_wait_2
        |PC = <:root_function_main_whileCond1_putcharN___LABEL_wait_2
        |root_function_main_whileCond1_putcharN___LABEL_transmit_3:
        |UART = [:root_function_main___VAR_a]
        |PCHITMP = <:root_function_main_whileCond1___LABEL_check
        |PC = >:root_function_main_whileCond1___LABEL_check
        |root_function_main_whileCond1___LABEL_bot:
        |""".stripMargin)

    assertSameEx(expected, actual)
  }


  private def compile(lines: String, quiet: Boolean = true) = {
    val scc = new SpamCC

    val actual: List[String] = scc.compile(lines)

    val endRemoved: List[String] = actual.filter(!_.equals("END"))
    val successfulTerminationLocation = List("PCHITMP = <$BEAF","PC = >$BEAF","END")

    // jump to signaling location - verilog program monitors this locn
    val str = (endRemoved++successfulTerminationLocation).mkString("\n")
    println("ASSEMBLING:\n" + str)

    val asm = new Assembler

    val roms = asm.assemble(str, quiet = quiet)

    // ditch comments
    val filtered = actual.filter { l =>
      ((!quiet) || !l.matches("^\\s*;.*"))
    }

    val tmpFileRom = new File("build", "spammcc-test.rom")

    println("WRITING ROM TO :\n" + tmpFileRom)
    val pw = new PrintWriter(tmpFileRom)

    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }

    pw.close()

    exec(tmpFileRom)

    filtered
  }

  def exec(romsPath: File): Unit = {
    import scala.language.postfixOps

    import scala.sys.process._
    val abs = romsPath.getPath.replaceAll("\\\\", "/")

    println("RUNNING :\n" + abs)

//    val pb: ProcessBuilder = Process(Seq("cmd", "/c", "bash", "-c", s"""../verilog/simulate_one.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$abs"""))
    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../verilog/spamcc_sim.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$abs"""))

    val success = new AtomicBoolean()

    val logger = ProcessLogger.apply(
      fout = output => {
        if (output.contains("SUCCESS - AT EXPECTED END OF PROGRAM")) success.set(true)
        println("\t   \t: " + output)
      },
      ferr = output =>
        println("\tERR\t: " + output)
    )

    // process has builtin timeout
    val process = pb.run(logger)

    process.exitValue()

    if (success.get())
      println("SUCCESSFUL SIMULATION")
    else
      fail("SIMULATION - DID NOT REACH END")

  }
}
