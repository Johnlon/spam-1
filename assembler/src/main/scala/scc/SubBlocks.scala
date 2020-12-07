package scc

import scc.SpamCC.split
import scala.language.postfixOps

trait SubBlocks {
  self : SpamCC =>

  sealed trait IsCompoundExpressionBlock {
    def variableName: Option[String]
  }
  trait IsStandaloneVarExpr {
    def variableName: String
  }


  def blkVarExpr: Parser[Block] = name ^^ {
    n =>
      new Block("blkVar", s"$n") with IsStandaloneVarExpr {
        override def toString = s"$n"

        override def gen(depth: Int, parent: Name): List[String] = {
          val labelSrcVar = parent.getVarLabel(n).fqn
          List(
            s"REGA = [:$labelSrcVar]",
          )
        }

        override def variableName: String = n
      }
  }

  def blkArrayElementExpr: Parser[Block] = name ~ "[" ~ compoundBlkExpr ~ "]" ^^ {
    case arrayName ~ _ ~ blkExpr ~ _ =>
      new Block("blkArrayElement", s"$arrayName[$blkExpr]") with IsStandaloneVarExpr {
        override def toString = s"$arrayName"

        override def gen(depth: Int, parent: Name): List[String] = {

          // drops result into A
          val stmts: List[String] = blkExpr.expr(depth + 1, parent)

          val labelSrcVar = parent.getVarLabel(arrayName).fqn

          stmts ++ List(
            s"MARLO = REGA + (>:$labelSrcVar) _S",
            s"MARHI = <:$labelSrcVar",
            s"MARHI = NU B_PLUS_1 <:$labelSrcVar _C",
            s"REGA = RAM",
          )
        }

        override def variableName: String = arrayName
      }
  }

  def blkNExpr: Parser[Block] = constExpr ^^ {
    i =>
      new Block("blkNExpr", s"$i") {
        override def toString = s"$i"

        override def gen(depth: Int, parent: Name): List[String] = {
          List(
            s"REGA = $i",
          )
        }

      }
  }

  def blkSingleExpr: Parser[Block] = blkArrayElementExpr | blkNExpr | blkVarExpr

  def blkExpr: Parser[Block] = blkSingleExpr | "(" ~> compoundBlkExpr <~ ")"

  def compoundBlkExpr: Parser[Block] = blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
    case leftExpr ~ otherExpr =>
      val description = otherExpr.foldLeft(leftExpr.toString()) {
        case (acc, b) =>
          s"$acc ${b._1} (${b._2})"
      }

      new Block("compoundBlkExpr", s" $description") with IsCompoundExpressionBlock {

        override def toString = s"$description"

        override def gen(depth: Int, parent: Name): List[String] = {

          val leftStatement: List[String] = leftExpr.expr(depth + 1, parent)

          // if there is no right side then no need for temporary variables or merge logic
          val optionalExtraForRight = if (otherExpr.size > 0) {
            val temporaryVarLabel = parent.assignVarLabel("compoundBlkExpr" + depth, IsVar).fqn

            val assignLeftToTemp =
              List(
                s"; assign clause 1 result to [:$temporaryVarLabel] = ${leftExpr.context} ",
                s"[:$temporaryVarLabel] = REGA"
              )

            // In an expression the result of the previous step is accumulated in the assigned temporaryVarLabel.
            // It is somewhat inefficient that I has to shove the value into RAM and back out on each step.
            var x = 1

            val otherStatements: List[String] = otherExpr.reverse.flatMap { case op ~ b =>
              // clause must drop it's result into REGC
              val expressionClause = b.expr(depth + 1, parent)

              x += 1
              expressionClause ++
                List(
                  s"; concatenate clause $x to [:$temporaryVarLabel] <= $op ${b.context}",
                  s"REGC = [:$temporaryVarLabel]",
                  s"[:$temporaryVarLabel] = REGC $op REGA"
                )
            }

            val suffix = split(
              s"""
                 |; assigning result back to REGA
                 |REGA = [:$temporaryVarLabel]
                 |""")

            assignLeftToTemp ++ otherStatements ++ suffix
          } else Nil

          leftStatement ++ optionalExtraForRight
        }

        /* populated only if this block evaluates to a standalone variable reference*/
        override def variableName: Option[String] = {
          // return a variable name ONLY if this expression is simply a standalone variable name
          if (otherExpr.isEmpty) {
            // is a single clause
            leftExpr match {
              case v: IsStandaloneVarExpr =>
                // clause is a variable name
                Some(v.variableName)
              case _ =>
                None
            }
          } else None
        }
      }
  }

}
