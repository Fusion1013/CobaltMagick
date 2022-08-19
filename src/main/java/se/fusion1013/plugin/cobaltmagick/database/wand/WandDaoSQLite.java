package se.fusion1013.plugin.cobaltmagick.database.wand;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WandDaoSQLite extends Dao implements IWandDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateWandsTable = "CREATE TABLE IF NOT EXISTS wands (" +
            "`id` INTEGER NOT NULL," +
            "`shuffle` boolean NOT NULL," +
            "`spells_per_cast` int(11) NOT NULL," +
            "`cast_delay` real NOT NULL," +
            "`recharge_time` real NOT NULL," +
            "`mana_max` int(11) NOT NULL," +
            "`mana_charge_speed` int(11) NOT NULL," +
            "`capacity` int(11) NOT NULL," +
            "`spread` real NOT NULL," +
            "`wand_tier` int(11) NOT NULL," +
            "PRIMARY KEY (`id`)" +
            ");";

    public static String SQLiteCreateWandSpellsTable = "CREATE TABLE IF NOT EXISTS wand_spells (" +
            "`wand_id` int(11) NOT NULL," +
            "`spell_id` INTEGER NOT NULL," +
            "`is_always_cast` boolean NOT NULL," +
            "`slot` INTEGER NOT NULL," +
            "`count` int(11) NOT NULL," +
            "PRIMARY KEY (wand_id, spell_id, slot)," +
            "FOREIGN KEY (wand_id) REFERENCES wands(id)" +
            ");";

    // ----- METHODS -----

    @Override
    public List<Wand> getWands() {
        List<Wand> wands = new ArrayList<>();

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement stWand = conn.prepareStatement("SELECT * FROM wands");
                PreparedStatement stSpells = conn.prepareStatement("SELECT * FROM wand_spells WHERE wand_id = ? ORDER BY slot ASC");
                ResultSet wandSet = stWand.executeQuery()
        ) {
            while (wandSet.next()) {
                int id = wandSet.getInt("id");
                boolean shuffle = wandSet.getBoolean("shuffle");
                int spellsPerCast = wandSet.getInt("spells_per_cast");
                double castDelay = wandSet.getDouble("cast_delay");
                double rechargeTime = wandSet.getDouble("recharge_time");
                int manaMax = wandSet.getInt("mana_max");
                int manaChargeSpeed = wandSet.getInt("mana_charge_speed");
                int capacity = wandSet.getInt("capacity");
                double spread = wandSet.getDouble("spread");
                int wandTier = wandSet.getInt("wand_tier");

                stSpells.setInt(1, id);
                try (
                        ResultSet rsSpells = stSpells.executeQuery()
                ) {

                    // Get all spells for the specific wand from the database
                    List<ISpell> alwaysCast = new ArrayList<>();
                    List<ISpell> spellList = new ArrayList<>();
                    while (rsSpells.next()){
                        int spellId = rsSpells.getInt("spell_id");
                        boolean isAlwaysCast = rsSpells.getBoolean("is_always_cast");
                        int count = rsSpells.getInt("count");
                        ISpell spell = SpellManager.getSpell(spellId);
                        spell.setCount(count);
                        if (isAlwaysCast) alwaysCast.add(spell);
                        else spellList.add(spell);
                    }

                    // Create the wand and set the spells
                    Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);
                    wand.setSpells(spellList);
                    wand.setId(id);

                    wands.add(wand);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return wands;
    }

    @Override
    public void insertWandAsync(Wand wand) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            insertWandSync(wand);
        });
    }

    @Override
    public void insertWandSync(Wand wand) {
        boolean shuffle = wand.isShuffle();
        int spellsPerCast = wand.getSpellsPerCast();
        double castDelay = wand.getCastDelay();
        double rechargeTime = wand.getRechargeTime();
        int manaMax = wand.getManaMax();
        int manaChargeSpeed = wand.getManaChargeSpeed();
        int capacity = wand.getCapacity();
        double spread = wand.getSpread();
        int wandTier = wand.getWandTier();

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement st = conn.prepareStatement("INSERT INTO wands(id, shuffle, spells_per_cast, cast_delay, recharge_time, mana_max, mana_charge_speed, capacity, spread, wand_tier) VALUES(?,?,?,?,?,?,?,?,?,?)");
        ) {
            st.setInt(1, wand.getId());
            st.setBoolean(2, shuffle);
            st.setInt(3, spellsPerCast);
            st.setDouble(4, castDelay);
            st.setDouble(5, rechargeTime);
            st.setInt(6, manaMax);
            st.setInt(7, manaChargeSpeed);
            st.setInt(8, capacity);
            st.setDouble(9, spread);
            st.setInt(10, wandTier);
            st.execute();

            updateWandSpellsAsync(wand);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateWandSpellsAsync(Wand... wands) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            updateWandSpellsSync(wands);
        });
    }

    @Override
    public void updateWandSpellsSync(Wand... wands) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM wand_spells WHERE wand_id = ?");
                PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO wand_spells(wand_id, spell_id, is_always_cast, slot, count) VALUES(?,?,?,?, ?)");
        ) {
            conn.setAutoCommit(false);

            for (Wand wand : wands){
                // Delete old data
                pstmt1.setInt(1, wand.getId());
                int rowsAffected = pstmt1.executeUpdate();

                // Insert new data
                for (int i = 0; i < wand.getAlwaysCast().size(); i++){ // Always cast spells
                    ISpell currentSpell = wand.getSpells().get(i);
                    pstmt2.setInt(1, wand.getId());
                    pstmt2.setInt(2, currentSpell.getId());
                    pstmt2.setBoolean(3, true);
                    pstmt2.setInt(4, i);
                    pstmt2.setInt(5, currentSpell.getCount());
                    rowsAffected += pstmt2.executeUpdate();
                }
                for (int i = 0; i < wand.getSpells().size(); i++){
                    ISpell currentSpell = wand.getSpells().get(i);
                    pstmt2.setInt(1, wand.getId());
                    pstmt2.setInt(2, currentSpell.getId());
                    pstmt2.setBoolean(3, false);
                    pstmt2.setInt(4, i);
                    pstmt2.setInt(5, currentSpell.getCount());
                    rowsAffected += pstmt2.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateWandSpellsAsync(List<Wand> wands) {
        updateWandSpellsAsync(wands.toArray(new Wand[0]));
    }

    @Override
    public void updateWandSpellsSync(List<Wand> wands) {
        updateWandSpellsSync(wands.toArray(new Wand[0]));
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateWandsTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateWandSpellsTable);
    }
}
