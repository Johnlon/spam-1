/*
todo:
  block comment
  peek/poke of const or expr
  16 bits maths / 32?
  chip 8
  basic compiler?
  "else"


   TODO: work out how to designate use of Signed of Unsigned comparison!!
   record vars as signed or unsigned?

   unsigned int8 e a = 1   // ditch var
   unsigned int16 e a = 1  // ditch var
   var                     // short for unsigned int8

   do it with the op?     "a <:s b"
   what about signed vs unsigned const?     a < -1:s   or is -1 automatically signed and 255 is automatically unsigned?
   and if using octal or hex then are they signed or unsigned?
   maybe restrict signs to the ops??

 */
// TODO: Add some traps for stupid errors like putchar("...") which I wasted loads of time on

package scc

import org.apache.commons.text.StringEscapeUtils
import scc.Program.RootEndLabel
import scc.SpamCC.{MAIN_LABEL, TWO_BYTE_STORAGE, intTo2xBytes, split}

import scala.collection.mutable
import scala.language.postfixOps
import scala.util.parsing.input.Positional


//
//case class DefVarEqConst(targetVar: String, konst: Int) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//    val label = parent.assignVarLabel(targetVar, IsVar8).fqn
//    List(
//      s"[:$label] = $konst",
//    )
//  }
//}
//
//case class DefVarEqConstOpVar(targetVar: String, konst: Int, oper: String, srcVar: String) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//    val sLabel = parent.getVarLabel(srcVar).fqn
//    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
//    List(
//      s"$WORKLO = $konst",
//      s"$WORKLO = $WORKLO $oper [:$sLabel]",
//      s"[:$tLabel] = $WORKLO",
//    )
//  }
//}
//
//case class DefVarEqVarOpConst(targetVar: String, srcVar: String, op: String, konst: Int) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//    val sLabel = parent.getVarLabel(srcVar).fqn
//    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
//    List(
//      s"$WORKLO = [:$sLabel]",
//      s"$WORKLO = $WORKLO $op $konst",
//      s"[:$tLabel] = $WORKLO",
//    )
//  }
//}
//
//
//case class DefVarEqVar(targetVar: String, srcVar: String) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//    val sLabel = parent.getVarLabel(srcVar).fqn
//    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
//    List(
//      s"$WORKLO = [:$sLabel]",
//      s"[:$tLabel] = $WORKLO",
//    )
//  }
//}
//
//case class DefVarEqVarOpVar(targetVar: String, srcVar1: String, op: String, srcVar2: String) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//    val s1Label = parent.getVarLabel(srcVar1).fqn
//    val s2Label = parent.getVarLabel(srcVar2).fqn
//    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
//    List(
//      s"$WORKLO = [:$s1Label]",
//      s"$V2 = [:$s2Label]",
//      s"[:$tLabel] = $WORKLO $op $V2",
//    )
//  }
//}
//
//case class DefUint8EqExpr(targetVar: String, block: Block) extends Block {
//  override def gen(depth: Int, parent: Scope): List[String] = {
//
//    val stmts: List[String] = block.expr(depth + 1, parent)
//
//    val labelTarget = parent.assignVarLabel(targetVar, IsVar8).fqn
//
//    val assign = List(
//      s"[:$labelTarget] = $WORKLO",
//    )
//    stmts ++ assign
//  }
//
//  override def dump(depth: Int): List[(Int, String)] =
//    List((depth, this.getClass.getSimpleName + "("), (depth + 1, targetVar)) ++
//      block.dump(depth + 1) ++
//      List((depth, ")"))
//}

case class DefUint16EqExpr(targetVar: String, block: Block) extends Block {
  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val stmts: Seq[String] = block.expr(depth + 1, parent)

    val labelTarget = parent.assignVarLabel(targetVar, IsVar16, data = TWO_BYTE_STORAGE).fqn

    val assign = List(
      s"[:$labelTarget] = $WORKLO",
      s"[:$labelTarget+1] = $WORKHI",
    )
    stmts ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List((depth, this.getClass.getSimpleName + "("), (depth + 1, targetVar)) ++
      block.dump(depth + 1) ++
      List((depth, ")"))
}

