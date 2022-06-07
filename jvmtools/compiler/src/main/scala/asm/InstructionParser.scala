package asm

import scala.language.postfixOps
import scala.util.parsing.combinator._

import asm.AddressMode.{DIRECT, REGISTER}
import asm.ConditionMode.{INVERT, STANDARD}

sealed class AddressMode(val code: String)

object AddressMode {
  case object DIRECT extends AddressMode("DIR")

  case object REGISTER extends AddressMode("REG")
}


// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait EnumParserOps {
  self: JavaTokenParsers =>
  def enumToParser[A <: E](e: Seq[A]): Parser[A] = {
    // reverse sorted to put longer operators ahead of shorter ones otherwise shorter ones gobble
    val longestFirst: Seq[A] = e.sortBy(_.enumName).reverse
    longestFirst map { m =>
      literal(m.enumName) ^^^ m
    } reduceLeft {
      _ | _
    }
  }

}

trait InstructionParser extends EnumParserOps with JavaTokenParsers {
  self: Lines with Knowing with Devices =>

  var dataAddress = 0

  //  override val whiteSpace =  """[ \t\f\x0B]++""".r // whitespace not including line endings

  def aluop: Parser[AluOp] = {
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
    longAluOps | shortAluOps
  }

  // amended vs 'stringLiteral' to include the short form \0
  def quotedString: Parser[String] = ("\"" + """([^"\x01-\x1F\x7F\\]|\\[\\'"0bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\"").r ^^ {
    s =>
      val withoutQuotes = s.stripPrefix("\"").stripSuffix("\"")
      val str = org.apache.commons.text.StringEscapeUtils.unescapeJava(withoutQuotes)
      str
  }

  def adev: Parser[ADevice] = enumToParser(ADevice.values)

  def bdev: Parser[BDevice] = enumToParser(BDevice.values)

  def bdevonly: Parser[BOnlyDevice] = enumToParser(BOnlyDevice.values)

  def tdev: Parser[TDevice] = enumToParser(TDevice.values)

  def controlCode: Parser[Control] = enumToParser(Control.values)

  def condition: Parser[Condition] = ("!" ?) ~ (controlCode ?) ^^ {
    case mode ~ ctrl =>
      Condition(mode.map(_ => INVERT).getOrElse(STANDARD), ctrl.getOrElse(Control._A))
  }


  def name: Parser[String] = "[a-zA-Z_][a-zA-Z0-9_]*".r ^^ (a => a)

  def pcref: Parser[Known[KnownInt]] = """.""" ^^ (v => sys.error("FIXME")) //Known("pc", pc))

  def dec: Parser[Known[KnownInt]] =
    """-?\d+""".r ^^ { v =>
      val vi = v.toInt
      Known("decimal", vi)
    }

  def char: Parser[Known[KnownInt]] = "'" ~> ".".r <~ "'" ^^ { v =>
    val i = v.codePointAt(0)
    if (i > 127) throw new RuntimeException(s"asm error: character '$v' codepoint $i is outside the 0-127 range")
    Known("", i.toByte)
  }

  /*
  Binary numbers use the % prefix (e.g. %1101).
  Octal numbers use the @ prefix (e.g. @15).
  Decimal numbers use no prefix (e.g. 13).
  Hexadecimal numbers use the $ prefix (e.g. $0D).
  */
  def hex: Parser[Known[KnownInt]] = "$" ~ "[0-9a-hA-H]+".r ^^ { case _ ~ v => Known("hex $" + v, Integer.valueOf(v, 16)) }

  def bin: Parser[Known[KnownInt]] = "%" ~ "[01]+".r ^^ { case _ ~ v => Known("bin %" + v, Integer.valueOf(v, 2)) }

  def oct: Parser[Known[KnownInt]] = "@" ~ "[0-7]+".r ^^ { case _ ~ v => Known("oct @" + v, Integer.valueOf(v, 8)) }

  def labelAddr: Parser[IsKnowable[KnownInt]] = ":" ~ name ^^ { case _ ~ v => forwardReference(v) }

  def labelLen: Parser[IsKnowable[KnownInt]] = "len(" ~ ":" ~> name <~ ")" ^^ {
    n =>
      def lookup(): Option[KnownInt] = {
        val maybeKnow = labels.get(n)
        val option: Option[_ <: KnownValue] = maybeKnow.flatMap(_.getVal)

        option.map {
          case KnownByteArray(_, b) =>
            KnownInt(b.length)
          case KnownInt(v) =>
            if (v < 0) {
              sys.error(s"asm error: len(...) is valid only for positive values, but got len( $v ) ")
            }
            val powerOf2ZeroOffset = Math.log(1 + v.toDouble) / Math.log(2.toDouble)
            val bytesNeeded = Math.ceil(powerOf2ZeroOffset / 8).toInt
            val n = Math.max(1, bytesNeeded)
            KnownInt(n)
        }
      }

      Knowable(s"len(:$n)", () =>
        lookup()
      )
  }

  def hiByte: Parser[IsKnowable[KnownInt]] = "<" ~> expr ^^ { e: Know[KnownInt] =>
    UniKnowable[KnownInt](() => e, i => KnownInt((i.value >> 8) & 0xff), "HI<")
  }

  def loByte: Parser[IsKnowable[KnownInt]] = ">" ~> expr ^^ { e: Know[KnownInt] =>
    UniKnowable[KnownInt](() => e, i => KnownInt(i.value & 0xff), "LO>")
  }

  def factor: Parser[IsKnowable[KnownInt]] = labelLen | char | pcref | dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte | labelAddr

  // picks up literal numeric expressions where both sides are expr
  def expr: Parser[IsKnowable[KnownInt]] = factor ~ rep("*" ~ factor | "/" ~ factor | "&" ~ factor | "|" ~ factor | "+" ~ factor | "-" ~ factor) ^^ {
    case number ~ list =>
      val value = list.foldLeft(number) {
        case (x, "*" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ * _, "*")
        case (x, "+" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ + _, "+")
        case (x, "/" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ / _, "/")
        case (x, "-" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ - _, "-")
        case (x, "&" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ & _, "&")
        case (x, "|" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ | _, "|")
        case (x, op ~ y) => sys.error(s"sw error : missing handler for op '$op' for operand $x and $y")
      }
      value
  }

  def ramDirect: Parser[RamDirect] = "[" ~ expr ~ "]" ^^ { case _ ~ v ~ _ => RamDirect(v) }

  def debug: Parser[Debug] = ";;" ~> ".*".r ^^ (a => Debug(a))

  def comment: Parser[Comment] = ";" ~> ".*".r ^^ (a => Comment(a))

  def targets: Parser[TExpression] = tdev | ramDirect

  def bdevices: Parser[BExpression] = bdev | ramDirect

  def bdeviceOrRamDirect: Parser[BOnlyDevice] = bdevonly | ramDirect


  def label: Parser[Label] = name ~ ":" ^^ {
    case n ~ _ =>
      //rememberKnown(n, Known(n, KnownInt(pc)))
      //Label(n, pc)
      val l = Label(n)
      rememberKnown(n, Knowable(n, () => l.value.map(KnownInt)))
      l
  }

  // .text added for compat with VBCC but at present is ignored - ie an empty block
  // signals the start of program code
  //  def textSegmentBlock: Parser[TextSegment] = positioned {
  //    ".text" ^^ {
  //      ~ => TextSegment()
  //    }
  //  }

  // .global added for compat with VBCC but at present is ignored - ie an empty block
  // exports the symbol from the compilation unit
  /*
  def globalSymbolBlock: Parser[Block] = positioned {
    ".global" ~> name ^^ (a => CodeBlock(List.empty))
  }
  */

  def equInstruction: Parser[EquInstruction] = (name <~ ":" ~ "EQU") ~ expr ^^ {
    case n ~ konst =>
      val renamed = konst.rename("EQU " + n + " " + konst.name)
      rememberKnown(n, renamed)
      EquInstruction(n, renamed)
  }

  def strInstruction: Parser[RamInitialisation] = (name <~ ":" ~ "STR") ~ quotedString ^^ {
    case n ~ b =>
      val bytes = b.getBytes("UTF-8")

      val v = Known("STR " + n, KnownByteArray(dataAddress, bytes.toList))

      val stored = rememberKnown(n, v)
      dataAddress = stored.knownVal.value // reset auto data layout back to this position - do we really wanna do that?

      val ramInit = Comment("STR " + n + " @ " + dataAddress + " size " + b.length) +: bytes.map { c => {
        val ni = inst(RamDirect(Known("BYTE-ADDR:" + n, dataAddress)), ADevice.NU, AluOp.PASS_B, BDevice.IMMED, Some(Condition.Default), Known("STR-BYTE", c))
        dataAddress += 1
        ni
      }
      }.toList

      RamInitialisation(ramInit)
  }

  // TODO - this is intiialised memory
  // We also need uninitialised memory that won't cause program bloat with zeros - leave it to software to set initial values.
  // This would be simply emitting a label with the right address during assembly - needa a size though
  // label: RESERVE 4 ; reserve unititalised space for a long
  def bytesInstruction: Parser[RamInitialisation] = (name <~ ":" ~ "BYTES" ~ "[") ~ repsep(expr, ",") <~ "]" ^^ {
    case n ~ expr =>
      if (expr.isEmpty) {
        sys.error(s"asm error: BYTES expression with label '$n' must have at least one byte but none were defined")
      }
      val exprs: List[Know[KnownInt]] = expr

      val ints: List[(String, Int)] = exprs.map(x => (x.name, x.getVal.get.value))

      ints.filter { x =>
        x._2 < Byte.MinValue || x._2 > 255
      }.foreach(x => sys.error(s"asm error: $x evaluates as out of range ${Byte.MinValue} to 255"))

      val v = Known("BYTES " + n, KnownByteArray(dataAddress, ints.map(_._2.toByte)))
      val stored = rememberKnown(n, v)
      dataAddress = stored.knownVal.value // reset auto data layout back to this position - do we really wanna do that?

      val ramInit = Comment("BYTES " + n + " @ " + dataAddress + " size " + ints.size) +: ints.map {
        c => {
          // c.toByte will render between -128 and  +127
          // then name "c" will render as whatever int value was actually presented in the code (eg when c=255 then toByte = -1 )
          val immed = Known(f"${c._1}", c._2.toByte)

          val ni = inst(RamDirect(Known("BYTES " + n, dataAddress)), ADevice.NU, AluOp.PASS_B, BDevice.IMMED, Some(Condition.Default), immed)
          dataAddress += 1
          ni
        }
      }

      RamInitialisation(ramInit)
  }

  // TODO - this is intiialised memory
  // We also need uninitialised memory that won't cause program bloat with zeros - leave it to software to set initial values.
  // This would be simply emitting a label with the right address during assembly - needa a size though
  // label: RESERVE 4 ; reserve unititalised space for a long
  def reserveInstruction: Parser[Line] = name ~ (":" ~ "RESERVE") ~ expr ^^ {
    case n ~ _ ~ expr =>

      val size = expr.getVal.get.value

      if (size < Byte.MinValue || size > 255) {
        sys.error(s"asm error.filter { x =>: $size evaluates as out of range ${Byte.MinValue} to 255")
      }

      val v = Known("RESERVED " + n, KnownInt(size))
      val stored = rememberKnown(n, Known(n, KnownInt(dataAddress)))
      dataAddress = stored.knownVal.value // reset auto data layout back to this position - do we really wanna do that?

      val comment = Comment("RESERVE " + n + " @ " + dataAddress + " size " + size)
      dataAddress += size

      comment
  }

  private def inst(t: TExpression, a: ADevice, op: AluOp, b: BExpression, f: Option[Condition], immed: Know[KnownInt]): Instruction = {
    val defaultCont = Condition(ConditionMode.STANDARD, Control._A)

    (t, b) match {
      case (t: TDevice, b: BDevice) =>
        Instruction(t, a, b, op, f.getOrElse(defaultCont), REGISTER, Irrelevant(), immed)
      case (t: TDevice, RamDirect(addr)) =>
        Instruction(t, a, BDevice.RAM, op, f.getOrElse(defaultCont), DIRECT, addr, immed)
      case (RamDirect(addr), b: BDevice) =>
        Instruction(TDevice.RAM, a, b, op, f.getOrElse(defaultCont), DIRECT, addr, immed)
      case (RamDirect(_), RamDirect(_)) =>
        sys.error(s"illegal instruction: target '$t' and source '$b' cannot both be RAM")
    }
  }

  def abInstruction: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ bdevices ~ (condition ?) ^^ {
    case t ~ a ~ op ~ b ~ f =>
      inst(t, a, op, b, f, Irrelevant())
  }

  def abInstructionImmed: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ expr ~ (condition ?) ^^ {
    case t ~ a ~ op ~ immed ~ f =>
      inst(t, a, op, BDevice.IMMED, f, immed)
  }

  def bInstruction: Parser[Line] = (targets <~ "=") ~ bdeviceOrRamDirect ~ (condition ?) ^^ {
    case t ~ b ~ f =>
      inst(t, ADevice.NU, AluOp.PASS_B, b, f, Irrelevant())
  }

  def bInstructionImmed: Parser[Line] = (targets <~ "=") ~ expr ~ (condition ?) ^^ {
    case t ~ immed ~ f =>
      inst(t, ADevice.NU, AluOp.PASS_B, BDevice.IMMED, f, immed)
  }

  def aInstruction: Parser[Line] = (targets <~ "=") ~ adev ~ (condition ?) ^^ {
    case t ~ a ~ f =>
      inst(t, a, AluOp.PASS_A, BDevice.NU, f, Irrelevant())
  }

  /*  merge single instruction and multi-inst instructions */
  def line: Parser[Seq[Line]] = (reserveInstruction | strInstruction | bytesInstruction | equInstruction
    | bInstruction | abInstructionImmed | abInstruction | aInstruction | bInstructionImmed
    | debug | comment | label) ^^ {

    case x: RamInitialisation =>
      x
    case x: Seq[_] =>
      x.asInstanceOf[Seq[Line]]
    case x: Line =>
      List(x)
    case x =>
      throw new MatchError(x + " is not a Line or Seq[Line] but is type " + x.getClass)
  }

  def lines: Parser[Seq[Line]] = line ~ (line *) <~ "END" ^^ {
    case a ~ b =>
      val allLines: List[Seq[Line]] = a :: b

      val inits = allLines.filter(i => i.isInstanceOf[RamInitialisation])
      val code = allLines.filter(i => !i.isInstanceOf[RamInitialisation])
      val reorganised = (inits ++ code).flatten
      reorganised
  }
}