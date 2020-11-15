import java.util.Arrays;
import java.util.Optional;

public enum TDevice implements TDevices  {
    REGA(0), REGB(1), REGC(2), REGD(2), MARLO(4), MARHI(5), UART(6), RAM(7), PCHITMP(13), PCLO(14), PC(15);

    TDevice(int id) {
        this.id = id;
    }

    final int id;

    static public TDevice valueOf(int id) {
        Optional<TDevice> first = Arrays.stream(values()).filter(c -> c.id == id).findFirst();
        if (first.isEmpty()) throw new RuntimeException("unknown TDevice " + id);
        return first.get();
    }
}
