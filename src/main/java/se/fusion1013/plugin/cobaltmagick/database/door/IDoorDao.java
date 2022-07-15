package se.fusion1013.plugin.cobaltmagick.database.door;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;

import java.util.Map;
import java.util.UUID;

public interface IDoorDao extends IDao {

    /**
     * Removes a <code>MagickDoor</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>MagickDoor</code>.
     */
    void removeDoorAsync(UUID uuid);

    /**
     * Removes a <code>MagickDoor</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>MagickDoor</code>.
     */
    void removeDoorSync(UUID uuid);

    /**
     * Updates the status of an array of <code>MagickDoor</code>'s.
     *
     * @param doors the <code>MagickDoor</code>'s to update the status of.
     */
    void updateDoorStatusAsync(MagickDoor[] doors);

    /**
     * Updates the status of an array of <code>MagickDoor</code>'s.
     *
     * @param doors the <code>MagickDoor</code>'s to update the status of.
     */
    void updateDoorStatusSync(MagickDoor[] doors);

    /**
     * Inserts a new <code>MagickDoor</code> into the database.
     *
     * @param door the <code>MagickDoor</code> to insert.
     */
    void insertDoorAsync(MagickDoor door);

    /**
     * Inserts a new <code>MagickDoor</code> into the database.
     *
     * @param door the <code>MagickDoor</code> to insert.
     */
    void insertDoorSync(MagickDoor door);

    /**
     * Gets a map of all <code>MagickDoor</code>'s in the database.
     *
     * @return a map of <code>MagickDoor</code>'s.
     */
    Map<UUID, MagickDoor> getDoors();

    @Override
    default String getId() { return "door"; }

}
