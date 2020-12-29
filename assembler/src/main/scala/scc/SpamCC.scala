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

package scc

import java.io.{File, PrintWriter}

import asm.{AluOp, EnumParserOps}
import scc.SpamCC.{MAIN_LABEL, TWO_BYTE_STORAGE, intTo2xBytes, split}

import scala.collection.mutable
import scala.io.Source
import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.input.Positional


object SpamCC {
  val ZERO = 0.toByte
  val MAIN_LABEL = "ROOT________main_start"
  val ONE_BYTE_STORAGE = List(0.toByte)
  val TWO_BYTE_STORAGE = List(0.toByte, 0.toByte)

  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      compile(args)
    }
    else {
      System.err.println("SpamCC ...")
      System.err.println("    usage:  file-name.scc ")
      sys.exit(1)
    }
  }

  private def compile(args: Array[String]): Unit = {
    val fileName = args(0)

    val code = readAllLines(fileName)

    val compiler = new SpamCC()

    val asm: List[String] = compiler.compile(code)

    val pw = new PrintWriter(new File(s"$fileName.asm"))
    asm.foreach { line =>
      line.foreach { rom =>
        pw.write(rom)
      }
      pw.write("\n")
    }
    pw.close()
  }

  private def readAllLines(fileName: String): String = {
    val src = Source.fromFile(fileName)
    try {
      src.getLines().mkString("\n")
    }
    catch {
      case ex: Throwable =>
        src.close()
        throw ex
    }
  }

  // convert in to storage format
  def intTo2xBytes(addr: Int): List[Byte] = {
    List((addr >> 8) toByte, addr.toByte)
  }

  def split(s: String): List[String] = {
    s.split("\\|").map(_.stripTrailing().stripLeading()).filterNot(_.isEmpty).toList
  }
}

class SpamCC extends ExprParser with ConstExprParser with ConditionParser with EnumParserOps with JavaTokenParsers {

  val SPACE = " "

  def compile(code: String): List[String] = {

    parse(program, code) match {
      case Success(matched, _) =>
        var indent = 0
        println("SpamCC parsed : ")
        println(matched.dump(1))

        matched.compile()
      case msg: Failure =>
        sys.error(s"FAILURE: $msg ")
      case msg: Error =>
        sys.error(s"ERROR: $msg")
    }
  }


