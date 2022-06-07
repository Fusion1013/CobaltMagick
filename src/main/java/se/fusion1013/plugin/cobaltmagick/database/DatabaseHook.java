package se.fusion1013.plugin.cobaltmagick.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.Database;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.manager.WorldManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.util.Warp;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This class is used to access and edit/retrieve values from the database
 */
public class DatabaseHook {

    // ----- VARIABLES -----

    private static final Database database = CobaltCore.getInstance().getRDatabase();

    // ----- TABLES -----

    public static String SQLiteCreateMusicBoxTable = "CREATE TABLE IF NOT EXISTS music_boxes (" +
            "`world` varchar(32) NOT NULL," +
            "`pos_x` real NOT NULL," +
            "`pos_y` real NOT NULL," +
            "`pos_z` real NOT NULL," +
            "`sound` varchar(32) NOT NULL," +
            "`id` int(11) NOT NULL," +
            "`message` varchar(32) NOT NULL," +
            "PRIMARY KEY (`id`)" +
            ");";

    public static String SQLiteCreateStatuesTable = "CREATE TABLE IF NOT EXISTS statues (" +
            "`world` varchar(32) NOT NULL," +
            "`pos_x` real NOT NULL," +
            "`pos_y` real NOT NULL," +
            "`pos_z` real NOT NULL," +
            "`radius` real NOT NULL," +
            "`mana_recharge` int(11) NOT NULL," +
            "`delay_between_charges` int(11) NOT NULL" +
            ");";

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

    public static String SQLiteCreateDoorsTable = "CREATE TABLE IF NOT EXISTS doors (" +
            "`door_id` varchar(36) PRIMARY KEY," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`width` INTEGER NOT NULL," +
            "`height` INTEGER NOT NULL," +
            "`depth` INTEGER NOT NULL," +
            "`is_closed` INTEGER NOT NULL," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateLockTable = "CREATE TABLE IF NOT EXISTS locks (" +
            "`lock_uuid` varchar(36)," +
            "`unlockable_uuid` varchar(36)," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`item_name` NOT NULL," +
            "PRIMARY KEY(lock_uuid, unlockable_uuid)," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateMessageTable = "CREATE TABLE IF NOT EXISTS messages (" +
            "`message_uuid` varchar(36)," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`text` TEXT NOT NULL," +
            "PRIMARY KEY(message_uuid)" +
            ");";

    // ----- VIEWS -----

    public static String SQLiteCreateDoorsView = "CREATE VIEW IF NOT EXISTS door_view AS" +
            " SELECT doors.door_id, doors.width, doors.height, doors.depth, doors.is_closed, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM doors" +
            " INNER JOIN locations ON locations.uuid = doors.location_uuid;";

    public static String SQLiteCreateLockView = "CREATE VIEW IF NOT EXISTS lock_view AS" +
            " SELECT locks.lock_uuid, locks.unlockable_uuid, locks.item_name, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM locks" +
            " INNER JOIN locations ON locations.uuid = locks.location_uuid;";

    public static void instantiateTables() {
        database.executeString(SQLiteCreateMusicBoxTable);
        database.executeString(SQLiteCreateStatuesTable);
        database.executeString(SQLiteCreateWandsTable);
        database.executeString(SQLiteCreateWandSpellsTable);
        database.executeString(SQLiteCreateDoorsTable);
        database.executeString(SQLiteCreateDoorsView);
        database.executeString(SQLiteCreateLockTable);
        database.executeString(SQLiteCreateLockView);
    }

    // ----- LOCKS -----

    public static void removeLock(UUID uuid) {
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM locks WHERE lock_uuid = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets all locks that are in the database.
     *
     * @return
     */
    public static Map<UUID, ItemLock> getLocks() {
        Map<UUID, ItemLock> locks = new HashMap<>();
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM lock_view");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("lock_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("x_pos");
                double y = rs.getDouble("y_pos");
                double z = rs.getDouble("z_pos");
                Location location = new Location(world, x, y, z);
                CustomItem item = CustomItemManager.getCustomItem(rs.getString("item_name"));
                Unlockable unlockable = WorldManager.getDoor(UUID.fromString(rs.getString("unlockable_uuid"))); // TODO: Replace with getUnlockable method call
                locks.put(uuid, new ItemLock(uuid, location, item, unlockable));
            }
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return locks;
    }

