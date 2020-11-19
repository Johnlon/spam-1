import java.io.{File, PrintWriter}

import scala.io.Source

object Assembler {

  def main(args: Array[String]) = {
    val code = Source.fromFile("fibonacci_stack_using_pointer_optimised.txt").getLines().mkString("\n")

    val asm = new Assembler()
    val roms: List[List[String]] = asm.assemble(code)

    val pw = new PrintWriter(new File("build/program.rom"))
    roms.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }
    pw.close()
  }
}

class Assembler extends InstructionParser with Knowing with Lines with Devices {


  def assemble(code: String): List[List[String]] = {

    parse(lines, code) match {
      case Success(matched, _) => {
        println("Statements:")
        matched.zipWithIndex.foreach(
          l => System.out.println(l._2.formatted("%03d") + " pc:" + l._1.instructionAddress.formatted("%04x") + " : " + l._1)
        )
        val unresolvedStatements = matched.zipWithIndex.filter(_._1.unresolved)
        if (unresolvedStatements.size > 0) {
          System.err.println("Unresolved values:")
          unresolvedStatements.foreach(l => System.err.println(l._2.formatted("%03d") + " : " + l._1))
          sys.exit(1)
        }

        val instructions = matched.collect { case x: Instruction => x.bytes }
        instructions.zipWithIndex.foreach(l => println("CODE : " + l))
        instructions
      }
      case msg: Failure => {
        sys.error(s"FAILURE: $msg ")

      }
      case msg: Error => {
        sys.error(s"ERROR: $msg")
      }
    }
  }
}

