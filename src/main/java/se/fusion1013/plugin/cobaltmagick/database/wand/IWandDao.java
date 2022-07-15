package se.fusion1013.plugin.cobaltmagick.database.wand;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.List;

public interface IWandDao extends IDao {

    /**
     * Gets a list of <code>Wand</code>'s from the database.
     *
     * @return a list of <code>Wand</code>'s.
     */
    List<Wand> getWands();

    /**
     * Inserts a <code>Wand</code> into the database.
     *
     * @param wand the <code>Wand</code> to insert.
     */
    void insertWandAsync(Wand wand);

    /**
     * Inserts a <code>Wand</code> into the database.
     *
     * @param wand the <code>Wand</code> to insert.
     */
    void insertWandSync(Wand wand);

    /**
     * Updates all spells for a list of <code>Wand</code>'s.
     *
     * @param wands a list of <code>Wand</code>'s to update the spells of.
     */
    void updateWandSpellsAsync(List<Wand> wands);

    /**
     * Updates all spells for a list of <code>Wand</code>'s.
     *
     * @param wands a list of <code>Wand</code>'s to update the spells of.
     */
    void updateWandSpellsSync(List<Wand> wands);

    /**
     * Updates all spells for an array of <code>Wand</code>'s.
     *
     * @param wands an array of <code>Wand</code>'s to update the spells of.
     */
    void updateWandSpellsAsync(Wand... wands);

    /**
     * Updates all spells for an array of <code>Wand</code>'s.
     *
     * @param wands an array of <code>Wand</code>'s to update the spells of.
     */
    void updateWandSpellsSync(Wand... wands);

    @Override
    default String getId() { return "wand"; }
}