/** evaluates to 1 if condition is true otherwise 0 */
case class DefUint16EqCondition(targetVar: String, flagToCheck: String, block: ConditionComplex) extends Block {
  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val stmts: Seq[String] = block.expr(depth + 1, parent)

    val labelTarget = parent.assignVarLabel(targetVar, IsVar16, data = TWO_BYTE_STORAGE).fqn

    val assign = List(
      s"[:$labelTarget] = 0",
      s"[:$labelTarget+1] = 0",
      s"[:$labelTarget] = 1 $flagToCheck",
    )
    stmts ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List((depth, this.getClass.getSimpleName + "("), (depth + 1, targetVar)) ++
      block.dump(depth + 1) ++
      List((depth, ")"))
}

// optimisation
case class LetVarEqConst(targetVar: String, konst: Int) extends Block {

  override def gen(depth: Int, parent: Scope): List[String] = {
    val variable = parent.getVarLabel(targetVar)
    val fqn = variable.fqn

    variable.typ match {
      //      case IsVar8 | IsData =>
      case IsVar16 | IsData =>
        List(
          s"; let var $targetVar = $konst",
          s"[:$fqn] = > $konst",
          s"[:$fqn + 1] = < $konst",
        )
      case IsRef =>
        List(
          s"; let ref $targetVar = $konst",
          s"[:$fqn] = <$konst",
          s"[:$fqn + 1] = >$konst "
        )
    }
  }
}

// general purpose
case class LetVarEqExpr(targetVar: String, block: Block) extends Block {
  override def gen(depth: Int, parent: Scope): Seq[String] = {

    val stmts: Seq[String] = block.expr(depth + 1, parent)
    val labelTarget = parent.getVarLabel(targetVar, IsVar16).fqn

    val assign = List(
      s"[:$labelTarget] = $WORKLO",
      s"[:$labelTarget + 1] = $WORKHI",
    )
    stmts ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + "("),
      (depth + 1, targetVar)
    ) ++ block.dump(depth + 1) ++
      List((depth, ")"))
}

// optimisation
case class LetVarEqVar(targetVarName: String, srcVarName: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {

    val targ = parent.getVarLabel(targetVarName)

    val srcFqn = parent.getVarLabel(srcVarName).fqn
    val targFqn = parent.getVarLabel(targetVarName).fqn

    targ.typ match {
      //      case IsVar8 | IsData =>
      case IsVar16 | IsData =>
        List(
          s"$WORKLO = [:$srcFqn]",
          s"[:$targFqn] = $WORKLO",
          s"$WORKLO = [:$srcFqn + 1]",
          s"[:$targFqn + 1] = $WORKLO",
        )
      case IsRef =>
        List(
          s"$WORKLO = <:$srcFqn ",
          s"[:$targFqn] = $WORKLO",
          s"$WORKLO = >:$srcFqn ",
          s"[:$targFqn + 1] = $WORKLO"
        )
    }
  }
}


