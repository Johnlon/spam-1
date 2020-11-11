import ADevice.ADevice
import AluOp.{AluOp, PASSA}
import BDevice.BDevice
import BOnlyDevice.BOnlyDevice
import Line.{pc, pcVal}
import TDevice.TDevice

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

object BDevice extends Enumeration {
  type BDevice = Value
  val REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM, NU = Value
}

object BOnlyDevice extends Enumeration {
  type BOnlyDevice = Value
  val RAM, NU = Value
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
  println(pc + " : " + this.getClass.getName)

  def incPc(): Unit = {
    pcVal += 1;
  }

  val thisPc = pc
}

object Line {
  private var pcVal = 0

  def pc: Int = pcVal
}


class Instruction(tdev: TDevice, adev: ADevice, bdev: BDevice, bonlydev: BOnlyDevice, aluop: AluOp, flags: Boolean, address: Long, immed: Byte)
  extends Line {
  incPc()
}

case class EquInstruction(variable: String, value: Know)
  extends Line

case class InstructionA(tdev: TDevice, adev: ADevice, flags: Boolean)
  extends Instruction(tdev, adev, BDevice.NU, BOnlyDevice.NU, AluOp.PASSA, flags, 0, 0)
    with Line

case class InstructionB(tdev: TDevice, bdev: BOnlyDevice, flags: Boolean)
  extends Instruction(tdev, ADevice.NU, BDevice.NU, bdev, AluOp.PASSA, flags, 0, 0)
    with Line

case class InstructionAB(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, flags: Boolean)
  extends Instruction(tdev, adev, bdev, BOnlyDevice.NU, aluop, flags, 0, 0)
    with Line

case class InstructionImmed(tdev: TDevice, immed: Byte, flags: Boolean)
  extends Instruction(tdev, ADevice.NU, BDevice.IMMED, BOnlyDevice.NU, AluOp.PASSA, flags, 0, immed)
    with Line

case class Comment(comment: String)
  extends Line

case class Label(name: String, location: Int)
  extends Line

case class LabelAddr(name: String)

case class Value()

trait Know {
  def symname: String

  def k: Option[Know] // none - means unknowable
}

case class UniKnowable(symname: String, a: () => Know, op: Int => Int) extends Know {
  def k: Option[Know] = a().k match {
    case Some(Known(_, v)) =>
      Some(Known(symname, op(v)))
    case None =>
      None
    case _ =>
      Some(this)
  }
}

case class BiKnowable(symname: String, a: () => Know, b: () => Know, op: (Int, Int) => Int) extends Know {
  def k = {
    (a().k, b().k) match {
      case (Some(Known(_, av)), Some(Known(_, bv))) =>
        Some(Known(symname, op(av, bv)))
      case (None, _) =>
        None
      case (_, None) =>
        None
      case _ =>
        Some(this)
    }
  }
}

case class Known(symname: String, v: Int) extends Know {
  def k = Some(this)
}

case class Knowable(symname: String) extends Know {
  var i = 0

  def k = {
    i += 1
    if (i % 3 == 1)
      Some(this)
    else
      Some(Known(symname, 2))
  }
}

case class Unknown(symname: String) extends Know {
  def k = None
}

object T extends App {
  val e1 = Unknown("u")
  //val e1 = Knowable("e1")
  val e2 = Known("e2", 1)

  val b = BiKnowable("b", () => e1, () => e2, (a, b) => a + b)
  val b2 = BiKnowable("b", () => e1, () => b, (a, b) => a + b)
  val v = UniKnowable("a", () => b2, _ + 1)

  v.k match {
    case Some(v) => println("got " + v)
    case _ => println("unknown")
  }

  v.k match {
    case Some(v) => println("got " + v)
    case _ => println("unknown")
  }
  v.k match {
    case Some(v) => println("got " + v)
    case _ => println("unknown")
  }
}


// expr "calculator" code taken from https://www.scala-lang.org/api/2.12.8/scala-parser-combinators/scala/util/parsing/combinator/RegexParsers.html
trait InstructionParser extends DeviceParser {
  def name: Parser[String] = "[a-zA-Z][a-zA-Z0-9_]*".r ^^ {
    case a => a
  }

