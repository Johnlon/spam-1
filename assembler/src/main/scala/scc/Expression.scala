package scc

import scc.Program.{DivByZeroLabel, MathErrorLabel}
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

  override def gen(depth: Int, parent: Scope): Seq[String] = {

    // drops result into WORKHI/LO
    val stmts: Seq[String] = indexExpr.expr(depth + 1, parent)

    val labelSrcVar = parent.getVarLabel(arrayName).fqn

    stmts ++ Seq(
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

  override def gen(depth: Int, parent: Scope): Seq[String] = {

    val leftStatement: Seq[String] = leftExpr.expr(depth + 1, parent)

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

          // clause must drop it's result into WORKLO/WORKHI
          val expressionValueClause = b.expr(depth + 1, parent)

          val label = s"; concatenate clause ${idx + 1} to ram [:$temporaryVarLabel] <= $op $b"

          // WORKLO/WORKI have already been updated by the next term so apply WORKLO/HI To the currently stashed accumulated value
          val thisClause = op match {
            case "+" =>
              List(
                s"$TMPREG = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMPREG A_PLUS_B $WORKLO _S",
                s"$TMPREG = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel+1] = $TMPREG A_PLUS_B_PLUS_C $WORKHI"
              )
            case "-" =>
              List(
                s"$TMPREG = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMPREG A_MINUS_B $WORKLO _S",
                s"$TMPREG = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel+1] = $TMPREG A_MINUS_B_MINUS_C $WORKHI"
              )
            case op@("&" | "|" | "^" | "~&" | "~") =>
              List(
                s"$TMPREG = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMPREG $op $WORKLO _S",
                s"$TMPREG = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel + 1] = $TMPREG $op $WORKHI"
              )
            case ">>" =>
              val shiftLoop = parent.fqnLabelPathUnique("shiftLoop")
              val doShift = parent.fqnLabelPathUnique("doShift")
              val endLoop = parent.fqnLabelPathUnique("endShiftLoop")

              // sadly my alu doesn't allow carry-in to the shift operations
              List(
                s"$shiftLoop:",

                s"; is loop done?",
                s"NOOP = $WORKHI A_MINUS_B 0 _S",
                s"PCHITMP = < :$doShift",
                s"PC = > :$doShift _NE",
                s"NOOP = $WORKLO A_MINUS_B 0 _S",
                s"PC = > :$doShift _NE",

                s"PCHITMP = < :$endLoop",
                s"PC = > :$endLoop",

                s"$doShift:",

                s"; count down loop",
                s"$WORKLO = $WORKLO A_MINUS_B 1 _S",
                s"$WORKHI = $WORKHI A_MINUS_B_MINUS_C 0",

                s"; do one shift",
                s"$TMPREG = [:$temporaryVarLabel + 1]",
                s"$V2 = $TMPREG",
                s"$V2 = $V2 & 1",
                s"$V2 = $V2 << 7",
                s"[:$temporaryVarLabel + 1] = $TMPREG A_LSR_B 1 _S",

                s"; LSR load lo byte and or in the carry",
                s"$TMPREG = [:$temporaryVarLabel]",
                s"$TMPREG = $TMPREG A_LSR_B 1",
                s"[:$temporaryVarLabel] = $TMPREG  | $V2",

                s"; loop again",
                s"PCHITMP = < :$shiftLoop",
                s"PC = > :$shiftLoop",
                s"$endLoop:",

              )
            case "<<" =>
              val shiftLoop = parent.fqnLabelPathUnique("shiftLoop")
              val doShift = parent.fqnLabelPathUnique("doShift")
              val endLoop = parent.fqnLabelPathUnique("endShiftLoop")

              // sadly my alu doesn't allow carry-in to the shift operations
              List(
                s"$shiftLoop:",

                s"; is loop done?",
                s"NOOP = $WORKHI A_MINUS_B 0 _S",
                s"PCHITMP = < :$doShift",
                s"PC = > :$doShift _NE",
                s"NOOP = $WORKLO A_MINUS_B 0 _S",
                s"PC = > :$doShift _NE",

                s"PCHITMP = < :$endLoop",
                s"PC = > :$endLoop",

                s"$doShift:",

                s"; count down loop",
                s"$WORKLO = $WORKLO A_MINUS_B 1 _S",
                s"$WORKHI = $WORKHI A_MINUS_B_MINUS_C 0",

                s"; do one shift",
                s"$TMPREG = [:$temporaryVarLabel]",
                s"$V2 = $TMPREG",
                s"$V2 = $V2 & %10000000",
                s"$V2 = $V2 >> 7",

                s"[:$temporaryVarLabel] = $TMPREG A_LSL_B 1 _S",

                s"; LSR load lo byte and or in the carry",
                s"$TMPREG = [:$temporaryVarLabel+1]",
                s"$TMPREG = $TMPREG A_LSL_B 1",
                s"[:$temporaryVarLabel+1] = $TMPREG  | $V2",

                s"; loop again",
                s"PCHITMP = < :$shiftLoop",
                s"PC = > :$shiftLoop",
                s"$endLoop:",

              )
            case "/" =>
              List(
                s"; only allow div by 0-255 for now so abort if RHS > 255",
                s"NOOP = 0 A_MINUS_B [:$temporaryVarLabel + 1] _S",
                s"PCHITMP = < $MathErrorLabel",
                s"PC = > $MathErrorLabel _NE",

                s"; check for divide by zero",
                s"NOOP = 0 A_MINUS_B [:$temporaryVarLabel] _S", // only do op is HI was 0, result is _Z if both are zero
                s"PCHITMP = < $DivByZeroLabel",
                s"PC = > $DivByZeroLabel _EQ",
                ???, // NEEDTO FIGURE OUT OR ALLOW ONLY @ 8 BIT ?
                s"; run division loop - MARHI is divisor, MARLO is quotient ",
                s"MARHI = [:$temporaryVarLabel]",
                s"MARLO = 0",

                // WORK  OUT THE  DIV LOOP
                s"$V2 = [:$temporaryVarLabel + 1]",
                s"$TMPREG = [:$temporaryVarLabel]",
                s"subtract_loop:",
                s"[:$temporaryVarLabel] = $TMPREG A_MINUS_B $WORKLO _S",
                s"PCHITMP = < :done_loop",
                s"PC = > :done_loop _C",

                s"[:$temporaryVarLabel+1] = $TMPREG A_MINUS_B_MINUS_C $WORKHI",
                s"done_loop:",

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
