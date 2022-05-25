package asm

import asm.AddressMode.{DIRECT, Mode}

trait Lines {
  self: Knowing with Devices =>

  var lineNo = 0
  var pc = 0

  sealed trait Line {

    //println(s"""${Line.instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} : ${this.getClass.getName}  : ${this.str}""")

    def unresolved: Boolean

    lineNo += 1
  }

  final case class Instruction(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, condition: Condition, amode: Mode, address: Know[KnownInt], immed: Know[KnownInt])
    extends Line {

    val instructionAddress = pc
    pc += 1

    (bdev, immed) match {
      case (BDevice.IMMED, _: Irrelevant) =>
        sys.error("sw error : b=IMMED but immed=Irrelevant")
      case _ =>
    }

    (amode, address) match {
      case (DIRECT, _: Irrelevant) =>
        sys.error("sw error : amode=DIRECT but address=Irrelevant")
      case _ =>
    }

    (tdev, bdev) match {
      case (TDevice.RAM, BDevice.RAM) =>
        sys.error(s"sw error : target and source cannot both be RAM")
      case _ =>
    }

    (tdev, adev) match {
      case (TDevice.UART, ADevice.UART) =>
        sys.error("sw error : target and source cannot both be UART")
      case _ =>
    }

    //    def str = {
    //      val fstr = control.toString
    //      s"""${instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} ${this.getClass.getName}(${tdev} = ${adev} ${aluop}$fstr ${bdev})  amode:${amode}   addr:${address.eval}  immed:${immed.eval}"""
    //    }

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
      !isResolved
    }

    def encode: List[String] = {
      val sAluop = bits("aluop", aluop.id.toBinaryString, 5)
      val sTDev = bits("tdev", tdev.id.toBinaryString, 5)
      val sADev = bits("adev", adev.id.toBinaryString, 3)
      val sBDev = bits("bdev", bdev.id.toBinaryString, 4)
      val sFlags = bits("control", bitString(condition.cond), 5)
      val sCondMode = condition.mode.bit
      val sAddrMode = if (amode == DIRECT) "1" else "0"
      val sAddress = bits("address", address.getVal.map(_.toBinaryString).getOrElse(""), 16)
      val sImmed = bits("immed", immed.getVal.map { v =>
        v.value & 0xff
      }.map(_.toBinaryString).getOrElse(""), 8)

      val sBDev_2_0 = sBDev.takeRight(3);
      val sBDev_3 = sBDev.take(1);

      val sTDev_3_0 = sTDev.takeRight(4);
      val sTDev_4 = sTDev.take(1);

      // CHECK THIS !!!!!! is T and B extra bit the right way round
      val i = sAluop + sTDev_3_0 + sADev + sBDev_2_0 + sFlags + sCondMode + sBDev_3 + sTDev_4 + sAddrMode + sAddress + sImmed
      if (i.length != 48) throw new RuntimeException(s"sw error: expected 48 bits but got ${i.size} in '${i}''")

      val list = i.grouped(8).toList
      list
    }

    private def bitString(control: Control): String = {
      val (cond, flag) = (control.cond, control.setflag)
      ((cond << 1) | flag.bit).toBinaryString
    }

    private def bits(name: String, value: String, len: Int): String = {
      val pad = "0" * Math.max(0, (len - value.length))
      val string = pad + value

      if (string.length > len) {
        //System.err.println(this)
        sys.error(s"${name} field max value ${len}(${Math.pow(2, len).toInt}) exceeded for value ${value}(${Integer.parseInt(value, 2)})")
      }

      string
    }
  }

//
//  case class TextSegment() extends Line with Positional {
//
//    def str = {
//      s"""${this.getClass.getName}"""
//    }
//
//    def unresolved = {
//          false
//    }
//  }
//
  case class EquInstruction(variable: String, value: IsKnowable[KnownInt]) extends Line {

    def str = {
      s"""${this.getClass.getName} ${variable} = ${value}"""
    }

    def unresolved = {
      value.getVal match {
        case None =>
          true
        case _ =>
          false
      }
    }
  }

  /* these are repositionable to the start of the code so that the ram gets initialised */
  case class RamInitialisation(inst: List[Line]) extends scala.collection.immutable.Seq[Line] {

    def str = {
      s"${this.getClass.getName} = $inst"
    }

    def unresolved = {
        !inst.filter(i => i.unresolved).isEmpty
    }

    override def length: Int = inst.length
    override def iterator: Iterator[Line] = inst.iterator

    override def apply(i: Int): Line = inst(i)
  }

  case class Debug(comment: String) extends Line {
    def unresolved = false
  }

  case class Comment(comment: String) extends Line {
    def unresolved = false
  }

  case class Label(name: String, pos: Int) extends Line {

    def unresolved = false

    override def toString = s"Label($name @ $pos)"
  }
}