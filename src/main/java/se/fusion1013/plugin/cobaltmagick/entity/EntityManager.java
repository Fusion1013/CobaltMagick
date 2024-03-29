package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.entity.create.*;
import se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand.SentientWand;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

import java.util.Random;

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
    public static final ICustomEntity ORC_HIVEMIND = OrcHivemind.register();
    public static final ICustomEntity ORC_BOMBER = OrcBomber.register();
    public static final ICustomEntity ORC_ARBALIST = OrcArbalist.register();
    public static final ICustomEntity ORC_RAVAGER_RIDER = OrcRavagerRider.register();

    public static final ICustomEntity SENTIENT_WAND = SentientWand.register();
    public static final ICustomEntity HIGH_ALCHEMIST = HighAlchemist.register();
    public static final ICustomEntity APPRENTICE = Apprentice.register();
    public static final ICustomEntity CURSE_MAGE = CurseMage.register();
    public static final ICustomEntity TELEPORT_MAGE = TeleportMage.register();

    public static final ICustomEntity MIMIC = Mimic.register();

    // Dragons
    // public static final ICustomEntity GREEN_DRAGON_MINION = GreenDragonMinion.register();
    // public static final ICustomEntity GREEN_DRAGONLORD = GreenDragonlord.register();

    // ----- LISTENERS ----- // TODO: Move into Core

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.WARDEN) {
            Random r = new Random();
            if (r.nextDouble() > .75) event.getDrops().add(ItemManager.ECHO_INGOT.getItemStack()); // TODO: Move into Cobalt Core
        }

        if (event.getEntity().getType() == EntityType.SHULKER) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
            event.getDrops().add(new ItemStack(Material.SHULKER_SHELL));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Allay allay) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.AMETHYST_SHARD) {
                PlayerUtil.reduceHeldItemStack(player, 1);
                allay.getWorld().spawn(allay.getLocation(), Allay.class);
                allay.getWorld().playSound(allay.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1, 1);
                allay.getWorld().spawnParticle(Particle.COMPOSTER, allay.getLocation(), 5, .2, .2, .2, 0);

                event.setCancelled(true);
            }
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
