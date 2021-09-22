package se.fusion1013.plugin.nicobalt.flags;

import org.bukkit.Location;

public class LocationFlag extends Flag<Location> {

    private final Location defaultValue;

    public LocationFlag(String name) {
        super(name);
        this.defaultValue = null;
    }

    public LocationFlag(String name, Location defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    public Location getDefault(){
        return defaultValue;
    }
}
