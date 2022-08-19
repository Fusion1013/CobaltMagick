package se.fusion1013.plugin.cobaltmagick.database.runelock;

import com.sk89q.wepif.PermissionsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.RuneLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RuneLockDaoSQLite extends Dao implements IRuneLockDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateRuneLockTable = "CREATE TABLE IF NOT EXISTS rune_locks (" +
            "`id` INTEGER," +
            "`unlockable_uuid` varchar(36)," +
            "`location_uuid` varchar(36) NOT NULL," +
            "PRIMARY KEY(id)," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateRuneLockItemsTable = "CREATE TABLE IF NOT EXISTS rune_lock_items (" +
            "`lock_id` INTEGER," +
            "`item` varchar(36)," +
            "`slot` INTEGER" +
            ");";

    public static String SQLiteCreateRuneLockView = "CREATE VIEW IF NOT EXISTS rune_lock_view AS" +
            " SELECT rune_locks.id, rune_locks.location_uuid, rune_locks.unlockable_uuid, rune_lock_items.item, rune_lock_items.slot, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM rune_locks" +
            " INNER JOIN locations ON locations.uuid = rune_locks.location_uuid" +
            " INNER JOIN rune_lock_items ON rune_lock_items.lock_id = rune_locks.id;";

    // ----- METHODS -----

    @Override
    public void removeRuneLockAsync(int id) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            removeRuneLockSync(id);
        });
    }

    @Override
    public void removeRuneLockSync(int id) {
        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM rune_locks WHERE id = ?")
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<Integer, RuneLock> getRuneLocks() {
        Map<Integer, RuneLock> runeLockMap = new HashMap<>();

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM rune_lock_view ORDER BY slot ASC");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String item = rs.getString("item");

                RuneLock runeLock = runeLockMap.get(id);
                if (runeLock == null) {
                    World world = Bukkit.getWorld(rs.getString("world"));
                    double x = rs.getDouble("x_pos");
                    double y = rs.getDouble("y_pos");
                    double z = rs.getDouble("z_pos");
                    Location location = new Location(world, x, y, z);

                    IActivatable activatable = WorldManager.getActivatable(UUID.fromString(rs.getString("unlockable_uuid")));

                    UUID locationUUID = UUID.fromString(rs.getString("location_uuid"));

                    runeLock = new RuneLock(location, activatable, id, locationUUID, item);

                    runeLockMap.put(id, runeLock);
                } else {
                    runeLock.addItem(item);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return runeLockMap;
    }

    @Override
    public void insertRuneLockAsync(RuneLock lock) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            insertRuneLockSync(lock);
        });
    }

    @Override
    public void insertRuneLockSync(RuneLock lock) {
        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(lock.getUuid(), lock.getLocation());

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement psLock = conn.prepareStatement("INSERT INTO rune_locks(id, unlockable_uuid, location_uuid) VALUES(?,?,?)");
                PreparedStatement psItems = conn.prepareStatement("INSERT INTO rune_lock_items(lock_id, item, slot) VALUES(?,?,?)")
        ) {
            conn.setAutoCommit(false);

            psLock.setInt(1, lock.getId());
            psLock.setString(2, lock.getActivatable().getUuid().toString());
            psLock.setString(3, lock.getUuid().toString());
            psLock.executeUpdate();

            // Insert all items
            for (int i = 0; i < lock.getItemsNeeded().size(); i++) {
                String item = lock.getItemsNeeded().get(i);
                psItems.setInt(1, lock.getId());
                psItems.setString(2, item);
                psItems.setInt(3, i);
                psItems.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateRuneLockItemsSync(RuneLock lock) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM rune_lock_items WHERE lock_id = ?");
                PreparedStatement psItems = conn.prepareStatement("INSERT INTO rune_lock_items(lock_id, item, slot) VALUES(?,?,?)")
        ) {
            conn.setAutoCommit(false);

            psDelete.setInt(1, lock.getId());
            psDelete.executeUpdate();

            // Insert all items
            for (int i = 0; i < lock.getItemsNeeded().size(); i++) {
                String item = lock.getItemsNeeded().get(i);
                psItems.setInt(1, lock.getId());
                psItems.setString(2, item);
                psItems.setInt(3, i);
                psItems.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateRuneLockItemsAsync(RuneLock lock) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            updateRuneLockItemsSync(lock);
        });
    }

    // ----- UTILITY METHODS -----

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateRuneLockTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateRuneLockItemsTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateRuneLockView);
    }
}
