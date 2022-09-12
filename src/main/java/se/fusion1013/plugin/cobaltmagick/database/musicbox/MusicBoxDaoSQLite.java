package se.fusion1013.plugin.cobaltmagick.database.musicbox;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MusicBoxDaoSQLite extends Dao implements IMusicBoxDao {

    // ----- TABLES & VIEWS -----

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

    // ----- METHODS -----

    @Override
    public void updateMusicBoxMessageAsync(int boxId, String msg) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            updateMusicBoxMessageSync(boxId, msg);
        });
    }

    @Override
    public void updateMusicBoxMessageSync(int boxId, String msg) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("UPDATE music_boxes SET message = ? WHERE id = ?")
            ) {
                ps.setString(1, msg);
                ps.setInt(2, boxId);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public Map<String, MusicBox> getMusicBoxes() {
        Map<String, MusicBox> boxMap = new HashMap<>();

        // TODO: Use location database insertion

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement stBox = conn.prepareStatement("SELECT * FROM music_boxes");
                ResultSet rsBox = stBox.executeQuery();
        ) {
            while (rsBox.next()) {
                double xPos = rsBox.getDouble("pos_x");
                double yPos = rsBox.getDouble("pos_y");
                double zPos = rsBox.getDouble("pos_z");
                String world = rsBox.getString("world");
                String sound = rsBox.getString("sound");
                int id = rsBox.getInt("id");
                MusicBox box = new MusicBox(new Location(Bukkit.getWorld(world), xPos, yPos, zPos), sound, id);

                String message = rsBox.getString("message");
                if (!message.equalsIgnoreCase("")) box.setMessage(message);

                boxMap.put(WorldManager.getFormattedLocation(box.getLocation()), box);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return boxMap;
    }

    @Override
    public void insertMusicBoxAsync(MusicBox musicBox) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            insertMusicBoxSync(musicBox);
        });
    }

    @Override
    public void insertMusicBoxSync(MusicBox musicBox) {
        Location location = musicBox.getLocation();
        double xPos = location.getX();
        double yPos = location.getY();
        double zPos = location.getZ();
        World world = location.getWorld();
        if (world == null) return;
        String worldName = world.getName();

        String sound = musicBox.getSound();
        int id = musicBox.getId();

        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement st = conn.prepareStatement("INSERT INTO music_boxes(world, pos_x, pos_y, pos_z, sound, id, message) VALUES(?,?,?,?,?,?,?)")
            ) {
                st.setString(1, worldName);
                st.setDouble(2, xPos);
                st.setDouble(3, yPos);
                st.setDouble(4, zPos);
                st.setString(5, sound);
                st.setInt(6, id);
                st.setString(7, musicBox.getMessage());
                st.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removeMusicBoxAsync(MusicBox musicBox) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
            removeMusicBoxSync(musicBox);
        });
    }

    @Override
    public void removeMusicBoxSync(MusicBox musicBox) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM music_boxes WHERE id = ?")
            ) {
                ps.setInt(1, musicBox.getId());
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateMusicBoxTable);
    }
}
