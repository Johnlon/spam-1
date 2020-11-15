
//
//object AluOp extends Enumeration {
//  type AluOp = Value
//  val ZERO, // useful for zeroing RAM because we can't fetch a zero from ROM at same time
//  PASS_A,
//  PASS_B,
//  NEGATE_A,
//  NEGATE_B,
//  BA_DIV_10,
//  BA_MOD_10,
//  B_PLUS_1, // REG = RAM + 1
//  B_MINUS_1, // REG = RAM - 1
//  A_PLUS_B,
//  A_MINUS_B,
//  B_MINUS_A,
//  A_MINUS_B_SIGNEDMAG,
//  A_PLUS_B_PLUS_C,
//  A_MINUS_B_MINUS_C,
//  B_MINUS_A_MINUS_C,
//  A_TIMES_B_LO,
//  A_TIMES_B_HI,
//  A_DIV_B,
//  A_MOD_B,
//  A_LSL_B,
//  A_LSR_B,
//  A_ASR_B,
//  A_RLC_B,
//  A_RRC_B,
//  A_AND_B,
//  A_OR_B,
//  A_XOR_B,
//  A_NAND_B,
//  NOT_B,
//  A_PLUS_B_BCD,
//  A_MINUS_B_BCD = Value
//}
//
//object TDevice extends Enumeration {
//  type TDevice = Value
//  val REGA, REGB, REGC, REGD, MARLO, MARHI, UART, RAM, PCHITMP, PCLO, PC = Value
//}
//
//object ADevice extends Enumeration {
//  type ADevice = Value
//  val REGA, REGB, REGC, REGD, MARLO, MARHI, UART, NU = Value
//}
//
//object BDevice extends Enumeration {
//  type BDevice = Value
//  val REGA, REGB, REGC, REGD, MARLO, MARHI, IMMED, RAM = Value
//  val NU = REGA
//}
//
//object BOnlyDevice extends Enumeration {
//  type BOnlyDevice = Value
//  val RAM = Value(BDevice.RAM.id)
//  val IMMED = Value(BDevice.IMMED.id)
//}

object Mode extends Enumeration {
  type Mode = Value
  val DIRECT, REGISTER = Value
}
//
///* Long names have to be first in matcher list or short char eats expression and rest doesn't match */
//object Control.scala extends Enumeration {
//  type Control.scala = Value
//  val C_S, Z_S, O_S, N_S, GT_S, LT_S, EQ_S, NE_S, DI_S, DO_S = Value
//  val A_S, A, S = Value
//  val C, Z, O, N, GT, LT, EQ, NE, DI, DO = Value
//}
