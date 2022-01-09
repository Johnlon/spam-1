enum class ADev(val id: Int) {
    rega(0),
    regb(1),
    regc(2),
    regd(3),
    marlo(4),
    marhi(5),
    uart(6),
    nu(7)
}

enum class BDev(val id: Int) {
    rega(0),
    regb(1),
    regc(2),
    regd(3),
    marlo(4),
    marhi(5),
    immed(6),
    ram(7),
    not_used(8),
    vram(9),
    port(10),
}

enum class TDev(val id: Int) {
    rega(0),
    regb(1),
    regc(2),
    regd(3),
    marlo(4),
    marhi(5),
    uart(6),
    ram(7),
    halt(8),
    vram(9),
    port(10),
    portsel(11),
    not_used12(12),
    pchitmp(13),
    pclo(14),
    pc(15)
}

enum class Cond (val id: Int){
    A(0),
    C(1),
    Z(2),
    O(3),
    N(4),
    EQ(5),
    NE(6),
    GT(7),
    LT(8),
    DI(9),
    DO(10)
}

enum class Op(val id: Int) {
    ZERO(0),
    A(1),
    B(2),
    NEGATE_A(3),
    NEGATE_B(4),
    BA_DIV_10(5),
    BA_MOD_10(6),
    B_PLUS_1(7),
    B_MINUS_1(8),
    A_PLUS_B(9),
    A_MINUS_B(10),
    B_MINUS_A(11),
    A_MINUS_B_SIGNEDMAG(12),
    A_PLUS_B_PLUS_C(13),
    A_MINUS_B_MINUS_C(14),
    B_MINUS_A_MINUS_C(15),
    A_TIMES_B_LO(16),
    A_TIMES_B_HI(17),
    A_DIV_B(18),
    A_MOD_B(19),
    A_LSL_B(20),
    A_LSR_B(21),
    A_ASR_B(22),
    A_RLC_B(23),
    A_RRC_B(24),
    A_AND_B(25),
    A_OR_B(26),
    A_XOR_B(27),
    A_NAND_B(28),
    NOT_B(29),
    A_PLUS_B_BCD(30),
    A_MINUS_B_BCD(31)
}


enum class Flag(val id: Int) {
    Set(1), Keep(0)
}
enum class CInv(val id: Int) {
    Inv(1), Std(0)
}
enum class AMode(val id: Int) {
    Dir(1), Reg(0)
}

enum class ReadPort(val id: Int) {
    Random(0),
    Gamepad1(1),
    Gamepad2(2),
    Timer1(3),
    Timer2(4),
    Parallel(7)
}
enum class WritePort(val id: Int) {
    Timer1(3),
    Timer2(4),
    Parallel(7)
}
