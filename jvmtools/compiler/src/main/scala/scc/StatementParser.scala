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
// TODO: Add some traps for stupid errors like putuart("...") which I wasted loads of time on
// TODO while(some expr) is needed

package scc

import asm.Ports.{ReadPort, WritePort}
import asm.{AluOp, EnumParserOps, Ports}

import java.nio.file.{Files, Paths}
import java.util.Objects
import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers


class StatementParser {

  self: ExpressionParser with ConstExpressionParser
    with ConditionParser with EnumParserOps with JavaTokenParsers
  =>

  val SPACE = " "

  def statement1: Parser[Block] = positioned {
    codeBlock |
      blockComment | lineComment |
      //statementUInt8Eq |
      statementConst |
      statementUInt16EqExpr |
      statementUInt16EqCondition |
      statementVarLocatedDatasource |
      statementVarDatasource |
      //      statementVarEqVarOpVar |
      //      statementVarEqVar |
      //      statementVarEqVarOpConst |
      //      statementVarEqConstOpVar |
      //      statementVarEqConst |
      //      statementVarEqOp |
      statementRef |
        statementLetVarEqConst |
        statementLetVarEqVar |
        statementLetVarEqExpr |
        statementLetStringIndexEqExpr |
        statementPutuartVarOptimisation | statementPutuartConstOptimisation | stmtPutuartGeneral |
        statementWritePort |
        stmtPutfuartGeneral | stmtPutfuartConst |
        statementPutsName |
        statementHalt |
        statementHaltVar |
         statementHaltVarVar |
        whileCond | whileTrue |
        ifCond |
      breakOut | functionCall ^^ {
      s =>
        s
    }
  }

  def label: Parser[Option[String]] = opt(name <~ ":")

  def statement = positioned {
    label ~ statement1 ^^ {
      case Some(l) ~ s => LabelledStatement(l, s)
      case None ~ s => s
    }
  }

  def codeBlock: Parser[Block] = positioned {
    "{" ~> rep(statement) <~ "}" ^^ (a => CodeBlock(a))
  }

