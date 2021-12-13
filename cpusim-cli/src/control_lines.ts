

export enum ADev {
  rega = 0,
  regb = 1,
  regc = 2,
  regd = 3,
  marlo = 4,
  marhi = 5,
  uart = 6,
  nu = 7,
}

abstract class APins {
  abstract chk_a_dev(pin: ADev): boolean
  adev_rega() { return () => this.chk_a_dev(ADev.rega); }
  adev_regb() { return () => this.chk_a_dev(ADev.regb); }
  adev_regc() { return () => this.chk_a_dev(ADev.regc); }
  adev_regd() { return () => this.chk_a_dev(ADev.regd); }
  adev_marlo() { return () => this.chk_a_dev(ADev.marlo); }
  adev_marhi() { return () => this.chk_a_dev(ADev.marhi); }
  adev_uart() { return () => this.chk_a_dev(ADev.uart); }
  adev_not_used() { return () => this.chk_a_dev(ADev.nu); }
}

export enum BDev {
  rega = 0,
  regb = 1,
  regc = 2,
  regd = 3,
  marlo = 4,
  marhi = 5,
  immed = 6,
  ram = 7,
  not_used = 8,
  vram = 9,
  port = 10,
}

export abstract class BPins extends APins {
  abstract chk_b_dev(pin: BDev): boolean
  bdev_rega() { return () => this.chk_b_dev(BDev.rega); }
  bdev_regb() { return () => this.chk_b_dev(BDev.regb); }
  bdev_regc() { return () => this.chk_b_dev(BDev.regc); }
  bdev_regd() { return () => this.chk_b_dev(BDev.regd); }
  bdev_marlo() { return () => this.chk_b_dev(BDev.marlo); }
  bdev_marhi() { return () => this.chk_b_dev(BDev.marhi); }
  bdev_immed() { return () => this.chk_b_dev(BDev.immed); }
  bdev_ram() { return () => this.chk_b_dev(BDev.ram); }
  bdev_not_used() { return () => this.chk_b_dev(BDev.not_used); }
  bdev_vram() { return () => this.chk_b_dev(BDev.vram); }
  bdev_port() { return () => this.chk_b_dev(BDev.port); }
}


export enum TDev {
  rega = 0,
  regb = 1,
  regc = 2,
  regd = 3,
  marlo = 4,
  marhi = 5,
  uart = 6,
  ram = 7,
  halt = 8,
  vram = 9,
  port = 10,
  portsel = 11,
  not_used12 = 12,
  pchitmp = 13,
  pclo = 14,
  pc = 15,
}

export abstract class TPins extends BPins {
  abstract chk_t_dev(pin: TDev): boolean

  rega_in() { return () => this.chk_t_dev(TDev.rega); }
  regb_in() { return () => this.chk_t_dev(TDev.regb); }
  regc_in() { return () => this.chk_t_dev(TDev.regc); }
  regd_in() { return () => this.chk_t_dev(TDev.regd); }
  marlo_in() { return () => this.chk_t_dev(TDev.marlo); }
  marhi_in() { return () => this.chk_t_dev(TDev.marhi); }
  uart_in() { return () => this.chk_t_dev(TDev.uart); }
  ram_in() { return () => this.chk_t_dev(TDev.ram); }
  halt_in() { return () => this.chk_t_dev(TDev.halt); }
  vram_in() { return () => this.chk_t_dev(TDev.vram); }
  port_in() { return () => this.chk_t_dev(TDev.port); }
  portsel_in() { return () => this.chk_t_dev(TDev.portsel); }
  not_used12_in() { return () => this.chk_t_dev(TDev.not_used12); }
  pchitmp_in() { return () => this.chk_t_dev(TDev.pchitmp); }
  pclo_in() { return () => this.chk_t_dev(TDev.pclo); }
  pc_in() { return () => this.chk_t_dev(TDev.pc); }
}

export abstract class Pins extends TPins {

}

export enum Cond {
  A,
  C,
  Z,
  O,
  N,
  GT,
  LT,
  EQ,
  NE,
  DI,
  DO,
}

export enum Op {
  ZERO =  0,
  A =  1,
  B =  2,
  NEGATE_A =  3,
  NEGATE_B =  4,
  BA_DIV_10 =  5,
  BA_MOD_10 =  6,
  B_PLUS_1 =  7,
  B_MINUS_1 =  8,
  A_PLUS_B =  9,
  A_MINUS_B =  10,
  B_MINUS_A =  11,
  A_MINUS_B_SIGNEDMAG =  12,
  A_PLUS_B_PLUS_C =  13,
  A_MINUS_B_MINUS_C =  14,
  B_MINUS_A_MINUS_C =  15,
  A_TIMES_B_LO =  16,
  A_TIMES_B_HI =  17,
  A_DIV_B =  18,
  A_MOD_B =  19,
  A_LSL_B =  20,
  A_LSR_B =  21,
  A_ASR_B =  22,
  A_RLC_B =  23,
  A_RRC_B =  24,
  A_AND_B =  25,
  A_OR_B =  26,
  A_XOR_B =  27,
  A_NAND_B =  28,
  NOT_B =  29,
  A_PLUS_B_BCD =  30,
  A_MINUS_B_BCD =  31
}
