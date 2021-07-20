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


  def blkLiteral: Parser[Block] = constExpression ^^ {
    konst =>
      BlkLiteral(konst)
  }

  def blkWaituart: Parser[Block] = positioned {
    "waituart" ~ "(" ~ ")" ^^ {
      _ => Waituart()
    }
  }

  def blkGetuart: Parser[Block] = positioned {
    "getuart" ~ "(" ~ ")" ^^ {
      _ => Getuart()
    }
  }

  def blkRandom: Parser[Block] = positioned {
    "random()" ^^ {
      _ => Random()
    }
  }

  def blkLsr8: Parser[Block] = positioned {
    "lsr8" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => Lsr8(block)
    }
  }

  def blkLsl8: Parser[Block] = positioned {
    "lsl8" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => Lsl8(block)
    }
  }

  def blkLsr1: Parser[Block] = positioned {
    "lsr1" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => Lsr1(block)
    }
  }

  def blkLsl1: Parser[Block] = positioned {
    "lsl1" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => Lsl1(block)
    }
  }

  def blkCompoundAluExpr: Parser[BlkCompoundAluExpr] = blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
    case leftExpr ~ otherExpr =>
      val o = otherExpr map {
        case a ~ e => AluExpr(a, e)
      }
      BlkCompoundAluExpr(leftExpr, o)
  }

  // ORDER MATTERS HERE!!!
  def factor: Parser[Block] = blkRandom | blkWaituart | blkGetuart |
    blkLsr8 | blkLsl8 | blkLsr1 | blkLsl1 |
    blkArrayElement | blkLiteral | blkName

  def blkExpr: Parser[Block] = factor | "(" ~> blkCompoundAluExpr <~ ")"
}
