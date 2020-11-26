package asm

sealed class AluOp private(val id: Int, val abbrev: String = null) extends E {
    def isAbbreviated: Boolean = abbrev != null
}

object AluOp  {
    object ZERO extends AluOp(0)
    object PASS_A extends AluOp(1)
    object PASS_B extends AluOp(2)
    object NEGATE_A extends AluOp(3)
    object NEGATE_B extends AluOp(4)
    object BA_DIV_10 extends AluOp(5)
    object BA_MOD_10 extends AluOp(6)
    object B_PLUS_1 extends AluOp(7)  // REG = RAM + 1
    object B_MINUS_1 extends AluOp(8)  // REG = RAM - 1
    object A_PLUS_B extends AluOp(9, "+")
    object A_MINUS_B extends AluOp(10, "-")
    object B_MINUS_A extends AluOp(11)
    object A_MINUS_B_SIGNEDMAG extends AluOp(12)
    object A_PLUS_B_PLUS_C extends AluOp(13)
    object A_MINUS_B_MINUS_C extends AluOp(14)
    object B_MINUS_A_MINUS_C extends AluOp(15)
    object A_TIMES_B_LO extends AluOp(16, "*LO")
    object A_TIMES_B_HI extends AluOp(17, "*HI")
    object A_DIV_B extends AluOp(18, "/")
    object A_MOD_B extends AluOp(19, "%")
    object A_LSL_B extends AluOp(20, "<<")
    object A_LSR_B extends AluOp(21, ">>")
    object A_ASR_B extends AluOp(22, ">>>")
    object A_RLC_B extends AluOp(23, "<-")
    object A_RRC_B extends AluOp(24, "->")
    object A_AND_B extends AluOp(25, "&")
    object A_OR_B extends AluOp(26, "|")
    object A_XOR_B extends AluOp(27, "^")
    object A_NAND_B extends AluOp(28, "!&")
    object NOT_B extends AluOp(29)
    object A_PLUS_B_BCD extends AluOp(20)
    object A_MINUS_B_BCD extends AluOp(31)

    def valueOf(id: Int) : AluOp = {
        values.find(c => c.id == id).getOrElse(throw new RuntimeException("unknown AluOp " + id))
    }
    def  valueOfAbbrev(abbrev: String): AluOp = {
        values.find(c => c.abbrev.equals(abbrev)).getOrElse(throw new RuntimeException("unknown AluOp abbrev '" + abbrev + "'"))
    }

    def values: Seq[AluOp ] = Seq(
          ZERO ,
          PASS_A ,
          PASS_B ,
          NEGATE_A ,
          NEGATE_B ,
          BA_DIV_10 ,
          BA_MOD_10 ,
          B_PLUS_1 ,
          B_MINUS_1 ,
          A_PLUS_B ,
          A_MINUS_B ,
          B_MINUS_A ,
          A_MINUS_B_SIGNEDMAG ,
          A_PLUS_B_PLUS_C ,
          A_MINUS_B_MINUS_C ,
          B_MINUS_A_MINUS_C ,
          A_TIMES_B_LO ,
          A_TIMES_B_HI ,
          A_DIV_B ,
          A_MOD_B ,
          A_LSL_B ,
          A_LSR_B ,
          A_ASR_B ,
          A_RLC_B ,
          A_RRC_B ,
          A_AND_B ,
          A_OR_B ,
          A_XOR_B ,
          A_NAND_B ,
          NOT_B ,
          A_PLUS_B_BCD ,
          A_MINUS_B_BCD
      )
}
