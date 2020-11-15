import java.util.Arrays;

public enum ADevice {
    REGA(0), REGB(1), REGC(2), REGD(2), MARLO(4), MARHI(5), UART(6), NU(7);

    final int id;

    ADevice(int id) {
        this.id = id;
    }

    static public ADevice valueOf(int id) {
        if (id > 7 || id < 0) throw new RuntimeException("unknown ADevice " + id);

        return Arrays.stream(values()).filter(c -> c.id == id).findFirst().get();
    }
}

