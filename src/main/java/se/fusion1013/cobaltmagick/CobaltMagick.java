package se.fusion1013.cobaltmagick;

import fr.skytasul.glowingentities.GlowingEntities;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionManager;
import se.fusion1013.cobaltmagick.alchemy.ritual.RitualManager;
import se.fusion1013.cobaltmagick.alchemy.transmutation.service.TransmutationService;
import se.fusion1013.cobaltmagick.commands.*;
import se.fusion1013.cobaltmagick.components.actions.MagickActionManager;
import se.fusion1013.cobaltmagick.components.conditions.MagickConditionManager;
import se.fusion1013.cobaltmagick.database.MagickDataManager;
import se.fusion1013.cobaltmagick.enchantments.EnchantmentManager;
import se.fusion1013.cobaltmagick.foundry.FoundryManager;
import se.fusion1013.cobaltmagick.item.properties.MagickItemPropertyManager;
import se.fusion1013.cobaltmagick.pedestal.PedestalManager;
import se.fusion1013.cobaltmagick.special.chess.ChessManager;
import se.fusion1013.cobaltmagick.special.well.WellManager;
import se.fusion1013.cobaltmagick.spell.SpellManager;
import se.fusion1013.cobaltmagick.wand.WandManager;

public final class CobaltMagick extends JavaPlugin implements CobaltPlugin {

    private static CobaltMagick INSTANCE;
    private static GlowingEntities GLOWING_ENTITIES;

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
        GLOWING_ENTITIES = new GlowingEntities(this);
    }

    @Override
    public void onDisable() {
        CobaltCore.getInstance().disableCobaltPlugin(this);
        GLOWING_ENTITIES.disable();
    }

    public static CobaltMagick getInstance() {
        return INSTANCE;
    }

    public static GlowingEntities getGlowingEntities() {
        return GLOWING_ENTITIES;
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
        WellCommand.register();
        PedestalCommand.register();
        FoundryCommand.register();
        ArrowRainCommand.register();
        WandCommand.register();
        CycleCommand.register();
        ChessCommand.register();
    }

    // ----- MANAGERS -----

    /**
     * Reloads all Cobalt managers
     */
    @Override
    public void reloadManagers() {
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickDataManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickConditionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickActionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), MagickItemPropertyManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), WandManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), SpellManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), PotionManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), AlchemyManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), CauldronManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), CauldronEffectManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), ElementalVeinManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), WellManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), FoundryManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), PedestalManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), EnchantmentManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), TransmutationService.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), ChessManager.class);
        CobaltCore.getInstance().getManager(CobaltMagick.getInstance(), RitualManager.class);
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
