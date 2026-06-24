package se.fusion1013.cobaltmagick.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Wisp {

    public final Location location;
    public final Vector velocity;

    public final int maxAge;
    public int age;

    public Wisp(
            Location location,
            Vector velocity,
            int maxAge
    ) {
        this.location = location;
        this.velocity = velocity;
        this.maxAge = maxAge;
    }
}
