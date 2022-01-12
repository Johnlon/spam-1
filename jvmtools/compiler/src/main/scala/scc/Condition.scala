package scc

import asm.AluOp
import scc.Scope.LABEL_NAME_SEPARATOR
import scc.SpamCC.TWO_BYTE_STORAGE

case class ConditionBlockBlockCompare(exprL: BlkCompoundAluExpr, compOp: String, exprR: BlkCompoundAluExpr) extends Block {

  override def toString() = s"expr $compOp expr"

  // FIXME - MAYBE A BUG HERE SWITCHING BETWEEN SIGNED AND UNSIGNED ON SECOND BYTE?
  // FIXME - NEEDS TESTS AS I DUNNO THE LOGIC

  //    val UseSigned = true
  //    val magOp = (if (UseSigned)
  //      AluOp.A_MINUS_B_SIGNEDMAG
  //    else
  //      AluOp.A_MINUS_B).preferredName
  val SignedComp = AluOp.A_MINUS_B_SIGNEDMAG
  val UnsignedComp = AluOp.A_MINUS_B

  override protected[this] def gen(depth: Int, parent: Scope): Seq[String] = {
    val valueLCode = exprL.expr(depth, parent)
    val valueRCode = exprR.expr(depth, parent)

    val temporaryVarLabelL = parent.assignVarLabel("compoundBlkExprL" + depth + LABEL_NAME_SEPARATOR + Scope.nextInt, IsVar16, TWO_BYTE_STORAGE).fqn
    val temporaryVarLabelR = parent.assignVarLabel("compoundBlkExprB" + depth + LABEL_NAME_SEPARATOR + Scope.nextInt, IsVar16, TWO_BYTE_STORAGE).fqn

    // code to run each expression and copy result into a temp var upon which the comparison can functionv
    val exprEvalCode: Seq[String] =
      valueLCode ++
      Seq(
        s"[:$temporaryVarLabelL]     = $WORKLO",
        s"[:$temporaryVarLabelL + 1] = $WORKHI"
      ) ++
      valueRCode ++
      Seq(
        s"[:$temporaryVarLabelR]     = $WORKLO",
        s"[:$temporaryVarLabelR + 1] = $WORKHI"
      )

    val compStatements = compOp match {
      case ">" | "<" | "==" | "!=" =>
        List(
          s"; condition : $compOp $exprL $exprR",
          s"; compare hi",
          s"$WORKHI = [:$temporaryVarLabelL + 1]",
          s"$WORKHI = $WORKHI $SignedComp [:$temporaryVarLabelR + 1] _S",
          s"; if was == then compare lo ",
          s"$WORKLO = [:$temporaryVarLabelL]",
          s"$WORKLO = $WORKLO $UnsignedComp [:$temporaryVarLabelR] _EQ_S"
          // sets various flags
        )
      case ">=" =>
        val checkLabel = parent.fqnLabelPathUnique("checkGE")
        List(
          s"; condition : $compOp $exprL $exprR",
          s"; compare hi",
          s"$WORKLO = [:$temporaryVarLabelL + 1]",
          s"$WORKLO = $WORKLO $SignedComp [:$temporaryVarLabelR + 1] _S", // this op is unimportant as we are doing magnitude comp

          s"; if _GT or LT then is definitive so break ",
          s"PCHITMP = < :$checkLabel",

          s"$WORKLO = 0 _GT", // set WORKLO=0 as a flag meaning condition was met
          s"PC = > :$checkLabel _GT",

          s"$WORKLO = 1 _LT", // clear  WORKLO=1 as a flag meaning condition was not met
          s"PC = > :$checkLabel _LT",

          s"; top byte is == so compare lo",
          s"$WORKLO = [:$temporaryVarLabelL]",
          s"$WORKLO = $WORKLO $UnsignedComp [:$temporaryVarLabelR] _S",

          s"; set $WORKLO==0 as flag to indicate condition was met",
          s"$WORKLO = 1",
          s"$WORKLO = 0 _GT", // set REG=0 if was GT
          s"$WORKLO = 0 _EQ", // set REG=0 if was EQ

          s"$checkLabel:",
          s"$WORKLO = $WORKLO _S", // set _Z if condition was met
          // sets _Z flag
        )
      case "<=" =>
        val checkLabel = parent.fqnLabelPathUnique("checkLE")
        List(
          s"; condition : $compOp $exprL $exprR",
          s"; compare hi",
          s"$WORKLO = [:$temporaryVarLabelL + 1]",
          s"$WORKLO = $WORKLO $SignedComp [:$temporaryVarLabelR + 1] _S", // this op is unimportant as we are dong magnitude

          s"; if _GT or LT then is definitive so break ",
          s"PCHITMP = < :$checkLabel",

          s"$WORKLO = 0 _LT", // set WORKLO=0 as a flag meaning condition was met
          s"PC = > :$checkLabel _LT",

          s"$WORKLO = 1 _GT", // clear  WORKLO=1 as a flag meaning condition was not met
          s"PC = > :$checkLabel _GT",

          s"; top byte is == so compare lo",
          s"$WORKLO = [:$temporaryVarLabelL]",
          s"$WORKLO = $WORKLO $UnsignedComp [:$temporaryVarLabelR] _S",

          s"; set $WORKLO==0 as flag to indicate condition was met",
          s"$WORKLO = 1",
          s"$WORKLO = 0 _LT", // set REG=0 if was GT
          s"$WORKLO = 0 _EQ", // set REG=0 if was EQ

          s"$checkLabel:",
          s"$WORKLO = $WORKLO _S", // set _Z if condition was me
          // sets _Z flag
        )
    }
    exprEvalCode ++ compStatements
  }
}

