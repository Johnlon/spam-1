import Mode.{DIRECT, _}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AssemblerTest extends AnyFlatSpec with Matchers {

  "Assembler" should "compile EQU const" in {
    val code = "CONST:    EQU ($10 + 1) ; some arbitrarily complicated constant expression\nEND"
    val asm = new Assembler()
    val roms = asm.assemble(code)
    val consts = asm.labels

    assertLabel(asm, "CONST", Some(17))
    roms.size shouldBe 0
  }

  "it" should "compile reg assign immed dec" in {
    val code = "REGA=17\nEND"
    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control.A, REGISTER, 0, 17)
  }

  "it" should "compile reg assign immed hex" in {
    val code = "REGA=$11\nEND"
    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control.A, REGISTER, 0, 17)
  }

  "it" should "compile reg assign immed expr" in {
    val code = "REGA=($11+%1+2+@7)\nEND"
    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control.A, REGISTER, 0, 27)
  }

  "it" should "compile reg assign reg" in {
    val code = List(
      "REGA=REGB",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.PASS_A, TDevice.REGA, ADevice.REGB, BDevice.NU, Control.A, REGISTER, 0, 0)
  }

  "it" should "compile reg assign forward" in {
    val code = List(
      "REGA=:LABEL",
      "LABEL: REGB=$ff",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 2
    decode(roms(0)) shouldBe(AluOp.PASS_B, TDevice.REGA, ADevice.NU, BDevice.IMMED, Control.A, REGISTER, 0, 1)
    decode(roms(1)) shouldBe(AluOp.PASS_B, TDevice.REGB, ADevice.NU, BDevice.IMMED, Control.A, REGISTER, 0, 255)
  }

  "it" should "compile A+KONST setflags" in {
    val code = List(
      "REGB=REGC A_PLUS_B $ff S",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control.S, REGISTER, 0, 255)
  }

  "it" should "compile A+B setflags C_S" in {
    val code = List(
      "REGB=REGC A_PLUS_B REGA C_S",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control.C_S, REGISTER, 0, 0)
  }

  "it" should "compile A+[] setflags C_S" in {
    val code = List(
      "REGB=REGC A_PLUS_B [1000] C_S",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.RAM, Control.C_S, DIRECT, 1000, 0)
  }

  "it" should "compile []=A setflags C_S" in {
    val code = List(
      "[1000]=REGA C_S",
      "END").mkString("\n")

    val asm = new Assembler()
    val roms = asm.assemble(code)

    roms.size shouldBe 1
    decode(roms(0)) shouldBe(AluOp.PASS_A, TDevice.RAM, ADevice.REGA, BDevice.NU, Control.C_S, DIRECT, 1000, 0)
  }

  "it" should "compile []=[] " in {
    val code = List(
      "[1000]=[1]",
      "END").mkString("\n")

    val asm = new Assembler()
    try {
      asm.assemble(code)
    }
    catch {
      case e: RuntimeException =>
        e.getMessage shouldBe "illegal instruction: both source and target cannot both be RAM"
    }
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    asm.labels.get(s).get.getVal shouldBe i
  }

  def fromBin(str: Iterator[Char], len: Int): Int = {
    val str1 = str.take(len).mkString("")
    Integer.valueOf(str1, 2)
  }

  def decode(rom: List[String]): (AluOp, TDevice, ADevice, BDevice, Control, Mode, Int, Int) = {
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
      TDevice.valueOf(t),
      ADevice.valueOf(a),
      BDevice.valueOf(b),
      Control.valueOf(cond, f),
      m,
      addr,
      immed
    )
  }
}