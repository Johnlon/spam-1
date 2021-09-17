package asm

object Ports {

  sealed class ReadPort private(val id: Int) extends E {
    def asmPortName = s"PORT_RD_${enumName}"
  }

  object ReadPort {

    def valueOf(id: Int): ReadPort = {
      values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown ReadPort " + id))
    }

    def valueOf(code: String): ReadPort = {
      values.find(c => c.enumName == code).getOrElse(throw new RuntimeException("unknown ReadPort " + code))
    }

    def values: Seq[ReadPort] = Seq(Gamepad1, Gamepad2, Random, Parallel)

    object Gamepad1 extends ReadPort(0)
    object Gamepad2 extends ReadPort(1)
    object Random extends ReadPort(2)
    object Parallel extends ReadPort(7)
  }

  sealed class WritePort private(val id: Int) extends E {
    def asmPortName = s"PORT_WR_${enumName}"
  }

  object WritePort {

    def valueOf(id: Int): WritePort = {
      values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown WritePort " + id))
    }

    def valueOf(code: String): WritePort = {
      values.find(c => c.enumName == code).getOrElse(throw new RuntimeException("unknown WritePort " + code))
    }

    def values: Seq[WritePort] = Seq(BeepDuration, BeepPitch, Parallel)

    object BeepDuration extends WritePort(0)
    object BeepPitch extends WritePort(1)
    object Parallel extends WritePort(7)
  }

}
