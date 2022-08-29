package scc

import scala.language.postfixOps

trait IsStandaloneVarExpr {
  def variableName: String
}

trait ExpressionParser {
  self: SpamCC =>

  def blkName: Parser[Block] = positioned {
    name ^^ {
      n =>
        BlkName(n)
    }
  }

  def blkArrayElement: Parser[Block] = positioned {
    name ~ "[" ~ blkCompoundAluExpr ~ "]" ^^ {
      case arrayName ~ _ ~ blkExpr ~ _ =>
        BlkArrayElement(arrayName, blkExpr)
    }
  }


  def blkLiteral: Parser[Block] = positioned {
    constExpression ^^ {
      konst =>
        BlkLiteral(konst)
    }
  }

  def blkReadPort: Parser[Block] = positioned {
    "readport" ~ "(" ~>! readPort <~ ")" ^^ {
      portname => BlkReadPort(portname)
    }
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
      _ =>
        Random()
    }
  }

  def blkCompoundAluExpr: Parser[BlkCompoundAluExpr] = positioned {
    blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
      case leftExpr ~ otherExpr =>
        val o = otherExpr map {
          case a ~ e => AluExpr(a, e)
        }
        BlkCompoundAluExpr(leftExpr, o)
    }
  }

  // ORDER MATTERS HERE!!!
  def factor: Parser[Block] =
    blkReadPort | blkRandom |
      blkWaituart | blkGetuart |
      blkArrayElement | blkLiteral | blkName

  def blkExpr: Parser[Block] = factor | "(" ~> blkCompoundAluExpr <~ ")"
}
