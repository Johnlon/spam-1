
import Mode.{DIRECT, Mode}

trait Lines {
  self: Knowing with Devices =>

  var instNo = 0
  var pc = 0

  sealed trait Line {

    val instructionAddress = pc
    //println(s"""${Line.instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} : ${this.getClass.getName}  : ${this.str}""")

    def str: String

    def unresolved: Boolean

    instNo += 1
  }

  case class Instruction(tdev: TDevice, adev: ADevice, bdev: BDevice, aluop: AluOp, control: Option[Control], amode: Mode, address: Know[KnownInt], immed: Know[KnownInt])
    extends Line {

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

    def str = {
      val fstr = control.toString
      s"""${instNo.formatted("%03d")} pc:${instructionAddress.formatted("%04x")} ${this.getClass.getName}(${tdev} = ${adev} ${aluop}$fstr ${bdev})  amode:${amode}   addr:${address.eval}  immed:${immed.eval}"""
    }

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

    private def bitString(control: Control): String = {
      val (cond, flag) = (control.cond, control.setflag)
      ((cond << 1) | flag).toBinaryString
    }

    def roms: List[String] = {
      val sAluop = bits("aluop", aluop.id.toBinaryString, 5)
      val sTDev = bits("tdev", tdev.id.toBinaryString, 4)
      val sADev = bits("adev", adev.id.toBinaryString, 3)
      val sBDev = bits("bdev", bdev.id.toBinaryString, 3)
      val sFlags = bits("control", bitString(control.getOrElse(Control._A)), 5)
      val sNU = bits("nu", "", 3)
      val sMode = if (amode == DIRECT) "1" else "0"
      val sAddress = bits("address", address.getVal.map(_.toBinaryString).getOrElse(""), 16)
      val sImmed = bits("immed", immed.getVal.map { v =>
        v.v & 0xff
      }.map(_.toBinaryString).getOrElse(""), 8)

      val i = sAluop + sTDev + sADev + sBDev + sFlags + sNU + sMode + sAddress + sImmed
      if (i.length != 48) throw new RuntimeException(s"sw error: expected 48 bits but got ${i.size} in '${i}''")

      val list = i.grouped(8).toList
      list
    }

    private def bits(name: String, value: String, len: Int): String = {
      val pad = "0" * Math.max(0, (len - value.length))
      val string = pad + value

      if (string.length > len) {
        System.err.println(this)
        System.err.println(s"${name} field length ${len}(${Math.pow(2, len).toInt}) exceeded by value ${value}(${Integer.parseInt(value, 2)})")
        sys.exit(1)
      }

      string
    }
  }

  case class EquInstruction(variable: String, value: Know[KnownInt]) extends Line {

    rememberKnown(variable, value)

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

  //  case class StrInstruction(variable: String, value: Seq[Byte]) extends Line {
  //
  //    def str = {
  //      s"""${this.getClass.getName} ${variable} = ${value}"""
  //    }
  //
  //    def unresolved = {
  //      false
  //    }
  //  }
  //

  case class BlankLine() extends Line {
    override def str: String =
      s"""${this.getClass.getName}"""

    override def unresolved: Boolean = false
  }

  case class Comment(comment: String) extends Line {
    def str = {
      s"""${this.getClass.getName} ${comment}"""
    }

    def unresolved = false
  }

  case class Label(name: String) extends Line {
    def str = {
      s"""${this.getClass.getName} ${name}"""
    }

    def unresolved = false
  }

}