import Mode.{DIRECT, REGISTER}

import scala.language.postfixOps
import scala.util.parsing.combinator._

object Mode extends Enumeration {
  type Mode = Value
  val DIRECT, REGISTER = Value
}

// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait InstructionParser extends JavaTokenParsers {
  self : Lines with Knowing with Devices =>

//  override val whiteSpace =  """[ \t\f\x0B]++""".r // whitespace not including line endings

  def aluop: Parser[AluOp] = AluOp.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def adev: Parser[ADevice] = ADevice.values.toList map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def bdev: Parser[BDevice] = BDevice.values.toList map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def bdevonly: Parser[BOnlyDevice] = BOnlyDevice.values.toList map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def tdev: Parser[TDevice] = TDevice.values.toList map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def controlCode: Parser[Control] = Control.values.toList map { m =>
    literal(m.enumName) ^^^ m
  } reduceLeft {
    _ | _
  }

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ { case a => a }

  def dec: Parser[Know] = """\d+""".r ^^ { case v => Known("", v.toInt) }

  def hex: Parser[Know] = "$" ~ """[0-9a-hA-H]+""".r ^^ { case _ ~ v => Known("$" + v, Integer.valueOf(v, 16)) }

  def bin: Parser[Know] = "%" ~ """[01]+""".r ^^ { case _ ~ v => Known("%" + v, Integer.valueOf(v, 2)) }

  def oct: Parser[Know] = "@" ~ """[0-7]+""".r ^^ { case _ ~ v => Known("@" + v, Integer.valueOf(v, 8)) }

  def labelAddr: Parser[Know] = ":" ~ name ^^ { case _ ~ v => knowable(v) }

  def label: Parser[Label] = name ~ ":" ^^ {
    case n ~ _ => {
      remember(n, pc)
      Label(n)
    }
  }

  def hiByte: Parser[Know] = "<" ~> expr ^^ { case  e => UniKnowable(() => e, i => ((i >> 8) & 0xff), "HI<") }

  def loByte: Parser[Know] = ">" ~> expr ^^ { case  e => UniKnowable(() => e, i => i & 0xff, "LO>") }

  def factor: Parser[Know] = (dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte | labelAddr)

  def expr: Parser[Know] = factor ~ rep("*" ~ factor | "/" ~ factor | "&" ~ factor | "|" ~ factor | "+" ~ factor | "-" ~ factor) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "*" ~ y) => BiKnowable(() => x, () => y, _ * _, "*")
      case (x, "+" ~ y) => BiKnowable(() => x, () => y, _ + _, "+")
      case (x, "/" ~ y) => BiKnowable(() => x, () => y, _ / _, "/")
      case (x, "-" ~ y) => BiKnowable(() => x, () => y, _ - _, "-")
      case (x, "&" ~ y) => BiKnowable(() => x, () => y, _ & _, "&")
      case (x, "|" ~ y) => BiKnowable(() => x, () => y, _ | _, "|")
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

  private def inst(t: TExpression, a: ADevice, op: AluOp, b: BExpression, f: Option[Control], immed: Know) = {
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

  def abInstruction: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ bdevices ~  (controlCode ?) ^^ {
    case t ~ a ~ op ~ b ~ f =>
      inst(t, a, op, b, f, Irrelevant())
  }

  def abInstructionShortform: Parser[Line] = (targets <~ "=") ~ adev ~ shortOps ~ bdevices ~ (controlCode?) ^^ {
    case t ~ a ~ op ~ b ~ f =>
      inst(t,a,op,b,f,Irrelevant())
  }

  def abInstructionImmed: Parser[Line] = (targets <~ "=") ~ adev ~ aluop ~ expr ~  (controlCode ?) ^^ {
    case t ~  a ~ op ~ immed ~ f =>
      inst(t,a,op,BDevice.IMMED,f, immed)
  }

  def abInstructionShortformImmed: Parser[Line] = (targets <~ "=") ~ adev ~ shortOps ~ expr ~ (controlCode?) ^^ {
    case t ~ a ~ op ~ immed ~ f =>
      inst(t,a,op,BDevice.IMMED,f, immed)
  }

  def bInstruction: Parser[Line] = (targets <~ "=") ~ bdeviceOrRamDirect ~  (controlCode ?) ^^ {
    case t ~ b ~ f => {
      inst(t,ADevice.NU,AluOp.PASS_B,b,f, Irrelevant())
    }
  }

  def bInstructionImmed: Parser[Line] = (targets <~ "=") ~ expr ~  (controlCode ?) ^^ {
    case t ~ immed ~ f => {
      inst(t,ADevice.NU, AluOp.PASS_B ,BDevice.IMMED ,f, immed)
    }
  }

  def aInstruction: Parser[Line] = (targets <~ "=") ~ adev ~  (controlCode ?) ^^ {
    case t ~ a ~ f => {
      inst(t,a, AluOp.PASS_A ,BDevice.NU ,f, Irrelevant())
    }
  }

  // reverse sorted to put longer operators ahead of shorter ones otherwise shorter ones gobble
  def shortOps: Parser[AluOp] = AluOp.values.filter(_.isAbbreviated).sorted.reverse.toList map { m =>
    literal(m.abbrev) ^^^ m
  } reduceLeft {
    _ | _
  }

  def line: Parser[Line] = (eqInstruction | bInstruction | abInstructionImmed | abInstructionShortform | abInstructionShortformImmed | abInstruction | aInstruction  | bInstructionImmed | comment | label)  ^^ {
    case x =>
      x
  }

  def lines: Parser[List[Line]] = line ~ (line *) <~ "END" ^^ {
    case a ~ b => a :: b
  }

}