// general purpose
case class LetStringIndexEqExpr(targetVar: String, indexBlock: BlkCompoundAluExpr, valueBlock: BlkCompoundAluExpr) extends Block {
  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val indexStatements: Seq[String] = indexBlock.expr(depth + 1, parent)
    val valueStatements: Seq[String] = valueBlock.expr(depth + 1, parent)

    // string indexing creates needs temp vars and a string indexing can occur multiple times at the same scope eg "a[1] = a[1] + 1" so
    // we need to make sure these temp vars are local - ie unique.
    val indexTempVarLo = parent.assignVarLabel("INDEX_TMP_LO" + Scope.nextInt, IsVar8But).fqn
    val indexTempVarHi = parent.assignVarLabel("INDEX_TMP_HI" + Scope.nextInt, IsVar8But).fqn

    val targLabel = parent.getVarLabel(targetVar).fqn

    val idxSaveStatements = Seq(
      s"[:$indexTempVarLo] = $WORKLO A_PLUS_B        (> :$targLabel) _S",
      s"[:$indexTempVarHi] = $WORKHI A_PLUS_B_PLUS_C (< :$targLabel)",
    )

    val marLocnStatements = Seq(
      s"MARLO = [:$indexTempVarLo]",
      s"MARHI = [:$indexTempVarHi]",
    )

    val assign = List(
      s"RAM = $WORKLO",
      // ignores upper byte of assignment as we only deal in byte arrays not word arrays at the moment
    )
    indexStatements ++ idxSaveStatements ++
      valueStatements ++
      marLocnStatements ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + "("),
      (depth + 1, targetVar)
    ) ++ valueBlock.dump(depth + 1) ++
      List((depth, ")"))
}


case class DefVarEqString(target: String, str: String) extends Block {
  private val escaped = StringEscapeUtils.escapeJava(str)

  override def toString = s"""DefVarEqString( $target, "$escaped" )"""

  override def gen(depth: Int, parent: Scope): List[String] = {
    // nothing to do but record the data with current scope - data will be laid out later
    parent.assignVarLabel(target, IsData, str.getBytes("ISO8859-1").toList).fqn
    List(
      s"""; var $target = "$escaped""""
    )
  }
}

case class DefVarEqData(target: String, data: Seq[Byte]) extends Block {
  private val escaped = data.map(b => f"$b%02X").mkString(" ")

  override def toString = s"DefVarEqData( $target, [$escaped] )"

  override def gen(depth: Int, parent: Scope): List[String] = {
    // nothing to do but record the data with current scope - data will be laid out later
    parent.assignVarLabel(target, IsData, data).fqn
    List(
      s"""; var $target = [$escaped]"""
    )
  }
}

case class LocatedData(location: Int, data: Seq[Byte])

case class DefVarEqLocatedData(target: String, locatedData: Seq[LocatedData]) extends Block {

  private val data = {
    val sortedByAddr = locatedData.sortBy(x => x.location)
    val extent: Int = locatedData.map(c => c.location + c.data.size).sorted.lastOption.getOrElse(0)

    val data = (0 until extent).map(_ => 0.toByte).toBuffer

    sortedByAddr.foreach {
      case LocatedData(addr, d) =>
        var pos = addr
        d.foreach {
          f =>
            data(pos) = f
            pos += 1
        }
    }
    data.toSeq
  }

  private val escaped = data.map(b => f"$b%02X").mkString(" ")

  override def toString = s"statementVarDataLocated( $target, [$locatedData] )"

  //  override def toString = s"statementVarDataLocated( $target, [$escaped] )"

  override def gen(depth: Int, parent: Scope): List[String] = {

    // nothing to do but record the data with current scope - data will be laid out later
    parent.assignVarLabel(target, IsData, data).fqn
    List(
      s"""; var $target = [$escaped]"""
    )
  }
}

case class DefRefEqVar(refName: String, target: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val targetLabelAddress = parent.getVarLabel(target).address
    val storage = intTo2xBytes(targetLabelAddress)
    val refLabel = parent.assignVarLabel(refName, IsRef, storage).fqn

    val str = storage.mkString("[Hi:", ",LO:", "]")
    List(
      s"""; ref $refName = "$target"     ($refLabel = $str)"""
    )
  }
}

case class Halt(haltCode: Int)
  extends Block(nestedName = s"halt_${haltCode}") {

  override def gen(depth: Int, parent: Scope): List[String] = {
    List(
      s"MARHI = " + ((haltCode >> 8) & 0xff),
      s"MARLO = " + (haltCode & 0xff),
      s"HALT = 1"
    )
  }
}

