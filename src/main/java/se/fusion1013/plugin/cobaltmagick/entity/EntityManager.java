package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.entity.create.*;
import se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand.SentientWand;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

public class EntityManager extends Manager implements Listener {

    // ----- CONSTRUCTORS -----

    public EntityManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- REGISTER -----

    public static final ICustomEntity ORC = Orc.register();
    public static final ICustomEntity ORC_BRUTE = OrcBrute.register();
    public static final ICustomEntity ORC_ARCHER = OrcArcher.register();
    public static final ICustomEntity ORC_FLAMESPITTER = OrcFlamespitter.register();
    public static final ICustomEntity SENTIENT_WAND = SentientWand.register();
    public static final ICustomEntity HIGH_ALCHEMIST = HighAlchemist.register();
    public static final ICustomEntity APPRENTICE = Apprentice.register();

    // ----- LISTENERS ----- // TODO: Move into Core

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.WARDEN) {
            event.getDrops().add(ItemManager.ECHO_INGOT.getItemStack()); // TODO: Move into Cobalt Core
        }
    }

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {
    }

    // ----- INSTANCE -----

    private static EntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>EntityManager</code>.
     *
     * @return The object of this class
     */
    public static EntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new EntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
