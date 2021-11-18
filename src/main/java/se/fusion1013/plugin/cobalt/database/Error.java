package se.fusion1013.plugin.cobalt.database;

import se.fusion1013.plugin.cobalt.Cobalt;

import java.util.logging.Level;

public class Error {
    public static void execute(Cobalt plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Cobalt plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
