
object Assembler extends InstructionParser {

  def main(args: Array[String]) = {
    val code = List(
      "A: EQU >$214"
      , "REGA=REGA PLUS REGB"
      , "REGA=REGB"
      , "REGA=RAM"
      , "REGA=>$123'S"
      , "REGA=>(2*111)'S"
      , "label1:"
      , "label2: REGA=REGB'S"
      , "REGA=:labelZ ; UNRESOLVED"
      , "; comment"
      , "REGB=REGA"
      , "label3: REGA=:label4"
      , "label4: REGA=:label1",
      "VAL1: EQU 1",
      "VAL2: EQU $ff",
      "VAL3: EQU $abcd",
      "VAL4: EQU :VAL1",
      "start1: ;comment",
      "",
      "label: REGA=1",
      "",
      "; assign ram",
      "[:label]=REGB",
      "RAM=REGB",
      "[:label]=REGB'S",
      "RAM=REGB'S",
      "",
      "REGA=REGA PLUS 123",
      "REGA=REGA PLUS REGC",
      "labelBack: REGA=REGB PLUS'S REGC",
      "REGA=REGA PLUS [:labelBack]",
      "",
      "; imediate values",
      "REGA=12",
      "REGA=12'S",
      "REGA=$ff",
      "REGA=$ff'S",
      "; imediate values by labels",
      "REGA=:label",
      "REGA=:label'S",
      "",
      "; ram access by register",
      "REGA=RAM",
      "REGA=RAM'S",
      "; ram access direct",
      "REGA=[12]",
      "REGA=[12]'S",
      "REGA=[$ff]",
      "REGA=[$ff]'S",
      "REGA=[:label]",
      "REGA=[:label]'S",
      ";ops"
      ,
      "END"
    ).mkString("\n")

    parse(lines, code) match {
      case Success(matched, _) => {
        println("MATCHED : ")
        matched.foreach(l =>
          println(l.str)
        )
        matched.filter(_.unresolved).foreach(l=>println("UNRESOLVED : " + l.str))
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
