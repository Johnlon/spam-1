export var ADev;
(function (ADev) {
    ADev[ADev["rega"] = 0] = "rega";
    ADev[ADev["regb"] = 1] = "regb";
    ADev[ADev["regc"] = 2] = "regc";
    ADev[ADev["regd"] = 3] = "regd";
    ADev[ADev["marlo"] = 4] = "marlo";
    ADev[ADev["marhi"] = 5] = "marhi";
    ADev[ADev["uart"] = 6] = "uart";
    ADev[ADev["nu"] = 7] = "nu";
})(ADev || (ADev = {}));
class APins {
    adev_rega() { return () => this.chk_a_dev(ADev.rega); }
    adev_regb() { return () => this.chk_a_dev(ADev.regb); }
    adev_regc() { return () => this.chk_a_dev(ADev.regc); }
    adev_regd() { return () => this.chk_a_dev(ADev.regd); }
    adev_marlo() { return () => this.chk_a_dev(ADev.marlo); }
    adev_marhi() { return () => this.chk_a_dev(ADev.marhi); }
    adev_uart() { return () => this.chk_a_dev(ADev.uart); }
    adev_not_used() { return () => this.chk_a_dev(ADev.nu); }
}
export var BDev;
(function (BDev) {
    BDev[BDev["rega"] = 0] = "rega";
    BDev[BDev["regb"] = 1] = "regb";
    BDev[BDev["regc"] = 2] = "regc";
    BDev[BDev["regd"] = 3] = "regd";
    BDev[BDev["marlo"] = 4] = "marlo";
    BDev[BDev["marhi"] = 5] = "marhi";
    BDev[BDev["immed"] = 6] = "immed";
    BDev[BDev["ram"] = 7] = "ram";
    BDev[BDev["not_used"] = 8] = "not_used";
    BDev[BDev["vram"] = 9] = "vram";
    BDev[BDev["port"] = 10] = "port";
})(BDev || (BDev = {}));
export class BPins extends APins {
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
export var TDev;
(function (TDev) {
    TDev[TDev["rega"] = 0] = "rega";
    TDev[TDev["regb"] = 1] = "regb";
    TDev[TDev["regc"] = 2] = "regc";
    TDev[TDev["regd"] = 3] = "regd";
    TDev[TDev["marlo"] = 4] = "marlo";
    TDev[TDev["marhi"] = 5] = "marhi";
    TDev[TDev["uart"] = 6] = "uart";
    TDev[TDev["ram"] = 7] = "ram";
    TDev[TDev["halt"] = 8] = "halt";
    TDev[TDev["vram"] = 9] = "vram";
    TDev[TDev["port"] = 10] = "port";
    TDev[TDev["portsel"] = 11] = "portsel";
    TDev[TDev["not_used12"] = 12] = "not_used12";
    TDev[TDev["pchitmp"] = 13] = "pchitmp";
    TDev[TDev["pclo"] = 14] = "pclo";
    TDev[TDev["pc"] = 15] = "pc";
})(TDev || (TDev = {}));
export class TPins extends BPins {
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
export class Pins extends TPins {
}
export var Cond;
(function (Cond) {
    Cond[Cond["A"] = 0] = "A";
    Cond[Cond["C"] = 1] = "C";
    Cond[Cond["Z"] = 2] = "Z";
    Cond[Cond["O"] = 3] = "O";
    Cond[Cond["N"] = 4] = "N";
    Cond[Cond["GT"] = 5] = "GT";
    Cond[Cond["LT"] = 6] = "LT";
    Cond[Cond["EQ"] = 7] = "EQ";
    Cond[Cond["NE"] = 8] = "NE";
    Cond[Cond["DI"] = 9] = "DI";
    Cond[Cond["DO"] = 10] = "DO";
})(Cond || (Cond = {}));
export var Op;
(function (Op) {
    Op[Op["ZERO"] = 0] = "ZERO";
    Op[Op["A"] = 1] = "A";
    Op[Op["B"] = 2] = "B";
    Op[Op["NEGATE_A"] = 3] = "NEGATE_A";
    Op[Op["NEGATE_B"] = 4] = "NEGATE_B";
    Op[Op["BA_DIV_10"] = 5] = "BA_DIV_10";
    Op[Op["BA_MOD_10"] = 6] = "BA_MOD_10";
    Op[Op["B_PLUS_1"] = 7] = "B_PLUS_1";
    Op[Op["B_MINUS_1"] = 8] = "B_MINUS_1";
    Op[Op["A_PLUS_B"] = 9] = "A_PLUS_B";
    Op[Op["A_MINUS_B"] = 10] = "A_MINUS_B";
    Op[Op["B_MINUS_A"] = 11] = "B_MINUS_A";
    Op[Op["A_MINUS_B_SIGNEDMAG"] = 12] = "A_MINUS_B_SIGNEDMAG";
    Op[Op["A_PLUS_B_PLUS_C"] = 13] = "A_PLUS_B_PLUS_C";
    Op[Op["A_MINUS_B_MINUS_C"] = 14] = "A_MINUS_B_MINUS_C";
    Op[Op["B_MINUS_A_MINUS_C"] = 15] = "B_MINUS_A_MINUS_C";
    Op[Op["A_TIMES_B_LO"] = 16] = "A_TIMES_B_LO";
    Op[Op["A_TIMES_B_HI"] = 17] = "A_TIMES_B_HI";
    Op[Op["A_DIV_B"] = 18] = "A_DIV_B";
    Op[Op["A_MOD_B"] = 19] = "A_MOD_B";
    Op[Op["A_LSL_B"] = 20] = "A_LSL_B";
    Op[Op["A_LSR_B"] = 21] = "A_LSR_B";
    Op[Op["A_ASR_B"] = 22] = "A_ASR_B";
    Op[Op["A_RLC_B"] = 23] = "A_RLC_B";
    Op[Op["A_RRC_B"] = 24] = "A_RRC_B";
    Op[Op["A_AND_B"] = 25] = "A_AND_B";
    Op[Op["A_OR_B"] = 26] = "A_OR_B";
    Op[Op["A_XOR_B"] = 27] = "A_XOR_B";
    Op[Op["A_NAND_B"] = 28] = "A_NAND_B";
    Op[Op["NOT_B"] = 29] = "NOT_B";
    Op[Op["A_PLUS_B_BCD"] = 30] = "A_PLUS_B_BCD";
    Op[Op["A_MINUS_B_BCD"] = 31] = "A_MINUS_B_BCD";
})(Op || (Op = {}));
