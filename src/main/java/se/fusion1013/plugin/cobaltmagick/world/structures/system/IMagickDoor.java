package se.fusion1013.plugin.cobaltmagick.world.structures.system;

import org.bukkit.Location;

import java.util.UUID;

public interface IMagickDoor {

    /**
     * Opens the door.
     */
    void open();

    /**
     * Closes the door.
     */
    void close();

    /**
     * Weather the door is closed or not.
     * @return boolean representing the open state of the door.
     */
    boolean isClosed();

    /**
     * Gets the corner location of the door.
     * @return the corner location.
     */
    Location getCorner();

    int getWidth();
    int getHeight();
    int getDepth();

    UUID getUuid();

}
