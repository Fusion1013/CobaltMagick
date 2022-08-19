package se.fusion1013.plugin.cobaltmagick.database.door;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoorDaoSQLite extends Dao implements IDoorDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateDoorsTable = "CREATE TABLE IF NOT EXISTS doors (" +
            "`door_id` varchar(36) PRIMARY KEY," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`width` INTEGER NOT NULL," +
            "`height` INTEGER NOT NULL," +
            "`depth` INTEGER NOT NULL," +
            "`is_closed` INTEGER NOT NULL," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateDoorsView = "CREATE VIEW IF NOT EXISTS door_view AS" +
            " SELECT doors.door_id, doors.width, doors.height, doors.depth, doors.is_closed, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM doors" +
            " INNER JOIN locations ON locations.uuid = doors.location_uuid;";

    // ----- METHODS -----

    @Override
    public void removeDoorAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            removeDoorSync(uuid);
        });
    }

    @Override
    public void removeDoorSync(UUID uuid) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM doors WHERE door_id = ?")
        ) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updateDoorStatusAsync(MagickDoor[] doors) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            updateDoorStatusSync(doors);
        });
    }

    @Override
    public void updateDoorStatusSync(MagickDoor[] doors) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE doors SET is_closed = ? WHERE door_id = ?")
        ) {
            conn.setAutoCommit(false);
            // Loop through all doors and update their status
            for (MagickDoor door : doors) {
                ps.setBoolean(1, door.isClosed());
                ps.setString(2, door.getUuid().toString());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insertDoorAsync(MagickDoor door) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            insertDoorSync(door);
        });
    }

    @Override
    public void insertDoorSync(MagickDoor door) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO doors(door_id, location_uuid, width, height, depth, is_closed) VALUES(?, ?, ?, ?, ?, ?)");
                PreparedStatement psLocation = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)")
        ) {
            // Insert location
            psLocation.setString(1, door.getUuid().toString());
            psLocation.setString(2, door.getCorner().getWorld().getName());
            psLocation.setDouble(3, door.getCorner().getX());
            psLocation.setDouble(4, door.getCorner().getY());
            psLocation.setDouble(5, door.getCorner().getZ());
            psLocation.setDouble(6, door.getCorner().getYaw());
            psLocation.setDouble(7, door.getCorner().getPitch());
            psLocation.execute();

            ps.setString(1, door.getUuid().toString());
            ps.setString(2, door.getUuid().toString());
            ps.setInt(3, door.getWidth());
            ps.setInt(4, door.getHeight());
            ps.setInt(5, door.getDepth());
            ps.setBoolean(6, door.isClosed());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Map<UUID, MagickDoor> getDoors() {
        Map<UUID, MagickDoor> doors = new HashMap<>();

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM door_view");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("door_id"));
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));
                doors.put(uuid, new MagickDoor(uuid, location, rs.getInt("width"), rs.getInt("height"), rs.getInt("depth"), rs.getBoolean("is_closed")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return doors;
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateDoorsTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateDoorsView);
    }
}
