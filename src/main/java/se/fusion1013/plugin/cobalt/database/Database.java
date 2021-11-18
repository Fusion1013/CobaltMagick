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

    public List<Warp> getWarps(){
        String sql = "SELECT * FROM warps";

        plugin.getLogger().info("Getting warps...");

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

                plugin.getLogger().info("Name: " + name + ", UUID: " + uuid + ", World: " + world + ", Location: " + x + ", " + y + ", " + z);

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

    public Integer getTokens(String string){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + string + "';");

            rs = ps.executeQuery();
            while (rs.next()){
                if (rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
                    return rs.getInt("kills");
                }
            }
        } catch (SQLException ex){
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex){
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public Integer getTotal(String string){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + string + "';");

            rs = ps.executeQuery();
            while (rs.next()){
                if (rs.getString("player").equalsIgnoreCase(string.toLowerCase())){
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex){
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex){
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public void setTokens(Player player, Integer tokens, Integer total) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (player,kills,total) VALUES(?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.setString(1, player.getName().toLowerCase());                                             // YOU MUST put these into this line!! And depending on how many
            // colums you put (say you made 5) All 5 need to be in the brackets
            // Seperated with comma's (,) AND there needs to be the same amount of
            // question marks in the VALUES brackets. Right now i only have 3 colums
            // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)

            ps.setInt(2, tokens); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
            // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
            // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
            // Tokens from another plugin :/
            ps.setInt(3, total);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
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
