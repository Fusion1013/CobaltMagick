package se.fusion1013.plugin.cobaltmagick.database.laser;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.laser.SimpleLaser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LaserDaoSQLite extends Dao implements ILaserDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateLasersTable = "CREATE TABLE IF NOT EXISTS lasers (" +
            "`laser_uuid` varchar(36) PRIMARY KEY" +
            ");";

    public static String SQLiteCreateLasersView = "CREATE VIEW IF NOT EXISTS laser_view AS" +
            " SELECT lasers.laser_uuid, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM lasers" +
            " INNER JOIN locations ON lasers.laser_uuid = locations.uuid;";

    // ----- METHODS -----

    @Override
    public void removeLaserAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            removeLaserSync(uuid);
        });
    }

    @Override
    public void removeLaserSync(UUID uuid) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM lasers WHERE laser_uuid = ?")
            ) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void insertSimpleLaserAsync(SimpleLaser simpleLaser) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            insertSimpleLaserSync(simpleLaser);
        });
    }

    @Override
    public void insertSimpleLaserSync(SimpleLaser simpleLaser) {

        getDataManager().getDao(ILocationDao.class).insertLocation(simpleLaser.getUUID(), simpleLaser.getStartLocation());

        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO lasers (laser_uuid) VALUES(?)")
            ) {
                ps.setString(1, simpleLaser.getUUID().toString());
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public List<SimpleLaser> getSimpleLasers() {
        List<SimpleLaser> lasers = new ArrayList<>();

        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM laser_view");
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("laser_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));
                lasers.add(new SimpleLaser(location, uuid));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lasers;
    }

    @Override
    public String getId() {
        return ILaserDao.super.getId();
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        getDataManager().getSqliteDb().executeString(SQLiteCreateLasersTable);
        getDataManager().getSqliteDb().executeString(SQLiteCreateLasersView);
    }
}
