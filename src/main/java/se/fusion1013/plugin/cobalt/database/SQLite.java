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

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS table_name (" +
            "`player` varchar(32) NOT NULL," +
            "`kills` int(11) NOT NULL," +
            "`total` int(11) NOT NULL," +
            "PRIMARY KEY (`player`)" +
            ");";

    public String SQLiteCreateWarpsTable = "CREATE TABLE IF NOT EXISTS warps (" +
            "`id` varchar(32) NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "`owner_uuid` varchar(32) NOT NULL," +
            "`world` varchar(32) NOT NULL," +
            "`pos_x` float(24) NOT NULL," +
            "`pos_y` float(24) NOT NULL," +
            "`pos_z` float(24) NOT NULL," +
            "`privacy` varchar(32) NOT NULL," +
            "CHECK (privacy in ('public','private'))";
    

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
        executeString(SQLiteCreateTokensTable); // TEST TABLE // TODO: REMOVE WHEN COMPLETED
        executeString(SQLiteCreateWarpsTable);
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
