package se.fusion1013.plugin.cobaltmagick.database.lock;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;

import java.util.Map;
import java.util.UUID;

public interface ILockDao extends IDao {

    /**
     * Removes a <code>ItemLock</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>ItemLock</code>.
     */
    void removeLockAsync(UUID uuid);

    /**
     * Removes a <code>ItemLock</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>ItemLock</code>.
     */
    void removeLockSync(UUID uuid);

    /**
     * Gets all <code>ItemLock</code>'s from the database.
     *
     * @return a map of all <code>ItemLock</code>'s.
     */
    Map<UUID, ItemLock> getLocks();

    /**
     * Inserts a <code>ItemLock</code> into the database.
     *
     * @param lock the <code>ItemLock</code> to insert.
     */
    void insertLockAsync(ItemLock lock);

    /**
     * Inserts a <code>ItemLock</code> into the database.
     *
     * @param lock the <code>ItemLock</code> to insert.
     */
    void insertLockSync(ItemLock lock);

    @Override
    default String getId() { return "lock"; }
}
