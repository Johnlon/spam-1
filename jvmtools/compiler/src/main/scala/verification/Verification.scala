package verification

import asm.Assembler
import org.junit.jupiter.api.Assertions.assertEquals
import scc.SpamCC
import terminal.VerilogUARTTerminalApp

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicReference
import scala.collection.mutable.ListBuffer

object Verification {

  def compile(linesRaw: String,
              verbose: Boolean = false,
              stripComments: Boolean = true,
              dataIn: List[String] = List("t10000000"),
              outputCheck: List[String] => Unit = _ => {},
              checkHalt: Option[HaltCode] = Some(HaltCode(65535, 255)),
              timeout: Int = 60
             ): List[String] = {

    val scc = new SpamCC

    val lines = "program {\n" + linesRaw + "\n}"
    val assemblyCode: List[String] = scc.compile(lines)

    val str = assemblyCode.mkString("\n")
    println("ASSEMBLING:\n")
    prettyPrintAsm(assemblyCode)

    val asm = new Assembler
    val roms = asm.assemble(str, stripComments = stripComments)

    verifyRoms(verbose, dataIn, outputCheck, checkHalt, timeout, roms)

    // ditch comments
    val filtered = assemblyCode.filter { l =>
      (!stripComments) || !l.matches("^\\s*;.*")
    }

//    if (verbose) {
//      print("ASM RAN OK\n" + filtered.map(_.stripLeading()).mkString("\n"))
//    }

    filtered
  }

  private def prettyPrintAsm(assemblyCode: List[String]): Unit = {
    var pc = 0

    val IsEqu = "^\\s*[a-zA-Z0-9_]+:\\s*EQU.*$".r
    val IsLabel = "^\\s*[a-zA-Z0-9_]+:\\s*$".r
    val IsComment = "^\\s*;.*$".r
    val IsBytes = """^\s*[a-zA-Z0-9_]+:\s*BYTES\s.*$""".r
    val IsString = """^\s*[a-zA-Z0-9_]+:\s*STR\s.*$""".r

    assemblyCode.foreach { l =>
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
  }

  def verifyRoms(verbose: Boolean,
                 uartDataIn: List[String],
                 outputCheck: List[String] => Unit,
                 checkHalt: Option[HaltCode],
                 timeout: Int,
                 roms: Seq[List[String]]): Unit = {


    val tmpFileRom = new File("build", "spammcc-test.rom")

    val tmpUartControl = new File(VerilogUARTTerminalApp.uartControl)

    println("WRITING ROM TO :\n" + tmpFileRom.getAbsolutePath)
    writeFile(roms, tmpFileRom)

    writeUartControlFile(tmpUartControl, uartDataIn)
    exec(tmpFileRom, tmpUartControl, verbose, outputCheck, checkHalt, timeout)

  }

  def writeFile(roms: Seq[List[String]], tmpFileRom: File): Unit = {
    if (tmpFileRom.getParentFile.exists()) {
      if (!tmpFileRom.getParentFile.isDirectory) {
        sys.error("expected a directory : " + tmpFileRom.getParentFile.getAbsolutePath)
      }
    } else {
      tmpFileRom.getParentFile.mkdirs()
    }

    val pw = new PrintWriter(tmpFileRom)

    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }

    pw.close()
  }

  def writeUartControlFile(tmpFileRom: File, data: List[String]): Unit = {
    val pw = new PrintWriter(tmpFileRom)

    data.foreach {
      pw.println
    }
    pw.close()
  }

  def exec(romsPath: File, tmpUartControl: File, verbose: Boolean,
           outputCheck: List[String] => Unit,
           checkHalt: Option[HaltCode],
           timeout: Int): Unit = {
    import scala.language.postfixOps
    import scala.sys.process._
    val romFileUnix = romsPath.getPath.replaceAll("\\\\", "/")
    val controlFileUnix = tmpUartControl.getPath.replaceAll("\\\\", "/")

    println("RUNNING :\n" + romFileUnix)

    //    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../verilog/spamcc_sim.sh ../verilog/cpu/demo_assembler_roms.v +rom=`pwd`/$romFileUnix  +uart_control_file=`pwd`/$controlFileUnix"""))
    val pb: ProcessBuilder = Process(Seq("bash", "-c", s"""../../verilog/spamcc_sim.sh '$timeout' ../../verilog/cpu/demo_assembler_roms.v `pwd`/$romFileUnix"""))

    val halted = new AtomicReference[HaltCode]()

    val lines = ListBuffer.empty[String]

    def addLine(line: String): Unit = {
      lines.synchronized {
        lines.append(line)
      }
    }

    var cycles = -1

    val logger = ProcessLogger.apply(
      fout = output => {
        addLine("OUT:" + output)
        if (output.contains("--- HALTED ")) {
          val haltedRegex = "--* HALTED <(\\d+)\\s.*> <(\\d+)\\s.*>.*".r
          val haltedRegex(marHaltCode, aluHaltCode) = output

          halted.set(HaltCode(marHaltCode.toInt, aluHaltCode.toInt))
        }
        if (verbose) println("\t   \t: " + output)

        val CyclesPattern =".*CYCLES ([0-9]+) .*".r

        output match {
          case CyclesPattern(c) => cycles = c.toInt
          case _ =>
        }
      },
      ferr = output => {
        addLine("ERR:" + output)
        if (verbose) println("\tERR\t: " + output)
      }
    )

    // process has builtin timeout
    println("CMD: " + pb)
    val process = pb.run(logger)
    val ex = process.exitValue()
    println("EXIT CODE " + ex)
    println("CYCLE COUNT " + cycles)

    if (ex == 127) {
      println("CANT FIND EXEC")
    }

    lines.synchronized {
      outputCheck(lines.toList)
    }

    val actualHalt = halted.get()
    val expectedHalt = checkHalt.orNull
    if (expectedHalt == null) {
      if (actualHalt != null) {
        println("expected no halt, but halted with: " + actualHalt)
      }
    } else {
      assertEquals(expectedHalt, actualHalt)
    }
    println("halted state: " + actualHalt)
  }
}

case class HaltCode private(mar: Short, alu: Byte) {
  override def toString: String = {
    val str = "00000000" + alu.toBinaryString
    val bin = str.substring(str.length - 8)
    f"HaltCode(mar:${mar & 0xffff}%-5d (0x${mar & 0xffff}%04x),  alu:$alu%-3d (0x${alu & 0xff}%02x, b$bin))"
  }
}
object HaltCode {
  def apply(mar: Int, alu: Int): HaltCode = {
    new HaltCode((mar & 0xffff).toShort, (alu & 0xff).toByte)
  }
}
