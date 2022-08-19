package se.fusion1013.plugin.cobaltmagick.database.hidden;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenObject;

import java.util.List;
import java.util.UUID;

public interface IHiddenObjectDao extends IDao {

    /**
     * Removes a <code>HiddenObject</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>HiddenObject</code>.
     */
    void removeHiddenObjectSync(UUID uuid);

    /**
     * Removes a <code>HiddenObject</code> from the database.
     *
     * @param uuid the <code>UUID</code> of the <code>HiddenObject</code>.
     */
    void removeHiddenObjectAsync(UUID uuid);

    /**
     * Inserts a <code>HiddenObject</code> into the database.
     *
     * @param hiddenObject the <code>HiddenObject</code> to insert.
     */
    void insertHiddenObjectSync(HiddenObject hiddenObject);

    /**
     * Inserts a <code>HiddenObject</code> into the database.
     *
     * @param hiddenObject the <code>HiddenObject</code> to insert.
     */
    void insertHiddenObjectAsync(HiddenObject hiddenObject);

    /**
     * Gets a list of all <code>HiddenObject</code>'s from the database.
     *
     * @return a list of <code>HiddenObject</code>'s.
     */
    List<HiddenObject> getHiddenParticles();

    void updateHiddenObject(HiddenObject hiddenObject);

    @Override
    default String getId() { return "hidden_object"; }
}
