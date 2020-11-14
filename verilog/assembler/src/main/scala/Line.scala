import ADevice.ADevice
import AluOp.AluOp
import BDevice.BDevice
import Control.Control
import Know.remember
import Line.{incPc, instNo, pc}
import Mode.{DIRECT, Mode}
import TDevice.TDevice

object Line {
  private var pcVal = 0
  var instNo = 0

  def incPc(): Unit = {
    pcVal += 1;
  }

  def pc: Int = pcVal
}

trait Line {

  val instructionAddress = pc
  //println(s"""${Line.instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} : ${this.getClass.getName}  : ${this.str}""")

  def str: String

  def unresolved: Boolean

  instNo += 1
}

case class Instruction(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, control: Control, amode: Mode, address: Know, immed: Know)
  extends Line {

  (bdev, immed) match {
    case (BDevice.IMMED, Irrelevant()) =>
      sys.error("sw error : b=IMMED but immed=Irrelevant")
    case _ =>
  }

  (amode, immed) match {
    case (DIRECT, Irrelevant()) => sys.error("sw error : amode=DIRECT but address=Irrelevant")
    case _ =>
  }

  (tdev, bdev) match {
    case (TDevice.RAM, BDevice.RAM) => sys.error("sw error : source and target cannot both be RAM")
    case _ =>
  }

  (tdev, adev) match {
    case (TDevice.UART, ADevice.UART) => sys.error("sw error : source and target cannot both be UART")
    case _ =>
  }

  def str = {
    val fstr = control.toString
    s"""${Line.instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} ${this.getClass.getName}(${tdev} = ${adev} ${aluop}$fstr ${bdev})  amode:${amode}   addr:${render(address)}  immed:${render(immed)}"""
  }

  private def render(k: Know) = {
    val o = k.eval
    s"${o}"
  }

  incPc()

  def unresolved = {
    val Aok = address.getVal match {
      case Some(_) => true
      case _ => amode != DIRECT
    }
    val Iok = immed.getVal match {
      case Some(_) => true
      case _ => bdev != BDevice.IMMED
    }

    val isResolved = Aok & Iok
    if (!isResolved) {
      println("wait")
    }

    !isResolved
  }

  def fctrl(control: Control): String = {
    val (ctrl, flag) = control match {
      case Control.A => (0, 1)
      case Control.S => (0, 0)
      case Control.A_S => (0, 0)
      case Control.C => (1, 1)
      case Control.Z => (2, 1)
      case Control.O => (3, 1)
      case Control.N => (4, 1)
      case Control.EQ => (5, 1)
      case Control.NE => (6, 1)
      case Control.GT => (7, 1)
      case Control.LT => (8, 1)
      case Control.DI => (9, 1)
      case Control.DO => (10, 1)
      case Control.C_S => (1, 0)
      case Control.Z_S => (2, 0)
      case Control.O_S => (3, 0)
      case Control.N_S => (4, 0)
      case Control.EQ_S => (5, 0)
      case Control.NE_S => (6, 0)
      case Control.GT_S => (7, 0)
      case Control.LT_S => (8, 0)
      case Control.DI_S => (9, 0)
      case Control.DO_S => (10, 0)
    }

    (((ctrl << 1) | flag).toBinaryString)
  }


  def bytes: List[String] = {
    val sAluop = bits("aluop", aluop.id.toBinaryString, 5)
    val sTDev = bits("tdev", tdev.id.toBinaryString, 4)
    val sADev = bits("adev", adev.id.toBinaryString, 3)
    val sBDev = bits("bdev", bdev.id.toBinaryString, 3)
    val sFlags = bits("control", fctrl(control), 5)
    val sNU = bits("nu", "", 3)
    val sMode = if (amode == DIRECT) "1" else "0"
    val sAddress = bits("address", address.getVal.map(_.toBinaryString).getOrElse(""), 16)
    val sImmed = bits("immed", immed.getVal.map(_.toBinaryString).getOrElse(""), 8)

    val i = sAluop + sTDev + sADev + sBDev + sFlags + sNU + sMode + sAddress + sImmed

    i.grouped(8).toList
  }

  private def bits(name: String, value: String, len: Int): String = {
    val pad = "0" * (len - value.length)
    val string = pad + value

    if (string.length > len) {
      System.err.println(this)
      System.err.println(s"${name} field length ${len}(${Math.pow(2,len).toInt}) exceeded by value ${value}(${Integer.parseInt(value,2)})")
      sys.exit(1)
    }

    string
  }
}

case class EquInstruction(variable: String, value: Know)
  extends Line {

  remember(variable, value)

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

case class RamDirect(addr: Know)

