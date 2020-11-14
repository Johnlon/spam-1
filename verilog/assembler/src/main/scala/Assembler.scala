import scala.io.Source

object Assembler extends InstructionParser {

  def main(args: Array[String]) = {
    val code = Source.fromFile("operations.txt").getLines.mkString("\n")

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

        matched.collect { case x: Instruction => x }.foreach(l => println("CODE : " + l.bytes))
      }
      case msg: Failure => {
        println(s"FAILURE: $msg ")
        System.exit(0)
      }
      case msg: Error => {
        println(s"ERROR: $msg")
        System.exit(0)
      }
    }
  }
}

