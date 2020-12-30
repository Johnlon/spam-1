package scc

trait ConditionParser {
  self: SpamCC =>

  def comparison: Parser[String] = ">=" | "<=" | ">" | "<" | "==" | "!="

  // return the block of code and the name of the flag to add to the jump operation
  def condition: Parser[(String, Block)] = name ~ comparison ~ constExpression ^^ {
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

}
