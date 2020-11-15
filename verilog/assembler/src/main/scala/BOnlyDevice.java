import java.util.Arrays;

public enum BOnlyDevice implements BOnlyDevices {
    IMMED(BDevice.IMMED.id), RAM(BDevice.RAM.id);

    final int id;

    BOnlyDevice(int id) {
        this.id = id;
    }

    static public BOnlyDevice valueOf(int id) {
        return Arrays.stream(values()).filter(c -> c.id == id).findFirst().get();
    }
}

