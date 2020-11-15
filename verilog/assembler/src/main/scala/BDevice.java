import java.util.Arrays;

public enum BDevice implements BDevices {
    // NU is also REGA
    REGA(0), REGB(1), REGC(2), REGD(2), MARLO(4), MARHI(5), IMMED(6), RAM(7);

    static BDevice NU = REGA;

    final int id;

    BDevice(int id) {
        this.id = id;
    }

    static public BDevice valueOf(int id) {
        if (id > 7 || id < 0) throw new RuntimeException("unknown BDevice " + id);

        return Arrays.stream(values()).filter(c -> c.id == id).findFirst().get();
    }
}