  val vars = mutable.Map.empty[String, Int]
  val labels = mutable.Map.empty[String, Int]

  def dec: Parser[Int] = """\d+""".r ^^ {
    _.toInt
  }

  def hex: Parser[Int] = "$" ~ """[0-9a-hA-H]+""".r ^^ { case _ ~ v => Integer.valueOf(v, 16) }

  def bin: Parser[Int] = "%" ~ """[01]+""".r ^^ { case _ ~ v => Integer.valueOf(v, 2) }

  def oct: Parser[Int] = "@" ~ """[0-7]+""".r ^^ { case _ ~ v => Integer.valueOf(v, 8) }

  def labelAddr: Parser[Know] =  ":" ~ name ^^ {
    case _ ~ v => Knowable(v)
  }

  def hiByte: Parser[Know] = "<" ~ expr ^^ { case _ ~ e => (e >> 8) & 0xff }

  def loByte: Parser[Know] = ">" ~ expr ^^ { case _ ~ e => e & 0xff }

  def factor: Parser[Know] = dec | hex | bin | oct | "(" ~> expr <~ ")" | hiByte | loByte;

  def expr: Parser[Know] = factor ~ rep("*" ~ factor | "/" ~ factor | "&" ~ factor | "|" ~ factor | "+" ~ log(expr)("Plus term") | "-" ~ log(expr)("Minus term")) ^^ {
    case number ~ list => list.foldLeft(number) {
      case (x, "*" ~ y) => x * y
      case (x, "/" ~ y) => x / y
      case (x, "+" ~ y) => x + y
      case (x, "-" ~ y) => x - y
      case (x, "&" ~ y) => x & y
      case (x, "|" ~ y) => x | y
    }
  }

  def label: Parser[Label] = name ~ ":" ^^ {
    case n ~ _ => Label(n, pc)
  }

  def comment: Parser[Comment] = ";" ~ "[^\r\n]*".r ^^ {
    case _ ~ a => Comment(a)
  }

  def eqInstruction: Parser[EquInstruction] = name ~ ":" ~ "EQU" ~ expr ^^ {
    case a ~ _ ~ _ ~ b => EquInstruction(a, b)
  }

  def aInstruction: Parser[InstructionA] = tdev ~ "=" ~ adev ~ "('S)?".r ^^ {
    case a ~ _ ~ b ~ f => InstructionA(a, b, flags(f))
  }

  def bInstruction: Parser[InstructionB] = tdev ~ "=" ~ bdevonly ~ "('S)?".r ^^ {
    case a ~ _ ~ b ~ f => InstructionB(a, b, flags(f))
  }

  def immedInstruction: Parser[InstructionImmed] = tdev ~ "=" ~ expr ~ "('S)?".r ^^ {
    case a ~ _ ~ b ~ f => InstructionImmed(a, b.toByte, flags(f))
  }

  def abInstruction: Parser[InstructionAB] = tdev ~ "=" ~ adev ~ aluop ~ bdev ^^ {
    case a ~ _ ~ b ~ op ~ c =>
      InstructionAB(a, b, c, op, flags(""))
  }

  private def flags(f: String) = {
    f == "'S"
  }

  def line: Parser[Line] = eqInstruction | abInstruction | aInstruction | bInstruction | immedInstruction | comment | label ^^ {
      case x => x
  }

  def lines: Parser[List[Line]] = line ~ (line *) ~ "END" ^^ {
    case a ~ b ~ _ => a :: b
  }

}

object Assembler extends InstructionParser {

  def main(args: Array[String]) = {
    val code = List(
      "A: EQU >$214"
      , "REGA=REGA PLUS REGB"
      , "REGA=REGB"
      , "REGA=RAM"
      , "REGA=>$123'S"
      , "label1:"
      , "label2: REGA=REGB'S"
      , "label2: REGA=<:label1"
      , "; comment"
      , "REGB=REGA"
      , "END"
    ).mkString("\n")

    parse(lines, code) match {
      case Success(matched, _) => {
        println("MATCHED : ")
        matched.foreach(println)
      }
      case msg: Failure => {
        println(s"FAILURE: $msg ")
        System.exit(0)
      }
      case msg: Error => {
        println(s"ERROR: $msg")
        System.exit(0)
      }
    }
  }
}
