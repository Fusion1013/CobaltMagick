package se.fusion1013.plugin.cobaltmagick.database.runelock;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.RuneLock;

import java.util.Map;

public interface IRuneLockDao extends IDao {

    /**
     * Removes a <code>RuneLock</code> from the database.
     *
     * @param id the id of the <code>RuneLock</code>.
     */
    void removeRuneLockAsync(int id);

    /**
     * Removes a <code>RuneLock</code> from the database.
     *
     * @param id the id of the <code>RuneLock</code>.
     */
    void removeRuneLockSync(int id);

    /**
     * Gets all <code>RuneLock</code>'s from the database.
     *
     * @return a map of <code>RuneLock</code>'s.
     */
    Map<Integer, RuneLock> getRuneLocks();

    /**
     * Insert a <code>RuneLock</code> into the database.
     *
     * @param lock the <code>RuneLock</code> to insert.
     */
    void insertRuneLockAsync(RuneLock lock);

    /**
     * Insert a <code>RuneLock</code> into the database.
     *
     * @param lock the <code>RuneLock</code> to insert.
     */
    void insertRuneLockSync(RuneLock lock);

    /**
     * Updates the items for the given <code>RuneLock</code>.
     *
     * @param lock the <code>RuneLock</code> to update the items of.
     */
    void updateRuneLockItemsSync(RuneLock lock);

    /**
     * Updates the items for the given <code>RuneLock</code>.
     *
     * @param lock the <code>RuneLock</code> to update the items of.
     */
    void updateRuneLockItemsAsync(RuneLock lock);

    @Override
    default String getId() { return "rune_lock"; }

}
