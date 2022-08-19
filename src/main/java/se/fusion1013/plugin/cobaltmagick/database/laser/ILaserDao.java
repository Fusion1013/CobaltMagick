package se.fusion1013.plugin.cobaltmagick.database.laser;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.laser.SimpleLaser;

import java.util.List;
import java.util.UUID;

public interface ILaserDao extends IDao {

    /**
     * Removes a <code>AbstractLaser</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>AbstractLaser</code>.
     */
    void removeLaserAsync(UUID uuid);

    /**
     * Removes a <code>AbstractLaser</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>AbstractLaser</code>.
     */
    void removeLaserSync(UUID uuid);

    /**
     * Inserts a <code>SimpleLaser</code> into the database.
     *
     * @param simpleLaser the <code>SimpleLaser</code> to insert into the database.
     */
    void insertSimpleLaserAsync(SimpleLaser simpleLaser);

    /**
     * Inserts a <code>SimpleLaser</code> into the database.
     *
     * @param simpleLaser the <code>SimpleLaser</code> to insert into the database.
     */
    void insertSimpleLaserSync(SimpleLaser simpleLaser);

    /**
     * Gets all <code>SimpleLaser</code>'s from the database.
     *
     * @return a list of <code>SimpleLaser</code>'s.
     */
    List<SimpleLaser> getSimpleLasers();

    @Override
    default String getId() { return "laser"; }
}
