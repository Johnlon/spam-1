package scc

trait Condition {
  self: SpamCC =>

  def comparison: Parser[String] = ">" | "<" | ">=" | "<" | "<=" | "==" | "!="

  // return the block of code and the name of the flag to add to the jump operation
  def condition: Parser[(String, Block)] = name ~ comparison ~ constExpr ^^ {
    case varName ~ compOp ~ konst =>
      val b = Condition(varName, compOp, konst)

      val cpuFlag = compOp match {
        case ">" => "_GT"
        case "<" => "_LT"
        case ">=" => "_Z"
        case "<=" => "_Z"
        case "==" => "_EQ"
        case "!=" => "_NE"
      }
      (cpuFlag, b)
  }

  case class Condition(varName: String, compOp: String, konst: Int) extends Block {
    override def gen(depth: Int, parent: Name): List[String] = {
      val label = parent.getVarLabel(varName).fqn

      compOp match {
        case ">" | "<" | "==" | "!=" =>
          List(
            s"; condition :  $varName $compOp $konst",
            s"REGA = [:$label]",
            s"REGA = REGA PASS_A $konst _S" // this op is unimportant
          )
        case ">=" =>
          List(
            s"; condition :  $varName $compOp $konst",
            s"REGA = [:$label]",
            s"REGA = REGA PASS_A $konst _S", // this op is unimportant as we are dong magnitude
            s"REGA = 1",
            s"REGA = 0 _GT", // set REGA=0 if was GT
            s"REGA = 0 _EQ", // set REGA=0 if was EQ
            s"REGA = REGA _S", // set REGA=0 if was EQ
          )
        case "<=" =>
          List(
            s"; condition :  $varName $compOp $konst",
            s"REGA = [:$label]",
            s"REGA = REGA PASS_A $konst _S", // this op is unimportant as we are dong magnitude
            s"REGA = 1",
            s"REGA = 0 _LT", // set REGA=0 if was LT
            s"REGA = 0 _EQ", // set REGA=0 if was EQ
            s"REGA = REGA _S", // set REGA=0 if was EQ
          )
      }
    }
  }

}
