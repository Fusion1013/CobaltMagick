package se.fusion1013.cobaltmagick.alchemy;

import org.bukkit.Color;

public enum Element {

    AETHER(Color.PURPLE), FIRE(Color.ORANGE), WATER(Color.BLUE), EARTH(Color.GREEN), AIR(Color.YELLOW);

    private final Color color;

    Element(Color color) {
        this.color = color;
    }

    public static Element getFromString(String string) {
        if (string.toLowerCase().contains("aether")) return AETHER;
        if (string.toLowerCase().contains("fire")) return FIRE;
        if (string.toLowerCase().contains("water")) return WATER;
        if (string.toLowerCase().contains("earth")) return EARTH;
        if (string.toLowerCase().contains("air")) return AIR;
        return null;
    }

    public Color getColor() {
        return color;
    }
}
