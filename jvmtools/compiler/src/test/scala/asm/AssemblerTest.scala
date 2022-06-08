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
  def vbccTestC1(): Unit = {

    val c =
      """
        |void halt(__reg("gpr0") char) = "\tHALT = [:gpr0]\n";
        |
        |int main() {
        |
        |    int value = 666;
        |
        |    if (value!=666) {
        |      halt(1);
        |    }
        |
        |    halt(0);
        |}
        |  END
        | """

    runTest(c, Some(HaltCode(0xffff, 0)))
  }

  private def runTest(cmpEq: String, someCode: Some[HaltCode]) = {
    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)
    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = someCode,
      timeout = 200,
      roms = roms);
  }

  @Test
  def vbccTest1(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |  [:sp]   = $ff
        |  [:sp+1] = $ff
        |  MARLO   = [:sp]
        |  MARHI   = [:sp+1]
        |	[:gpr6+0] = $9a
        |	[:gpr6+1] = $02
        |	[:gpr6+2] = $00
        |	[:gpr6+3] = $00
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |	PCHITMP = <:l4
        |	PCLO = >:l4 _EQ
        |l3:
        |	[:gpr0+0] = $01
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l4:
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |l1:
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	PCHITMP = REGA
        |
        |	gtmp1     :	RESERVE 4
        |	gtmp2     :	RESERVE 4
        |	ftmp1     :	RESERVE 8
        |	ftmp2     :	RESERVE 8
        |	gpr0      :	RESERVE 4
        |	gpr1      :	RESERVE 4
        |	gpr2      :	RESERVE 4
        |	gpr3      :	RESERVE 4
        |	gpr4      :	RESERVE 4
        |	gpr5      :	RESERVE 4
        |	gpr6      :	RESERVE 4
        |	gpr7      :	RESERVE 4
        |	gpr8      :	RESERVE 4
        |	gpr9      :	RESERVE 4
        |	gpr10     :	RESERVE 4
        |	gpr11     :	RESERVE 4
        |	gpr12     :	RESERVE 4
        |	gpr13     :	RESERVE 4
        |	gpr14     :	RESERVE 4
        |	gpr15     :	RESERVE 4
        |	fpr0      :	RESERVE 8
        |	fpr1      :	RESERVE 8
        |	fpr2      :	RESERVE 8
        |	fpr3      :	RESERVE 8
        |	fpr4      :	RESERVE 8
        |	fpr5      :	RESERVE 8
        |	fpr6      :	RESERVE 8
        |	fpr7      :	RESERVE 8
        |	fpr8      :	RESERVE 8
        |	fpr9      :	RESERVE 8
        |	fpr10     :	RESERVE 8
        |	fpr11     :	RESERVE 8
        |	fpr12     :	RESERVE 8
        |	fpr13     :	RESERVE 8
        |	fpr14     :	RESERVE 8
        |	fpr15     :	RESERVE 8
        |	sp_stash  :	RESERVE 2
        |	sp        :	RESERVE 2
        |END
        | """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0xffff, 0)),
      timeout = 200,
      roms = roms);
  }
  @Test
  def vbccTest2(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |[:sp]   = $ff
        |[:sp+1] = $ff
        |MARLO   = [:sp]
        |MARHI   = [:sp+1]
        |	[:gpr6+0] = $9a
        |	[:gpr6+1] = $02
        |	[:gpr6+2] = $00
        |	[:gpr6+3] = $00
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |	PCHITMP = <:l4
        |	PCLO    = >:l4 _EQ
        |l3:
        |	[:gpr0+0] = $01
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l4:
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |	PCHITMP = <:l6
        |	PCLO    = >:l6 _NE
        |l5:
        |	[:gpr6+0] = $e7
        |	[:gpr6+1] = $03
        |	[:gpr6+2] = $00
        |	[:gpr6+3] = $00
        |	PCHITMP = <:l7
        |	PCLO    = >:l7
        |l6:
        |	[:gpr0+0] = $02
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l7:
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |	PCHITMP = <:l9
        |	PCLO    = >:l9 _NE
        |l8:
        |	[:gpr0+0] = $03
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l9:
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $03 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $e7 _EQ_S
        |	PCHITMP = <:l11
        |	PCLO    = >:l11 _EQ
        |l10:
        |	[:gpr0+0] = $04
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l11:
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |l1:
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	PCHITMP = REGA
        |	gtmp1     :	RESERVE 4
        |	gtmp2     :	RESERVE 4
        |	ftmp1     :	RESERVE 8
        |	ftmp2     :	RESERVE 8
        |	gpr0      :	RESERVE 4
        |	gpr1      :	RESERVE 4
        |	gpr2      :	RESERVE 4
        |	gpr3      :	RESERVE 4
        |	gpr4      :	RESERVE 4
        |	gpr5      :	RESERVE 4
        |	gpr6      :	RESERVE 4
        |	gpr7      :	RESERVE 4
        |	gpr8      :	RESERVE 4
        |	gpr9      :	RESERVE 4
        |	gpr10     :	RESERVE 4
        |	gpr11     :	RESERVE 4
        |	gpr12     :	RESERVE 4
        |	gpr13     :	RESERVE 4
        |	gpr14     :	RESERVE 4
        |	gpr15     :	RESERVE 4
        |	fpr0      :	RESERVE 8
        |	fpr1      :	RESERVE 8
        |	fpr2      :	RESERVE 8
        |	fpr3      :	RESERVE 8
        |	fpr4      :	RESERVE 8
        |	fpr5      :	RESERVE 8
        |	fpr6      :	RESERVE 8
        |	fpr7      :	RESERVE 8
        |	fpr8      :	RESERVE 8
        |	fpr9      :	RESERVE 8
        |	fpr10     :	RESERVE 8
        |	fpr11     :	RESERVE 8
        |	fpr12     :	RESERVE 8
        |	fpr13     :	RESERVE 8
        |	fpr14     :	RESERVE 8
        |	fpr15     :	RESERVE 8
        |	sp_stash  :	RESERVE 2
        |	sp        :	RESERVE 2
        |END
        | """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0xffff, 0)),
      timeout = 200,
      roms = roms);
  }


  @Test
  def vbccTest4(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |; jump to _main
        |PCHITMP   = < :_main
        |PC        = > :_main
        |_adder:
        |; ALLOCREG - gpr2
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr2 sp_stash
        |; ASSIGN type:i srcreg:noreg -> destreg:gpr2
        |	; load_reg targ from VAR - reg size:4    src data size:4
        |	; stash SP
        |	[:sp_stash]   = MARLO
        |	[:sp_stash+1] = MARHI
        |	; adjust SP by offset 8
        |	MARLO = MARLO + (> 8) _S            ; add lo byte of offset
        |	MARHI = MARHI A_PLUS_B_PLUS_C (< 8) ; add hi byte of offset plus any carry
        |	; copy 4 bytes
        |	REGA     = RAM
        |	[:gpr2+0] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr2+1] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr2+2] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr2+3] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	; restore SP
        |	MARLO = [:sp_stash]
        |	MARHI = [:sp_stash+1]
        |; ALLOCREG - gpr1
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr1 gpr2 sp_stash
        |; ASSIGN type:i srcreg:noreg -> destreg:gpr1
        |	; load_reg targ from VAR - reg size:4    src data size:4
        |	; stash SP
        |	[:sp_stash]   = MARLO
        |	[:sp_stash+1] = MARHI
        |	; adjust SP by offset 4
        |	MARLO = MARLO + (> 4) _S            ; add lo byte of offset
        |	MARHI = MARHI A_PLUS_B_PLUS_C (< 4) ; add hi byte of offset plus any carry
        |	; copy 4 bytes
        |	REGA     = RAM
        |	[:gpr1+0] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr1+1] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr1+2] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA     = RAM
        |	[:gpr1+3] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	; restore SP
        |	MARLO = [:sp_stash]
        |	MARHI = [:sp_stash+1]
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr1 gpr2 sp_stash
        |; ARITHMETIC : add gpr0 = gpr1 - gpr2
        |	; clear flags
        |	REGA = 0 _S
        |	; sum reg and reg
        |	REGA = [:gpr1+0]
        |	REGB = [:gpr2+0]
        |	[:gpr0+0] = REGA A_PLUS_B_PLUS_C REGB _S
        |	REGA = [:gpr1+1]
        |	REGB = [:gpr2+1]
        |	[:gpr0+1] = REGA A_PLUS_B_PLUS_C REGB _S
        |	REGA = [:gpr1+2]
        |	REGB = [:gpr2+2]
        |	[:gpr0+2] = REGA A_PLUS_B_PLUS_C REGB _S
        |	REGA = [:gpr1+3]
        |	REGB = [:gpr2+3]
        |	[:gpr0+3] = REGA A_PLUS_B_PLUS_C REGB _S
        |; SETRETURN - zreg = gpr0
        |	; load_reg skipping redundant self-copy of gpr0
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr1 gpr2 sp_stash
        |_L1:
        |; FUNCTION BOTTOM
        |	; return
        |	; pop PCHITMP
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop PC
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop 2 unused PC byes
        |	MARLO   = MARLO + 2 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; do jump
        |	PC      = REGA
        |	; returned
        |_main:
        |; _main routine
        |[:sp]   = $ff
        |[:sp+1] = $ff
        |MARLO   = [:sp]
        |MARHI   = [:sp+1]
        |; ALLOCREG - gpr6
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 sp_stash
        |; PUSH KONST 1
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $01
        |; PUSH KONST 2
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $00
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = $02
        |; CALL adder(..) and return to _L7
        |	; call	_adder
        |	; vbcc assume 4 bytes of address pushed
        |	; skip 2 return bytes
        |	MARLO = MARLO - 2 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	; push return lo
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = (>:_L7)
        |	; push return hi
        |	MARLO = MARLO - 1 _S
        |	MARHI = MARHI A_MINUS_B_MINUS_C 0
        |	RAM = (<:_L7)
        |	; call adder
        |	PCHITMP = (<:_adder)
        |	PC      = (>:_adder)
        |	; return location _L7
        |_L7:
        |	; rewinding pushed args 8
        |	MARLO = MARLO + 8 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	; call unwound and complete
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 sp_stash
        |; GETRETURN
        |	; memCopy : move 4 bytes  gpr6 <- gpr0
        |	REGA = [:gpr0+0]
        |	[:gpr6+0] = REGA
        |	REGA = [:gpr0+1]
        |	[:gpr6+1] = REGA
        |	REGA = [:gpr0+2]
        |	[:gpr6+2] = REGA
        |	REGA = [:gpr0+3]
        |	[:gpr6+3] = REGA
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 sp_stash
        |; COMPARE START ========
        |	; ORIGINAL ASM: 		cmp.i	gpr6,3
        |	; BRANCH-TYPE-WILL-BE bne
        |	; NOTE ! This is a magnitude comparison NOT a subtraction so we start with the top digit
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $03 _EQ_S
        |; BRANCH BLOCK ne
        |	PCHITMP = <:_L6
        |	PCLO    = >:_L6 _NE
        |; BRANCH TO LABEL _L6
        |_L5:
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 sp_stash
        |; ASSIGN type:i srcreg:noreg -> destreg:gpr0
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |; CALL INLINE ASM : halt(..)
        |	HALT = [:gpr0]
        |
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 sp_stash
        |_L6:
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 sp_stash
        |; ASSIGN type:i srcreg:gpr6 -> destreg:gpr0
        |	; load_reg targ from REG - reg size:4    src data size:4
        |	; memCopy : move 4 bytes  gpr0 <- gpr6
        |	REGA = [:gpr6+0]
        |	[:gpr0+0] = REGA
        |	REGA = [:gpr6+1]
        |	[:gpr0+1] = REGA
        |	REGA = [:gpr6+2]
        |	[:gpr0+2] = REGA
        |	REGA = [:gpr6+3]
        |	[:gpr0+3] = REGA
        |; CALL INLINE ASM : halt(..)
        |	HALT = [:gpr0]
        |
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 sp_stash
        |; SETRETURN - zreg = gpr0
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |_L3:
        |; FUNCTION BOTTOM
        |	; adjust SP to rewind over the variable stack by offset 4
        |	MARLO = MARLO + (> 4) _S            ; add lo byte of offset
        |	MARHI = MARHI A_PLUS_B_PLUS_C (< 4) ; add hi byte of offset plus any carry
        |	; return
        |	; pop PCHITMP
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop PC
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop 2 unused PC byes
        |	MARLO   = MARLO + 2 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; do jump
        |	PC      = REGA
        |	; returned
        |; registers
        |	gtmp1     :	RESERVE 4
        |	gtmp2     :	RESERVE 4
        |	ftmp1     :	RESERVE 8
        |	ftmp2     :	RESERVE 8
        |	gpr0      :	RESERVE 4
        |	gpr1      :	RESERVE 4
        |	gpr2      :	RESERVE 4
        |	gpr3      :	RESERVE 4
        |	gpr4      :	RESERVE 4
        |	gpr5      :	RESERVE 4
        |	gpr6      :	RESERVE 4
        |	gpr7      :	RESERVE 4
        |	gpr8      :	RESERVE 4
        |	gpr9      :	RESERVE 4
        |	gpr10     :	RESERVE 4
        |	gpr11     :	RESERVE 4
        |	gpr12     :	RESERVE 4
        |	gpr13     :	RESERVE 4
        |	gpr14     :	RESERVE 4
        |	gpr15     :	RESERVE 4
        |	fpr0      :	RESERVE 8
        |	fpr1      :	RESERVE 8
        |	fpr2      :	RESERVE 8
        |	fpr3      :	RESERVE 8
        |	fpr4      :	RESERVE 8
        |	fpr5      :	RESERVE 8
        |	fpr6      :	RESERVE 8
        |	fpr7      :	RESERVE 8
        |	fpr8      :	RESERVE 8
        |	fpr9      :	RESERVE 8
        |	fpr10     :	RESERVE 8
        |	fpr11     :	RESERVE 8
        |	fpr12     :	RESERVE 8
        |	fpr13     :	RESERVE 8
        |	fpr14     :	RESERVE 8
        |	fpr15     :	RESERVE 8
        |	sp_stash  :	RESERVE 2
        |	sp        :	RESERVE 2
        |END
        |
        | """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0xffff, 0)),      // expect MAR (ie SP) to be properly rewound to the top
      timeout = 200,
      roms = roms);
  }
  @Test
  def vbccTest3(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |[:sp]   = $ff
        |[:sp+1] = $ff
        |MARLO   = [:sp]
        |MARHI   = [:sp+1]
        |	[:gpr6+0] = $00
        |	[:gpr6+1] = $00
        |	[:gpr6+2] = $00
        |	[:gpr6+3] = $00
        |	PCHITMP = <:l4
        |	PCLO    = >:l4
        |l3:
        |	REGA = 0 _S ; clear flags
        |	REGA = [:gpr6+0]
        |	[:gpr6+0] = REGA A_PLUS_B_PLUS_C $02 _S
        |	REGA = [:gpr6+1]
        |	[:gpr6+1] = REGA A_PLUS_B_PLUS_C $00 _S
        |	REGA = [:gpr6+2]
        |	[:gpr6+2] = REGA A_PLUS_B_PLUS_C $00 _S
        |	REGA = [:gpr6+3]
        |	[:gpr6+3] = REGA A_PLUS_B_PLUS_C $00 _S
        |l4:
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $03 _EQ_S
        |	PCHITMP = <:l3
        |	PCLO    = >:l3 _LT
        |l5:
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $04 _EQ_S
        |	PCHITMP = <:l7
        |	PCLO    = >:l7 _EQ
        |l6:
        |	[:gpr0+0] = $01
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |l7:
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |	HALT = [:gpr0]
        |
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |l1:
        |	MARLO = MARLO + (> 4)               ; add lo byte of offset
        |	MARHI = MARHI A_PLUS_B_PLUS_C (< 4) ; add hi byte of offset plus any carry
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	PCHITMP = REGA
        |	gtmp1     :	RESERVE 4
        |	gtmp2     :	RESERVE 4
        |	ftmp1     :	RESERVE 8
        |	ftmp2     :	RESERVE 8
        |	gpr0      :	RESERVE 4
        |	gpr1      :	RESERVE 4
        |	gpr2      :	RESERVE 4
        |	gpr3      :	RESERVE 4
        |	gpr4      :	RESERVE 4
        |	gpr5      :	RESERVE 4
        |	gpr6      :	RESERVE 4
        |	gpr7      :	RESERVE 4
        |	gpr8      :	RESERVE 4
        |	gpr9      :	RESERVE 4
        |	gpr10     :	RESERVE 4
        |	gpr11     :	RESERVE 4
        |	gpr12     :	RESERVE 4
        |	gpr13     :	RESERVE 4
        |	gpr14     :	RESERVE 4
        |	gpr15     :	RESERVE 4
        |	fpr0      :	RESERVE 8
        |	fpr1      :	RESERVE 8
        |	fpr2      :	RESERVE 8
        |	fpr3      :	RESERVE 8
        |	fpr4      :	RESERVE 8
        |	fpr5      :	RESERVE 8
        |	fpr6      :	RESERVE 8
        |	fpr7      :	RESERVE 8
        |	fpr8      :	RESERVE 8
        |	fpr9      :	RESERVE 8
        |	fpr10     :	RESERVE 8
        |	fpr11     :	RESERVE 8
        |	fpr12     :	RESERVE 8
        |	fpr13     :	RESERVE 8
        |	fpr14     :	RESERVE 8
        |	fpr15     :	RESERVE 8
        |	sp_stash  :	RESERVE 2
        |	sp        :	RESERVE 2
        |END
        |
        | """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0xffff, 0)),
      timeout = 200,
      roms = roms);
  }
  @Test
  def vbccTest5(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |_main:
        |; _main routine
        |[:sp]   = $ff
        |[:sp+1] = $ff
        |MARLO   = [:sp]
        |MARHI   = [:sp+1]
        |; ALLOCREG - gpr7
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr7 sp_stash
        |; ALLOCREG - gpr6
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 gpr7 sp_stash
        |; ASSIGN type:i size:4
        |	; assign into a var
        |; assign step 1 - load to temp reg
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gtmp2+0] = $9a
        |	[:gtmp2+1] = $02
        |	[:gtmp2+2] = $00
        |	[:gtmp2+3] = $00
        |; assign step 2 - store to var
        |	; stash SP
        |	[:sp_stash]   = MARLO
        |	[:sp_stash+1] = MARHI
        |	; adjust SP by offset 0 to point to var
        |	; copy 4 bytes from temp register to memory
        |	REGA  = [:gtmp2+0]
        |	RAM   = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = [:gtmp2+1]
        |	RAM   = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = [:gtmp2+2]
        |	RAM   = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = [:gtmp2+3]
        |	RAM   = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	; restore SP
        |	MARLO = [:sp_stash]
        |	MARHI = [:sp_stash+1]
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 gpr7 sp_stash
        |; ADDRESS todo
        |	[:gpr7+0] = MARLO + (> 0) _S
        |	[:gpr7+1] = MARHI A_PLUS_B_PLUS_C (< 0)
        |	[:gpr7+2] = 0
        |	[:gpr7+3] = 0
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 gpr7 sp_stash
        |; ASSIGN type:i size:4
        |	; assign into a register
        |	; stash SP
        |	[:sp_stash]   = MARLO
        |	[:sp_stash+1] = MARHI
        |	MARLO = [:gpr7]
        |	MARHI = [:gpr7+1]
        |	REGA  = RAM
        |	[:gpr6+0] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = RAM
        |	[:gpr6+1] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = RAM
        |	[:gpr6+2] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	REGA  = RAM
        |	[:gpr6+3] = REGA
        |	MARLO = MARLO + 1 _S
        |	MARHI = MARHI A_PLUS_B_PLUS_C 0
        |	MARLO = [:sp_stash]
        |	MARHI = [:sp_stash+1]
        |; COMPARE START ========
        |	; ORIGINAL ASM: 		cmp.i	gpr6,666
        |	; BRANCH-TYPE-WILL-BE beq
        |	; NOTE ! This is a magnitude comparison NOT a subtraction so we start with the top digit
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |; BRANCH BLOCK eq
        |	PCHITMP = <:_L4
        |	PCLO    = >:_L4 _EQ
        |; BRANCH TO LABEL _L4
        |_L3:
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 gpr7 sp_stash
        |; ASSIGN type:i size:4
        |	; assign into a register
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $01
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |; CALL INLINE ASM : halt(..)
        |	HALT = [:gpr0]
        |
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 gpr7 sp_stash
        |_L4:
        |; COMPARE START ========
        |	; ORIGINAL ASM: 		cmp.i	gpr6,666
        |	; BRANCH-TYPE-WILL-BE bne
        |	; NOTE ! This is a magnitude comparison NOT a subtraction so we start with the top digit
        |	REGA=[:gpr6+3]
        |	NOOP = REGA A_MINUS_B_SIGNEDMAG $00 _S
        |	REGA=[:gpr6+2]
        |	NOOP = REGA A_MINUS_B           $00 _EQ_S
        |	REGA=[:gpr6+1]
        |	NOOP = REGA A_MINUS_B           $02 _EQ_S
        |	REGA=[:gpr6+0]
        |	NOOP = REGA A_MINUS_B           $9a _EQ_S
        |; BRANCH BLOCK ne
        |	PCHITMP = <:_L6
        |	PCLO    = >:_L6 _NE
        |; BRANCH TO LABEL _L6
        |_L5:
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 gpr7 sp_stash
        |; ASSIGN type:i size:4
        |	; assign into a register
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |; CALL INLINE ASM : halt(..)
        |	HALT = [:gpr0]
        |
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 gpr7 sp_stash
        |_L6:
        |; ALLOCREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr0 gpr6 gpr7 sp_stash
        |; ASSIGN type:i size:4
        |	; assign into a register
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $02
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |; CALL INLINE ASM : halt(..)
        |	HALT = [:gpr0]
        |
        |; FREEREG - gpr0
        |	; allocated: gtmp1 gtmp2 ftmp1 ftmp2 gpr6 gpr7 sp_stash
        |; SETRETURN - zreg = gpr0
        |	; load_reg targ from KONST - reg size:4    src data size:4
        |	[:gpr0+0] = $00
        |	[:gpr0+1] = $00
        |	[:gpr0+2] = $00
        |	[:gpr0+3] = $00
        |_L1:
        |; FUNCTION BOTTOM
        |	; adjust SP to rewind over the variable stack by offset 12
        |	MARLO = MARLO + (> 12) _S            ; add lo byte of offset
        |	MARHI = MARHI A_PLUS_B_PLUS_C (< 12) ; add hi byte of offset plus any carry
        |	; return
        |	; pop PCHITMP
        |	PCHITMP = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop PC
        |	REGA    = RAM
        |	MARLO   = MARLO + 1 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; pop 2 unused PC byes
        |	MARLO   = MARLO + 2 _S
        |	MARHI   = MARHI A_PLUS_B_PLUS_C 0
        |	; do jump
        |	PC      = REGA
        |	; returned
        |; registers
        |	gtmp1     :	RESERVE 4
        |	gtmp2     :	RESERVE 4
        |	ftmp1     :	RESERVE 8
        |	ftmp2     :	RESERVE 8
        |	gpr0      :	RESERVE 4
        |	gpr1      :	RESERVE 4
        |	gpr2      :	RESERVE 4
        |	gpr3      :	RESERVE 4
        |	gpr4      :	RESERVE 4
        |	gpr5      :	RESERVE 4
        |	gpr6      :	RESERVE 4
        |	gpr7      :	RESERVE 4
        |	gpr8      :	RESERVE 4
        |	gpr9      :	RESERVE 4
        |	gpr10     :	RESERVE 4
        |	gpr11     :	RESERVE 4
        |	gpr12     :	RESERVE 4
        |	gpr13     :	RESERVE 4
        |	gpr14     :	RESERVE 4
        |	gpr15     :	RESERVE 4
        |	fpr0      :	RESERVE 8
        |	fpr1      :	RESERVE 8
        |	fpr2      :	RESERVE 8
        |	fpr3      :	RESERVE 8
        |	fpr4      :	RESERVE 8
        |	fpr5      :	RESERVE 8
        |	fpr6      :	RESERVE 8
        |	fpr7      :	RESERVE 8
        |	fpr8      :	RESERVE 8
        |	fpr9      :	RESERVE 8
        |	fpr10     :	RESERVE 8
        |	fpr11     :	RESERVE 8
        |	fpr12     :	RESERVE 8
        |	fpr13     :	RESERVE 8
        |	fpr14     :	RESERVE 8
        |	fpr15     :	RESERVE 8
        |	sp_stash  :	RESERVE 2
        |	sp        :	RESERVE 2
        |END
        |
        | """

    val code = cmpEq.split("\\|").map(x => x.trim).filter(_.length > 0)

    val asm = new Assembler()

    val roms = assemble(code, asm)

    roms.zipWithIndex.foreach {
      c => {
        print(c._2.toString + "\t : " + c._1 + "\t " + asm.decode(c._1) + "\n")
      }
    }

    verifyRoms(
      verbose = true,
      uartDataIn = List(),
      outputCheck = (output: List[String]) => {},
      checkHalt = Some(HaltCode(0xffff, 0)),
      timeout = 200,
      roms = roms);
  }

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
  def vbcc(): Unit = {
    // test the injection of the jmp macro
    val cmpEq =
      """
        |_sub:
        |
        |        [:gpr12+0] = $00
        |        [:gpr12+1] = $00
        |        [:gpr12+2] = $00
        |        [:gpr12+3] = $00
        |
        |        [:gpr3+0] = $00
        |        [:gpr3+1] = $00
        |        [:gpr3+2] = $00
        |        [:gpr3+3] = $00
        |
        |l1:
        |_main:
        |
        |        [:gpr11+0] = $af
        |        [:gpr11+1] = $be
        |        [:gpr11+2] = $00
        |        [:gpr11+3] = $00
        |
        |        [:gpr12+0] = $fe
        |        [:gpr12+1] = $fe
        |        [:gpr12+2] = $00
        |        [:gpr12+3] = $00
        |
        |        REGA=[:gpr11+3]
        |        NOOP = REGA A_MINUS_B_SIGNEDMAG [:gpr12+3] _S
        |        REGA=[:gpr11+2]
        |        NOOP = REGA A_MINUS_B           [:gpr12+2] _EQ_S
        |        REGA=[:gpr11+1]
        |        NOOP = REGA A_MINUS_B           [:gpr12+1] _EQ_S
        |        REGA=[:gpr11+0]
        |        NOOP = REGA A_MINUS_B           [:gpr12+0] _EQ_S
        |        REGA=0
        |        REGA = REGA A_OR_B 1 _LT
        |        REGA = REGA A_OR_B 2 _GT
        |        REGA = REGA A_OR_B 4 _NE
        |        REGA = REGA A_OR_B 8 _EQ
        |
        |        HALT = REGA ; HACK
        |
        |        PCHITMP = <:l6
        |        PCLO = >:l6 _NE
        |
        |l5:
        |
        |        [:gpr11+0] = $aa
        |        [:gpr11+1] = $00
        |        [:gpr11+2] = $00
        |        [:gpr11+3] = $00
        |
        |        PCHITMP = <:l7
        |        PCLO = >:l7
        |
        |l6:
        |
        |
        |
        |
        |l7:
        |
        |        [:gpr3+0] = $63
        |        [:gpr3+1] = $00
        |        [:gpr3+2] = $00
        |        [:gpr3+3] = $00
        |
        |l3:
        |        noreg : BYTES [0,0,0,0]
        |        d4 : RESERVE 4
        |        gpr0  : BYTES [0,0,0,0]
        |        gpr1  : BYTES [0,0,0,0]
        |        gpr2  : BYTES [0,0,0,0]
        |        gpr3  : BYTES [0,0,0,0]
        |        gpr4  : BYTES [0,0,0,0]
        |        gpr5  : BYTES [0,0,0,0]
        |        gpr6  : BYTES [0,0,0,0]
        |        gpr7  : BYTES [0,0,0,0]
        |        gpr8  : BYTES [0,0,0,0]
        |        gpr9  : BYTES [0,0,0,0]
        |        gpr10 : BYTES [0,0,0,0]
        |        gpr11 : BYTES [0,0,0,0]
        |        gpr12 : BYTES [0,0,0,0]
        |        gpr13 : BYTES [0,0,0,0]
        |        gpr14 : BYTES [0,0,0,0]
        |        gpr15 : BYTES [0,0,0,0]
        |        fpr0  : BYTES [0,0,0,0]
        |        fpr1  : BYTES [0,0,0,0]
        |        fpr2  : BYTES [0,0,0,0]
        |        fpr3  : BYTES [0,0,0,0]
        |        fpr4  : BYTES [0,0,0,0]
        |        fpr5  : BYTES [0,0,0,0]
        |        fpr6  : BYTES [0,0,0,0]
        |        fpr7  : BYTES [0,0,0,0]
        |        fpr8  : BYTES [0,0,0,0]
        |        fpr9  : BYTES [0,0,0,0]
        |        fpr10 : BYTES [0,0,0,0]
        |        fpr11 : BYTES [0,0,0,0]
        |        fpr12 : BYTES [0,0,0,0]
        |        fpr13 : BYTES [0,0,0,0]
        |        fpr14 : BYTES [0,0,0,0]
        |        fpr15 : BYTES [0,0,0,0]
        |        fp    : BYTES [0,0,0,0]
        |        sp    : BYTES [0,0,0,0]
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
      instructions(code, asm)
      fail("expected an error")
    } catch {
      case ex: RuntimeException =>
        val err = "BYTES expression with label 'ILLEGAL' must have at least one byte but none were defined"
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