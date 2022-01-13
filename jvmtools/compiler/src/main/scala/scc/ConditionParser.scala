package scc

import scala.util.parsing.input.Positional

case class Condition(flagToCheck: String, block: ConditionBlock) extends Positional

trait ConditionParser {
  self: SpamCC =>

  def comparison: Parser[String] = ">=" | "<=" | ">" | "<" | "==" | "!="


  // OPTIMISATION
  // return the block of code and the name of the flag to add to the jump operation
  def conditionWithConst: Parser[Condition] = positioned {
    name ~ comparison ~ constExpression ^^ {
      case varName ~ compOp ~ konst =>
        val b = ConditionVarConstCompare(varName, compOp, konst)

        val cpuFlag: String = conditionFlags(compOp)
        Condition(cpuFlag, b)
    }
  }

  // GENERAL PURPOSE
  // return the block of code and the name of the flag to add to the jump operation
  def conditionWithExpr: Parser[Condition] = positioned {
    blkCompoundAluExpr ~ comparison ~ blkCompoundAluExpr ^^ {
      case exprL ~ compOp ~ exprR =>
        val b = ConditionBlockBlockCompare(exprL, compOp, exprR)

        val cpuFlag: String = conditionFlags(compOp)
        Condition(cpuFlag, b)
    }
  }

  def condition: Parser[Condition] = conditionWithConst | conditionWithExpr

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

