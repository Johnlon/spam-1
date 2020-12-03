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

  def assertSameEx(expected: List[String], actual: List[String]): Unit = {
    val end = split(
      """PCHITMP = <:root_end
        |PC = >:root_end
        |root_end:
        |END
                  """.stripMargin)

    println("\nCOMPARING ASM:")
    assertSame(expected ++ end, actual)
  }

  def assertSame(expected: List[String], actual: List[String]): Unit = {

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
      """def main() {
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
  def varEq1AndVarEq1Plus1(): Unit = {

    val lines =
      """
        |def main() {
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
  def twoFunctions(): Unit = {

    val lines =
      """
        |def main() {
        | var a=1;
        | var b=2;
        |}
        |def other() {
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
  def returnLiteralConst(): Unit = {

    val lines =
      """
        |def main() {
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
  def returnVar(): Unit = {

    val lines =
      """
        |def main() {
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
  def varEqSimpleTwoArgExpr(): Unit = {

    val lines =
      """
        |def main() {
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
  def varEqNestedExpr(): Unit = {

    val lines =
      """def main() {
        |  var a = 1;
        |  var b = 2 + (a + 3);
        |  putchar(b)
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main___VAR_a: EQU 0
        |root_function_main___VAR_b: EQU 1
        |[:root_function_main___VAR_a] = 1
        |REGA = 2
        |REGC = REGA
        |REGA = [:root_function_main___VAR_a]
        |REGC = REGA
        |REGA = 3
        |REGC = REGC + REGA
        |REGA = REGC
        |REGC = REGC + REGA
        |REGA = REGC
        |[:root_function_main___VAR_b] = REGA
        |root_function_main_putcharN_b____LABEL_wait_1:
        |PCHITMP = <:root_function_main_putcharN_b____LABEL_transmit_2
        |PC = >:root_function_main_putcharN_b____LABEL_transmit_2 _DO
        |PCHITMP = <:root_function_main_putcharN_b____LABEL_wait_1
        |PC = <:root_function_main_putcharN_b____LABEL_wait_1
        |root_function_main_putcharN_b____LABEL_transmit_2:
        |UART = [:root_function_main___VAR_b]""".stripMargin)

    assertSameEx(expected, actual)
  }

  @Test
  def putchar(): Unit = {

    val lines =
      """
        |def main() {
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
  def whileLoopCond(): Unit = {

    val lines =
      """
        |def main() {
        | var a=10;
        | while(a>0) {
        |   var a=a-1;
        |   putchar(a)
        | }
        |}
        |""".stripMargin

    val actual: List[String] = compile(lines)

    val expected = split(
      """root_function_main___VAR_a: EQU 0
        |[:root_function_main___VAR_a] = 10
        |root_function_main_whileCond1___LABEL_check:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA PASS_A 0 _S
        |PCHITMP = <:root_function_main_whileCond1___LABEL_body
        |PC = >:root_function_main_whileCond1___LABEL_body _GT
        |PCHITMP = <:root_function_main_whileCond1___LABEL_bot
        |PC = >:root_function_main_whileCond1___LABEL_bot
        |root_function_main_whileCond1___LABEL_body:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA - 1
        |[:root_function_main___VAR_a] = REGA
        |root_function_main_whileCond1_putcharN_a____LABEL_wait_2:
        |PCHITMP = <:root_function_main_whileCond1_putcharN_a____LABEL_transmit_3
        |PC = >:root_function_main_whileCond1_putcharN_a____LABEL_transmit_3 _DO
        |PCHITMP = <:root_function_main_whileCond1_putcharN_a____LABEL_wait_2
        |PC = <:root_function_main_whileCond1_putcharN_a____LABEL_wait_2
        |root_function_main_whileCond1_putcharN_a____LABEL_transmit_3:
        |UART = [:root_function_main___VAR_a]
        |PCHITMP = <:root_function_main_whileCond1___LABEL_check
        |PC = >:root_function_main_whileCond1___LABEL_check
        |root_function_main_whileCond1___LABEL_bot:""".stripMargin)

    assertSameEx(expected, actual)
  }


  @Test
  def whileLoopTrueIfBreak(): Unit = {

    val lines =
      """
        |def main() {
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
      """root_function_main___VAR_a: EQU 0
        |[:root_function_main___VAR_a] = 1
        |root_function_main_whileTrue2___LABEL_body:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA + 1
        |[:root_function_main___VAR_a] = REGA
        |root_function_main_whileTrue2_ifCond1___LABEL_check:
        |REGA = [:root_function_main___VAR_a]
        |REGA = REGA PASS_A 10 _S
        |PCHITMP = <:root_function_main_whileTrue2_ifCond1___LABEL_body
        |PC = >:root_function_main_whileTrue2_ifCond1___LABEL_body _GT
        |PCHITMP = <:root_function_main_whileTrue2_ifCond1___LABEL_bot
        |PC = >:root_function_main_whileTrue2_ifCond1___LABEL_bot
        |root_function_main_whileTrue2_ifCond1___LABEL_body:
        |PCHITMP = <:root_function_main_whileTrue2___LABEL_after
        |PC = >:root_function_main_whileTrue2___LABEL_after
        |root_function_main_whileTrue2_ifCond1___LABEL_bot:
        |PCHITMP = <:root_function_main_whileTrue2___LABEL_body
        |PC = >:root_function_main_whileTrue2___LABEL_body
        |root_function_main_whileTrue2___LABEL_after:""".stripMargin)

    assertSameEx(expected, actual)
  }

  @Test
  def functionCalls(): Unit = {

    val lines =
      """
        |// START FN COMMAND
        |
        |def print(a1, a2, a3 out) {
        | // FN COMMENT
        | var d = a1;
        | putchar(d)
        | putchar(a2)
        |
        | var a2 = 'Z';
        | // END FN COMMENT
        |}
        |
        |def main() {
        | var arg1 = '!';
        | var arg2 = '?';
        | var arg3 = '~';
        |
        | // CALLING PRINT
        | print(arg1, arg2, arg3)
        |
        | // CALLING PUT CHAR
        | putchar(65)
        | putchar(arg3)
        | putchar(66)
        |
        |}
        |
        |// END  COMMAND
        |""".stripMargin

    val actual: List[String] = compile(lines, quiet = true, outputCheck = str => {
      if (!str.find(_.contains("TRANSMITTING h21")).isDefined) {
        fail("did not transmit '!' char")
      }
      if (!str.find(_.contains("TRANSMITTING h3f")).isDefined) {
        fail("did not transmit '?' char")
      }
    })

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

  private def compile(linesRaw: String, quiet: Boolean = true, outputCheck: List[String] => Unit = _ => {}): List[String] = {
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

    actual.foreach { l =>
      if (IsComment.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsEqu.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
      }
      else if (IsLabel.matches(l)) {
        println(s"${"".formatted("%5s")}  $l")
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

    exec(tmpFileRom, outputCheck)

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

  def exec(romsPath: File, outputCheck: List[String] => Unit): Unit = {
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
        println("\t   \t: " + output)
      },
      ferr = output => {
        lines.append(output)
        println("\tERR\t: " + output)
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