  def statementConst: Parser[DefConst] = positioned {
    "const" ~> name ~ "=" ~ constExpression <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ konst =>
        DefConst(targetVar, konst)
    }
  }


  //
  //  // optimisation of "var VARIABLE=CONST"  8 bit
  //  def statementVarEqConst: Parser[DefVarEqConst] = positioned {
  //    "var" ~> name ~ "=" ~ constExpression <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ konst =>
  //        DefVarEqConst(targetVar, konst)
  //    }
  //  }
  //
  //  // optimisation of "var VARIABLE=CONST op VARIABLE"
  //  def statementVarEqConstOpVar: Parser[DefVarEqConstOpVar] = positioned {
  //    "var" ~> name ~ "=" ~ constExpression ~ aluOp ~ name <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ konst ~ oper ~ srcVar =>
  //        DefVarEqConstOpVar(targetVar, konst, oper, srcVar)
  //    }
  //  }
  //
  //  // optimisation of "var VARIABLE=VARIABLE op CONST"
  //  def statementVarEqVarOpConst: Parser[DefVarEqVarOpConst] = positioned {
  //    "var" ~> name ~ "=" ~ name ~ aluOp ~ constExpression <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ srcVar ~ op ~ konst =>
  //        DefVarEqVarOpConst(targetVar, srcVar, op, konst)
  //    }
  //  }
  //
  //  // optimisation of "var VARIABLE=VARIABLE"
  //  def statementVarEqVar: Parser[DefVarEqVar] = positioned {
  //    "var" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ srcVar =>
  //        DefVarEqVar(targetVar, srcVar)
  //    }
  //  }
  //
  //  // optimisation of "var VARIABLE=VARIABLE op VARIABLE"
  //  def statementVarEqVarOpVar: Parser[DefVarEqVarOpVar] = positioned {
  //    "var" ~> name ~ "=" ~ name ~ aluOp ~ name <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ srcVar1 ~ op ~ srcVar2 =>
  //        DefVarEqVarOpVar(targetVar, srcVar1, op, srcVar2)
  //    }
  //  }

  // general purpose
  //  def statementVarEqOp: Parser[DefUint8EqExpr] = positioned {
  //    "var" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ block => DefUint8EqExpr(targetVar, block)
  //    }
  //  }
  //
  //  // general purpose
  //  def statementUInt8Eq: Parser[DefUint8EqExpr] = positioned {
  //    "uint8" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ block => DefUint8EqExpr(targetVar, block)
  //    }
  //  }

  // general purpose
  def statementUInt16EqExpr: Parser[DefUint16EqExpr] = positioned {
    "uint16" ~> name ~ "=" ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ block =>
        DefUint16EqExpr(targetVar, block)
    }
  }

  // special purpose
  def statementUInt16EqCondition: Parser[DefUint16EqCondition] = positioned {
    "uint16" ~> name ~ "=" ~ conditionWithExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ condition =>
        DefUint16EqCondition(targetVar, condition)
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

  // general purpose
  def statementLetStringIndexEqExpr: Parser[LetStringIndexEqExpr] = positioned {
    name ~ "[" ~ blkCompoundAluExpr ~ ("]" ~ "=") ~ blkCompoundAluExpr <~ SEMICOLON ^^ {
      case varName ~ _ ~ idxExpr ~ _ ~ valExpr =>
        LetStringIndexEqExpr(varName, idxExpr, valExpr)
    }
  }

  def statementLetVarEqVar: Parser[LetVarEqVar] = positioned {
    name ~ "=" ~ name <~ SEMICOLON ^^ {
      case targetVarName ~ _ ~ srcVarName =>
        LetVarEqVar(targetVarName, srcVarName)
    }
  }

  def dataSourceString: Parser[Seq[Byte]] = quotedString ^^ {
    str =>
      str.getBytes("ISO8859-1").toSeq
  }

  def dataSourceBytes: Parser[Seq[Byte]] =
    rep1(constExpression) ^^ {
      data =>
        data.map(_.toByte)
    }

  def systemPropString: Parser[String] =
    "systemProp(" ~> quotedString <~ ")" ^^ {
      name => {
        val propVal = System.getProperty(name)
        Objects.requireNonNull(propVal, "System Property undefined : '" + name + "'")
        if (propVal.trim.isEmpty) {
          sys.error("System Property defined as blank : '" + name + "'")
        }
        propVal
      }
    }

  def stringValue: Parser[String] = quotedString | systemPropString

  def dataSourceFile: Parser[Seq[Byte]] =
    "file(" ~> stringValue <~ ")" ^^ {
      fileName => {
        if (fileName.trim.isEmpty) {
          System.err.println("illegal datasource:   file(<blank or null>)")
          sys.exit(1)
        }
        val path = Paths.get(fileName)
        if (!path.toFile.exists()) {
          System.err.println("\nnot found " + fileName)
          sys.exit(1)
        }
        System.err.println("\nloading " + fileName)
        try {
          Files.readAllBytes(path).toSeq
        } catch {
          case e: Throwable =>

            System.err.println("\ncan't read data source '"
            + fileName
            + "' (path:" + path.toFile.getPath + ", abs:" + path.toFile.getAbsolutePath + ")")

            sys.exit(1)
        }
      }
    }

  def dataSourceHexFile: Parser[Seq[Byte]] =
    "hexfile(" ~> stringValue <~ ")" ^^ {
      fileName => {
        if (fileName.trim.length == 0) {
          sys.error("illegal datasource:   hexfile(<blank or null>)")
        }
        val path = Paths.get(fileName)
        val str = try {
          Files.readString(path).replaceAll("(?m)\\s", "")
        } catch {
          case ex: Throwable =>
            sys.error("can't read data source '"
              + fileName
              + "' (path:" + path.toFile.getPath + ", abs:" + path.toFile.getAbsolutePath + ") \n" + ex.getMessage)
        }
        val b = try {
          str.grouped(2).map(i => Integer.parseInt(i, 16).toByte).toSeq
        } catch {
          case ex: Throwable =>
            sys.error("can't decode data source '"
              + fileName
              + "' (path:" + path.toFile.getPath + ", abs:" + path.toFile.getAbsolutePath + ")\n" + ex.getMessage)
        }

        b.foreach(x => println("I: " + x.toInt))
        b
      }
    }

  def dataSource: Parser[Seq[Byte]] = dataSourceFile | dataSourceHexFile | dataSourceString | dataSourceBytes

  /*
  positions are relative to the start of the variable, not absolute memory.
  it is an illegal situation to have a zero length data at index 0 - we can't usefully declare a zero bytes of data at index 0.
  at other indexes the datasource can be zero length in which case it is merely a means to ensure capacity upto but excluding that index without specifying data.
  */
  def locatedDatasource: Parser[LocatedData] = positioned {
    constExpression ~ (":" ~ "[") ~ opt(dataSource) <~ "]" ^^ {
      case offset ~ _ ~ ds =>
        LocatedData(offset, ds.getOrElse(Nil))
    }
  }


  def address: Parser[Int] = {
    "@" ~> constExpression ^^ (address => address)
  }


  /*
  STRING:     STR     "ABC\n\0\u0000"
  BYTE_ARR:   BYTES   [ 'A', 65 ,$41, %10101010 ] ; parse as hex bytes and then treat as per STR
  */
  def statementVarDatasource: Parser[DefVarEqData] = positioned {
    "var" ~> name ~ ("=" ~ "[") ~ dataSource <~ ("]" ~ SEMICOLON) ^^ {
      case target ~ _ ~ str =>
        DefVarEqData(target, str)
    }
  }
  // permits any mix of data and comments
  def statementVarLocatedDatasource: Parser[Block] = positioned {
    "var" ~> name ~ "=" ~ opt(address) ~ "[" ~ rep1(locatedDatasource | blockComment | lineComment) <~ ("]" ~ SEMICOLON) ^^ {
      // doesn't preserve the comments - too much hassle
      case target ~ _ ~ absLocation ~ _ ~ dataL =>
        val d = dataL.collect { case b: LocatedData => b }
        DefVarEqLocatedData(target, absLocation, d)
    }
  }

  def statementRef: Parser[DefRefEqVar] = positioned {
    "ref" ~> name ~ "=" ~ name <~ SEMICOLON ^^ {
      case refName ~ _ ~ target => DefRefEqVar(refName, target)
    }
  }

  def statementHalt: Parser[Block] = positioned {
    "halt" ~ "(" ~> constExpression ~ "," ~ constExpression <~ ")" ^^ {
      case haltCode ~ _ ~ num =>
        val i = haltCode.toInt
        if (i > 65535 || i < 0) sys.error("halt const out of range : " + i)
        Halt(i, num.toByte)
    }
  }

  def statementHaltVar: Parser[Block] = positioned {
    "halt" ~ "(" ~> name ~ "," ~ constExpression <~ ")" ^^ {
      case varName ~ _ ~ num =>
        HaltVar(varName, num.toByte)
    }
  }

  def statementHaltVarVar: Parser[Block] = positioned {
    "halt" ~ "(" ~> name ~ "," ~ name <~ ")" ^^ {
      case varName ~ _ ~ varName2 =>
        HaltVarVar(varName, varName2)
    }
  }

  def statementWritePort: Parser[Block] = positioned {
    "writeport" ~ "(" ~> writePort ~ "," ~ blkExpr <~ ")" ^^ {
      case port ~ _ ~ value => BlkWritePort(port, value)
    }
  }

  def statementPutsName: Parser[Block] = positioned {
    "puts" ~ "(" ~> name <~ ")" ^^ {
      varName => Puts(varName)
    }
  }

  def statementPutuartConstOptimisation: Parser[Block] = positioned {
    "putuart" ~ "(" ~> constExpression <~ ")" ^^ {
      konst => PutuartConst(konst)
    }
  }


  def statementPutuartVarOptimisation: Parser[Block] = positioned {
    "putuart" ~ "(" ~> name <~ ")" ^^ {
      varName: String => PutuartVar(varName)
    }
  }

  def stmtPutuartGeneral: Parser[Block] = positioned {
    "putuart" ~ "(" ~> blkCompoundAluExpr <~ ")" ^^ {
      block => Putuart(block)
    }
  }

  def fmtChar: Parser[Char] = "[CBX]".r ^^ {
    str => str.head
  }

  def stmtPutfuartGeneral: Parser[Block] = positioned {
    "putfuart" ~ "(" ~> fmtChar ~ ", " ~ blkCompoundAluExpr <~ ")" ^^ {
      case code ~ _ ~ block => Putfuart(code, block)
    }
  }

  def stmtPutfuartConst: Parser[Block] = positioned {
    "putfuart" ~ "(" ~> fmtChar ~ ", " ~ constExpression <~ ")" ^^ {
      case code ~ _ ~ block => PutfuartConst(code, block)
    }
  }

  def argumentDefTerm: Parser[FunctionArg] = name ~ opt("out") ^^ {
    case name ~ isOutput =>
      FunctionArg(name, isOutput.isDefined)
  }

  def functionDef: Parser[Block] = positioned {
    "fun " ~> name ~ "(" ~ repsep(argumentDefTerm, ",") ~ ")" ~ codeBlock ^^ {
      case fnName ~ _ ~ args ~ _ ~ content =>
        DefFunction(fnName, args, content)
    }
  }

  def functionCall: Parser[Block] = positioned {
    name ~ "(" ~ repsep(blkCompoundAluExpr, ",") ~ ")" ^^ {
      case fnName ~ _ ~ argExpr ~ _ =>
        CallFunction(fnName, argExpr)
    }
  }

  def whileTrue: Parser[Block] = positioned {
    label ~ ("while" ~ "(" ~ "true" ~ ")") ~! statement ^^ {
      case l ~ _ ~ content => WhileTrue(l, content)
    }
  }

  def whileCond: Parser[Block] = positioned {
    "while" ~ "(" ~> condition ~ ")" ~! statement ^^ {
      case cond ~ _ ~ content =>
        WhileCond(cond, content)
    }
  }

  def breakOut: Parser[Block] = positioned {
    "break" ^^ {
      _ => Break()
    }
  }

  def elseStatement: Parser[List[Block]] = "else" ~>! statement ^^ {
    b =>
      List(b)
  }

  def ifCond: Parser[Block] = positioned {
    "if" ~ "(" ~> condition ~ ")" ~! statement ~! opt(elseStatement) ^^ {
      case condition ~ _ ~ content ~ elseContent =>
        IfCond(condition, content, elseContent.getOrElse(Nil))
    }
  }

  def lineComment: Parser[LineComment] = positioned {
    "//.*".r ^^ {
      c =>
        LineComment(c)
    }
  }

  def blockComment: Parser[BlockComment] = positioned {
    "(?s)/\\*(.*?)(?=\\*/)".r <~ "*/" ^^ {
      c =>
        BlockComment(c)
    }
  }

  def program: Parser[Program] = "program" ~ "{" ~> ((statementUInt16EqExpr | statementVarDatasource | statementVarLocatedDatasource | lineComment | blockComment | functionDef) +) <~ "}" ^^ {
    fns => Program(fns)
  }

  def readPortName: Parser[String] = {
    enumToParser(ReadPort.values).map(_.enumName)
  }

  def readPort: Parser[ReadPort] = readPortName ^^ {
    name => ReadPort.valueOf(name)
  }

  def writePortName: Parser[String] = {
    enumToParser(WritePort.values).map(_.enumName)
  }

  def writePort: Parser[WritePort] = writePortName ^^ {
    name => WritePort.valueOf(name)
  }
}