  def statement: Parser[Block] = positioned {
    comment |
      statementUInt8Eq |
      statementUInt16Eq |
      //      statementVarEqVarOpVar |
      //      statementVarEqVar |
      //      statementVarEqVarOpConst |
      //      statementVarEqConstOpVar |
      //      statementVarEqConst |
      statementVarEqOp |
      statementRef |
      statementLetVarEqConst |
      statementLetVarEqVar |
      statementLetVarEqExpr |
      statementPutcharVarOptimisation | statementPutcharConstOptimisation | stmtPutcharGeneral |
      stmtPutsName |
      statementGetchar |
      whileCond | whileTrue | ifCond | breakOut | functionCall |
      statementVarString ^^ {
        s =>
          s
      }
  }

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  // optimisation of "var VARIABLE=CONST"
  def statementVarEqConst: Parser[DefVarEqConst] = positioned {
    "var" ~> name ~ "=" ~ constExpression <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ konst =>
        DefVarEqConst(targetVar, konst)
    }
  }

  // optimisation of "var VARIABLE=CONST op VARIABLE"
  def statementVarEqConstOpVar: Parser[DefVarEqConstOpVar] = positioned {
    "var" ~> name ~ "=" ~ constExpression ~ aluOp ~ name <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ konst ~ oper ~ srcVar =>
        DefVarEqConstOpVar(targetVar, konst, oper, srcVar)
    }
  }

  // optimisation of "var VARIABLE=VARIABLE op CONST"
  def statementVarEqVarOpConst: Parser[DefVarEqVarOpConst] = positioned {
    "var" ~> name ~ "=" ~ name ~ aluOp ~ constExpression <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ srcVar ~ op ~ konst =>
        DefVarEqVarOpConst(targetVar, srcVar, op, konst)
    }
  }

  // optimisation of "var VARIABLE=VARIABLE"
  def statementVarEqVar: Parser[DefVarEqVar] = positioned {
    "var" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ srcVar =>
        DefVarEqVar(targetVar, srcVar)
    }
  }

  // optimisation of "var VARIABLE=VARIABLE op VARIABLE"
  def statementVarEqVarOpVar: Parser[DefVarEqVarOpVar] = positioned {
    "var" ~> name ~ "=" ~ name ~ aluOp ~ name <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ srcVar1 ~ op ~ srcVar2 =>
        DefVarEqVarOpVar(targetVar, srcVar1, op, srcVar2)
    }
  }

  // general purpose
  def statementVarEqOp: Parser[DefUint8EqExpr] = positioned {
    "var" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ block => DefUint8EqExpr(targetVar, block)
    }
  }

  // general purpose
  def statementUInt8Eq: Parser[DefUint8EqExpr] = positioned {
    "uint8" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ block => DefUint8EqExpr(targetVar, block)
    }
  }

  // general purpose
  def statementUInt16Eq: Parser[DefUint16EqExpr] = positioned {
    "uint16" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ block => DefUint16EqExpr(targetVar, block)
    }
  }

  // optimisation of "let VARIABLE=CONST"
  def statementLetVarEqConst: Parser[LetVarEqConst] = positioned {
    name ~ "=" ~ constExpression <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ konst =>
        LetVarEqConst(targetVar, konst)
    }
  }

  // general purpose
  def statementLetVarEqExpr: Parser[LetVarEqExpr] = positioned {
    name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case target ~ _ ~ expr =>
        LetVarEqExpr(target, expr)
    }
  }

  def statementLetVarEqVar: Parser[LetVarEqVar] = positioned {
    name ~ "=" ~ name <~ SEMICOLON ^^ {
      case targetVarName ~ _ ~ srcVarName =>
        LetVarEqVar(targetVarName, srcVarName)
    }
  }

  /*
  STRING:     STR     "ABC\n\0\u0000"
  BYTE_ARR:   BYTES   [ 'A', 65 ,$41, %10101010 ] ; parse as hex bytes and then treat as per STR
  */
  def statementVarString: Parser[DefVarEqString] = positioned {
    "var" ~> name ~ "=" ~ quotedString <~ SEMICOLON ^^ {
      case target ~ _ ~ str => DefVarEqString(target, str)
    }
  }

  def statementRef: Parser[DefRefEqVar] = positioned {
    "ref" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
      case refName ~ _ ~ target => DefRefEqVar(refName, target)
    }
  }

  def stmtPutsName: Parser[Block] = positioned {
    "puts" ~ "(" ~> name <~ ")" ^^ {
      varName => Puts(varName)
    }
  }

  def statementGetchar: Parser[Block] = positioned {
    "getchar" ~ "(" ~ ")" ^^ {
      _ => Getchar()
    }
  }

  def statementPutcharConstOptimisation: Parser[Block] = positioned {
    "putchar" ~ "(" ~> constExpression <~ ")" ^^ {
      konst => PutcharConst(konst)
    }
  }


  def statementPutcharVarOptimisation: Parser[Block] = positioned {
    "putchar" ~ "(" ~> name <~ ")" ^^ {
      varName: String => PutcharVar(varName)
    }
  }


  def stmtPutcharGeneral: Parser[Block] = positioned {
    "putchar" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => PutChar(block)
    }
  }

  def argumentDefTerm: Parser[FunctionArg] = name ~ opt("out") ^^ {
    case name ~ isOutput =>
      FunctionArg(name, isOutput.isDefined)
  }

  def functionDef: Parser[Block] = positioned {
    "fun " ~> name ~ "(" ~ repsep(argumentDefTerm, ",") ~ (")" ~ "{") ~ statements <~ "}" ^^ {
      case fnName ~ _ ~ args ~ _ ~ content => DefFunction(fnName, args, content)
    }
  }

  def functionCall: Parser[Block] = positioned {
    name ~ "(" ~ repsep(blkCompoundAluExpr, ",") ~ ")" ^^ {
      case fnName ~ _ ~ argExpr ~ _ =>
        CallFunction(fnName, argExpr)
    }
  }

  def whileTrue: Parser[Block] = positioned {
    ("while" ~ "(" ~ "true" ~ ")" ~ "{") ~> statements <~ "}" ^^ {
      content => WhileTrue(content)
    }
  }

  def whileCond: Parser[Block] = positioned {
    "while" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
      case cond ~ _ ~ _ ~ content =>
        WhileCond(cond._1, cond._2, content)
    }
  }

  def breakOut: Parser[Block] = positioned {
    "break" ^^ {
      _ => Break()
    }
  }

  def ifCond: Parser[Block] = positioned {
    "if" ~ "(" ~> condition ~ ")" ~ "{" ~ statements <~ "}" ^^ {
      case cond ~ _ ~ _ ~ content => IfCond(cond._1, cond._2, content)
    }
  }


  def comment: Parser[Block] = positioned {
    "//.*".r ^^ {
      c => Comment(c)
    }
  }

  def program: Parser[Program] = "program" ~ "{" ~> ((comment | functionDef) +) <~ "}" ^^ {
    fns => Program(fns)
  }

  def aluOp: Parser[String] = {
    val shortAluOps = {
      // reverse sorted to put longer operators ahead of shorter ones otherwise shorter ones gobble
      val reverseSorted = AluOp.values.filter(_.isAbbreviated).sortBy(x => x.abbrev).reverse.toList
      reverseSorted map { m =>
        literal(m.abbrev) ^^^ m
      } reduceLeft {
        _ | _
      }
    }
    val longAluOps = enumToParser(AluOp.values)
    (longAluOps | shortAluOps).map(_.preferredName)
  }
}

