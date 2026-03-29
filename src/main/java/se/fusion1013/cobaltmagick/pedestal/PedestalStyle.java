package se.fusion1013.cobaltmagick.pedestal;

import java.util.Arrays;

public enum PedestalStyle {

    NORMAL("normal", "block/pedestal_normal"),
    RECHARGE("recharge", "block/recharge_pedestal"),
    ELDRITCH("eldritch", "block/pedestal_eldritch");

    private final String name;
    private final String model;

    PedestalStyle(String name, String model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public static PedestalStyle fromModel(String model) {
        return Arrays.stream(PedestalStyle.values()).filter(ps -> ps.getModel().equalsIgnoreCase(model)).findFirst().orElse(null);
    }
}
