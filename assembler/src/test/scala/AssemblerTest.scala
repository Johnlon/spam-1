import Mode.{DIRECT, _}
import org.junit.runner.RunWith
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AssemblerTest extends AnyFlatSpec with Matchers {

  it should "allow positioning of data" in {
    val code = Seq(
      "POSN: EQU 10",
      "POSN: STR \"AB\"",
      "END"
    )

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
        i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 10, 'A'.toByte),
        i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 11, 'B'.toByte)
    )
  }

  it should "compile EQU const" in {
    val code = Seq("CONST:    EQU ($10 + 1) ; some arbitrarily complicated constant expression", "END")

    val asm = new Assembler()
    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONST", Some(17))
  }

  it should "compile EQU 'A'" in {
    val code = Seq("CONSTA:    EQU 'A'",
      "CONSTB: EQU :CONSTA+1",
      "END")

    val asm = new Assembler()
    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONSTA", Some(65))
    assertLabel(asm, "CONSTB", Some(66))
  }

  "it" should "compile reg assign immed dec" in {
    val code = Seq("REGA=17", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    )
  }

  it should "compile REGA=UART" in {
    val code = Seq("REGA=UART", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_A, TDevice.REGA, ADevice.UART, BDevice.REGA, Control._A, REGISTER, 0, 0)
    )
  }

  it should "compile REGA assign RAM" in {
    val code = Seq("REGA=RAM", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, 0, 0)
    )
  }

  it should "compile NOOP assign RAM" in {
    val code = Seq("NOOP = RAM", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_B, TDevice.NOOP, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, 0, 0)
    )
  }

  "it" should "compile reg assign immed hex" in {
    val code = Seq("REGA=$11", "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    )
  }


  "it" should "compile reg assign immed expr" in {
    val code = Seq("REGA=($11+%1+2+@7)", "END")
    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 27))
  }

  "it" should "compile reg assign reg" in {
    val code = List(
      "REGA=REGB",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(i(AluOp.PASS_A, TDevice.REGA, ADevice.REGB, BDevice.REGA, Control._A, REGISTER, 0, 0))
  }

  it should "compile REGA=REGA PASS_A NU" in {
    val code = List(
      "REGA=REGA PASS_A NU",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(i(AluOp.PASS_A, TDevice.REGA, ADevice.REGA, BDevice.REGA, Control._A, REGISTER, 0, 0))
  }

  "it" should "compile reg assign forward" in {
    val code = Seq(
      "REGA=:LABEL",
      "LABEL: REGB=$ff",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 1),
      i(AluOp.PASS_B, TDevice.REGB, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 255.toByte)
    )
  }

  "it" should "compile A+KONST setflags" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B $ff _S",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, 0, 255.toByte)
    )
  }

  "it" should "compile A+B" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._A, REGISTER, 0, 0)
    )
  }

  "it" should "compile A+B setflags C_S" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._C_S, REGISTER, 0, 0)
    )
  }

  "it" should "compile A+[] setflags C_S" in {
    val code = Seq(
      "REGB=REGC A_PLUS_B [1000] _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.RAM, Control._C_S, DIRECT, 1000, 0)
    )
  }

  "it" should "compile []=A setflags _C_S" in {
    val code = Seq(
      "[1000]=REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    instructions(code, asm) shouldBe Seq(
      i(AluOp.PASS_A, TDevice.RAM, ADevice.REGA, BDevice.REGA, Control._C_S, DIRECT, 1000, 0)
    )
  }

  "it" should "compile strings to RAM" in {
    val code = Seq(
      "STRING1: STR     \"AB\\u0000\\n\"",
      "END")

    val asm = new Assembler()
    import asm._

    val compiled = instructions(code, asm)

    asm.labels("STRING1").getVal shouldBe Some(KnownByteArray(0, Seq(65, 66, 0, 10)))

    compiled shouldBe Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 0, 'A'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 1, 'B'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 2, 0),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 3, '\n')
    )
  }

  it should "compile bytes to RAM" in {
    val code = Seq(
      "FIRST:       BYTES     [ 1,2,3 ]",
      "SECOND:      BYTES     [ 'A', 65, $41, %01000001, 255 , -1, 127, 128 ]",
      "FIRST_LEN:   EQU       len(:FIRST)",
      "SECOND_LEN:  EQU       len(:SECOND)",
      "FIRST_POS:   EQU       :FIRST",
      "SECOND_POS:  EQU       :SECOND",
      "END")

    val asm = new Assembler()
    import asm._

    val compiled = instructions(code, asm)

    val B_65 = 65.toByte

    asm.labels("FIRST").getVal shouldBe Some(KnownByteArray(0, Seq(1,2,3)))
    asm.labels("SECOND").getVal shouldBe Some(KnownByteArray(3, Seq(B_65, B_65, B_65, B_65, 255.toByte, -1, 127, 128.toByte)))
    asm.labels("FIRST_LEN").getVal shouldBe Some(KnownInt(3))
    asm.labels("SECOND_LEN").getVal shouldBe Some(KnownInt(8))
    asm.labels("FIRST_POS").getVal shouldBe Some(KnownInt(0))
    asm.labels("SECOND_POS").getVal shouldBe Some(KnownInt(3))

    var pos = 0
    def nextPos = {
      pos += 1
      pos-1
    }
    compiled shouldBe Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 1),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 2),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 3),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 255.toByte), // 255 unsigned has same bit pattern as -1 signed
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, (-1).toByte), // 255 unsigned has same bit pattern as -1 signed
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, (127).toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, -128) // unsigned 128 has sae bit pattern as -128 twos compl
    )
  }

  "it" should "compile strings len" in {
    val code = Seq(
      "MYSTR:     STR     \"AB\"",
      "MYSTRLEN:  EQU len(:MYSTR)",
      "",
      "[$ff]= :MYSTRLEN",
      "[$ff]= :MYSTRLEN+1 ; foo",
      "END")

    val asm = new Assembler()
    import asm._

    val compiled = instructions(code, asm)
    asm.labels("MYSTRLEN").getVal shouldBe Some(KnownInt(2))
    asm.labels("MYSTR").getVal shouldBe Some(KnownByteArray(0, Seq(65, 66)))

    compiled shouldBe Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 0, 'A'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 1, 'B'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 255, 2),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 255, 3)
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
    roms.map(r => asm.decode(r))
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    asm.labels(s).getVal shouldBe i.map(asm.KnownInt)
  }

}