case class DefVarEqConst(targetVar: String, konst: Int) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val label = parent.assignVarLabel(targetVar, IsVar8).fqn
    List(
      s"[:$label] = $konst",
    )
  }
}

case class DefVarEqConstOpVar(targetVar: String, konst: Int, oper: String, srcVar: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val sLabel = parent.getVarLabel(srcVar).fqn
    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
    List(
      s"REGA = $konst",
      s"REGA = REGA $oper [:$sLabel]",
      s"[:$tLabel] = REGA",
    )
  }
}

case class DefVarEqVarOpConst(targetVar: String, srcVar: String, op: String, konst: Int) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val sLabel = parent.getVarLabel(srcVar).fqn
    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
    List(
      s"REGA = [:$sLabel]",
      s"REGA = REGA $op $konst",
      s"[:$tLabel] = REGA",
    )
  }
}


case class DefVarEqVar(targetVar: String, srcVar: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val sLabel = parent.getVarLabel(srcVar).fqn
    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
    List(
      s"REGA = [:$sLabel]",
      s"[:$tLabel] = REGA",
    )
  }
}

case class DefVarEqVarOpVar(targetVar: String, srcVar1: String, op: String, srcVar2: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val s1Label = parent.getVarLabel(srcVar1).fqn
    val s2Label = parent.getVarLabel(srcVar2).fqn
    val tLabel = parent.assignVarLabel(targetVar, IsVar8).fqn
    List(
      s"REGA = [:$s1Label]",
      s"REGB = [:$s2Label]",
      s"[:$tLabel] = REGA $op REGB",
    )
  }
}

case class DefUint8EqExpr(targetVar: String, block: Block) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {

    val stmts: List[String] = block.expr(depth + 1, parent)

    val labelTarget = parent.assignVarLabel(targetVar, IsVar8).fqn

    val assign = List(
      s"[:$labelTarget] = REGA",
    )
    stmts ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List((depth, this.getClass.getSimpleName + "("), (depth + 1, targetVar)) ++
      block.dump(depth + 1) ++
      List((depth, ")"))
}

case class DefUint16EqExpr(targetVar: String, block: Block with BlockWith16BitValue) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val stmts: List[String] = block.expr(depth + 1, parent)

    val labelTarget = parent.assignVarLabel(targetVar, IsVar16, data = TWO_BYTE_STORAGE).fqn

    val assign = List(
      s"[:$labelTarget] = REGA",
    )
    stmts ++ assign
  }

  override def dump(depth: Int): List[(Int, String)] =
    List((depth, this.getClass.getSimpleName + "("), (depth + 1, targetVar)) ++
      block.dump(depth + 1) ++
      List((depth, ")"))
}

