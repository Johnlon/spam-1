
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

    import BDevice._

    def values: Seq[BOnlyDevice] = Seq(IMMED, RAM)
  }

  sealed class BDevice private(val id: Int) extends BExpression with E

  object BDevice {

    def valueOf(id: Int): BDevice = {
      if (id > 7 || id < 0) throw new RuntimeException("unknown BDevice " + id);

      values.filter(_.id == id).head
    }

    def values: Seq[BDevice] = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM)

    object REGA extends BDevice(0)

    object REGB extends BDevice(1)

    object REGC extends BDevice(2)

    object REGD extends BDevice(3)

    object MARLO extends BDevice(4)

    object MARHI extends BDevice(5)

    object IMMED extends BDevice(6) with BOnlyDevice

    object RAM extends BDevice(7) with BOnlyDevice

    val NU = REGA
  }

  sealed class ADevice private(val id: Int) extends E

  object ADevice {

    def valueOf(id: Int): ADevice = {
      if (id > 7 || id < 0) throw new RuntimeException("unknown ADevice " + id);

      values.filter(_.id == id).head
    }

    def values: Seq[ADevice] = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, UART, NU)

    object REGA extends ADevice(0)

    object REGB extends ADevice(1)

    object REGC extends ADevice(2)

    object REGD extends ADevice(3)

    object MARLO extends ADevice(4)

    object MARHI extends ADevice(5)

    object UART extends ADevice(6)

    object NU extends ADevice(7)

  }

  sealed trait TExpression

  sealed class TDevice private(val id: Int) extends TExpression with E

  object TDevice {

    def valueOf(id: Int): TDevice = {
      val first = values.filter(_.id == id).headOption
      if (first.isEmpty) throw new RuntimeException("unknown TDevice " + id)
      first.get
    }

    def values = Seq(REGA, REGB, REGC, REGD, MARLO, MARHI, UART, RAM, PCHITMP, PCLO, PC)

    object REGA extends TDevice(0)

    object REGB extends TDevice(1)

    object REGC extends TDevice(2)

    object REGD extends TDevice(3)

    object MARLO extends TDevice(4)

    object MARHI extends TDevice(5)

    object UART extends TDevice(6)

    object RAM extends TDevice(7)

    object NOOP extends TDevice(12)

    object PCHITMP extends TDevice(13)

    object PCLO extends TDevice(14)

    object PC extends TDevice(15)

  }

  case class RamDirect(addr: Know) extends TExpression with BOnlyDevice {
    override def toString: String = s"[${addr}]"
  }
}