case class HaltVar(srcVarName: String)

  extends Block(nestedName = s"haltVar_${srcVarName}_") {

  override def gen(depth: Int, parent: Scope): List[String] = {

    val srcFqn = parent.getVarLabel(srcVarName).fqn

    List(
      s"MARHI = [:$srcFqn + 1]",
      s"MARLO = [:$srcFqn]",
      s"HALT = 2"
    )
  }
}

case class Puts(varName: String)
  extends Block(nestedName = s"puts_${varName}_") {

  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelStartLoop = parent.fqnLabelPathUnique("startloop")
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelTransmit = parent.fqnLabelPathUnique("transmit")
    val labelEndLoop = parent.fqnLabelPathUnique("endloop")

    val variable = parent.getVarLabel(varName)

    val marSetup = variable.typ match {
      case IsVar16 | IsData =>
        //      case IsVar8 | IsData =>
        val varLabel = variable.fqn
        List(
          s"MARLO = >:$varLabel",
          s"MARHI = <:$varLabel"
        )
      case IsRef =>
        val varLabel = variable.fqn
        List(
          s"MARHI = [:$varLabel]",
          s"MARLO = [:$varLabel+1]"
        )
    }

    marSetup ++
      split(
        s"""
           |$labelStartLoop:
           |; break if NULL
           |NOOP = RAM _S
           |PCHITMP = <:$labelEndLoop
           |PC = >:$labelEndLoop _Z
           |; wait for tx ready
           |$labelWait:
           |PCHITMP = <:$labelTransmit
           |PC = >:$labelTransmit _DO
           |PCHITMP = <:$labelWait
           |PC = >:$labelWait
           |; do transmit
           |$labelTransmit:
           |UART = RAM
           |; goto next char
           |MARLO = MARLO + 1 _S
           |MARHI = MARHI + 1 _C
           |PCHITMP = <:$labelStartLoop
           |PC = >:$labelStartLoop
           |$labelEndLoop:
           |; done break from loop
         """)
  }
}

case class PutChar(block: Block) extends Block(nestedName = "putcharGeneral") {
  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelTransmit = parent.fqnLabelPathUnique("transmit")

    // leaves result in $V1
    val stmts: Seq[String] = block.expr(depth + 1, parent)

    stmts ++ split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelTransmit
         |PC = >:$labelTransmit _DO
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait
         |$labelTransmit:
         |UART = $WORKLO
         |""")
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + "(")
    ) ++ block.dump(depth + 1) ++
      List((depth, ")"))
}

//
//  def stmtPutsGeneral: Parser[Block] = "puts" ~ "(" ~> blkExpr <~ ")" ^^ {
//    bex =>
//      new Block("stmtPutsGeneral", s"$bex", nestedName = "putsGeneral") {
//        override def gen(depth: Int, parent: Name): List[String] = {
//          val labelWait = parent.fqnLabelPathUnique("wait")
//          val labelTransmit = parent.fqnLabelPathUnique("transmit")
//
//          // leaves result in $V1
//          val stmts: List[String] = bex.expr(depth + 1, parent)
//
//          stmts ++ split(
//            s"""
//               |$labelWait:
//               |PCHITMP = <:$labelTransmit
//               |PC = >:$labelTransmit _DO
//               |PCHITMP = <:$labelWait
//               |PC = >:$labelWait
//               |$labelTransmit:
//               |UART = $V1
//               |""")
//        }
//      }
//  }

case class PutcharVar(varName: String) extends Block(nestedName = s"putcharVar_${varName}_") {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelTransmit = parent.fqnLabelPathUnique("transmit")
    val varLocn = parent.getVarLabel(varName).fqn
    split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelTransmit
         |PC = >:$labelTransmit _DO
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait
         |$labelTransmit:
         |UART = [:$varLocn]
         |""")
  }
}

case class Getchar() extends Block(nestedName = s"getchar_") {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelReceive = parent.fqnLabelPathUnique("receive")
    split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelReceive
         |PC = >:$labelReceive _DI
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait
         |$labelReceive:
         |$WORKLO = UART
         |$WORKHI = 0
         |""")
  }
}

