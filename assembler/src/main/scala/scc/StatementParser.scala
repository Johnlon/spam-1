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

import java.nio.file.{Files, Paths}

import asm.{AluOp, EnumParserOps}

import scala.language.postfixOps
import scala.util.parsing.combinator.JavaTokenParsers


class StatementParser {

  self: ExpressionParser with ConstExpressionParser with ConditionParser with EnumParserOps with JavaTokenParsers =>

  val SPACE = " "

  def statement: Parser[Block] = positioned {
    blockComment | lineComment |
      //statementUInt8Eq |
      statementUInt16EqExpr |
      statementUInt16EqCondition |
      statementVarDataLocated |
      statementVarData |
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
      statementPutcharVarOptimisation | statementPutcharConstOptimisation | stmtPutcharGeneral |
      stmtPutsName |
      statementGetchar |
      whileCond | whileTrue | ifCond | breakOut | functionCall ^^ {
      s =>
        s
    }
  }

  def statements: Parser[List[Block]] = statement ~ (statement *) ^^ {
    case a ~ b =>
      a +: b
  }

  //
  //  // optimisation of "var VARIABLE=CONST"
  //  def statementVarEqConst: Parser[DefVarEqConst] = positioned {
  //    "var" ~> name ~ "=" ~ constExpression <~ SEMICOLON ^^ {
  //      case targetVar ~ _ ~ konst =>
  //        Def`VarEqConst(targetVar, konst)
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
    "uint16" ~> name ~ "=" ~ conditionExpr <~ SEMICOLON ^^ {
      case targetVar ~ _ ~ block =>
        DefUint16EqCondition(targetVar, block._1, block._2)
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

  def dataSourceFile: Parser[Seq[Byte]] =
    "file(" ~> quotedString <~ ")" ^^ {
      fileName => {
        val path = Paths.get(fileName)
        try {
          Files.readAllBytes(path)
        } catch {
          case _: Throwable =>
            sys.error("can't read " + path.toFile.getPath + " (" + path.toFile.getAbsolutePath + ")")
        }
      }
    }

  def dataSource = dataSourceFile | dataSourceString | dataSourceBytes

  /*
  STRING:     STR     "ABC\n\0\u0000"
  BYTE_ARR:   BYTES   [ 'A', 65 ,$41, %10101010 ] ; parse as hex bytes and then treat as per STR
  */
  def statementVarData: Parser[DefVarEqData] = positioned {
    "var" ~> name ~ ("=" ~ "[") ~ dataSource <~ ("]" ~ SEMICOLON) ^^ {
      case target ~ _ ~ str =>
        DefVarEqData(target, str)
    }
  }

  def locatedData: Parser[(Int, Seq[Byte])] = constExpression ~ (":" ~ "[") ~ opt(dataSource) <~ "]" ^^ {
    case k ~ _ ~ ds =>
      (k, ds.getOrElse(Nil))
  }

  def statementVarDataLocated: Parser[DefVarEqLocatedData] = positioned {
    "var" ~> name ~ ("=" ~ "[") ~ rep1(locatedData) <~ ("]" ~ SEMICOLON) ^^ {
      case target ~ _ ~ dataL =>
        DefVarEqLocatedData(target, dataL)
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
    ("while" ~ "(" ~ "true" ~ ")" ~ "{") ~> statements <~ "}" ^^ {
      content => WhileTrue(content)
    }
  }

  def whileCond: Parser[Block] = positioned {
    "while" ~ "(" ~> conditionWithConst ~ ")" ~ "{" ~ statements <~ "}" ^^ {
      case cond ~ _ ~ _ ~ content =>
        WhileCond(cond._1, cond._2, content)
    }
  }

  def breakOut: Parser[Block] = positioned {
    "break" ^^ {
      _ => Break()
    }
  }

  def elseCurlyBlock: Parser[List[Block]] = "else" ~ "{" ~> statements <~ "}"

  def elseNonCurlyBlock: Parser[List[Block]] = "else" ~> statement ^^ {
    b =>
      List(b)
  }

  def elseBlock: Parser[List[Block]] = elseCurlyBlock | elseNonCurlyBlock ^^ {
    blk =>
      blk
  }

  def ifCond: Parser[Block] = positioned {
    "if" ~ "(" ~> conditionExpr ~ ")" ~ "{" ~ statements ~ "}" ~ opt(elseBlock) ^^ {
      case cond ~ _ ~ _ ~ content ~ _ ~ elseContent =>
        IfCond(cond._1, cond._2, content, elseContent.getOrElse(Nil))
    }
  }

  def lineComment: Parser[Block] = positioned {
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

  def program: Parser[Program] = "program" ~ "{" ~> ((statementUInt16EqExpr | lineComment | blockComment | functionDef) +) <~ "}" ^^ {
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
