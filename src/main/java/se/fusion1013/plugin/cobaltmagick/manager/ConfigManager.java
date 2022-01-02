package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager extends Manager {

    private static ConfigManager INSTANCE;

    private File customConfigFile;
    private FileConfiguration customConfig;

    public static ConfigManager getInstance() {
        if (INSTANCE == null){
            INSTANCE = new ConfigManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    /**
     * Creates a new <code>ConfigManager</code>
     *
     * @param cobaltMagick the plugin
     */
    public ConfigManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
        INSTANCE = this;
    }

    public String getFromConfig(String key){
        return String.valueOf(customConfig.get(key));
    }

    public void writeToConfig(String key, String value){
        Object currentValue = customConfig.get(key);

        if (currentValue instanceof Boolean) customConfig.set(key, Boolean.parseBoolean(value));

        try {
            customConfig.save(customConfigFile);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getCustomConfig(){
        return this.customConfig;
    }

    /**
     * Creates a configuration file if one does not exist. If one does exist, updates it with potentially new keys and populates them with default values.
     * This will not override old configuration values.
     */
    private void updateCustomConfig(){
        File oldConfig = getFileInDataFolder("magick.yml");
        YamlConfiguration oldYamlConfig = new YamlConfiguration();
        try {
            oldYamlConfig.load(oldConfig);
        } catch (IOException |InvalidConfigurationException e){
            e.printStackTrace();
        }
        List<String> keys = new ArrayList<>(oldYamlConfig.getKeys(true));
        Map<String, Object> values = new HashMap<>(oldYamlConfig.getValues(true));

        oldConfig.renameTo(new File(CobaltMagick.getInstance().getDataFolder(), "magick.old.yml"));
        File newConfig = getFileInDataFolder("magick.yml");
        YamlConfiguration newYamlConfig = new YamlConfiguration();
        try {
            newYamlConfig.load(newConfig);
        } catch (IOException |InvalidConfigurationException e){
            e.printStackTrace();
        }

        for (String key : keys){
            newYamlConfig.set(key, values.get(key));

            try{
                newYamlConfig.save(newConfig);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        customConfigFile = newConfig;
        customConfig = newYamlConfig;

        File configToDelete = getFileInDataFolder("magick.old.yml");
        if (configToDelete.exists()) configToDelete.delete();
    }

    private File getFileInDataFolder(String fileName){
        File file = new File(CobaltMagick.getInstance().getDataFolder(), fileName);
        if (!file.exists()){
            file.getParentFile().mkdirs();
            CobaltMagick.getInstance().saveResource(fileName, false);
        }
        return file;
    }

    @Override
    public void reload() {
        updateCustomConfig();
    }

    @Override
    public void disable() {

    }
}
