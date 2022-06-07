package se.fusion1013.plugin.cobaltmagick.world.structures.system;

import org.bukkit.Location;

import java.util.UUID;

public interface IStructure {

    UUID getUuid();

    Location getLocation();
    int getWidth();
    int getHeight();
    int getDepth();

}
