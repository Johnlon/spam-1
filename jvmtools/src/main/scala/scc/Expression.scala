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

object COUNTER {
  var iii : Int = 0
}

import scc.COUNTER.iii

case class BlkCompoundAluExpr(leftExpr: Block, otherExpr: List[AluExpr])
  extends Block(nestedName = s"Compound${Scope.nextInt}") {

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

      // !!!!!!!
      // The expr evaluates left to right and results accumulate in the temp var pair and new right hand side args are found in $WORKLO/$WORKHI
      // !!!!!!!

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

          val scope = parent.pushScope("CHAIN"+ idx + "_" + iii)
          
          // clause caled here is expected to drop it's result into WORKLO/WORKHI
          val expressionValueClause = b.expr(depth + 1, scope)

          val label = s"; concatenate clause ${idx + 1} to ram [:$temporaryVarLabel] <= $op $b"

          // WORKLO/WORKI have already been updated by the next term so apply WORKLO/HI To the currently stashed accumulated value
          val thisClause = op match {
            case "+" =>
              List(
                s"$TMP1 = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMP1 A_PLUS_B $WORKLO _S",
                s"$TMP1 = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel+1] = $TMP1 A_PLUS_B_PLUS_C $WORKHI"
              )
            case "-" =>
              List(
                s"$TMP1 = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMP1 A_MINUS_B $WORKLO _S",
                s"$TMP1 = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel+1] = $TMP1 A_MINUS_B_MINUS_C $WORKHI"
              )
            case "*" =>
              val resultLo = ":" + scope.assignVarLabel("divisor", IsVar16, TWO_BYTE_STORAGE).fqn
              val resultHi = s"($resultLo + 1)"

              //   val res = ((timeHi(a.l, b.l) + timeLo(a.l, b.h) + timeLo(a.h, b.l)) << 8) + timeLo(a.l, b.l)
              List(
                s"$TMP1 = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMP1 *LO $WORKLO", // units:  lo * lo
                s"$TMP2 = $TMP1 *HI $WORKLO", // 256's : lo * lo
                s"$TMP1 = $TMP1 *LO $WORKHI", // 256's : lo * hi
                s"$TMP2 = $TMP2  + $TMP1",
                s"$TMP1 = [:$temporaryVarLabel+1]",
                s"$TMP1 = $TMP1 *LO $WORKLO _S", // 256's : hi * lo
                s"$TMP2 = $TMP2 + $TMP1",

                s"[:$temporaryVarLabel+1] = $TMP2"
              )
            case op@("&" | "|" | "^" | "~&" | "~") =>
              List(
                s"$TMP1 = [:$temporaryVarLabel]",
                s"[:$temporaryVarLabel] = $TMP1 $op $WORKLO _S",
                s"$TMP1 = [:$temporaryVarLabel + 1]",
                s"[:$temporaryVarLabel + 1] = $TMP1 $op $WORKHI"
              )
            case ">>" =>
              val shiftLoop = scope.fqnLabelPathUnique("shiftLoop")
              val doShift = scope.fqnLabelPathUnique("doShift")
              val endLoop = scope.fqnLabelPathUnique("endShiftLoop")

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
                s"$TMP1 = [:$temporaryVarLabel + 1]",
                s"$TMP2 = $TMP1",
                s"$TMP2 = $TMP2 & 1",
                s"$TMP2 = $TMP2 << 7",
                s"[:$temporaryVarLabel + 1] = $TMP1 A_LSR_B 1 _S",

                s"; LSR load lo byte and or in the carry",
                s"$TMP1 = [:$temporaryVarLabel]",
                s"$TMP1 = $TMP1 A_LSR_B 1",
                s"[:$temporaryVarLabel] = $TMP1  | $TMP2",

                s"; loop again",
                s"PCHITMP = < :$shiftLoop",
                s"PC = > :$shiftLoop",
                s"$endLoop:",

              )
            case "<<" =>
              val shiftLoop = scope.fqnLabelPathUnique("shiftLoop")
              val doShift = scope.fqnLabelPathUnique("doShift")
              val endLoop = scope.fqnLabelPathUnique("endShiftLoop")

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
                s"$TMP1 = [:$temporaryVarLabel]",
                s"$TMP2 = $TMP1 & %10000000", // move the shifted out bit into the RHS pos
                s"$TMP2 = $TMP2 >> 7",

                s"[:$temporaryVarLabel] = $TMP1 A_LSL_B 1 _S",

                s"; LSR load lo byte and or in the carry",
                s"$TMP1 = [:$temporaryVarLabel+1]",
                s"$TMP1 = $TMP1 A_LSL_B 1",
                s"[:$temporaryVarLabel+1] = $TMP1  | $TMP2",

                s"; loop again",
                s"PCHITMP = < :$shiftLoop",
                s"PC = > :$shiftLoop",
                s"$endLoop:",

              )
            case "/" =>
              // https://codebase64.org/doku.php?id=base:16bit_division_16-bit_result
              val divisorLo = ":" + scope.assignVarLabel("divisor", IsVar16, TWO_BYTE_STORAGE).fqn
              val divisorHi = s"($divisorLo + 1)"

              val dividendLo = ":" + scope.assignVarLabel("dividend", IsVar16, TWO_BYTE_STORAGE).fqn
              val dividendHi = s"($dividendLo + 1)"

              val remainderLo = ":" + scope.assignVarLabel("remainder", IsVar16, TWO_BYTE_STORAGE).fqn
              val remainderHi = s"($remainderLo + 1)"

              val loopVar = ":" + scope.assignVarLabel("loopVar", IsVar16, TWO_BYTE_STORAGE).fqn

              val divideLabel = scope.fqnLabelPathUnique("divide")
              val divLoopLabel = scope.fqnLabelPathUnique("divLoop")
              val skipLabel = scope.fqnLabelPathUnique("skip")
              val endLabel = scope.fqnLabelPathUnique("end")

              val tmpLabel = scope.fqnLabelPathUnique("tmp")

              // https://www.tutorialspoint.com/8085-program-to-divide-two-16-bit-numbers#:~:text=8085%20has%20no%20division%20operation.&text=To%20perform%2016%2Dbit%20division,stored%20at%20FC02%20and%20FC03.
              List(

                s"; DIVIDE OPERATION",
                s"; setup input params",
                s"    $TMP1 = [:$temporaryVarLabel]",
                s"    [$dividendLo] = $TMP1",
                s"    $TMP1 = [:$temporaryVarLabel+1]",
                s"    [$dividendHi] = $TMP1",
                s"    [$divisorLo] = $WORKLO",
                s"    [$divisorHi] = $WORKHI",

                s"  $divideLabel:",
                s"; lda #0 -- NO NEED IN SPAM1 IMPL",

                s"; sta remainder",
                s"    [$remainderLo] = 0",

                s"; sta remainder+1",
                s"    [$remainderHi] = 0",

                s"; ldx 16",
                s"    [$loopVar] = 16",

                s"  $divLoopLabel:",

                s"; asl dividend",
                s"    $TMP1         = [$dividendLo]",
                s"    [$dividendLo] = $TMP1 A_LSL_B 1",
                s"    $TMP2         = $TMP1 A_LSR_B 7", // move carried out bit into RHS of byte
                s"; rol dividend+C    rotate in the shifted out bit",
                s"    $TMP1         = [$dividendHi]",
                s"    $TMP1         = $TMP1 A_LSL_B 1 _S", // sets the flags for the rol remainder block to consume
                s"    [$dividendHi] = $TMP1 A_PLUS_B $TMP2; add the carry bit",

                s"; rol remainder",
                s"    PCHITMP = < :${tmpLabel}_2_CARRY",
                s"    PC      = > :${tmpLabel}_2_CARRY _C",
                s"    $TMP1        = [$remainderLo]",
                s"    [$remainderLo] = $TMP1 A_LSL_B 1 _S ; no carry path",
                s"    PCHITMP = < :${tmpLabel}_2_DONE",
                s"    PC      = > :${tmpLabel}_2_DONE",
                s"  ${tmpLabel}_2_CARRY:",
                s"    $TMP1        = [$remainderLo]",
                s"    $TMP1        = $TMP1 A_LSL_B 1 _S",
                s"    [$remainderLo] = $TMP1 A_PLUS_B 1 ; add the carry bit",
                s"  ${tmpLabel}_2_DONE:",

                s"; rol remainder+1",
                s"    PCHITMP = < :${tmpLabel}_3_CARRY",
                s"    PC      = > :${tmpLabel}_3_CARRY _C",
                s"    $TMP1        = [$remainderHi]",
                s"    [$remainderHi] = $TMP1 A_LSL_B 1 _S ; no carry path",
                s"    PCHITMP = < :${tmpLabel}_3_DONE",
                s"    PC      = > :${tmpLabel}_3_DONE",
                s"  ${tmpLabel}_3_CARRY:",
                s"    $TMP1        = [$remainderHi]",
                s"    $TMP1        = $TMP1 A_LSL_B 1 _S",
                s"    [$remainderHi] = $TMP1 A_PLUS_B 1 ; add the carry bit",
                s"  ${tmpLabel}_3_DONE:",

                s"; lda remainder",
                s"    $TMP1 = [$remainderLo]",

                s"; sec -  set carry - SBC uses the inverse of the carry bit so SEC is actualll clearing carry",
                s"    ; NOT_USED = $WORKLO B 0 _S ; clear carry << bit not needed as SPAM has dedicate MINUS without carry in",

                s"; sbc divisor",
                s"    $TMP1 = $TMP1 A_MINUS_B [$divisorLo] _S",

                s"; tay - WORKHI is Y REG",
                s"    $WORKHI = $TMP1",

                s"; lda remainder+1",
                s"    $TMP1 = [$remainderHi]",

                s"; sbc divisor+1",
                s"    $TMP1 = $TMP1 A_MINUS_B_MINUS_C [$divisorHi] _S",

                s"; bcc skip -  if a cleared carry bit means carry occured, then bcc means skip if carry occured",
                s"    PCHITMP = < :$skipLabel",
                s"    PC      = > :$skipLabel _C",

                s"; sta remainder+1",
                s"    [$remainderHi] = $TMP1",

                s"; sty remainder",
                s"    [$remainderLo] = $WORKHI",

                s"; inc result",
                s"    $TMP1     = [$dividendLo]",
                s"    [$dividendLo] = $TMP1 + 1",

                s"  $skipLabel:",

                s"; dex",
                s"    $TMP1    = [$loopVar]",
                s"    [$loopVar] = $TMP1 - 1 _S",

                s"; bne divloop",
                s"    PCHITMP = < :$endLabel ; equal so continue to next instruction",
                s"    PC      = > :$endLabel _Z",
                s"    PCHITMP = < :$divLoopLabel ; not equal so branch",
                s"    PC      = > :$divLoopLabel",

                s"  $endLabel:",

                s"    $TMP1 = [$dividendLo]",
                s"    [:$temporaryVarLabel] = $TMP1",
                s"    $TMP1 = [$dividendHi]",
                s"    [:$temporaryVarLabel+1] = $TMP1"
          )
            case x =>
              sys.error("NOT IMPL ALU OP '" + x + "'")
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

