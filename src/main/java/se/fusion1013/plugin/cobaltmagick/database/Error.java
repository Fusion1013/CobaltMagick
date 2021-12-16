package se.fusion1013.plugin.cobaltmagick.database;

import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.logging.Level;

public class Error {
    public static void execute(CobaltMagick plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(CobaltMagick plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
