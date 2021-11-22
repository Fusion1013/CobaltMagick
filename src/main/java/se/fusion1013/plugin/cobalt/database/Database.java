package se.fusion1013.plugin.cobalt.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.util.Warp;

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
    public String table = "table_name";
    public int tokens = 0;

    public Database(Cobalt instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    // TODO: Switch to prepared statements

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

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}