case class ConditionVarConstCompare(varName: String, compOp: String, konst: Int) extends Block {
  override def toString() = s"$varName $compOp $konst"

  override def gen(depth: Int, parent: Scope): List[String] = {
    val label = parent.getVarLabel(varName).fqn

    // TODO: work out how to designate use of Signed of Unsigned comparison!!
    // record vars as signed or unsigned?    unsigned byte a = 1    // ditch var
    // do it with the op?     a <:s
    // what about signed vs unsigned const?     a < -1:s   or is -1 automatically signed and 255 is automatically unsigned?
    // and if using octal or hex then are they signed or unsigned?
    // maybe restrict signs to the ops??

    // FIXME - MAYBE A BUG HERE SWITCHING BETWEEN SIGNED AND UNSIGNED ON SECOND BYTE?
    // FIXME - NEEDS TESTS AS I DUNNO THE LOGIC

    //    val UseSigned = true
    //    val magOp = (if (UseSigned)
    //      AluOp.A_MINUS_B_SIGNEDMAG
    //    else
    //      AluOp.A_MINUS_B).preferredName
    val SignedComp = AluOp.A_MINUS_B_SIGNEDMAG
    val UnsignedComp = AluOp.A_MINUS_B


    val compStatements = compOp match {
      case ">" | "<" | "==" | "!=" =>
        List(
          s"; condition :  $varName $compOp $konst",
          s"; compare hi",
          s"$WORKHI = [:$label + 1]",
          s"$WORKHI = $WORKHI $SignedComp (<$konst) _S",
          s"; if was == then compare lo ",
          s"$WORKLO = [:$label]",
          s"$WORKLO = $WORKLO $UnsignedComp (> $konst) _EQ_S"
          // sets various flags
        )
      case ">=" =>
        val checkLabel = parent.fqnLabelPathUnique("checkGE")
        List(
          s"; condition :  $varName $compOp $konst",
          s"; compare hi",
          s"$WORKLO = [:$label + 1]",
          s"$WORKLO = $WORKLO $SignedComp (< $konst) _S", // this op is unimportant as we are dong magnitude

          s"; if _GT or LT then is definitive so break ",
          s"PCHITMP = < :$checkLabel",

          s"$WORKLO = 0 _GT", // set WORKLO=0 as a flag meaning condition was met
          s"PC = > :$checkLabel _GT",

          s"$WORKLO = 1 _LT", // clear  WORKLO=1 as a flag meaning condition was not met
          s"PC = > :$checkLabel _LT",

          s"; top byte is == so compare lo",
          s"$WORKLO = [:$label]",
          s"$WORKLO = $WORKLO $UnsignedComp $konst _S",

          s"; set $WORKLO==0 as flag to indicate condition was met",
          s"$WORKLO = 1",
          s"$WORKLO = 0 _GT", // set REG=0 if was GT
          s"$WORKLO = 0 _EQ", // set REG=0 if was EQ

          s"$checkLabel:",
          s"$WORKLO = $WORKLO _S", // set _Z if condition was me
          // sets _Z flag
        )
      case "<=" =>
        val checkLabel = parent.fqnLabelPathUnique("checkLE")
        List(
          s"; condition :  $varName $compOp $konst",
          s"; compare hi",
          s"$WORKLO = [:$label + 1]",
          s"$WORKLO = $WORKLO $SignedComp (< $konst) _S", // this op is unimportant as we are dong magnitude

          s"; if _GT or LT then is definitive so break ",
          s"PCHITMP = < :$checkLabel",

          s"$WORKLO = 0 _LT", // set WORKLO=0 as a flag meaning condition was met
          s"PC = > :$checkLabel _LT",

          s"$WORKLO = 1 _GT", // clear  WORKLO=1 as a flag meaning condition was not met
          s"PC = > :$checkLabel _GT",

          s"; top byte is == so compare lo",
          s"$WORKLO = [:$label]",
          s"$WORKLO = $WORKLO $UnsignedComp $konst _S",

          s"; set $WORKLO==0 as flag to indicate condition was met",
          s"$WORKLO = 1",
          s"$WORKLO = 0 _LT", // set REG=0 if was GT
          s"$WORKLO = 0 _EQ", // set REG=0 if was EQ

          s"$checkLabel:",
          s"$WORKLO = $WORKLO _S", // set _Z if condition was me
          // sets _Z flag
        )
    }

    compStatements

  }
}
