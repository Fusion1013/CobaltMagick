package se.fusion1013.plugin.cobaltmagick.world.structures.laser;

import org.bukkit.Location;

import java.util.UUID;

public class SimpleLaser extends AbstractLaser {

    public SimpleLaser(Location startLocation) {
        super(startLocation);
    }

    public SimpleLaser(Location startLocation, UUID uuid) {
        super(startLocation, uuid);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
