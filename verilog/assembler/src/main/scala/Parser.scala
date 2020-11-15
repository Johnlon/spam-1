import AluOp._
import Mode.{DIRECT, REGISTER}

import scala.language.postfixOps
import scala.util.parsing.combinator._

// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait InstructionParser extends JavaTokenParsers {
  self : Lines with Knowing =>

  def aluop: Parser[AluOp] = AluOp.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def adev: Parser[ADevice] = ADevice.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def bdev: Parser[BDevice] = BDevice.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def bdevonly: Parser[BOnlyDevice] = BOnlyDevice.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def tdev: Parser[TDevice] = TDevice.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def controlCode: Parser[Control] = Control.values.toList map { m =>
    literal(m.toString) ^^^ m
  } reduceLeft {
    _ | _
  }

  def control: Parser[Control] = (controlCode ?) ^^ {
    case Some(c) => c
    case None => Control.A
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

  def hiByte: Parser[Know] = "<" ~ expr ^^ { case _ ~ e => UniKnowable(() => e, i => ((i >> 8) & 0xff), "<") }

  def loByte: Parser[Know] = ">" ~ expr ^^ { case _ ~ e => UniKnowable(() => e, i => i & 0xff, ">") }

  def factor: Parser[Know] = dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte | labelAddr

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

  def comment: Parser[Comment] = ";" ~ "[^\r\n]*".r ^^ {
    case _ ~ a => Comment(a)
  }

  def targets = tdev | ramDirect

  def bdevices = bdev | ramDirect

  def bdeviceOrRamDirect = bdevonly | ramDirect

  def eqInstruction: Parser[EquInstruction] = name ~ ":" ~ "EQU" ~ expr ^^ {
    case a ~ _ ~ _ ~ b => {
      EquInstruction(a, b)
    }
  }

  def aInstruction: Parser[Line] = targets ~ "=" ~ adev ~ control ^^ {
    case t ~ _ ~ a ~ f => {
      t match {
        case t: TDevice =>
          Instruction(t, a, BDevice.NU, AluOp.PASS_A, f, REGISTER, Irrelevant(), Irrelevant())
        case RamDirect(addr) =>
          Instruction(TDevice.RAM, a, BDevice.NU, AluOp.PASS_A, f, DIRECT, addr, Irrelevant())
      }
    }
  }

  def bInstruction: Parser[Line] = targets ~ "=" ~ bdeviceOrRamDirect ~ control ^^ {
    case t ~ _ ~ b ~ f => {
      val t1: TDevices = t
      val b1 : BOnlyDevices = b

      (t1, b1) match {
        case (t: TDevice, b: BOnlyDevice) =>
          Instruction(t, ADevice.NU, BDevice.valueOf(b.toString), AluOp.PASS_B, f, REGISTER, Irrelevant(), Irrelevant())
        case (t: TDevice, RamDirect(addr)) =>
          Instruction(t, ADevice.NU, BDevice.RAM, AluOp.PASS_B, f, DIRECT, addr, Irrelevant())
        case (RamDirect(addr), b: BOnlyDevice) =>
          Instruction(TDevice.RAM, ADevice.NU, BDevice.RAM, AluOp.PASS_B, f, DIRECT, addr, Irrelevant())
        case (RamDirect(_), RamDirect(_)) =>
          sys.error("illegal instruction: both source and target cannot both be RAM")
        case a  =>
          sys.error("illegal bInstruction args: " + a)
      }
    }
  }


  def immedInstruction: Parser[Line] = targets ~ "=" ~ expr ~ control ^^ {
    case t ~ _ ~ immed ~ f => {
      t match {
        case t: TDevice =>
          Instruction(t, ADevice.NU, BDevice.IMMED, AluOp.PASS_B, f, REGISTER, Irrelevant(), immed)
        case RamDirect(addr) =>
          Instruction(TDevice.RAM, ADevice.NU, BDevice.valueOf(immed.toString), AluOp.PASS_B, f, DIRECT, addr, immed)
      }
    }
  }

  def shortOps: Parser[AluOp] = ("+" | "-") ^^ {
    case "+" => A_PLUS_B
    case "-" => A_MINUS_B
  }

  def abInstructionShortform: Parser[Line] = targets ~ "=" ~ adev ~ shortOps ~ bdevices ~ control ^^ {
    case t ~ _ ~ a ~ op ~ b ~ f =>
      handleAB(t, a, op, b, f)
  }

  private def handleAB(t: TDevices, a: ADevice, op: AluOp, b: BDevices, f: Control) = {
    (t, b) match {
      case (t: TDevice, b: BDevice) =>
        Instruction(t, a, BDevice.valueOf(b.toString), op, f, REGISTER, Irrelevant(), Irrelevant())
      case (t: TDevice, RamDirect(addr)) =>
        Instruction(t, a, BDevice.RAM, op, f, DIRECT, addr, Irrelevant())
      case (RamDirect(addr), b: BDevice) =>
        Instruction(TDevice.RAM, a, b, AluOp.A_PLUS_B, f, DIRECT, addr, Irrelevant())
      case (RamDirect(_), RamDirect(_)) =>
        sys.error("illegal instruction: source and target cannot both be RAM")
      case a =>
        sys.error("illegal abInstruction: " + a)
    }
  }

  def abInstruction: Parser[Line] = targets ~ "=" ~ adev ~ aluop ~ bdevices ~ control ^^ {
    case t ~ _ ~ a ~ op ~ b ~ f =>
      handleAB(t, a, op, b, f)
  }


  def abInstructionImmed: Parser[Line] = targets ~ "=" ~ adev ~ aluop ~ expr ~ control ^^ {
    case t ~ _ ~ a ~ op ~ immed ~ f =>
//      handleAB(t, a, op, BDevice.IMMED, f)
      t match {
        case t: TDevice =>
          Instruction(t, a, BDevice.IMMED, op, f, REGISTER, Irrelevant(), immed)
        case RamDirect(addr) =>
          Instruction(TDevice.RAM, a, BDevice.IMMED, op, f, DIRECT, addr, immed)
      }
  }

  def line: Parser[Line] = eqInstruction | abInstructionImmed | abInstructionShortform | abInstruction | aInstruction | bInstruction | immedInstruction | comment | label ^^ {
    case x => x
  }

  def lines: Parser[List[Line]] = line ~ (line *) ~ "END" ^^ {
    case a ~ b ~ _ => a :: b
  }

}
