package asm

import asm.Mode._
import org.junit.Assert.{assertEquals, fail}
import org.junit.Test

class AssemblerTest {

  @Test
  def `allow_positioning_of_data`() {
    val code = Seq(
      "A:     STR \"A\"",
      "POSN:  EQU 10",
      "POSN:  STR \"PP\"",
      "B:     STR \"B\"",
      "END"
    )

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 0, 'A'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 10, 'P'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 11, 'P'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 12, 'B'.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `LEN_and_EQU_arith`() {
    val codeTuples = Seq[(String, java.lang.Integer)](
      ("A0: EQU 0             ", 0),
      ("A1: EQU 1             ", 1),
      ("A2: EQU 255           ", 255),
      ("A3: EQU 256           ", 256),
      ("A4: EQU 65535         ", 65535),
      ("A5: EQU 65536         ", 65536),
      ("A6: EQU (65535+1)     ", 65536),
      ("A7: EQU 65535+1       ", 65536),
      ("A8: EQU $ffff-%1010+10", 65535),
      ("A9: EQU -1            ", -1),
      ("AA: EQU 'A' + 'B'     ", 65 + 66),

      ("B0: EQU :A1+1+2     ", 4),
      ("B1: EQU :A3+1+2     ", 259),
      ("B2: EQU :A4+1+2     ", 65538),
      ("B3: EQU len(:B0)+3+4", 8),

      ("L0: EQU len(:A0)    ", 1),
      ("L1: EQU len(:A1)    ", 1),
      ("L2: EQU len(:A2)    ", 1),
      ("L3: EQU len(:A3)    ", 2),
      ("L4: EQU len(:A4)    ", 2),
      ("L5: EQU len(:A5)    ", 3),
      ("END", null)
    )

    val asm = new Assembler()

    assertEqualsList(Seq(), instructions(codeTuples.map(_._1), asm))

    val results = codeTuples.filter(_._2 != null).map { x =>
      val v = asm.labels(x._1.split(":")(0))
      val actual = v.getVal.get.value
      if (actual == x._2) (true, s"${x._1} = ${x._2}")
      else (false, s"${x._1} = ${x._2} expected but got $actual")
    }
    val errCount = results.count(!_._1)
    if (errCount > 0) {
      fail("found errors: in results\n" + results.mkString("\n"))
    }
  }

  @Test
  def `EQU_const`() {
    val code = Seq("CONST:    EQU ($10 + 1) ; some arbitrarily complicated constant expression", "END")

    val asm = new Assembler()

    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONST", Some(17))
  }

  @Test
  def `EQU_CHAR`() {
    val code = Seq("CONSTA:    EQU 'A'",
      "CONSTB: EQU :CONSTA+1",
      "END")

    val asm = new Assembler()
    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONSTA", Some(65))
    assertLabel(asm, "CONSTB", Some(66))
  }

  @Test
  def `REGA_eq_immed_dec`() {
    val code = Seq("REGA=17", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_UART`() {
    val code = Seq("REGA=UART", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_A, TDevice.REGA, ADevice.UART, BDevice.REGA, Control._A, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_RAM`() {
    val code = Seq("REGA=RAM", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `NOOP_eq_RAM`() {
    val code = Seq("NOOP = RAM", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.NOOP, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_immed_hex`() {
    val code = Seq("REGA=$11", "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 17)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_immed_expr`() {
    val code = Seq("REGA=($11+%1+2+@7)", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 27)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_REGB`() {
    val code = List(
      "REGA=REGB",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(i(AluOp.PASS_A, TDevice.REGA, ADevice.REGB, BDevice.REGA, Control._A, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_REGA__PASS_A__NU`() {
    val code = List(
      "REGA=REGA PASS_A NU",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(i(AluOp.PASS_A, TDevice.REGA, ADevice.REGA, BDevice.REGA, Control._A, REGISTER, 0, 0)),
      instructions(code, asm))
  }

  @Test
  def `REGA_eq_forward_label`() {
    val code = Seq(
      "REGA=:LABEL",
      "LABEL: REGB=$ff",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 1),
      i(AluOp.PASS_B, TDevice.REGB, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 255.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_KONST_setflags`() {
    val code = Seq(
      "REGB=REGC A_PLUS_B $ff _S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, 0, 255.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_REGA`() {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._A, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_REGA__setflags_C_S`() {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._C_S, REGISTER, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_RAM_direct__setflags_C_S`() {
    val code = Seq(
      "REGB=REGC A_PLUS_B [1000] _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.RAM, Control._C_S, DIRECT, 1000, 0)
    ), instructions(code, asm))
  }

  @Test
  def `RAM_direct__eq__REGA__setflags__C_S`() {
    val code = Seq(
      "[1000]=REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      i(AluOp.PASS_A, TDevice.RAM, ADevice.REGA, BDevice.REGA, Control._C_S, DIRECT, 1000, 0)
    ), instructions(code, asm))
  }

  @Test
  def `const_strings_to_RAM`() {
    val code = Seq(
      "STRING1: STR     \"AB\\u0000\\n\"",
      "END")

    val asm = new Assembler()
    import asm._
    val compiled = instructions(code, asm)

    assertEquals(Some(KnownByteArray(0, List(65, 66, 0, 10))), asm.labels("STRING1").getVal)

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 0, 'A'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 1, 'B'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 2, 0),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 3, '\n')
    ), compiled)
  }

  @Test
  def `compile_bytes_to_RAM`() {
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

    assertEquals(Some(KnownByteArray(0, List(1, 2, 3))), asm.labels("FIRST").getVal)
    assertEquals(Some(KnownByteArray(3, List(B_65, B_65, B_65, B_65, 255.toByte, -1, 127, 128.toByte))), asm.labels("SECOND").getVal)
    assertEquals(Some(KnownInt(3)), asm.labels("FIRST_LEN").getVal)
    assertEquals(Some(KnownInt(8)), asm.labels("SECOND_LEN").getVal)
    assertEquals(Some(KnownInt(0)), asm.labels("FIRST_POS").getVal)
    assertEquals(Some(KnownInt(3)), asm.labels("SECOND_POS").getVal)

    var pos = 0

    def nextPos = {
      pos += 1
      pos - 1
    }

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 1),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 2),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 3),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, B_65),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 255.toByte), // 255 unsigned has same bit pattern as -1 signed
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, (-1).toByte), // 255 unsigned has same bit pattern as -1 signed
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, 127.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, nextPos, -128) // unsigned 128 has sae bit pattern as -128 twos compl
    ), compiled)
  }

  @Test
  def `strings_len`() {
    val code = Seq(
      "REGA = 1", // put this ahead of the data so make sure it's not simply counting the PC then allocating addresses for data
      "MYSTR:     STR     \"AB\"", // should be at address 0
      "MYSTRLEN:  EQU len(:MYSTR)",
      "YOURSTR:   STR     \"CD\"", // should be at address 2
      "",
      "[$ff]= :MYSTRLEN",
      "[$ff]= :MYSTRLEN+1 ; foo",
      "END")

    val asm = new Assembler()
    import asm._

    val compiled = instructions(code, asm)
    assertEquals(Some(KnownInt(2)), asm.labels("MYSTRLEN").getVal)
    assertEquals(Some(KnownByteArray(0, List(65, 66))), asm.labels("MYSTR").getVal)
    assertEquals(Some(KnownByteArray(2, List(67, 68))), asm.labels("YOURSTR").getVal)

    assertEqualsList(Seq(
      i(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, 0, 1),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 0, 'A'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 1, 'B'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 2, 'C'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 3, 'D'.toByte),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 255, 2),
      i(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, 255, 3)
    ), compiled)
  }

  @Test
  def `ram_direct_eq_ram_direct_illegal`() {
    val code = List(
      "[1000]=[1]",
      "END")

    val asm = new Assembler()

    try {
      instructions(code, asm)
    }
    catch {
      case e: RuntimeException =>
        assertEqualsList("illegal instruction: target '[1000]' and source '[1]' cannot both be RAM", e.getMessage)
    }
  }

  private def instructions(code: Seq[String], asm: Assembler): Seq[(AluOp, Any, Any, Any, Control, Mode, Int, Byte)] = {
    val roms = asm.assemble(code.mkString("\n")) // comments run to end of line
    roms.map(r => asm.decode(r))
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    assertEquals(i.map(asm.KnownInt), asm.labels(s).getVal)
  }

  def assertEqualsList[T](expected: IterableOnce[T], actual: IterableOnce[T]): Unit = {
    assertEquals(expected.iterator.mkString("\n"), actual.iterator.mkString("\n"))
  }

}