import ADevice.{ADevice, NU}
import AluOp.{AluOp, PASSA}
import BDevice.{BDevice, IMMED}
import BOnlyDevice.BOnlyDevice
import Line.{pc, pcVal}
import TDevice.{RAM, TDevice}

trait Know {
  def eval: Option[Know] // none - means unknowable
  def getVal: Option[Int]
}


case class UniKnowable(a: () => Know, op: Int => Int) extends Know {
  def eval: Option[Know] = a().eval match {
    case Some(Known(v)) =>
      Some(Known(op(v)))
    case None =>
      None
    case _ =>
      Some(this)
  }

  def getVal: Option[Int] = a().getVal match {
    case Some(v) =>
      Some(op(v))
    case None =>
      None
  }
}

case class BiKnowable(a: () => Know, b: () => Know, op: (Int, Int) => Int) extends Know {
  def eval: Option[Know] = {
    (a().eval, b().eval) match {
      case (Some(Known(av)), Some(Known(bv))) =>
        Some(Known(op(av, bv)))
      case (None, _) =>
        None
      case (_, None) =>
        None
      case _ =>
        Some(this)
    }
  }

  def getVal = (a().getVal, b().getVal) match {
    case (Some(av), Some(bv)) =>
      Some(op(av, bv))
    case _ =>
      None
  }
}

case class Knowable(name: String, a: () => Option[Int]) extends Know {
  def eval: Option[Know] = {
    a().map(v => Known(v)).orElse(Some(this))
  }

  def getVal = a()
}

case class Known(knownVal: Int) extends Know {
  def eval = {
    Some(this)
  }

  def getVal = Some(knownVal)
}

case class Irrelevant() extends Know {
  def eval = {
    Some(this)
  }

  def getVal = Some(0)
}

case class Unknown(name: String) extends Know {
  def eval = None

  override def getVal = None
}


import scala.collection.mutable

import scala.util.parsing.combinator._

case class Device(name: String, id: Int)

object AluOp extends Enumeration {
  type AluOp = Value
  val ZERO, PASSA, PASSB, PLUS, TIMES = Value
}

object ADevice extends Enumeration {
  type ADevice = Value
  val REGA, REGB, NU = Value
}

object BOnlyDevice extends Enumeration {
  type BOnlyDevice = Value
  val RAM, IMMED, NU = Value
}

object BDevice extends Enumeration {
  type BDevice = Value
  val REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM, NU = Value
}

object TDevice extends Enumeration {
  type TDevice = Value
  val REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM = Value
}

trait DeviceParser extends JavaTokenParsers {
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
}


trait Line {
  val instructionAddress = pc
  println(instructionAddress + " : " + this.getClass.getName + " : " + this.str)

  def incPc(): Unit = {
    pcVal += 1;
  }

  def str: String

  def unresolved: Boolean
}

object Line {
  private var pcVal = 0

  def pc: Int = pcVal
}

case class Instruction(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, flags: Boolean, directAddressing: Boolean, address: Know, immed: Know)
  extends Line {
  def str = {
    s"""location:${instructionAddress} ${this.getClass.getName}(${tdev} = ${adev} ${aluop}${if (flags) "'s" else ""} ${bdev})  amode:${if (directAddressing) "DIRECT" else "REGISTER"}   addr:${render(address)}  immed:${render(immed)}"""
  }

  private def render(k: Know) = {
    val o = k.eval
    s"""${o.flatMap(_.getVal).getOrElse("unknown")}(${o})"""
  }

  incPc()

  def unresolved = {
    val Aok = address.getVal match {
      case Some(_) => true
      case _ => !directAddressing
    }
    val Iok = immed.getVal match {
      case Some(_) => true
      case _ => bdev != IMMED
    }

    val isResolved = Aok & Iok
    if (!isResolved) {
      println("wait")
    }

    !isResolved
  }
}

case class EquInstruction(variable: String, value: Know)
  extends Line {
  def str = {
    s"""${this.getClass.getName} ${variable} = ${value}"""
  }


  def unresolved = {
    value.getVal match {
      case None => true
      case _ => false
    }
  }
}

