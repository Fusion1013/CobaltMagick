package se.fusion1013.plugin.cobalt.database;

import org.bukkit.Location;
import se.fusion1013.plugin.cobalt.Cobalt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * This class is responsible for creating the database and setting up the tables and values
 */
public class SQLite extends Database {
    String dbname;
    public SQLite(Cobalt plugin){
        super(plugin);
        dbname = plugin.getConfig().getString("SQLite.Filename", "cobalt");
    }

    // TODO: ID is currently not implemented
    public String SQLiteCreateWarpsTable = "CREATE TABLE IF NOT EXISTS warps (" +
            "`id` INTEGER NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "`owner_uuid` varchar(32) NOT NULL," +
            "`world` varchar(32) NOT NULL," +
            "`pos_x` real NOT NULL," +
            "`pos_y` real NOT NULL," +
            "`pos_z` real NOT NULL," +
            "`privacy` varchar(32) NOT NULL," +
            "PRIMARY KEY (`name`)," +
            "CHECK (privacy in ('public','private'))" +
            ");";

    public String SQLiteCreateWandsTable = "CREATE TABLE IF NOT EXISTS wands (" +
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
            ");"; // Always cast spells and spells will be stored in a separate table

    public String SQLiteCreateWandSpellsTable = "CREATE TABLE IF NOT EXISTS wand_spells (" +
            "`wand_id` int(11) NOT NULL," +
            "`spell_id` INTEGER NOT NULL," +
            "`is_always_cast` boolean NOT NULL," +
            "`slot` INTEGER NOT NULL," +
            "PRIMARY KEY (wand_id, spell_id, slot)," +
            "FOREIGN KEY (wand_id) REFERENCES wands(id)" +
            ");";
    

    public Connection getSQLConnection(){
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()){
            try {
                plugin.getDataFolder().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e){
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db", e);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            System.out.println(dbname);
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex){
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    public void load(){
        executeString(SQLiteCreateWarpsTable);
        executeString(SQLiteCreateWandsTable);
        executeString(SQLiteCreateWandSpellsTable);
    }

    /**
     * Performs the string statement on the database
     * @param string Statement to execute.
     */
    private void executeString(String string){
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(string);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
