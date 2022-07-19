package asm

import asm.AddressMode._
import org.junit.jupiter.api.Assertions.{assertEquals, fail}
import org.junit.jupiter.api.Test
import verification.HaltCode
import verification.Verification.verifyRoms

// FIXME - check if any initialised or uninitialised ranges overlap
// TODO review logic where datalocn pointer is reset by a prev statement

class AssemblerTest {

  @Test
  def labelAddressesArentMessedUpByMovingDataBlocksToStart(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        | b8 : RESERVE 8
        | ; PADDING SO THAT
        | ; TEST
        | PCHITMP = <:good
        | PCLO = >:good
        |
        | ; PADDING SO THAT IF THE JUMP ENDS UP HERE BY ACCIDENT BECAUSE MOVING DATA AROUND RUINS THE OFFSETS THEN TEST FAILS
        | ; IF OFFSETS ARE MESSED UP BY MOVING DATA TO START OF PROG THEN IT WILL JUMP SHORT OF THE good: LABEL AND END UP ON HALT=1
        | HALT=1
        | HALT=1
        | HALT=1
        |
        | ; should get here
        | good:
        |
        | ; and data should have been initialised
        | REGA=[66]
        | REGA=REGA - $BB _S
        |
        | HALT=$aa _Z ; << SUCCESS
        |
        | HALT=2
        | HALT=2
        | HALT=2
        |
        | ;WRITE TO RAM REGISTER  !!!!!! FIXME
        | [:b4] = 44
        |
        | ; THIS MUST BE INITIALISED ADDRESS 66 = BYTE BB BUT THAT MEANS IT MUST BE EXECUTED BEFORE ALL OTHER INSTRUCTIONS
        | ; IF ARRANGING THAT MESSES UP ADDRESSES THEN WE WANT TO KNOW ABOUT IT
        | data : EQU 66
        | data : BYTES [$BB]
        | b2 : RESERVE 2
        | b4 : RESERVE 4
        | b6 : RESERVE 6
        | d0: BYTES [$DD, @77, %10101010 ]
        | d1: BYTES [$DD]
        |END
        """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1))
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0, 0xaa)),
      timeout = 200,
      roms = roms);
  }

  @Test
  def bytes(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |data1:
        |    BYTES [0,1,2,3]
        |    STR "£"  ; pound
        |    STR "John\0"
        |    BYTES [4,5,6,7]
        |data2:
        |    BYTES [8,9,10]
        |
        |HALT  = [:data1 + 4]
        |END
        """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1))
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0, '£')),
      timeout = 200,
      roms = roms);
  }

  @Test
  def `can I put vars at end`(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |
        |MARLO=0
        |MARHI=0
        |
        |REGA=[:INTA+3]
        |NOOP = REGA A_MINUS_B_SIGNEDMAG [:INTB+3] _S
        |
        |REGA=[:INTA+2]
        |NOOP = REGA A_MINUS_B           [:INTB+2] _EQ_S
        |
        |REGA=[:INTA+1]
        |NOOP = REGA A_MINUS_B           [:INTB+1] _EQ_S
        |
        |REGA=[:INTA+0]
        |NOOP = REGA A_MINUS_B           [:INTB+0] _EQ_S
        |
        |REGA=0
        |REGA = REGA A_OR_B 1 _LT
        |REGA = REGA A_OR_B 2 _GT
        |REGA = REGA A_OR_B 4 _NE
        |REGA = REGA A_OR_B 8 _EQ
        |
        |HALT = REGA
        |
        |HALT = 1 _LT
        |HALT = 2 _GT
        |HALT = 3 _NE
        |HALT = 4 _EQ
        |HALT = 5
        |
        |; these are data not instructions so they can be anywhere in the script
        |INTA:       EQU       1
        |INTA:       BYTES     [ 255,255,255,255 ]
        |INTB:       BYTES     [ 0,0,0,0 ]
        |STRING:     STR       "HELLO"
        |
        |END
        """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1))
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0, 1 | 4)),
      timeout = 200,
      roms = roms);
  }

  @Test
  def `passingStatusFlagAcrossOpsToCreateAggregate4ByteCompare`(): Unit = {

    val cmpEq =
      """
        |                   ;  LSB       MSB
        |A:       BYTES     [ 255,255,255,255 ]
        |B:       BYTES     [ 0,0,0,0 ]
        |
        |MARLO=0
        |MARHI=0
        |
        |REGA=[:A+3]
        |NOOP = REGA A_MINUS_B_SIGNEDMAG [:B+3] _S
        |
        |REGA=[:A+2]
        |NOOP = REGA A_MINUS_B           [:B+2] _EQ_S
        |
        |REGA=[:A+1]
        |NOOP = REGA A_MINUS_B           [:B+1] _EQ_S
        |
        |REGA=[:A+0]
        |NOOP = REGA A_MINUS_B           [:B+0] _EQ_S
        |
        |REGA=0
        |REGA = REGA A_OR_B 1 _LT
        |REGA = REGA A_OR_B 2 _GT
        |REGA = REGA A_OR_B 4 _NE
        |REGA = REGA A_OR_B 8 _EQ
        |
        |HALT = REGA
        |
        |HALT = 1 _LT
        |HALT = 2 _GT
        |HALT = 3 _NE
        |HALT = 4 _EQ
        |HALT = 5
        |
        |END
        |"""

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0, 1 | 4)),
      timeout = 200,
      roms = roms);
  }
  /*
    @Test
    def `cppJmp`(): Unit = {
      // test the injection of the jmp macro
      val code = Seq(
        "REGA = 0",
        "label: ",
        "REGB = 1",
        "jmp(label)",
        "REGC = 2",
        "END"
      )

      val asm = new Assembler()
      import asm._

      assertEqualsList(Seq(
        inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0),
        inst(AluOp.PASS_B, TDevice.REGB, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 1),
        inst(AluOp.PASS_B, TDevice.PCHITMP, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0),
        inst(AluOp.PASS_B, TDevice.PC, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 1),
        inst(AluOp.PASS_B, TDevice.REGC, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 2),
      ), instructions(code, asm))
    }
   */

  @Test
  def `allow_positioning_of_data`(): Unit = {
    val code = Seq(
      "A:     STR \"A\"",
      "POSN:  EQU 10", // sets the value of the label POSN to be address 10
      "POSN:  STR \"PP\"", // sets the data at POSN to be the data "PP"
      "LENPP: EQU len(:POSN)", // sets asm var LENPP to be the length of the data POSN
      "X:     BYTES [ len(:POSN), :LENPP ]", // sets twp data vytes , each the length of the data at POSN
      "B:     STR \"B\"",
      "END"
    )

    val asm = new Assembler()
    import asm._

    val actual = instructions(code, asm)
    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 0, 'A'.toByte), // A
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 10, 'P'.toByte), // DATA='P'
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 11, 'P'.toByte), // DATA='P'
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 12, 2), // byte = length of "PP"
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 13, 2), // byte = length of "PP" via LENPP
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 14, 'B'.toByte)
    ), actual)
  }

  @Test
  def `LEN_and_EQU_arith`(): Unit = {
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
  def `EQU_const`(): Unit = {
    val code = Seq("CONSTNAME:    EQU ($10 + 1) ; some arbitrarily complicated constant expression", "END")

    val asm = new Assembler()

    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONSTNAME", Some(17))
  }

  @Test
  def `EQU_CHAR`(): Unit = {
    val code = Seq("CONSTA:    EQU 'A'",
      "CONSTB: EQU :CONSTA+1",
      "END")

    val asm = new Assembler()
    asm.assemble(code.mkString("\n")) // comments run to end of line

    assertLabel(asm, "CONSTA", Some(65))
    assertLabel(asm, "CONSTB", Some(66))
  }

  @Test
  def `REGA_eq_immed_dec`(): Unit = {
    val code = Seq("REGA=17", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 17)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_UART`(): Unit = {
    val code = Seq("REGA=UART", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_A, TDevice.REGA, ADevice.UART, BDevice.NU, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_RAM`(): Unit = {
    val code = Seq("REGA=RAM", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `NOOP_eq_RAM`(): Unit = {
    val code = Seq("NOOP = RAM", "END")
    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.NOOP, ADevice.REGA, BDevice.RAM, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_immed_hex`(): Unit = {
    val code = Seq("REGA=$11", "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 17)
    ), instructions(code, asm))
  }

  @Test
  def `REGA_eq_immed_expr`(): Unit = {
    val code = Seq("REGA=($11+%1+2+@7)", "END")
    val asm = new Assembler()
    import asm._

    val value = instructions(code, asm)
    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 27)
    ), value)
  }

  @Test
  def `REGA_eq_REGB`(): Unit = {
    val code = List(
      "REGA=REGB",
      "END")

    val asm = new Assembler()
    import asm._

    val actual = instructions(code, asm)
    assertEqualsList(Seq(inst(AluOp.PASS_A, TDevice.REGA, ADevice.REGB, BDevice.NU, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), actual)
  }

  @Test
  def `REGA_eq_REGA__PASS_A__NU`(): Unit = {
    val code = List(
      "REGA=REGA PASS_A NU",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(inst(AluOp.PASS_A, TDevice.REGA, ADevice.REGA, BDevice.NU, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)),
      instructions(code, asm))
  }

  @Test
  def `REGA_eq_forward_label`(): Unit = {
    val code = Seq(
      "REGA=:LABEL",
      "LABEL: REGB=$ff",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 1),
      inst(AluOp.PASS_B, TDevice.REGB, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 255.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `Not_Conditions`(): Unit = {
    val code = Seq(
      "REGB=REGC A_PLUS_B $ff ! _A_S",
      "REGB=REGC A_PLUS_B $ff ! _A",
      "REGB=REGC A_PLUS_B $ff !",
      "REGB=REGC A_PLUS_B $ff ! _S",
      "REGB=REGC A_PLUS_B $ff",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, ConditionMode.INVERT, 0, 255.toByte),
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A, REGISTER, ConditionMode.INVERT, 0, 255.toByte),
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A, REGISTER, ConditionMode.INVERT, 0, 255.toByte),
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, ConditionMode.INVERT, 0, 255.toByte),
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 255.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_KONST_setflags`(): Unit = {
    val code = Seq(
      "REGB=REGC A_PLUS_B $ff _S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.IMMED, Control._A_S, REGISTER, ConditionMode.STANDARD, 0, 255.toByte)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_REGA`(): Unit = {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_REGA__setflags_C_S`(): Unit = {
    val code = Seq(
      "REGB=REGC A_PLUS_B REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.REGA, Control._C_S, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), instructions(code, asm))
  }

  @Test
  def `REGB_eq_REGC_plus_RAM_direct__setflags_C_S`(): Unit = {
    val code = Seq(
      "REGB=REGC A_PLUS_B [1000] _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.A_PLUS_B, TDevice.REGB, ADevice.REGC, BDevice.RAM, Control._C_S, DIRECT, ConditionMode.STANDARD, 1000, 0)
    ), instructions(code, asm))
  }

  @Test
  def `RAM_direct__eq__REGA__setflags__C_S`(): Unit = {
    val code = Seq(
      "[1000]=REGA _C_S",
      "END")

    val asm = new Assembler()
    import asm._

    assertEqualsList(Seq(
      inst(AluOp.PASS_A, TDevice.RAM, ADevice.REGA, BDevice.NU, Control._C_S, DIRECT, ConditionMode.STANDARD, 1000, 0)
    ), instructions(code, asm))
  }

  @Test
  def `const_strings_to_RAM`(): Unit = {
    val code = Seq(
      "STRING1: STR     \"AB\\u0000\\n\"",
      "END")

    val asm = new Assembler()
    import asm._
    val compiled = instructions(code, asm)

    assertEquals(Some(KnownByteArray(0, List(65, 66, 0, 10))), asm.labels("STRING1").getVal)

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 0, 'A'.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 1, 'B'.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 2, 0),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 3, '\n')
    ), compiled)
  }

  @Test
  def `compile_bytes_to_RAM`(): Unit = {
    // 255 == -1
    //  == -1
    val code = Seq(
      "FIRST:       BYTES     [ 1,2,3 ]",
      "SECOND:      BYTES     [ 'A', 65, $41, %01000001, 255 , -1, -127, 0, 127, 128 ]",
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
    val minus127: Byte = (-127 & 0xff).toByte

    val value: List[Byte] = List[Byte](B_65, B_65, B_65, B_65, 255.toByte, 255.toByte, minus127, 0.toByte, 127.toByte, 128.toByte)

    assertEquals(Some(KnownByteArray(3, value)), asm.labels("SECOND").getVal)
    assertEquals(Some(KnownInt(3)), asm.labels("FIRST_LEN").getVal)
    assertEquals(Some(KnownInt(10)), asm.labels("SECOND_LEN").getVal)
    assertEquals(Some(KnownInt(0)), asm.labels("FIRST_POS").getVal)
    assertEquals(Some(KnownInt(3)), asm.labels("SECOND_POS").getVal)

    var pos = 0

    def nextPos = {
      pos += 1
      pos - 1
    }

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 1),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 2),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 3),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, B_65),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, B_65),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, B_65),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, B_65),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 255.toByte), // 255 unsigned has same bit pattern as -1 signed
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 255.toByte), // 255 unsigned has same bit pattern as -1 signed
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, (-127).toByte), // 255 unsigned has same bit pattern as -1 signed
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 0.toByte), // 255 unsigned has same bit pattern as -1 signed
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, 127.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, nextPos, -128) // unsigned 128 has sae bit pattern as -128 twos compl
    ), compiled)
  }

  @Test
  def `compile_no_bytes_to_RAM_is_illegal`(): Unit = {
    val code = Seq(
      "ILLEGAL:       BYTES   [ ]",
      "END")

    val asm = new Assembler()

    try {
      val decoded = instructions(code, asm)
      fail("expected an error")
    } catch {
      case ex: RuntimeException =>
        val err = "at least one byte but none were defined"
        if (!ex.getMessage.contains(err)) {
          sys.error("expected error : " + err)
        }
    }
  }

  @Test
  def `strings_len`(): Unit = {
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
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 0, 'A'.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 1, 'B'.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 2, 'C'.toByte),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 3, 'D'.toByte),
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 1),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 255, 2),
      inst(AluOp.PASS_B, TDevice.RAM, ADevice.REGA, BDevice.IMMED, Control._A, DIRECT, ConditionMode.STANDARD, 255, 3)
    ), compiled)
  }

  @Test
  def `ram_direct_eq_ram_direct_illegal`(): Unit = {
    val code = List(
      "[1000]=[1]",
      "END")

    val asm = new Assembler()

    try {
      instructions(code, asm)
    }
    catch {
      case e: RuntimeException =>
        val message = e.getMessage
        assertEqualsList("illegal instruction: target '[Known(1000, Name:decimal)]' and source '[Known(1, Name:decimal)]' cannot both be RAM", message)
    }
  }

  @Test
  def `REGA_eq_PORT_ID_CONST`(): Unit = {
    // these two lines are equivalent
    val code = List(
      "REGA = :PORT_RD_Gamepad2",
      "END")

    val asm = new Assembler()
    import asm._

    val assembled = instructions(code, asm)

    assertEqualsList(Seq(
      inst(AluOp.PASS_B, TDevice.REGA, ADevice.REGA, BDevice.IMMED, Control._A, REGISTER, ConditionMode.STANDARD, 0, 2)
    ), assembled)
  }

  @Test
  def `PORTSEL_AND_PORT_EQ_REGA`(): Unit = {
    // these two lines are equivalent
    val code = List(
      "PORTSEL = REGA",
      "PORT = REGA",
      "END")

    val asm = new Assembler()
    import asm._

    val assembled = instructions(code, asm)

    assertEqualsList(Seq(
      inst(AluOp.PASS_A, TDevice.PORTSEL, ADevice.REGA, BDevice.NU, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0),
      inst(AluOp.PASS_A, TDevice.PORT, ADevice.REGA, BDevice.NU, Control._A, REGISTER, ConditionMode.STANDARD, 0, 0)
    ), assembled)
  }

  private def instructions(code: Seq[String], asm: Assembler): Seq[(AluOp, Any, Any, Any, Control, AddressMode, ConditionMode, Int, Byte)] = {
    val roms: Seq[List[String]] = assemble(code, asm)
    decode(roms, asm)
  }

  private def decode(roms: Seq[List[String]], asm: Assembler) = {
    roms.map(r =>
      asm.decode(r)
    )
  }

  private def assemble(code: Seq[String], asm: Assembler) = {
    // comments run to end of line
    asm.assemble(code.mkString("\n"))
  }

  def assertLabel(asm: Assembler, s: String, i: Some[Int]): Unit = {
    assertEquals(i.map(asm.KnownInt), asm.labels(s).getVal)
  }

  def assertEqualsList[T](expected: IterableOnce[T], actual: IterableOnce[T]): Unit = {
    assertEquals(expected.iterator.mkString("\n"), actual.iterator.mkString("\n"))
  }

}