//case class InstructionA(override tdev: TDevice, adev: ADevice, flags: Boolean)
//  extends Instruction(tdev, adev, BDevice.NU, AluOp.PASSA, flags, Irrelevant(), Irrelevant())
//
//case class InstructionB(tdev: TDevice, bdev: BOnlyDevice, flags: Boolean, immed: Know = Irrelevant())
//  extends Instruction(tdev, ADevice.NU, BDevice.withName(bdev.toString), AluOp.PASSB, flags, Irrelevant(), immed)
//
//case class InstructionAB(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, flags: Boolean)
//  extends Instruction(tdev, adev, bdev, aluop, flags, Irrelevant(), Irrelevant())

case class Comment(comment: String)
  extends Line {
  def str() = {
    s"""${this.getClass.getName} ${comment}"""
  }

  def unresolved = false
}

case class Label(name: String)
  extends Line {
  def str() = {
    s"""${this.getClass.getName} ${name}"""
  }

  def unresolved = false
}


// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait InstructionParser extends DeviceParser {

  object Know {
    val labels = mutable.Map.empty[String, Int]

    def remember(name: String, v: Int): Known = {
      labels.get(name).map(e => sys.error(s"symbol '${name}' has already defined as ${e} can't assign new value ${v}"))
      labels(name) = v
      Known(v)
    }

    def knowable(name: String): Know = {
      val maybeKnown = labels.get(name).map(Known)
      maybeKnown.getOrElse(Knowable(name, () => get(name)))
    }


    def get(name: String): Option[Int] = labels.get(name)

  }

  import Know._

  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ {
    case a => a
  }

  def dec: Parser[Know] =
    """\d+""".r ^^ {
      case v => Known(v.toInt)
    }

  def hex: Parser[Know] = "$" ~ """[0-9a-hA-H]+""".r ^^ { case _ ~ v => Known(Integer.valueOf(v, 16)) }

  def bin: Parser[Know] = "%" ~ """[01]+""".r ^^ { case _ ~ v => Known(Integer.valueOf(v, 2)) }

  def oct: Parser[Know] = "@" ~ """[0-7]+""".r ^^ { case _ ~ v => Known(Integer.valueOf(v, 8)) }

  def labelAddr: Parser[Know] = ":" ~ name ^^ {
    case _ ~ v =>
      knowable(v)
  }

  def label: Parser[Label] = name ~ ":" ^^ {
    case n ~ _ => {
      remember(n, pc)
      Label(n)
    }
  }


  def hiByte: Parser[Know] = "<" ~ expr ^^ { case _ ~ e => UniKnowable(() => e, i => ((i >> 8) & 0xff)) }

  def loByte: Parser[Know] = ">" ~ expr ^^ { case _ ~ e => UniKnowable(() => e, i => i & 0xff) }

  def factor: Parser[Know] = dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte | labelAddr

  def expr: Parser[Know] = factor ~ rep("*" ~ factor | "/" ~ factor | "&" ~ factor | "|" ~ factor | "+" ~ log(expr)("Plus term") | "-" ~ log(expr)("Minus term")) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "*" ~ y) => BiKnowable(() => x, () => y, _ * _)
      case (x, "+" ~ y) => BiKnowable(() => x, () => y, _ + _)
      //      case (x, "/" ~ y) => x / y
      //      case (x, "-" ~ y) => x - y
      //      case (x, "&" ~ y) => x & y
      //      case (x, "|" ~ y) => x | y
    }
  }

  case class RamDirect(addr: Know)

  def ramDirect: Parser[RamDirect] = "[" ~ expr ~ "]" ^^ { case _ ~ v ~ _ => RamDirect(v) }

  def comment: Parser[Comment] = ";" ~ "[^\r\n]*".r ^^ {
    case _ ~ a => Comment(a)
  }

  def targets = tdev | ramDirect

  def bdevices = bdev | ramDirect

  def bdeviceOrRamDirect = bdevonly | ramDirect

  def eqInstruction: Parser[EquInstruction] = name ~ ":" ~ "EQU" ~ expr ^^ {
    case a ~ _ ~ _ ~ b => EquInstruction(a, b)
  }

  def aInstruction: Parser[Instruction] = targets ~ "=" ~ adev ~ "('S)?".r ^^ {
    case t ~ _ ~ a ~ f => {
      t match {
        case t: TDevice =>
          Instruction(t, a, BDevice.NU, AluOp.PASSA, flags(f), false, Irrelevant(), Irrelevant())
        case RamDirect(addr) =>
          Instruction(RAM, a, BDevice.NU, AluOp.PASSA, flags(f), true, addr, Irrelevant())
      }
    }
  }

  def bInstruction: Parser[Instruction] = targets ~ "=" ~ bdeviceOrRamDirect ~ "('S)?".r ^^ {
    case t ~ _ ~ b ~ f => {
      (t, b) match {
        case (t: TDevice, b: BOnlyDevice) =>
          Instruction(t, NU, BDevice.withName(b.toString), AluOp.PASSB, flags(f), false, Irrelevant(), Irrelevant())
        case (t: TDevice, RamDirect(addr)) =>
          Instruction(t, NU, BDevice.RAM, AluOp.PASSB, flags(f), true, addr, Irrelevant())
        case (RamDirect(addr), b: BOnlyDevice) =>
          Instruction(TDevice.RAM, NU, BDevice.RAM, AluOp.PASSB, flags(f), true, addr, Irrelevant())
        case (RamDirect(_), RamDirect(_)) =>
          sys.error("illegal instruction: source and target cannot both be RAM")
      }
    }
  }

  def immedInstruction: Parser[Instruction] = targets ~ "=" ~ expr ~ "('S)?".r ^^ {
    case t ~ _ ~ immed ~ f => {
      t match {
        case t: TDevice =>
          Instruction(t, NU, BDevice.IMMED, AluOp.PASSB, flags(f), false, Irrelevant(), immed)
        case RamDirect(addr) =>
          Instruction(RAM, NU, BDevice.withName(immed.toString), AluOp.PASSB, flags(f), true, addr, immed)
      }
    }

  }

  def abInstruction: Parser[Instruction] = targets ~ "=" ~ adev ~ aluop ~ "('S)?".r ~ bdevices ^^ {
    case t ~ _ ~ a ~ op ~ f ~ b =>
      (t, b) match {
        case (t: TDevice, b: BDevice) =>
          Instruction(t, a, BDevice.withName(b.toString), op, flags(f), false, Irrelevant(), Irrelevant())
        case (t: TDevice, RamDirect(addr)) =>
          Instruction(t, a, BDevice.RAM, op, flags(f), true, addr, Irrelevant())
        case (RamDirect(addr), b: BDevice) =>
          Instruction(TDevice.RAM, a, b, op, flags(f), true, addr, Irrelevant())
        case (RamDirect(_), RamDirect(_)) =>
          sys.error("illegal instruction: source and target cannot both be RAM")
      }
  }


  def abInstructionImmed: Parser[Instruction] = targets ~ "=" ~ adev ~ aluop ~ "('S)?".r ~ expr ^^ {
    case t ~ _ ~ a ~ op ~ f ~ immed =>
      t match {
        case t: TDevice =>
          Instruction(t, a, BDevice.IMMED, op, flags(f), false, Irrelevant(), immed)
        case RamDirect(addr) =>
          Instruction(RAM, a, BDevice.IMMED, op, flags(f), true, addr, immed)
      }
  }


  private def flags(f: String) = {
    f == "'S"
  }

  def line: Parser[Line] = eqInstruction | abInstructionImmed | abInstruction | aInstruction | bInstruction | immedInstruction | comment | label ^^ {
    case x => x
  }

  def lines: Parser[List[Line]] = line ~ (line *) ~ "END" ^^ {
    case a ~ b ~ _ => a :: b
  }

}
