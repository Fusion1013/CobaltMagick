package se.fusion1013.plugin.cobaltmagick.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public class MagickConfigManager extends Manager {

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>MagickConfigManager</code>
     *
     * @param cobaltCore the plugin
     */
    public MagickConfigManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- LOGIC -----

    /**
     * Creates all the configuration files for <code>CobaltMagick</code>.
     */
    private void createConfig() {
        ConfigManager.getInstance().updateCustomConfig(CobaltMagick.getInstance(), "magick.yml");
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        createConfig();
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static MagickConfigManager INSTANCE;
    /**
     * Returns the object representing this <code>MagickConfigManager</code>.
     *
     * @return The object of this class
     */
    public static MagickConfigManager getInstance() {
        if (INSTANCE == null){
            INSTANCE = new MagickConfigManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
