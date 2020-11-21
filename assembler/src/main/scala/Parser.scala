import Mode.{DIRECT, REGISTER}

import scala.language.postfixOps
import scala.util.parsing.combinator._

object Mode extends Enumeration {
  type Mode = Value
  val DIRECT, REGISTER = Value
}

// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait InstructionParser extends JavaTokenParsers {
  self: Lines with Knowing with Devices =>

  //  override val whiteSpace =  """[ \t\f\x0B]++""".r // whitespace not including line endings

  def enumToParser[A <: E](e: Seq[A]): Parser[A] = e map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def aluop: Parser[AluOp] = enumToParser(AluOp.values)

  def adev: Parser[ADevice] = enumToParser(ADevice.values)

  def bdev: Parser[BDevice] = enumToParser(BDevice.values)

  def bdevonly: Parser[BOnlyDevice] = enumToParser(BOnlyDevice.values)

  def tdev: Parser[TDevice] = enumToParser(TDevice.values)

  def controlCode: Parser[Control] = enumToParser(Control.values)

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ { case a => a }

  def pcref: Parser[Know[KnownInt]] = """.""" ^^ { case v => Known("pc", pc) }

  def dec: Parser[Know[KnownInt]] = """\d+""".r ^^ { case v => Known("", v.toInt) }

  def char: Parser[Know[KnownInt]] = "'" ~> ".".r <~ "'" ^^ { case v =>
    val i = v.codePointAt(0)
    if (i>255) throw new RuntimeException(s"asm error: character '${v}' codepoint ${i} is outside the 0-255 range")
    Known("", i.toByte)
  }

  def hex: Parser[Know[KnownInt]] = "$" ~ """[0-9a-hA-H]+""".r ^^ { case _ ~ v => Known("$" + v, Integer.valueOf(v, 16)) }

  def bin: Parser[Know[KnownInt]] = "%" ~ """[01]+""".r ^^ { case _ ~ v => Known("%" + v, Integer.valueOf(v, 2)) }

  def oct: Parser[Know[KnownInt]] = "@" ~ """[0-7]+""".r ^^ { case _ ~ v => Known("@" + v, Integer.valueOf(v, 8)) }

  def labelAddr: Parser[Know[KnownInt]] = ":" ~ name ^^ { case _ ~ v => forwardReference(v) }

  def labelLen: Parser[Know[KnownInt]] = "len(" ~ ":" ~> name <~ ")" ^^ {
    case v =>
      val v1: Know[KnownValue] = forwardReference(v).eval
      val k : Know[KnownInt] = v1 match {
        case Known(name, knownVal) => knownVal match {
          case KnownInt(_) => Known(s"len(:${v})", KnownInt(1))
          case KnownByteArray(b) => Known(s"len(:${v})", KnownInt(b.length))
        }
        case x => sys.error(s"asm error : ${v} unknown")
      }
      k
  }

  def label: Parser[Label] = name ~ ":" ^^ {
    case n ~ _ => {
      rememberValue(n, KnownInt(pc))
      Label(n)
    }
  }

  def hiByte: Parser[Know[KnownInt]] = "<" ~> expr ^^ { case e: Know[KnownInt] => UniKnowable[KnownInt](() => e, i => KnownInt((i.v >> 8) & 0xff), "HI<") }

  def loByte: Parser[Know[KnownInt]] = ">" ~> expr ^^ { case e: Know[KnownInt] => UniKnowable[KnownInt](() => e, i => KnownInt(i.v & 0xff), "LO>") }

  def factor: Parser[Know[KnownInt]] = (labelLen | char | pcref | dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte | labelAddr)

  def expr: Parser[Know[KnownInt]] = factor ~ rep("*" ~ factor | "/" ~ factor | "&" ~ factor | "|" ~ factor | "+" ~ factor | "-" ~ factor) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "*" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ * _, "*")
      case (x, "+" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ + _, "+")
      case (x, "/" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ / _, "/")
      case (x, "-" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ - _, "-")
      case (x, "&" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ & _, "&")
      case (x, "|" ~ y) => BiKnowable[KnownInt, KnownInt, KnownInt](() => x, () => y, _ | _, "|")
    }
  }

  def ramDirect: Parser[RamDirect] = "[" ~ expr ~ "]" ^^ { case _ ~ v ~ _ => RamDirect(v) }

  def comment: Parser[Comment] = ";" ~> ".*".r ^^ {
    case a => Comment(a)
  }

  def targets = (tdev | ramDirect)

  def bdevices = (bdev | ramDirect)

  def bdeviceOrRamDirect = (bdevonly | ramDirect)

  def eqInstruction: Parser[EquInstruction] = (name <~ ":" ~ "EQU") ~ expr ^^ {
    case a ~ b => {
      EquInstruction(a, b)
    }
  }

  var dataAddress = 0


  // amended vs 'stringLiteral' to include the short form \0
  def quotedString: Parser[String] = ("\"" + """([^"\x01-\x1F\x7F\\]|\\[\\'"0bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\"").r ^^ {
    case s =>
      val withoutQuotes = s.stripPrefix("\"").stripSuffix("\"")
      val str = org.apache.commons.text.StringEscapeUtils.unescapeJava(withoutQuotes)
      str
  }

  def strInstruction: Parser[List[Line]] = (name <~ ":" ~ "STR") ~ quotedString ^^ {
    case a ~ b => {
      val bytes = b.getBytes("UTF-8").toSeq

      rememberValue(a, KnownByteArray(bytes))

      Label(a) +: bytes.map { c => {
        val ni = inst(RamDirect(Known("", dataAddress)), ADevice.NU, AluOp.PASS_B, BDevice.IMMED, Some(Control._A), Known("", c))
        dataAddress += 1
        ni
      }
      }.toList
    }
  }

  private def inst(t: TExpression, a: ADevice, op: AluOp, b: BExpression, f: Option[Control], immed: Know[KnownInt]): Instruction = {
    (t, b) match {
      case (t: TDevice, b: BDevice) =>
        Instruction(t, a, b, op, f, REGISTER, Irrelevant(), immed)
      case (t: TDevice, RamDirect(addr)) =>
        Instruction(t, a, BDevice.RAM, op, f, DIRECT, addr, immed)
      case (RamDirect(addr), b: BDevice) =>
        Instruction(TDevice.RAM, a, b, op, f, DIRECT, addr, immed)
      case (RamDirect(_), RamDirect(_)) =>
        sys.error(s"illegal instruction: target '${t}' and source '${b}' cannot both be RAM")
    }
  }

  def abInstruction: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ bdevices ~ (controlCode ?) ^^ {
    case t ~ a ~ op ~ b ~ f =>
      inst(t, a, op, b, f, Irrelevant())
  }

  def abInstructionShortform: Parser[Line] = (targets <~ "=") ~ adev ~ shortOps ~ bdevices ~ (controlCode ?) ^^ {
    case t ~ a ~ op ~ b ~ f =>
      inst(t, a, op, b, f, Irrelevant())
  }

  def abInstructionImmed: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ expr ~ (controlCode ?) ^^ {
    case t ~ a ~ op ~ immed ~ f =>
      inst(t, a, op, BDevice.IMMED, f, immed)
  }

  def abInstructionShortformImmed: Parser[Line] = (targets <~ "=") ~ adev ~ shortOps ~ expr ~ (controlCode ?) ^^ {
    case t ~ a ~ op ~ immed ~ f =>
      inst(t, a, op, BDevice.IMMED, f, immed)
  }

  def bInstruction: Parser[Line] = (targets <~ "=") ~ bdeviceOrRamDirect ~ (controlCode ?) ^^ {
    case t ~ b ~ f => {
      inst(t, ADevice.NU, AluOp.PASS_B, b, f, Irrelevant())
    }
  }

  def bInstructionImmed: Parser[Line] = (targets <~ "=") ~ expr ~ (controlCode ?) ^^ {
    case t ~ immed ~ f => {
      inst(t, ADevice.NU, AluOp.PASS_B, BDevice.IMMED, f, immed)
    }
  }

  def aInstruction: Parser[Line] = (targets <~ "=") ~ adev ~ (controlCode ?) ^^ {
    case t ~ a ~ f => {
      inst(t, a, AluOp.PASS_A, BDevice.NU, f, Irrelevant())
    }
  }

  // reverse sorted to put longer operators ahead of shorter ones otherwise shorter ones gobble
  def shortOps: Parser[AluOp] = AluOp.values.filter(_.isAbbreviated).sortBy(x => x.enumName).reverse.toList map { m =>
    literal(m.abbrev) ^^^ m
  } reduceLeft {
    _ | _
  }

  def line: Parser[List[Line]] = (strInstruction | eqInstruction | bInstruction | abInstructionImmed | abInstructionShortform | abInstructionShortformImmed | abInstruction | aInstruction | bInstructionImmed | comment | label) ^^ {
    case x: List[_] => x.asInstanceOf[List[Line]]
    case x: Line => List(x)
  }

  def lines: Parser[List[Line]] = line ~ (line *) <~ "END" ^^ {
    case a ~ b =>
      (a ++ b.flatten)
  }
}
