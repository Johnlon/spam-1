import java.util.Arrays;

public enum AluOp {
    // useful for zeroing RAM because we can't fetch a zero from ROM at same time
    ZERO(0),
    PASS_A(1),
    PASS_B(2),
    NEGATE_A(3),
    NEGATE_B(4),
    BA_DIV_10(5),
    BA_MOD_10(6),
    B_PLUS_1(7),  // REG = RAM + 1
    B_MINUS_1(8),  // REG = RAM - 1
    A_PLUS_B(9, "+"),
    A_MINUS_B(10, "-"),
    B_MINUS_A(11),
    A_MINUS_B_SIGNEDMAG(12),
    A_PLUS_B_PLUS_C(13),
    A_MINUS_B_MINUS_C(14),
    B_MINUS_A_MINUS_C(15),
    A_TIMES_B_LO(16, "*LO"),
    A_TIMES_B_HI(17, "*HI"),
    A_DIV_B(18, "/"),
    A_MOD_B(19, "%"),
    A_LSL_B(20, "<<"),
    A_LSR_B(21, ">>"),
    A_ASR_B(22, ">>>"),
    A_RLC_B(23, "<-"),
    A_RRC_B(24, "->"),
    A_AND_B(25, "&"),
    A_OR_B(26, "|"),
    A_XOR_B(27, "^"),
    A_NAND_B(28, "!&"),
    NOT_B(29),
    A_PLUS_B_BCD(20),
    A_MINUS_B_BCD(31);

    final int id;
    final String abbrev;

    AluOp(int id) {
        this.id = id;
        this.abbrev = null;
    }
    AluOp(int id, String abbrev) {
        this.id = id;
        this.abbrev = abbrev;
    }

    boolean isAbbreviated() {
        return abbrev != null;
    }
    static public AluOp valueOf(int id) {
        if (id > 31 || id < 0) throw new RuntimeException("unknown AluOp " + id);

        return Arrays.stream(values()).filter(c -> c.id == id).findFirst().get();
    }
    static public AluOp valueOfAbbrev(String abbrev) {
        return Arrays.stream(values()).filter(c -> c.abbrev.equals(abbrev)).findFirst().get();
    }
}
