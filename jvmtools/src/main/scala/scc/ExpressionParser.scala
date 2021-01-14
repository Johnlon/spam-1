package scc

import scala.language.postfixOps

trait IsStandaloneVarExpr {
  def variableName: String
}


trait ExpressionParser {
  self: SpamCC =>

  def blkName: Parser[Block] = name ^^ {
    n =>
      BlkName(n)
  }

  def blkArrayElement: Parser[Block] = name ~ "[" ~ blkCompoundAluExpr ~ "]" ^^ {
    case arrayName ~ _ ~ blkExpr ~ _ =>
      BlkArrayElement(arrayName, blkExpr)
  }


  def blkConst: Parser[Block] = constExpression ^^ {
    konst =>
      BlkConst(konst)
  }

  def blkCompoundAluExpr: Parser[BlkCompoundAluExpr] = blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
    case leftExpr ~ otherExpr =>
      val o = otherExpr map {
        case a ~ e => AluExpr(a, e)
      }
      BlkCompoundAluExpr(leftExpr, o)
  }

  def factor: Parser[Block] = statementGetchar | blkArrayElement | blkConst | blkName

  def blkExpr: Parser[Block] = factor | "(" ~> blkCompoundAluExpr <~ ")"
}