/*
Division is based on this : https://codebase64.org/doku.php?id=base:16bit_division_16-bit_result
6502 code below executed on this emulator https://sbc.rictor.org/kowalski.html
The converted to SPAM-1

Observation : Rotate with carry in would save a lot of lines

	*= $10

divisorL = $0     ;$59 used for hi-byte
divisorH = $1     ;$59 used for hi-byte

dividendL = $2	  ;$fc used for hi-byte
dividendH = $3	  ;$fc used for hi-byte
remainderL = $4	  ;$fe used for hi-byte
remainderH = $5	  ;$fe used for hi-byte

result = dividendL ;save memory by reusing divident to store the result

	lda #2
	sta divisorL
	lda #0
	sta divisorH
	LDA #6
	sta dividendL
	lda #0
	sta dividendH

.divide
	LDA #0	        ;preset remainder to 0
	sta remainderL
	sta remainderH
	ldx #16	        ;repeat for each bit: ...
;10
.divloop
 	ASL dividendL	;dividend lb & hb*2, msb -> Carry
	rol dividendH
	rol remainderL	;remainder lb & hb * 2 + msb from carry
	rol remainderH
	lda remainderL
;15
	sec
	sbc divisorL	;substract divisor to see if it fits in
	tay	        ;lb result -> Y, for we may need it later
	lda remainderH
	sbc divisorH
;20
	bcc .skip	;if carry=0 then divisor didn't fit in yet

	sta remainderH	;else save substraction result as new remainder,
	sty remainderL
	inc result	;and INCrement result cause divisor fit in 1 times
;24
.skip
	DEX
	bne .divloop
	LDA result
	rts
 */