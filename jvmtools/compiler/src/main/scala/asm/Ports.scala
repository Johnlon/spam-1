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

    def values: Seq[ReadPort] = Seq(Gamepad1, Gamepad2, Random, Timer1, Timer2, Parallel)

    object Random extends ReadPort(0)
    object Gamepad1 extends ReadPort(1)
    object Gamepad2 extends ReadPort(2)
    object Timer1 extends ReadPort(3)
    object Timer2 extends ReadPort(4)
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

    def values: Seq[WritePort] = Seq(Timer1, Timer2, Parallel)

    object Timer1 extends WritePort(3)
    object Timer2 extends WritePort(4)
    object Parallel extends WritePort(7)
  }

}
