package se.fusion1013.cobaltmagick;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionManager;
import se.fusion1013.cobaltmagick.commands.AlchemyCommand;
import se.fusion1013.cobaltmagick.commands.DoorCommand;
import se.fusion1013.cobaltmagick.commands.SpellCommand;
import se.fusion1013.cobaltmagick.components.actions.MagickActionManager;
import se.fusion1013.cobaltmagick.components.conditions.MagickConditionManager;
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

    @Override
    public boolean isHidden() {
        return true;
    }

    // ----- COMMANDS -----

    /**
     * Registers all Cobalt commands
     */
    @Override
    public void registerCommands() {
        SpellCommand.register();
        DoorCommand.register();
        AlchemyCommand.register();
    }

    // ----- MANAGERS -----

    /**
     * Reloads all Cobalt managers
     */
    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickConditionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickActionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickItemPropertyManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), WandManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), SpellManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), PotionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), AlchemyManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), CauldronManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), ElementalVeinManager.class);
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
