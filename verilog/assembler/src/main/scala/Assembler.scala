import java.io.{File, PrintWriter}

import scala.io.Source

object Assembler {

  def main(args: Array[String]) = {
    if (args.size!=1) {
      System.err.println("missing argument : asm file name")
      sys.exit(1)
    }
    val fileName = args(0)

    val code = Source.fromFile(fileName).getLines().mkString("\n")

    val asm = new Assembler()
    val roms: List[List[String]] = asm.assemble(code)

    val pw = new PrintWriter(new File(s"${fileName}.rom"))
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
          l => {
            val address = l._1.instructionAddress
            System.out.println(l._2.formatted("%03d") + " pc:" + address.formatted("%04x") + ":" + address.formatted("%05d") + ": " + l._1)
          }
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

