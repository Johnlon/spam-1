import Mode.{DIRECT, _}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AssemblerTest extends AnyFlatSpec with Matchers {

  "Assembler" should "compile EQU const" in {
    val code = Seq("CONST:    EQU ($10 + 1) ; some arbitrarily complicated constant expression", "END")

    val asm = new Assembler()
    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONST", Some(17))
  }

  "it" should "compile reg assign immed dec" in {
    val code = Seq("REGA=17", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      (AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    )
  }

  "it" should "compile reg assign immed hex" in {
    val code = Seq("REGA=$11", "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      (AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    )
  }


  "it" should "compile reg assign immed expr" in {
    val code = Seq("REGA=($11+%1+2+@7)", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq((AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control._A, REGISTER, 0, 27))
  }

  "it" should "compile reg assign reg" in {
    val code = List(
      "REGA=REGB",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq((AluOp.PASS_A, TDevice.REGA, ADevice.REGB, BDevice.NU, Control._A, REGISTER, 0, 0))
  }

  "it" should "compile reg assign forward" in {
    val code = Seq(
      "REGA=:LABEL",
      "LABEL: REGB=$ff",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      (AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control._A, REGISTER, 0, 1),
      (AluOp.PASS_B, TDevice.REGB, ADevice.NU, BDevice.IMMED, Control._A, REGISTER, 0, 255)
    )
  }

  "it" should "compile A+KONST setflags" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B $ff _S",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      (AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, 0, 255)
    )
  }

  "it" should "compile A+B" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      (AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._A, REGISTER, 0, 0)
    )
  }

  "it" should "compile A+B setflags C_S" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._
    instructions(code, asm) shouldBe Seq(
      (AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._C_S, REGISTER, 0, 0)
    )
  }

  "it" should "compile A+[] setflags C_S" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B [1000] _C_S",
      "END")

    val asm = new Assembler()
    import asm._
    instructions(code, asm) shouldBe Seq(
      (AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.RAM, Control._C_S, DIRECT, 1000, 0)
    )
  }

  "it" should "compile []=A setflags _C_S" in {
    val code = Seq(
      "[1000]=REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._
    instructions(code, asm) shouldBe Seq(
      (AluOp.PASS_A, TDevice.RAM, ADevice.REGA, BDevice.NU, Control._C_S, DIRECT, 1000, 0)
    )
  }

  "it" should "compile []=[] " in {
    val code = List(
      "[1000]=[1]",
      "END")

    val asm = new Assembler()

    try {
      instructions(code, asm)
    }
    catch {
      case e: RuntimeException =>
        e.getMessage shouldBe "illegal instruction: target '[1000]' and source '[1]' cannot both be RAM"
    }
  }


  private def instructions(code: Seq[String], asm: Assembler) = {
    val roms = asm.assemble(code.mkString("\n")) // comments run to end of line

    val inst = roms.map(decode(asm, _))
    inst
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    asm.labels.get(s).get.getVal shouldBe i
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }

  def decode[A <: Assembler](asm: A, rom: List[String]): (AluOp, A#TDevice, A#ADevice, A#BDevice, Control, Mode, Int, Int) = {
    val str = rom.mkString("");
    val sitr = str.iterator.buffered

    val op = fromBin(sitr, 5)
    val t = fromBin(sitr, 4)
    val a = fromBin(sitr, 3)
    val b = fromBin(sitr, 3)
    val cond = fromBin(sitr, 4)
    val f = fromBin(sitr, 1)
    sitr.take(3).mkString("")
    val m = if (fromBin(sitr, 1) == 1) Mode.DIRECT else Mode.REGISTER
    val addr = fromBin(sitr, 16)
    val immed = fromBin(sitr, 8)

    (
      AluOp.valueOf(op),
      asm.TDevice.valueOf(t),
      asm.ADevice.valueOf(a),
      asm.BDevice.valueOf(b),
      Control.valueOf(cond, f),
      m,
      addr,
      immed
    )
  }
}