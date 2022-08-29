package scc

import asm.AluOp

import scala.util.parsing.combinator.JavaTokenParsers

trait  AluOpParser {
  self: JavaTokenParsers =>

  // used by the Compiler and includes 16 bit ops or anything else needed
  def aluOp: Parser[String] = {
    val abbreviatedOpNames: Seq[String] = AluOp.values.filter(_.isAbbreviated).map(x => x.abbrev)
    val longOpNames: Seq[String] = AluOp.values.map(_.enumName)

    val synthetic16BitOps = Seq("*")

    val allOpNames = (longOpNames ++ abbreviatedOpNames ++ synthetic16BitOps).reverse.toList

    val opParser = allOpNames.map(literal).reduceLeft {
      // combine the parsers using an Or operation
      _ | _
    }

    opParser
  }
}
