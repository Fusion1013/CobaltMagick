package se.fusion1013.cobaltmagick;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltmagick.commands.DoorCommand;
import se.fusion1013.cobaltmagick.commands.SpellCommand;
import se.fusion1013.cobaltmagick.item.properties.MagickItemPropertyManager;
import se.fusion1013.cobaltmagick.spell.SpellManager;
import se.fusion1013.cobaltmagick.wand.WandManager;

public final class CobaltMagick extends JavaPlugin implements CobaltPlugin {

    private static CobaltMagick INSTANCE;

    public CobaltMagick() {
        INSTANCE = this;
    }

    @Override
    public String getPrefix() {
        return "prefix.magick";
    }

    @Override
    public String getInternalName() {
        return "cobalt_magick";
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        CobaltCore.getInstance().disableCobaltPlugin(this);
    }

    public static CobaltMagick getInstance() {
        return INSTANCE;
    }

    // ----- COMMANDS -----

    /**
     * Registers all Cobalt commands
     */
    @Override
    public void registerCommands() {
        SpellCommand.register();
        DoorCommand.register();
    }

    // ----- MANAGERS -----

    /**
     * Reloads all Cobalt managers
     */
    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickItemPropertyManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), WandManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), SpellManager.class);
    }

    // ----- LISTENERS -----

    @Override
    public void registerListeners() {
    }

    // ----- POST INIT -----

    @Override
    public void postInit() {
    }

    @Override
    public void initDatabaseTables() {
    }
}