case class LetVarEqConst(targetVar: String, konst: Int) extends Block {

  override def gen(depth: Int, parent: Scope): List[String] = {
    val variable = parent.getVarLabel(targetVar)
    val fqn = variable.fqn

    variable.typ match {
      case IsVar8 | IsData =>
        List(
          s"; let var $targetVar = $konst",
          s"[:$fqn] = $konst",
        )
      case IsRef =>
        List(
          s"; let ref $targetVar = $konst",
          s"[:$fqn] = <$konst",
          s"[:$fqn+1] = >$konst "
        )
    }
  }
}

case class LetVarEqExpr(targetVar: String, block: Block) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {

    val stmts: List[String] = block.expr(depth + 1, parent)
    val labelTarget = parent.getVarLabel(targetVar, IsVar8).fqn

    val assign = List(
      s"[:$labelTarget] = REGA",
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

case class LetVarEqVar(targetVarName: String, srcVarName: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {

    val srcVar = parent.getVarLabel(srcVarName)
    val targVar = parent.getVarLabel(targetVarName)

    targVar.typ match {
      case IsVar8 | IsData =>
        List(
          s"REGA = [:${srcVar.fqn}]",
          s"[:${targVar.fqn}] = REGA",
        )
      case IsRef =>
        List(
          s"REGA = <:${srcVar.fqn} ",
          s"[:${targVar.fqn}] = REGA",
          s"REGA = >:${srcVar.fqn} ",
          s"[:${targVar.fqn} + 1] = REGA"
        )
    }
  }
}


case class DefVarEqString(target: String, str: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    // nothing to do but record the data with current scope - data will be laid out later
    parent.assignVarLabel(target, IsData, str.getBytes("UTF-8").toList).fqn
    List(
      s"""; var $target = "$str""""
    )
  }
}

case class DefRefEqVar(refName: String, target: String) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val targetLabelAddress = parent.getVarLabel(target).address
    val storage = intTo2xBytes(targetLabelAddress)
    val refLabel = parent.assignVarLabel(refName, IsRef, storage).fqn

    List(
      s"""; ref $refName = "$target"     ($refLabel = ${
        storage.mkString("[Hi:", ",LO:", "]")
      })"""
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
      case IsVar8 | IsData =>
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
  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelTransmit = parent.fqnLabelPathUnique("transmit")

    // leaves result in REGA
    val stmts: List[String] = block.expr(depth + 1, parent)

    stmts ++ split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelTransmit
         |PC = >:$labelTransmit _DO
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait
         |$labelTransmit:
         |UART = REGA
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
//          // leaves result in REGA
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
//               |UART = REGA
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
         |REGA = UART
         |""")
  }
}

case class PutcharConst(konst: Int) extends Block(nestedName = s"putcharConst_${konst}_") {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelWait = parent.fqnLabelPathUnique("wait")
    val labelTransmit = parent.fqnLabelPathUnique("transmit")
    split(
      s"""
         |$labelWait:
         |PCHITMP = <:$labelTransmit
         |PC = >:$labelTransmit _DO
         |PCHITMP = <:$labelWait
         |PC = >:$labelWait
         |$labelTransmit:
         |UART = $konst
         |""")
  }
}


case class DefFunction(fnName: String, functionArgs: List[FunctionArg], content: List[Block])
  extends Block(nestedName = s"function_$fnName") with IsFunction {

  override def functionName: String = fnName

  override def gen(depth: Int, scope: Scope): List[String] = {

    // side affect of defining the function args as vars
    val FunctionDef(startLabel, returnHi, returnLo, argsLabels) = defScopedArgLabels(scope)

    val prefix = if (fnName == "main") {
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

    val suffix = if (fnName == "main") {
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

  override def dump(depth: Int): List[(Int, String)] =
    List(
      (depth, this.getClass.getSimpleName + s" $fnName ("),
    ) ++
      functionArgs.flatMap {
        a => a.dump(depth + 1)
      } ++
      content.flatMap(_.dump(depth + 1))
}

case class WhileTrue(content: List[Block])
  extends Block(nestedName = s"whileTrue${Scope.nextInt}") {

  override def gen(depth: Int, parent: Scope): List[String] = {

    val labelBody = parent.toFqLabelPath("BODY")
    val labelAfter = parent.toFqLabelPath("AFTER")

    val prefix = split(s"""$labelBody:""")

    val stmts = content.flatMap {
      b => {
        val nameName = parent.copy(endLabel = Some(labelAfter))
        b.expr(depth + 1, nameName) //JL1.pushName(newName = label))
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
    val labelBot = parent.toFqLabelPath("AFTER")

    val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

    val conditionalJump = {
      List(s"$labelCheck:") ++
        condStatements ++
        split(
          s"""
             |PCHITMP = <:$labelBody
             |PC = >:$labelBody $flagToCheck
             |PCHITMP = <:$labelBot
             |PC = >:$labelBot
             |$labelBody:
               """)
    }

    val stmts = content.flatMap {
      b => {
        b.expr(depth + 1, parent) //.pushName(newName = labelBase))
      }
    }

    val suffix = split(
      s"""
         |PCHITMP = <:$labelCheck
         |PC = >:$labelCheck
         |$labelBot:
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

case class IfCond(flagToCheck: String, conditionBlock: Block, content: List[Block])
  extends Block(nestedName = s"ifCond${Scope.nextInt}") {

  override def gen(depth: Int, parent: Scope): List[String] = {
    val labelCheck = parent.toFqLabelPath("CHECK")
    val labelBody = parent.toFqLabelPath("BODY")
    val labelBot = parent.toFqLabelPath("AFTER")


    val condStatements = conditionBlock.expr(depth + 1, parent) // IMPORTANT TO USE THE PARENT DIRECTLY HERE AS THE CONDITION VAR IS DEFINED IN THE SURROUNDING CONTEXT

    val conditionalJump = {
      List(s"$labelCheck:") ++
        condStatements ++
        split(
          s"""
             |PCHITMP = <:$labelBody
             |PC = >:$labelBody $flagToCheck
             |PCHITMP = <:$labelBot
             |PC = >:$labelBot
             |$labelBody:
                 """)
    }

    val stmts = content.flatMap {
      b => {
        b.expr(depth + 1, parent) //.pushName(newName = labelBase))
      }
    }

    val suffix = split(
      s"""
         |$labelBot:
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

case class CallFunction(fnName: String, argExpr: List[BlkCompoundAluExpr]) extends Block {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val fns = parent.lookupFunction(fnName)
    val (functionScope: Scope, fn: Block with IsFunction) = fns.getOrElse(sys.error(s"no such function '$fnName''"))

    val FunctionDef(startLabel, returnHiLabel, returnLoLabel, argsLabelsAndDir) = fn.getScopedArgLabels(functionScope)

    val argNamesAndDir: List[FunctionArg] = fn.functionArgs

    if (argExpr.length != argNamesAndDir.size) {
      val argsNames = argNamesAndDir.map(_.argName)
      sys.error(
        s"""call to function "$fnName" has wrong number of arguments for ; expected $fnName(${
          argsNames.mkString(",")
        }) but got $fnName(${
          argExpr.mkString(",")
        })""")
    }

    val argDefinitionVsExpression = argsLabelsAndDir.zip(argExpr)

    // instructions needed to evaluate parameter clauses and set the values of the function input variables
    val setupCallParams: List[String] = argDefinitionVsExpression.flatMap {
      case (FunctionArgNameAndLabel(argLabel, _), argBlk) =>
        // evaluate the arg expression
        val stmts: List[String] = argBlk.expr(depth + 1, parent)
        // put the result into the input var locations
        stmts :+ s"[:$argLabel] = REGA"
    }

    // instructions needed to capture the output of the function into local vars within the caller's scope
    val setupOutParams: List[String] = argDefinitionVsExpression.flatMap {
      case (FunctionArgNameAndLabel(argLabel, functionArg), argBlk) =>
        if (functionArg.isOutput) {
          val argName = functionArg.argName
          argBlk.standaloneVariableName match {
            case Some(name) =>
              val localVarLabel = parent.lookupVarLabel(name).getOrElse {
                sys.error(s"""output parameter variable '$name' in call to function "$fnName" is not defined""")
              }.fqn

              // recover ourput value from the function and assign back to the local variable
              List(
                s"REGA = [:$argLabel]",
                s"[:$localVarLabel] = REGA"
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

case class Comment(comment: String) extends Block(logEntryExit = false) {
  override def gen(depth: Int, parent: Scope): List[String] = {
    val withoutLeading = comment.replace("//", "")
    List(s"; $withoutLeading")
  }
}

case class Program(fns: List[Block]) {
  def dump(depth: Int): String = {

    fns.flatMap {
      _.dump(depth)
    }.map {
      ds => ("      " * ds._1) + ds._2
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

    varlist ++ jumpToMain ++ asm :+
      "root_end:" :+ "END"
  }
}

trait IsFunction {
  self: Block =>

  def functionName: String

  def functionArgs: List[FunctionArg]

  def defScopedArgLabels(scope: Scope): FunctionDef = {
    val returnHiLabel = scope.assignVarLabel("RETURN_HI", IsVar8).fqn
    val returnLoLabel = scope.assignVarLabel("RETURN_LO", IsVar8).fqn
    // These locations where we write the input parameters into the function.
    // Also, read from these locations to fetch "out" values.
    val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.map {
      argName =>
        FunctionArgNameAndLabel(scope.assignVarLabel(argName.argName, IsVar8).fqn, FunctionArg(argName.argName, argName.isOutput))
    }
    val fnStart = scope.toFqLabelPath("START")

    FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
  }

  def getScopedArgLabels(scope: Scope): FunctionDef = {
    val returnHiLabel = scope.getVarLabel("RETURN_HI", IsVar8).fqn
    val returnLoLabel = scope.getVarLabel("RETURN_LO", IsVar8).fqn
    // These locations where we write the input parameters into the function.
    // Also, read from these locations to fetch "out" values.
    val argNamesLabelsDirection: List[FunctionArgNameAndLabel] = functionArgs.map {
      argName =>
        FunctionArgNameAndLabel(scope.getVarLabel(argName.argName, IsVar8).fqn, FunctionArg(argName.argName, argName.isOutput))
    }
    val fnStart = scope.toFqLabelPath("START")

    FunctionDef(fnStart, returnHiLabel, returnLoLabel, argNamesLabelsDirection)
  }
}

abstract class Block(nestedName: String = "", logEntryExit: Boolean = true) extends Positional {

  if (!nestedName.matches("^[a-zA-Z0-9_]*$")) {
    sys.error(s"invalid block nested name ;'$nestedName'")
  }

  // for logging
  def dump(depth: Int): List[(Int, String)] = List((depth, this.toString))

  final def expr(depth: Int, parentScope: Scope): List[String] = {

    val thisScope = localize(parentScope)

    val enter = s"${prefixComment(depth)}ENTER ${thisScope.blockName} @ $this $pos"
    val exit = s"${prefixComment(depth)}EXIT  ${thisScope.blockName} @ $this"

    try {
      val value: List[String] = this match {
        case bf: Block with IsFunction =>
          parentScope.addFunction(thisScope, bf)

          gen(depth, thisScope).map(l => {
            prefixOp(depth) + l
          })
        case _ =>
          gen(depth, thisScope).map(l => {
            prefixOp(depth) + l
          })
      }

      if (logEntryExit) enter +: value :+ exit
      else value

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

  private def prefixComment(depth: Int) = s"; ($depth) ${" " * depth}"

  private def prefixOp(depth: Int) = prefixComment(depth).replaceAll(".", " ")

  protected[this] def gen(depth: Int, parent: Scope): List[String]
}

// TODO : Use me to mark blocks that have a value and len of value
trait BlockWith16BitValue {
  self: Block =>
}
