package scc

trait ConditionParser {
  self: SpamCC =>

  def comparison: Parser[String] = ">=" | "<=" | ">" | "<" | "==" | "!="

  // return the block of code and the name of the flag to add to the jump operation
  def conditionWithConst: Parser[(String, Block)] = name ~ comparison ~ constExpression ^^ {
    case varName ~ compOp ~ konst =>
      val b = ConditionVarConstCompare(varName, compOp, konst)

      val cpuFlag: String = conditionFlags(compOp)
      (cpuFlag, b)
  }

  // return the block of code and the name of the flag to add to the jump operation
  def conditionExpr: Parser[(String, ConditionBlockBlockCompare)] = blkCompoundAluExpr ~ comparison ~ blkCompoundAluExpr ^^ {
    case exprL ~ compOp ~ exprR =>
      val b = ConditionBlockBlockCompare(exprL, compOp, exprR)

      val cpuFlag: String = conditionFlags(compOp)
      (cpuFlag, b)
  }

  private def conditionFlags(compOp: String): String = {
    val cpuFlag = compOp match {
      case ">" => "_GT"
      case "<" => "_LT"
      case ">=" => "_Z"
      case "<=" => "_Z"
      case "==" => "_EQ"
      case "!=" => "_NE"
    }
    cpuFlag
  }
}

