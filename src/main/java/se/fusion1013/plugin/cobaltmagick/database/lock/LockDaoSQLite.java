package se.fusion1013.plugin.cobaltmagick.database.lock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockDaoSQLite extends Dao implements ILockDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateLockTable = "CREATE TABLE IF NOT EXISTS locks (" +
            "`lock_uuid` varchar(36)," +
            "`unlockable_uuid` varchar(36)," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`item_name` NOT NULL," +
            "PRIMARY KEY(lock_uuid, unlockable_uuid)," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateLockView = "CREATE VIEW IF NOT EXISTS lock_view AS" +
            " SELECT locks.lock_uuid, locks.unlockable_uuid, locks.item_name, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM locks" +
            " INNER JOIN locations ON locations.uuid = locks.location_uuid;";

    // ----- METHODS -----

    @Override
    public void removeLockAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            removeLockSync(uuid);
        });
    }

    @Override
    public void removeLockSync(UUID uuid) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM locks WHERE lock_uuid = ?")
        ) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<UUID, ItemLock> getLocks() {
        Map<UUID, ItemLock> locks = new HashMap<>();

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM lock_view");
                ResultSet rs = ps.executeQuery()
        ) {
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return locks;
    }

    @Override
    public void insertLockAsync(ItemLock lock) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            insertLockSync(lock);
        });
    }

    @Override
    public void insertLockSync(ItemLock lock) {
        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(lock.getUuid(), lock.getLocation());

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO locks(lock_uuid, unlockable_uuid, location_uuid, item_name) VALUES(?, ?, ?, ?)")
        ) {
            ps.setString(1, lock.getUuid().toString());
            ps.setString(2, lock.getUnlockable().getUuid().toString());
            ps.setString(3, lock.getUuid().toString());
            ps.setString(4, lock.getItem().getInternalName());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- UTIL -----

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateLockTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateLockView);
    }
}
