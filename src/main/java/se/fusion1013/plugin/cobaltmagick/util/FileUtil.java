package se.fusion1013.plugin.cobaltmagick.util;

import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> listFilesForFolder(final File folder) {
        List<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry);
            }
        }
        return files;
    }

    public static List<String> listFileNamesForFolder(final File folder){
        List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFileNamesForFolder(fileEntry);
            } else {
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

    public static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = CobaltMagick.getInstance().getClass().getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private static final boolean spigot = classExists("org.spigotmc.SpigotConfig");

    /**
     * Whether or not this server is running Spigot or a Spigot fork. This works by checking
     * if the SpigotConfig exists, which should be true of all forks.
     * @return True if it is, false if not.
     */
    public static boolean isUsingSpigot() {
        return false;
        // return spigot;
    }
}
