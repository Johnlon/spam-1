package scc

import scc.Scope.LABEL_NAME_SEPARATOR
import scc.SpamCC.{TWO_BYTE_STORAGE, split}

case class BlkName(variableName: String) extends Block with IsStandaloneVarExpr {
  override def toString = s"$variableName"

  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelSrcVar = parent.getVarLabel(variableName).fqn
    List(
      s"$WORKLO = [:$labelSrcVar]",
      s"$WORKHI = [:$labelSrcVar + 1]",
    )
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + s"( $variableName )")
    )

}


case class BlkArrayElement(arrayName: String, indexExpr: Block) extends Block with IsStandaloneVarExpr {
  override def toString = s"$arrayName"

  override def gen(depth: Int, parent: Scope): List[String] = {

    // drops result into WORKHI/LO
    val stmts: List[String] = indexExpr.expr(depth + 1, parent)

    val labelSrcVar = parent.getVarLabel(arrayName).fqn

    stmts ++ List(
      s"; add the low byte of the index to the low byte of the array address + set flags",
      s"MARLO = $WORKLO + (>:$labelSrcVar) _S",
      s"; add the hi byte of the index to the hi byte of the array address - do not set flags",
      s"MARHI = $WORKHI + (<:$labelSrcVar)",
      s"; if the low byte carried then add one to MARHI",
      s"MARHI = NU B_PLUS_1 MARHI _C",
      s"; pull from RAM into WORK registers",
      s"$WORKLO = RAM",
      s"$WORKHI = 0",
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

  override def gen(depth: Int, parent: Scope): List[String] = {
    List(
      s"$WORKLO = > $konst",
      s"$WORKHI = < $konst",
    )
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + s"( $konst )")
    )

}


case class AluExpr(aluop: String, rhs: Block)


case class BlkCompoundAluExpr(leftExpr: Block, otherExpr: List[AluExpr]) extends Block {
  val description: String = otherExpr.foldLeft(leftExpr.toString) {
    case (acc, b) =>
      s"$acc ${b.aluop} (${b.rhs})"
  }

  override def toString = s"$description"

  override def gen(depth: Int, parent: Scope): List[String] = {

    val leftStatement: List[String] = leftExpr.expr(depth + 1, parent)

    // if there is no right side then no need for temporary variables or merge logic
    val optionalExtraForRight = if (otherExpr.nonEmpty) {
      val temporaryVarLabel = parent.assignVarLabel("compoundBlkExpr" + depth + LABEL_NAME_SEPARATOR + Scope.nextInt, IsVar16, TWO_BYTE_STORAGE).fqn

      val assignLeftToTemp =
        List(
          s"; backup the clause 0 result to ram : [:$temporaryVarLabel] = $leftExpr ",
          s"[:$temporaryVarLabel] = $WORKLO",
          s"[:$temporaryVarLabel+1] = $WORKHI"
        )

      // In an expression the result of the previous step is accumulated in the assigned temporaryVarLabel.
      // It is somewhat inefficient that I has to shove the value into RAM and back out on each step.
      val otherStatements: List[String] = otherExpr.reverse.zipWithIndex.flatMap {
        case (AluExpr(op, b), idx) =>
          // clause must drop it's result into $V3
          val expressionValueClause = b.expr(depth + 1, parent)

          val label = s"; concatenate clause ${idx + 1} to ram [:$temporaryVarLabel] <= $op $b"

          val thisClause = op match {
            case "+" =>
              List(
                label,
                s"$TMPREG = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMPREG A_PLUS_B $WORKLO _S",
                s"$TMPREG = [:$temporaryVarLabel+1]",
                s"[:$temporaryVarLabel+1] = $TMPREG A_PLUS_B_PLUS_C $WORKHI"
              )
            case "-" =>
              List(
                label,
                s"$TMPREG = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMPREG A_MINUS_B $WORKLO _S",
                s"$TMPREG = [:$temporaryVarLabel+1]",
                s"[:$temporaryVarLabel+1] = $TMPREG A_MINUS_B_MINUS_C $WORKHI"
              )
            case x =>
              sys.error("NOT IMPL " + x)
          }
          expressionValueClause ++ (label +: thisClause)
      }

      val suffix = split(
        s"""
           |; assigning lo result back to $WORKLO and hi to $WORKHI
           |$WORKLO = [:$temporaryVarLabel]
           |$WORKHI = [:$temporaryVarLabel+1]
           |""")

      assignLeftToTemp ++ otherStatements ++ suffix
    } else Nil

    leftStatement ++ optionalExtraForRight
  }

  /* populated only if this block evaluates to a standalone variable reference */
  def standaloneVariableName: Option[String] = {
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
        case AluExpr(op, more) =>
          List(depth + 2 -> op) ++
            more.dump(depth + 2)

      } ++
      List((depth, ")"))

}
