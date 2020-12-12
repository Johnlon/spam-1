package scc

import scc.SpamCC.split
import scala.language.postfixOps

trait SubBlocks {
  self: SpamCC =>

  sealed trait IsCompoundExpressionBlock {
    def variableName: Option[String]
  }

  trait IsStandaloneVarExpr {
    def variableName: String
  }


  def blkVarExpr: Parser[Block] = name ^^ {
    n => BlkName(n)
  }

  def blkArrayElementExpr: Parser[Block] = name ~ "[" ~ compoundBlkExpr ~ "]" ^^ {
    case arrayName ~ _ ~ blkExpr ~ _ => BlkArrayElement(arrayName, blkExpr)
  }


  def blkNExpr: Parser[Block] = constExpr ^^ {
    konst => BlkConst(konst)
  }

  def blkSingleExpr: Parser[Block] = statementGetchar | blkArrayElementExpr | blkNExpr | blkVarExpr

  def blkExpr: Parser[Block] = blkSingleExpr | "(" ~> compoundBlkExpr <~ ")"

  def compoundBlkExpr: Parser[Block] = blkExpr ~ ((aluOp ~ blkExpr) *) ^^ {
    case leftExpr ~ otherExpr => BlkCompound(leftExpr, otherExpr)
  }

  case class BlkName(variableName: String) extends Block with IsStandaloneVarExpr {
    override def toString = s"$variableName"

    override def gen(depth: Int, parent: Name): List[String] = {
      val labelSrcVar = parent.getVarLabel(variableName).fqn
      List(
        s"REGA = [:$labelSrcVar]",
      )
    }

    override def dump(depth: Int): List[(Int, String)] =
      List(
        (depth, this.getClass.getSimpleName + s"( $variableName )")
      )

  }


  case class BlkArrayElement(arrayName: String, indexExpr: Block) extends Block with IsStandaloneVarExpr {
    override def toString = s"$arrayName"

    override def gen(depth: Int, parent: Name): List[String] = {

      // drops result into A
      val stmts: List[String] = indexExpr.expr(depth + 1, parent)

      val labelSrcVar = parent.getVarLabel(arrayName).fqn

      stmts ++ List(
        s"MARLO = REGA + (>:$labelSrcVar) _S",
        s"MARHI = <:$labelSrcVar",
        s"MARHI = NU B_PLUS_1 <:$labelSrcVar _C",
        s"REGA = RAM",
      )
    }

    override def variableName: String = arrayName

    override def dump(depth: Int): List[(Int, String)] =
      List(
        (depth, this.getClass.getSimpleName + "( name [")
      ) ++
        indexExpr.dump(depth + 1) ++
        List((depth, "] )"))

  }

  case class BlkConst(konst: Int) extends Block {
    override def toString = s"$konst"

    override def gen(depth: Int, parent: Name): List[String] = {
      List(
        s"REGA = $konst",
      )
    }

    override def dump(depth: Int): List[(Int, String)] =
      List(
        (depth, this.getClass.getSimpleName + s"( $konst )")
      )

  }

  case class BlkCompound(leftExpr: Block, otherExpr: List[String ~ Block]) extends Block with IsCompoundExpressionBlock {
    val description = otherExpr.foldLeft(leftExpr.toString()) {
      case (acc, b) =>
        s"$acc ${b._1} (${b._2})"
    }

    override def toString = s"$description"

    override def gen(depth: Int, parent: Name): List[String] = {

      val leftStatement: List[String] = leftExpr.expr(depth + 1, parent)

      // if there is no right side then no need for temporary variables or merge logic
      val optionalExtraForRight = if (otherExpr.nonEmpty) {
        val temporaryVarLabel = parent.assignVarLabel("compoundBlkExpr" + depth + LABEL_NAME_SEPARATOR + Name.nextInt, IsVar).fqn

        val assignLeftToTemp =
          List(
            s"; assign clause 1 result to [:$temporaryVarLabel] = $leftExpr ",
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
              s"; concatenate clause $x to [:$temporaryVarLabel] <= $op $b",
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

    override def dump(depth: Int): List[(Int, String)] =
      List(
        (depth, this.getClass.getSimpleName + "( ")
      ) ++
        leftExpr.dump(depth + 1) ++
        otherExpr.flatMap {
          case op ~ more =>
            List(depth + 2 -> op) ++
              more.dump(depth + 2)

        } ++
        List((depth, ")"))

  }

}