    /**
     * Inserts a lock into the database.
     *
     * @param lock the lock to insert.
     */
    public static void insertLock(ItemLock lock) {

        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(lock.getUuid(), lock.getLocation());

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO locks(lock_uuid, unlockable_uuid, location_uuid, item_name) VALUES(?, ?, ?, ?)");
            ps.setString(1, lock.getUuid().toString());
            ps.setString(2, lock.getUnlockable().getUuid().toString());
            ps.setString(3, lock.getUuid().toString());
            ps.setString(4, lock.getItem().getInternalName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- DOORS -----

    public static void removeDoor(UUID uuid) {
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM doors WHERE door_id = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
            ps.close();

            DataManager.getInstance().getDao(ILocationDao.class).removeLocation(uuid);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateDoorsStatus(MagickDoor[] doors) {
        try {
            Connection conn = database.getSQLConnection();
            conn.setAutoCommit(false);
            for (MagickDoor door : doors) {
                PreparedStatement ps = conn.prepareStatement("UPDATE doors SET is_closed = ? WHERE door_id = ?");
                ps.setBoolean(1, door.isClosed());
                ps.setString(2, door.getUuid().toString());
                ps.close();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertDoor(MagickDoor door) {
        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(door.getUuid(), door.getCorner()); // Insert the doors corner location

        CobaltMagick.getInstance().getLogger().info("Inserting door into database: " + door.getCorner());

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO doors(door_id, location_uuid, width, height, depth, is_closed) VALUES(?, ?, ?, ?, ?, ?)");

            ps.setString(1, door.getUuid().toString());
            ps.setString(2, door.getUuid().toString());
            ps.setInt(3, door.getWidth());
            ps.setInt(4, door.getHeight());
            ps.setInt(5, door.getDepth());
            ps.setBoolean(6, door.isClosed());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<UUID, MagickDoor> getDoors() {
        Map<UUID, MagickDoor> doors = new HashMap<>();
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM door_view");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("door_id"));
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));
                doors.put(uuid, new MagickDoor(uuid, location, rs.getInt("width"), rs.getInt("height"), rs.getInt("depth"), rs.getBoolean("is_closed")));
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return doors;
    }

    // ----- MUSIC BOXES -----

    public static void updateMusicBoxMessage(int boxId, String msg) {
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE music_boxes SET message = ? WHERE id = ?");

            ps.setString(1, msg);
            ps.setInt(2, boxId);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, MusicBox> getMusicBoxes() {
        Map<String, MusicBox> boxMap = new HashMap<>();
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stBox = conn.prepareStatement("SELECT * FROM music_boxes");
            ResultSet rsBox = stBox.executeQuery();

            while (rsBox.next()) {
                MusicBox box = getBoxFromResult(rsBox);
                if (box != null) boxMap.put(WorldManager.getFormattedLocation(box.getLocation()), box);
            }
            CobaltMagick.getInstance().getLogger().info("Loaded " + boxMap.size() + " music boxes from database");
            stBox.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return boxMap;
    }

    private static MusicBox getBoxFromResult(ResultSet boxSet) {
        try {
            double xPos = boxSet.getDouble("pos_x");
            double yPos = boxSet.getDouble("pos_y");
            double zPos = boxSet.getDouble("pos_z");
            String world = boxSet.getString("world");
            String sound = boxSet.getString("sound");
            int id = boxSet.getInt("id");
            MusicBox box = new MusicBox(new Location(Bukkit.getWorld(world), xPos, yPos, zPos), sound, id);

            String message = boxSet.getString("message");
            if (!message.equalsIgnoreCase("")) box.setMessage(message);

            return box;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void insertMusicBox(MusicBox musicBox){
        Location location = musicBox.getLocation();
        double xPos = location.getX();
        double yPos = location.getY();
        double zPos = location.getZ();
        World world = location.getWorld();
        if (world == null) return;
        String worldName = world.getName();

        String sound = musicBox.getSound();
        int id = musicBox.getId();

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement st = conn.prepareStatement("INSERT INTO music_boxes(world, pos_x, pos_y, pos_z, sound, id, message) VALUES(?,?,?,?,?,?,?)");

            st.setString(1, worldName);
            st.setDouble(2, xPos);
            st.setDouble(3, yPos);
            st.setDouble(4, zPos);
            st.setString(5, sound);
            st.setInt(6, id);
            st.setString(7, musicBox.getMessage());

            st.executeUpdate();
            st.close();

            return;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    // ----- WANDS -----

    /**
     * Gets all wands that are currently in the database
     *
     * @return a list of wands
     */
    public static List<Wand> getWands() {
        List<Wand> wands = new ArrayList<>();
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stWand = conn.prepareStatement("SELECT * FROM wands");
            ResultSet rsWand = stWand.executeQuery();

            while (rsWand.next()) {
                wands.add(getWandFromResult(rsWand));
            }
            CobaltMagick.getInstance().getLogger().info("Loaded " + wands.size() + " wands from database");
            stWand.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wands;
    }

    /**
     * Returns the wand with the given id
     * @param id id of the wand to find
     * @return a wand
     */
    public static Wand getWandByID(int id){
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stWand = conn.prepareStatement("SELECT * FROM wands WHERE id = ?");
            stWand.setInt(1, id);
            ResultSet rsWand = stWand.executeQuery();

            Wand wand = getWandFromResult(rsWand);
            stWand.close();

            return wand;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets a wand from a <code>ResultSet</code> and populates it with spells
     *
     * @param wandSet the <code>ResultSet</code> to extract information from
     * @return a new wand
     */
    private static Wand getWandFromResult(ResultSet wandSet) {
        try {
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

            PreparedStatement stSpells = database.getSQLConnection().prepareStatement("SELECT * FROM wand_spells WHERE wand_id = ? ORDER BY slot ASC");
            stSpells.setInt(1, id);
            ResultSet rsSpells = stSpells.executeQuery();

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
            stSpells.close();

            Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);
            wand.setSpells(spellList);
            wand.setId(id);

            return wand;
        } catch (SQLException e) {
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
    public static int insertWand(Wand wand){
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
            Connection conn = database.getSQLConnection();
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

            wand.setId(wandId);
            List<Wand> wands = new ArrayList<>();
            wands.add(wand);
            updateWandSpells(wands); // TODO: Add method that takes only one wand instead of creating new list

            return wandId;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Updates the stored wand spells within the database for all given wands
     *
     * @param wands the wands with the spells to insert into the database
     */
    public static void updateWandSpells(List<Wand> wands){

        String deleteWandSpells = "DELETE FROM wand_spells WHERE wand_id = ?";
        String insertWandSpell = "INSERT INTO wand_spells(wand_id, spell_id, is_always_cast, slot, count) VALUES(?,?,?,?, ?)";

        Connection conn = null;
        PreparedStatement pstmt1 = null, pstmt2 = null;

        try {
            conn = database.getSQLConnection();
            if (conn == null) return;

            conn.setAutoCommit(false);

            for (Wand wand : wands){
                // Delete old data
                pstmt1 = conn.prepareStatement(deleteWandSpells);
                pstmt1.setInt(1, wand.getId());
                int rowsAffected = pstmt1.executeUpdate();

                // Insert new data
                for (int i = 0; i < wand.getAlwaysCast().size(); i++){
                    ISpell currentSpell = wand.getSpells().get(i);
                    pstmt2 = conn.prepareStatement(insertWandSpell);
                    pstmt2.setInt(1, wand.getId());
                    pstmt2.setInt(2, currentSpell.getId());
                    pstmt2.setBoolean(3, true);
                    pstmt2.setInt(4, i);
                    pstmt2.setInt(5, currentSpell.getCount());
                    rowsAffected += pstmt2.executeUpdate();
                }
                for (int i = 0; i < wand.getSpells().size(); i++){
                    ISpell currentSpell = wand.getSpells().get(i);
                    pstmt2 = conn.prepareStatement(insertWandSpell);
                    pstmt2.setInt(1, wand.getId());
                    pstmt2.setInt(2, currentSpell.getId());
                    pstmt2.setBoolean(3, false);
                    pstmt2.setInt(4, i);
                    pstmt2.setInt(5, currentSpell.getCount());
                    rowsAffected += pstmt2.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) conn.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
}
