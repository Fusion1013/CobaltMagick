package se.fusion1013.plugin.cobaltmagick;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.commands.cgive.CGiveCommand;
import se.fusion1013.plugin.cobaltmagick.commands.*;
import se.fusion1013.plugin.cobaltmagick.database.system.MagickDataManager;
import se.fusion1013.plugin.cobaltmagick.entity.EntityManager;
import se.fusion1013.plugin.cobaltmagick.eyes.CrystalSong;
import se.fusion1013.plugin.cobaltmagick.gui.AbstractGUIListener;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.item.enchantments.MagickEnchantment;
import se.fusion1013.plugin.cobaltmagick.item.enchantments.MagickEnchantmentManager;
import se.fusion1013.plugin.cobaltmagick.storage.MagickObjectManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.CauldronManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.laser.LaserManager;
import se.fusion1013.plugin.cobaltmagick.manager.*;
import se.fusion1013.plugin.cobaltmagick.scene.SceneManager;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.wand.WandEvents;
import se.fusion1013.plugin.cobaltmagick.wand.WandManager;
import se.fusion1013.plugin.cobaltmagick.world.WorldEvents;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickStructureManager;

public final class CobaltMagick extends JavaPlugin implements CobaltPlugin {

    private static CobaltMagick INSTANCE;

    static final int BUKKIT_DEV_ID = 313786;

    public CobaltMagick(){
        INSTANCE = this;
    }

    @Override
    public String getPrefix() {
        return "prefix.magick";
    }

    @Override
    public void onLoad(){
        WorldGuardManager.initialize(); // Registers WorldGuard flags
    } // TODO: Move worldguard to Core

    @Override
    public void onEnable() {
        CobaltCore.getInstance().registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        CobaltCore.getInstance().disableCobaltPlugin(this);
    }

    public static CobaltMagick getInstance(){
        return INSTANCE;
    }

    // ----- COMMANDS -----

    /**
     * Registers all Cobalt commands
     */
    @Override
    public void registerCommands() {
        CGiveCommand.register();
        KillSpellsCommand.register();
        MagickCommand.register();
        DirectionalPersonalParticle.register();
        ArmorStandTestCommand.register();
    }

    // ----- MANAGERS -----

    /**
     * Reloads all Cobalt managers
     */
    @Override
    public void reloadManagers(){
        CobaltCore.getInstance().getManager(this, MagickDataManager.class);
        CobaltCore.getInstance().getManager(this, LaserManager.class);
        CobaltCore.getInstance().getManager(this, SpellManager.class);

        // TODO: Move worldguard integration to core
        if (WorldGuardManager.isEnabled()) CobaltCore.getInstance().getManager(this, WorldGuardManager.class); // TODO: Add isEnabled method to all managers and move check to registration

        CobaltCore.getInstance().getManager(this, MagickConfigManager.class);
        CobaltCore.getInstance().getManager(this, ItemManager.class);
        CobaltCore.getInstance().getManager(this, DreamManager.class);
        CobaltCore.getInstance().getManager(this, EntityManager.class);
        CobaltCore.getInstance().getManager(this, WandManager.class);
        CobaltCore.getInstance().getManager(this, WorldManager.class);
        CobaltCore.getInstance().getManager(this, SceneManager.class);
        CobaltCore.getInstance().getManager(this, MagickStructureManager.class);
        CobaltCore.getInstance().getManager(this, CauldronManager.class);
        CobaltCore.getInstance().getManager(this, MagickSettingsManager.class);
        CobaltCore.getInstance().getManager(this, MagickEnchantmentManager.class);

        CobaltCore.getInstance().reloadPluginIntegrationManager("CrazyAdvancementsAPI", this, MagickAdvancementManager.class);

        CobaltCore.getInstance().getManager(this, MagickObjectManager.class);
    }

    // ----- LISTENERS -----

    @Override
    public void registerListeners() {
        // TODO: Move to managers
        getServer().getPluginManager().registerEvents(new AbstractGUIListener(), this);
        getServer().getPluginManager().registerEvents(new WandEvents(), this);
        getServer().getPluginManager().registerEvents(new CrystalSong(), this);
        getServer().getPluginManager().registerEvents(new WorldEvents(), this);
    }

    // ----- POST INIT -----

    @Override
    public void postInit() {}

    @Override
    public void initDatabaseTables() {
    }
}