case class PutcharConst(konst: Int) extends Block(nestedName = s"putcharConst_${konst}_") {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait ! _DO
         |UART = $konst
         |""")
  }
}

case class WhileTrue(content: List[Block])
  extends Block(nestedName = s"whileTrue${Scope.nextInt}") {

  override def gen(depth: Int, parent: Scope): List[String] = {

    val labelBody = parent.toFqLabelPath("BODY")
    val labelAfter = parent.toFqLabelPath("AFTER")

    val prefix = split(s"""$labelBody:""")

    val stmts = content.flatMap {
      b => {
        val breakName = parent.copy(endLabel = Some(labelAfter))
        b.expr(depth + 1, breakName)
      }
    }

    val suffix = split(
      s"""
         |PCHITMP = <:$labelBody
         |PC = >:$labelBody
         |$labelAfter:
         |""")


    prefix ++ stmts ++ suffix
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName),
    ) ++
      content.flatMap(_.dump(depth + 1))
}

case class WhileCond(flagToCheck: String, conditionBlock: Block, content: List[Block])
  extends Block(nestedName = s"whileCond${Scope.nextInt}") {

  override def gen(depth: Int, parent: Scope): List[String] = {

    val labelCheck = parent.toFqLabelPath("CHECK")
    val labelBody = parent.toFqLabelPath("BODY")
    val labelAfter = parent.toFqLabelPath("AFTER")

    val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

    val conditionalJump = {
      List(s"$labelCheck:") ++
        condStatements ++
        split(
          s"""
             |PCHITMP = <:$labelBody
             |PC = >:$labelBody $flagToCheck
             |PCHITMP = <:$labelAfter
             |PC = >:$labelAfter
             |$labelBody:
               """)
    }

    val stmts = content.flatMap {
      b => {
        val breakName = parent.copy(endLabel = Some(labelAfter))

        b.expr(depth + 1, breakName)
      }
    }

    val suffix = split(
      s"""
         |PCHITMP = <:$labelCheck
         |PC = >:$labelCheck
         |$labelAfter:
         |""")

    conditionalJump ++ stmts ++ suffix
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + "( " + flagToCheck)
    ) ++
      conditionBlock.dump(depth + 1) ++
      List(
        (depth, ")")
      ) ++
      content.flatMap(_.dump(depth + 1))

}

case class IfCond(flagToCheck: String,
                  conditionBlock: Block,
                  content: List[Block],
                  elseContent: List[Block]
                 )
  extends Block(nestedName = s"ifCond${Scope.nextInt}") {

  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelCheck = parent.toFqLabelPath("CHECK")
    val labelBody = parent.toFqLabelPath("BODY")
    val labelElse = parent.toFqLabelPath("ELSE")
    val labelBot = parent.toFqLabelPath("AFTER")

    val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

    val conditionalJump = {
      List(s"$labelCheck:") ++
        condStatements ++
        split(
          s"""
             |PCHITMP = <:$labelBody
             |PC = >:$labelBody $flagToCheck
             |PCHITMP = <:$labelElse
             |PC = >:$labelElse
              """)
    }

    // need separate scopes for the main and else blocks to lexically scope vars
    val scopeMatch = parent.pushScope("MATCH")
    val scopeNotMatch = parent.pushScope("NOTMATCH")
    val stmts = content.flatMap {
      b => {
        b.expr(depth + 1, scopeMatch) // SHOULD THIS BE this RATHER THAN PARENT - test case two if's in the same function and both declare same var name
      }
    }

    val elseStmts = elseContent.flatMap {
      b => {
        b.expr(depth + 1, scopeNotMatch) //.pushName(newName = labelBase))
      }
    }

    conditionalJump ++
      Seq(s"$labelBody:") ++
      stmts ++
      Seq(
        s"PCHITMP = <:$labelBot",
        s"PC = >:$labelBot",
        s"$labelElse:"
      ) ++
      elseStmts ++
      Seq(
        s"$labelBot:"
      )
  }

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + "( " + flagToCheck)
    ) ++
      conditionBlock.dump(depth + 1) ++
      List(
        (depth, ")")
      ) ++
      List(
        (depth, "Body ( ")
      ) ++
      content.flatMap(_.dump(depth + 1)) ++
      List(
        (depth, ")"),
        (depth, "Else (")
      ) ++
      elseContent.flatMap(_.dump(depth + 1)) ++
      List(
        (depth, ")"),
      )

}

case class Break() extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {

    val breakToLabel = parent.getEndLabel.getOrElse {
      throw new RuntimeException("spamcc error: cannot use 'break' without surrounding 'while' block")
    }

    split(
      s"""
         |PCHITMP = <:$breakToLabel
         |PC = >:$breakToLabel
           """)
  }
}

case class DefFunction(functionName: String, functionArgs: List[FunctionArg], content: List[Block])
  extends Block(nestedName = s"function_$functionName") {

  def gen(depth: Int, scope: Scope): List[String] = {

    // side affect of defining the function args as vars
    val FunctionDef(startLabel, returnHi, returnLo, argsLabels) = defScopedArgLabels(scope)

    val prefix = if (functionName == "main") {
      List(
        s"$MAIN_LABEL:",
        s"$startLabel:"
      )
    } else
      List(s"$startLabel:")

    // eval the code that uses these vars
    val stmts = content.flatMap {
      b => {
        b.expr(depth + 1, scope)
      }
    }

    val suffix = if (functionName == "main") {
      List(
        "PCHITMP = <:root_end",
        "PC = >:root_end"
      )
    } else {
      List(
        s"PCHITMP = [:$returnHi]",
        s"PC = [:$returnLo]"
      )
    }

    prefix ++ stmts ++ suffix
  }

  override def dump(depth: Int): List[(Int, String)] = {
    List(
      (depth, this.getClass.getSimpleName + s" $functionName ("),
    ) ++
      functionArgs.flatMap {
        a => a.dump(depth + 1)
      } ++
      content.flatMap(_.dump(depth + 1))
  }

  // DEF
  def defScopedArgLabels(scope: Scope): FunctionDef = {
    val returnHiLabel = scope.assignVarLabel("RETURN_HI", IsVar8But).fqn
    val returnLoLabel = scope.assignVarLabel("RETURN_LO", IsVar8But).fqn
    // These locations where we write the input parameters into the function.
    // Also, read from these locations to fetch "out" values.
    val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.
      map {
        argName =>
          val fnArgLabel = scope.assignVarLabel(argName.argName, IsVar16, TWO_BYTE_STORAGE).fqn
          val arg = FunctionArg(argName.argName, argName.isOutput)
          FunctionArgNameAndLabel(fnArgLabel, arg)
      }
    val fnStart = scope.toFqLabelPath("START")

    FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
  }

  // GET - TODO remove dupe code with defXXX above
  def getScopedArgLabels(scope: Scope): FunctionDef = {
    val returnHiLabel = scope.getVarLabel("RETURN_HI", IsVar8But).fqn
    val returnLoLabel = scope.getVarLabel("RETURN_LO", IsVar8But).fqn
    // These locations where we write the input parameters into the function.
    // Also, read from these locations to fetch "out" values.
    val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.map {
      argName =>
        val fnArgLabel = scope.getVarLabel(argName.argName, IsVar16).fqn
        val arg = FunctionArg(argName.argName, argName.isOutput)
        FunctionArgNameAndLabel(fnArgLabel, arg)
    }
    val fnStart = scope.toFqLabelPath("START")

    FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
  }

}

case class CallFunction(fnName: String, argExpr: List[BlkCompoundAluExpr]) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val fns = parent.lookupFunction(fnName)
    val (functionScope: Scope, fn: DefFunction) = fns.getOrElse(sys.error(s"no such function '$fnName''"))

    val FunctionDef(startLabel, returnHiLabel, returnLoLabel, argsLabelsAndDir) = fn.getScopedArgLabels(functionScope)

    val argNamesAndDir: List[FunctionArg] = fn.functionArgs

    if (argExpr.length != argNamesAndDir.size) {
      val argsNames = argNamesAndDir.map(_.argName)
      val argNameCommas = argsNames.mkString(",")
      val argExprCommas = argExpr.mkString(",")
      sys.error(s"""call to function "$fnName" has wrong number of arguments for ; expected $fnName($argNameCommas) but got $fnName($argExprCommas)""")
    }

    val argDefinitionVsExpression = argsLabelsAndDir.zip(argExpr)

    // instructions needed to evaluate parameter clauses and set the values of the function input variables
    val setupCallParams: List[String] = argDefinitionVsExpression.flatMap {
      case (FunctionArgNameAndLabel(argLabel, _), argBlk) =>
        // evaluate the arg expression
        val argValueStatements: Seq[String] = argBlk.expr(depth + 1, parent)

        // put the result into the input var locations
        argValueStatements ++ Seq(
          s"[:$argLabel] = $WORKLO",
          s"[:$argLabel+1] = $WORKHI"
        )
    }

    // instructions needed to capture the output args of the function into local vars within the caller's scope
    val setupOutParams: List[String] = argDefinitionVsExpression.flatMap {
      case (FunctionArgNameAndLabel(argLabel, functionArg), argBlk) =>
        if (functionArg.isOutput) {
          val argName = functionArg.argName
          argBlk.standaloneVariableName match {
            case Some(name) =>
              val localVarLabel = parent.lookupVarLabel(name).getOrElse {
                sys.error(s"""output parameter variable '$name' in call to function "$fnName" is not defined""")
              }.fqn

              // recover output value from the function and assign back to the local variable
              List(
                s"$WORKLO = [:$argLabel]",
                s"[:$localVarLabel] = $WORKLO",
                s"$WORKHI = [:$argLabel+1]",
                s"[:$localVarLabel+1] = $WORKHI"
              )
            case _ =>
              sys.error(
                s"""value of output parameter '$argName' in call to function "$fnName" is not a pure variable reference, but is '$argBlk'""")
          }
        }
        else // not an output param
          Nil
    }

    val labelReturn = parent.fqnLabelPathUnique("RETURN_LOCATION")

    val setupReturnJumpParams: List[String] = List(
      s"; set return address variables of function",
      s"[:$returnHiLabel] = < :$labelReturn",
      s"[:$returnLoLabel] = > :$labelReturn"
    )

    val setupJumpToFn = List(
      s"; do jump to function '$fnName''",
      s"PCHITMP = < :$startLabel",
      s"PC = > :$startLabel",
      s"; will return to this next location",
      s"$labelReturn:"
    )

    setupCallParams ++ setupReturnJumpParams ++ setupJumpToFn ++ setupOutParams
  }


  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + s" $fnName ( ")
    ) ++
      argExpr.flatMap(_.dump(depth + 1)) ++
      List((depth, " ) "))
}

case class LineComment(comment: String) extends Block(logEntryExit = false) {
  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val withoutLeading = comment.replace("//", "")
    List(s"; $withoutLeading")
  }
}

case class BlockComment(comment: String) extends Block(logEntryExit = false) {
  override def toString: String =
    "BlockComment(" + comment.replaceAll("[\n\r]", "\\\\n") + ")"

  override def gen(depth: Int, parent: Scope): Seq[String] = {
    val patched = comment.
      replaceAll("^/\\*", "").
      replaceAll("\\*/$", "").
      split("[\n\r]+").
      map(";" + _).toSeq
    patched
  }
}

case class Program(fns: List[Block]) {
  def dump(depth: Int): String = {

    fns.flatMap {
      _.dump(depth)
    }.map {
      ds => ("  " * ds._1) + ds._2
    }.mkString("\n")
  }


  def compile(): List[String] = {
    val variables: mutable.ListBuffer[Variable] = mutable.ListBuffer.empty[Variable]

    val RootName: Scope = Scope(null, "root", variables = variables)

    val asm: List[String] = fns.flatMap {
      b => {
        b.expr(0, RootName)
      }
    }

    val varlist = variables.flatMap {
      case Variable(name, fqn, address, bytes, typ) =>
        val byteString = bytes.map(_.toInt).mkString(", ")

        Seq(
          s"; $typ : $name : $fqn",
          s"$fqn: EQU   $address",
          s"$fqn: BYTES [$byteString]"
        )
    }.toList

    val jumpToMain = split(
      s"""
         |PCHITMP = < :$MAIN_LABEL
         |PC = > :$MAIN_LABEL
         |""")

    varlist ++ jumpToMain ++ asm ++
      Seq(
        /*
         // jump over whatever is next to end or program
          s"PCHITMP = < :$RootEndLabel",
          s"PC = > :$RootEndLabel",
          // other std functions can sit here
          " ; nothing extra at moment",

          // end of program has no code at this location and sim can detect this
         */
        s"$RootEndLabel:"
      ) :+ "END"
  }
}

object Program {
  val RootEndLabel = "root_end"
  val DivByZeroLabel = s"(:$RootEndLabel+2)"
  val MathErrorLabel = s"(:$RootEndLabel+4)"
}

abstract class Block(nestedName: String = "", logEntryExit: Boolean = true) extends Positional {

  if (!nestedName.matches("^[a-zA-Z0-9_]*$")) {
    sys.error(s"invalid block nested name ;'$nestedName'")
  }

  // for logging
  def dump(depth: Int): List[(Int, String)] = List((depth, this.toString))

  final def expr(depth: Int, parentScope: Scope): Seq[String] = {

    val thisScope = localize(parentScope)

    val enter = s"${commentPrefix(depth)}ENTER ${thisScope.blockName} @ $this $pos"
    val exit = s"${commentPrefix(depth)}EXIT  ${thisScope.blockName} @ $this"
    val indentPrefix = opPrefix(depth)

    try {
      // register functions ...
      this match {
        case bf: DefFunction =>
          val fns = parentScope.lookupFunction(bf.functionName)
          fns.foreach(found => sys.error(s"function already defined '${bf.functionName}' at scope ${found._1.blockName} as ${found._2}"))

          parentScope.addFunction(thisScope, bf)
        case _ =>
      }

      // generate code
      val generatedCode = gen(depth, thisScope)
      val prettyCode: Seq[String] = generatedCode.map(l => {
        indentPrefix + l
      })

      // optionally add more comments
      if (logEntryExit)
        enter +: prettyCode :+ exit
      else
        prettyCode

    } catch {
      case ex: Exception =>
        // throw with updated message but existing stack trace
        val message = ex.getMessage
        val cause = if (ex.getCause == null) ex else ex.getCause
        val exception = new RuntimeException(s"$message @ \n '${parentScope.blockName}'", cause)
        exception.setStackTrace(ex.getStackTrace)
        throw exception
    }
  }

  def localize(parent: Scope): Scope = {
    parent.pushScope(nestedName)
  }

  private def commentPrefix(depth: Int) = s"; ($depth)  "

  private def opPrefix(indent: Int) = {
    // indent ops to the same amount as the first word of the surrounding comment text
    val prefixOfCommentAtThisDepth = commentPrefix(indent)
    // convert comment prefix to whitespace
    prefixOfCommentAtThisDepth.replaceAll(".", " ")
  }

  protected[this] def gen(depth: Int, parent: Scope): Seq[String]
}

