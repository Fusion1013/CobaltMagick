package se.fusion1013.plugin.cobaltmagick.database.hidden;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenObject;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HiddenObjectDaoSQLite extends Dao implements IHiddenObjectDao {

    // ----- TABLES & VIEWS -----

    public static String SQLiteCreateHiddenParticleTable = "CREATE TABLE IF NOT EXISTS hidden_objects (" +
            "`uuid` varchar(36) PRIMARY KEY," +
            "`has_particle_group` BOOLEAN NOT NULL," +
            "`particle_group_name` TEXT," +
            "`has_item` BOOLEAN NOT NULL," +
            "`item` TEXT," +
            "`has_wand` BOOLEAN NOT NULL," +
            "`wand_tier` INTEGER," +
            "`reveal_method` TEXT NOT NULL," +
            "`is_revealed` BOOLEAN NOT NULL," +
            "`delete_on_activation` BOOLEAN NOT NULL" +
            ");";

    public static String SQLiteCreateHiddenParticleView = "CREATE VIEW IF NOT EXISTS hidden_objects_view AS" +
            " SELECT hidden_objects.uuid, hidden_objects.has_particle_group, hidden_objects.particle_group_name, hidden_objects.has_item, hidden_objects.item, hidden_objects.has_wand, hidden_objects.wand_tier, hidden_objects.reveal_method, hidden_objects.is_revealed, hidden_objects.delete_on_activation, locations.world, locations.x_pos, locations.y_pos, locations.z_pos" +
            " FROM hidden_objects" +
            " INNER JOIN locations ON hidden_objects.uuid = locations.uuid";

    // ----- METHODS -----

    @Override
    public void removeHiddenObjectSync(UUID uuid) {

        getDataManager().getDao(ILocationDao.class).removeLocationSync(uuid);

        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM hidden_objects WHERE uuid = ?")
        ) {
            ps.setString(1, uuid.toString());
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeHiddenObjectAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            removeHiddenObjectSync(uuid);
        });
    }

    @Override
    public void insertHiddenObjectSync(HiddenObject hiddenObject) {
        getDataManager().getDao(ILocationDao.class).insertLocation(hiddenObject.getUuid(), hiddenObject.getLocation());

        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO hidden_objects (uuid, has_particle_group, particle_group_name, has_item, item, has_wand, wand_tier, reveal_method, is_revealed, delete_on_activation) VALUES(?,?,?,?,?,?,?,?,?,?)")
        ) {
            ps.setString(1, hiddenObject.getUUID().toString());
            ps.setBoolean(2, hiddenObject.hasParticleGroup());
            ps.setString(3, hiddenObject.getParticleGroupName());
            ps.setBoolean(4, hiddenObject.spawnsItem());
            ps.setString(5, hiddenObject.getItem());
            ps.setBoolean(6, hiddenObject.spawnsWand());
            ps.setInt(7, hiddenObject.getWandLevel());
            ps.setString(8, hiddenObject.getRevealMethod().toString());
            ps.setBoolean(9, hiddenObject.isActive());
            ps.setBoolean(10, hiddenObject.deleteOnActivation());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void insertHiddenObjectAsync(HiddenObject hiddenObject) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            insertHiddenObjectSync(hiddenObject);
        });
    }

    @Override
    public List<HiddenObject> getHiddenParticles() {
        List<HiddenObject> hiddenObjects = new ArrayList<>();

        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM hidden_objects_view");
                ResultSet rs = ps.executeQuery())
        {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, rs.getDouble("x_pos"), rs.getDouble("y_pos"), rs.getDouble("z_pos"));
                boolean isRevealed = rs.getBoolean("is_revealed");
                RevealMethod revealMethod = RevealMethod.valueOf(rs.getString("reveal_method"));
                HiddenObject hiddenObject = new HiddenObject(location, revealMethod, uuid, isRevealed);

                boolean hasParticleGroup = rs.getBoolean("has_particle_group");
                if (hasParticleGroup) {
                    String particleGroupName = rs.getString("particle_group_name");
                    ParticleGroup particleGroup = ParticleGroupManager.getParticleGroup(particleGroupName);
                    hiddenObject.setParticleGroup(particleGroup);
                }

                boolean hasItem = rs.getBoolean("has_item");
                if (hasItem) {
                    String item = rs.getString("item");
                    hiddenObject.setItemSpawn(item);
                }

                boolean hasWand = rs.getBoolean("has_wand");
                if (hasWand) {
                    int wandTier = rs.getInt("wand_tier");
                    hiddenObject.setWandSpawn(wandTier);
                }

                boolean deleteOnActivation = rs.getBoolean("delete_on_activation");
                hiddenObject.setDeleteOnActivation(deleteOnActivation);

                hiddenObjects.add(hiddenObject);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return hiddenObjects;
    }

    @Override
    public void updateHiddenObject(HiddenObject hiddenObject) {
        try (
                Connection conn = getDataManager().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE hidden_objects SET has_particle_group = ?, particle_group_name = ?, has_item = ?, item = ?, has_wand = ?, wand_tier = ?, delete_on_activation = ? WHERE uuid = ?")
        ) {
            ps.setBoolean(1, hiddenObject.hasParticleGroup());
            ps.setString(2, hiddenObject.getParticleGroupName());
            ps.setBoolean(3, hiddenObject.spawnsItem());
            ps.setString(4, hiddenObject.getItem());
            ps.setBoolean(5, hiddenObject.spawnsWand());
            ps.setInt(6, hiddenObject.getWandLevel());
            ps.setBoolean(7, hiddenObject.deleteOnActivation());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        getDataManager().getSqliteDb().executeString(SQLiteCreateHiddenParticleTable);
        getDataManager().getSqliteDb().executeString(SQLiteCreateHiddenParticleView);
    }
}
