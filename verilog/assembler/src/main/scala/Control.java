import java.util.Arrays;

public enum Control {
    S(0, 0),
    A_S(0, 0),
    C_S(1, 0),
    Z_S(2, 0),
    O_S(3, 0),
    N_S(4, 0),
    GT_S(5, 0),
    LT_S(6, 0),
    EQ_S(7, 0),
    NE_S(8, 0),
    DI_S(9, 0),
    DO_S(10, 0),
    A(0, 1),
    C(1, 1),
    Z(2, 1),
    O(3, 1),
    N(4, 1),
    GT(5, 1),
    LT(6, 1),
    EQ(7, 1),
    NE(8, 1),
    DI(9, 1),
    DO(10, 1);

    Control(int cond, int flagbit) {
        this.cond = cond;
        this.setFlag = flagbit;
    }

    final int cond;
    final int setFlag;

    static public Control valueOf(int cond, int flagbit) {
        return Arrays.stream(values()).filter(c -> c.cond == cond && c.setFlag == flagbit).findFirst().get();
    }
}
