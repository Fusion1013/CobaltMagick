package se.fusion1013.plugin.cobalt.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.spells.ISpell;
import se.fusion1013.plugin.cobalt.spells.Spell;
import se.fusion1013.plugin.cobalt.util.Warp;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class is used to access and edit/retrieve values from the database
 */
public abstract class Database {
    Cobalt plugin;
    Connection connection;

    public Database(Cobalt instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    // TODO: Switch to prepared statements
    // TODO: Don't forget to close statements

    // ----- WANDS -----

    /**
     * Returns the wand with the given id
     * @param id id of the wand to find
     * @return a wand
     */
    public Wand getWandByID(int id){
        try {
            Connection conn = getSQLConnection();
            PreparedStatement stWand = conn.prepareStatement("SELECT * FROM wands WHERE id = ?");
            stWand.setInt(1, id);
            ResultSet rsWand = stWand.executeQuery();

            boolean shuffle = rsWand.getBoolean("shuffle");
            int spellsPerCast = rsWand.getInt("spells_per_cast");
            double castDelay = rsWand.getDouble("cast_delay");
            double rechargeTime = rsWand.getDouble("recharge_time");
            int manaMax = rsWand.getInt("mana_max");
            int manaChargeSpeed = rsWand.getInt("mana_charge_speed");
            int capacity = rsWand.getInt("capacity");
            double spread = rsWand.getDouble("spread");
            int wandTier = rsWand.getInt("wand_tier");

            stWand.close();

            PreparedStatement stSpells = conn.prepareStatement("SELECT * FROM wand_spells WHERE wand_id = ?");
            stSpells.setInt(1, id);
            ResultSet rsSpells = stSpells.executeQuery();

            List<ISpell> alwaysCast = new ArrayList<>();
            List<ISpell> spellList = new ArrayList<>();
            while (rsSpells.next()){
                int spellId = rsSpells.getInt("spell_id");
                boolean isAlwaysCast = rsSpells.getBoolean("is_always_cast");
                ISpell spell = Spell.getSpellFromID(spellId);
                if (isAlwaysCast) alwaysCast.add(spell);
                else spellList.add(spell);

                stSpells.close();
            }

            Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);
            wand.setSpells(spellList);

            return wand;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Inserts a new wand into the database. This should only be done ONCE.
     * Once the wand is in the database it should always be accessed through there
     *
     * @param wand The wand to insert into the database
     * @return The id of the wand. Returns -1 if the wand was not able to be inserted into the database.
     */
    public int insertWand(Wand wand){

        boolean shuffle = wand.isShuffle();
        int spellsPerCast = wand.getSpellsPerCast();
        double castDelay = wand.getCastDelay();
        double rechargeTime = wand.getRechargeTime();
        int manaMax = wand.getManaMax();
        int manaChargeSpeed = wand.getManaChargeSpeed();
        int capacity = wand.getCapacity();
        double spread = wand.getSpread();
        int wandTier = wand.getWandTier();

        try {
            Connection conn = getSQLConnection();
            PreparedStatement st = conn.prepareStatement("INSERT INTO wands(shuffle, spells_per_cast, cast_delay, recharge_time, mana_max, mana_charge_speed, capacity, spread, wand_tier) VALUES(?,?,?,?,?,?,?,?,?)");

            st.setBoolean(1, shuffle);
            st.setInt(2, spellsPerCast);
            st.setDouble(3, castDelay);
            st.setDouble(4, rechargeTime);
            st.setInt(5, manaMax);
            st.setInt(6, manaChargeSpeed);
            st.setInt(7, capacity);
            st.setDouble(8, spread);
            st.setInt(9, wandTier);

            st.executeUpdate();
            st.close();
            PreparedStatement st2 = conn.prepareStatement("SELECT COUNT(*) AS total FROM wands");
            ResultSet rs2 = st2.executeQuery();
            int wandId = rs2.getInt("total");
            st2.close();
            return wandId;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // ----- WARPS -----

    /**
     * Deletes the warps with the given name
     * @param name the name of the warp(s)
     * @return the number of deleted warps
     */
    public int deleteWarp(String name){
        try {
            Connection conn = getSQLConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM warps WHERE name = ?");
            st.setString(1, name);
            return st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns a list of all warps
     * @return a list of all warps
     */
    public List<Warp> getWarps(){
        String sql = "SELECT * FROM warps";

        try {
            Connection conn = getSQLConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            List<Warp> warps = new ArrayList<>();

            while (rs.next()){
                String id = rs.getString("id");
                String name = rs.getString("name");
                UUID uuid = UUID.fromString(rs.getString("owner_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("pos_x");
                double y = rs.getDouble("pos_y");
                double z = rs.getDouble("pos_z");
                String privacy = rs.getString("privacy");

                Warp warp = new Warp(name, uuid, new Location(world, x, y, z));
                warp.setPrivacyLevel(privacy);

                warps.add(warp);
            }

            return warps;

        } catch (SQLException e){
            plugin.getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }

        return null;
    }

    /**
     * Returns a list of warps with the given name
     * @param name name of the warps to find
     * @return a list of warps
     */
    public List<Warp> getWarpsByName(String name){
        String sql = "SELECT * FROM warps WHERE name = '" + name + "'";

        try {

            Connection conn = getSQLConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            List<Warp> warps = new ArrayList<>();

            while (rs.next()){
                String id = rs.getString("id");
                UUID uuid = UUID.fromString(rs.getString("owner_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("pos_x");
                double y = rs.getDouble("pos_y");
                double z = rs.getDouble("pos_z");
                String privacy = rs.getString("privacy");

                Warp warp = new Warp(name, uuid, new Location(world, x, y, z));
                warp.setPrivacyLevel(privacy);

                warps.add(warp);
            }

            return warps;
        } catch (SQLException e){
            plugin.getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }
        return null;
    }

    /**
     * Insert a warp into the database
     * @param warp the warp to insert
     */
    public void insertWarp(Warp warp){
        int id = warp.getId();
        String name = warp.getName();
        UUID owner = warp.getOwner();
        Location location = warp.getLocation();
        String privacyLevel = warp.getPrivacyLevel().name().toLowerCase();

        String sql = "INSERT INTO warps(id, name, owner_uuid, world, pos_x, pos_y, pos_z, privacy) VALUES(?,?,?,?,?,?,?,?)";

        int rowsInserted = 0;
        PreparedStatement ps = null;

        try {
            ps = getSQLConnection().prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, owner.toString());
            ps.setString(4, location.getWorld().getName());
            ps.setDouble(5, location.getX());
            ps.setDouble(6, location.getY());
            ps.setDouble(7, location.getZ());
            ps.setString(8, privacyLevel);

            rowsInserted = ps.executeUpdate();

        } catch (SQLException e){
            plugin.getLogger().log(Level.FINE, "SQLException when inserting into database: ", e);
        }

        plugin.getLogger().info("Inserted new warp '" + name + "' into database. " + rowsInserted + " rows inserted");
    }
}
