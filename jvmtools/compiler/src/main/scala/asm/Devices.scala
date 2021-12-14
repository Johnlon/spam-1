package asm

trait E {
  def enumName = {
    this.getClass.getSimpleName.replaceAll("\\$", "")
  }

  override def toString = enumName
}

trait Devices {
  self: Knowing =>

  sealed trait BExpression

  sealed trait BOnlyDevice extends BExpression with E

  object BOnlyDevice {
    def values: Seq[BOnlyDevice] = BDevice.values.collect{ case b: BOnlyDevice => b}
  }

  sealed class BDevice private(val id: Int) extends BExpression with E

  object BDevice {

    def valueOf(id: Int): BDevice = {
      values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown BDevice " + id))
    }

    def values: Seq[BDevice] = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM, NU, VRAM, PORT)

    object REGA extends BDevice(0)

    object REGB extends BDevice(1)

    object REGC extends BDevice(2)

    object REGD extends BDevice(3)

    object MARLO extends BDevice(4)

    object MARHI extends BDevice(5)

    object IMMED extends BDevice(6) with BOnlyDevice

    object RAM extends BDevice(7) with BOnlyDevice
    object NU extends BDevice(8)

    object VRAM extends BDevice(9) with BOnlyDevice
    object PORT extends BDevice(10) with BOnlyDevice
    object RAND extends BDevice(11) with BOnlyDevice
    object CLOCK extends BDevice(12) with BOnlyDevice

  }

  sealed class ADevice private(val id: Int) extends E

  object ADevice {

    def valueOf(id: Int): ADevice = {
      values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown ADevice " + id))
    }

    def values: Seq[ADevice] = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, UART, NU)

    object REGA extends ADevice(0)

    object REGB extends ADevice(1)

    object REGC extends ADevice(2)

    object REGD extends ADevice(3)

    object MARLO extends ADevice(4)

    object MARHI extends ADevice(5)

    object UART extends ADevice(6)

    object NU extends ADevice(REGA.id)
  }

  sealed trait TExpression

  sealed class TDevice private(val id: Int) extends TExpression with E

  object TDevice {

    def valueOf(id: Int): TDevice = {
      values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown TDevice " + id))
    }

    def values = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, UART, NOOP, RAM, HALT, VRAM, PORTSEL, PORT, PCHITMP, PCLO, PC)

    object REGA extends TDevice(0)

    object REGB extends TDevice(1)

    object REGC extends TDevice(2)

    object REGD extends TDevice(3)

    object MARLO extends TDevice(4)

    object MARHI extends TDevice(5)

    object UART extends TDevice(6)

    object RAM extends TDevice(7)

    object HALT extends TDevice(8)
    object VRAM extends TDevice(9)
    object PORT extends TDevice(10)
    object PORTSEL extends TDevice(11)

    object NOOP extends TDevice(12)

    object PCHITMP extends TDevice(13)

    object PCLO extends TDevice(14)

    object PC extends TDevice(15)
  }

  case class RamDirect(addr: Know[KnownInt]) extends TExpression with BOnlyDevice {
    override def toString: String = s"[${addr}]"
  }
}

