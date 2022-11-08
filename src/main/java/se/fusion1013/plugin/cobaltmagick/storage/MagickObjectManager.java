package se.fusion1013.plugin.cobaltmagick.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.EntityDeathEvent;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;
import se.fusion1013.plugin.cobaltmagick.world.structures.RewardPedestal;
import se.fusion1013.plugin.cobaltmagick.world.structures.crafting.MagickAnvil;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenObject;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenParticle;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.MaterialLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.RuneLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MeditationPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.SpellPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.MultiActivatable;
import se.fusion1013.plugin.cobaltmagick.world.structures.trap.TrappedChestEntity;

public class MagickObjectManager extends Manager implements Listener {

    // ----- OBJECT REGISTER -----

    // Portals
    public static IStorageObject MAGICK_PORTAL = ObjectManager.registerDefaultStorage(new MagickPortal(null, null));
    public static IStorageObject MEDITATION_PORTAL = ObjectManager.registerDefaultStorage(new MeditationPortal(null, null));
    public static IStorageObject SPELL_PORTAL = ObjectManager.registerDefaultStorage(new SpellPortal(null, null, null));

    // Trapped Chests
    public static IStorageObject TRAPPED_CHEST_ENTITY = ObjectManager.registerDefaultStorage(new TrappedChestEntity(null, "mimic"));

    // Locks
    public static IStorageObject MATERIAL_LOCK = ObjectManager.registerDefaultStorage(new MaterialLock());
    public static IStorageObject RUNE_LOCK = ObjectManager.registerDefaultStorage(new RuneLock());
    public static IStorageObject ITEM_LOCK = ObjectManager.registerDefaultStorage(new ItemLock());

    // Activatables
    public static IStorageObject MULTI_ACTIVATABLE = ObjectManager.registerDefaultStorage(new MultiActivatable());

    // Hidden
    public static IStorageObject HIDDEN_PARTICLE = ObjectManager.registerDefaultStorage(new HiddenParticle());
    public static IStorageObject HIDDEN_OBJECT = ObjectManager.registerDefaultStorage(new HiddenObject());

    // Doors
    public static IStorageObject MAGICK_DOOR = ObjectManager.registerDefaultStorage(new MagickDoor());

    // Crafting Structures
    public static IStorageObject MAGICK_ANVIL = ObjectManager.registerDefaultStorage(new MagickAnvil());

    // Reward
    public static IStorageObject REWARD_PEDESTAL = ObjectManager.registerDefaultStorage(new RewardPedestal());

    // ----- CONSTRUCTORS -----

    public MagickObjectManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- EVENTS -----

    @EventHandler
    public void onItemDrop(ItemSpawnEvent event) {
        IStorageObject[] magickAnvilObjects = ObjectManager.getLoadedObjectsOfType(MAGICK_ANVIL.getObjectIdentifier());
        for (IStorageObject object : magickAnvilObjects) object.onTrigger(event.getEntity(), event.getLocation());
    }

    @EventHandler
    public void onSpellCast(SpellCastEvent event) {
        if (event.getSpell().getId() == SpellManager.ALL_SEEING_EYE.getId()) {
            revealNearbyHiddenParticles(RevealMethod.ALL_SEEING_EYE, event.getSpell().getCaster().getLocation()); // TODO: Replace with actual spell location
        }

        IStorageObject[] spellPortalObjects = ObjectManager.getLoadedObjectsOfType(SPELL_PORTAL.getObjectIdentifier());
        for (IStorageObject object : spellPortalObjects) object.onTrigger(event.getSpell());
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        revealNearbyHiddenParticles(RevealMethod.PROXIMITY, event.getPlayer().getLocation());

        IStorageObject[] rewardPedestalObjects = ObjectManager.getLoadedObjectsOfType(REWARD_PEDESTAL.getObjectIdentifier());
        for (IStorageObject object : rewardPedestalObjects) object.onTrigger("player_move", event);
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        IStorageObject[] rewardPedestalObjects = ObjectManager.getLoadedObjectsOfType(REWARD_PEDESTAL.getObjectIdentifier());
        for (IStorageObject object : rewardPedestalObjects) object.onTrigger("entity_hit", event);
    }

    private static void revealNearbyHiddenParticles(RevealMethod revealMethod, Location location) {
        IStorageObject[] hiddenObjects = ObjectManager.getLoadedObjectsOfType(HIDDEN_OBJECT.getObjectIdentifier());
        for (IStorageObject object : hiddenObjects) object.onTrigger(location, revealMethod);
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
        // Material Locks
        IStorageObject[] materialLockObjects = ObjectManager.getLoadedObjectsOfType(MATERIAL_LOCK.getObjectIdentifier());
        for (IStorageObject materialLockObject : materialLockObjects) {
            materialLockObject.onTrigger();
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        // Material Locks
        IStorageObject[] materialLockObjects = ObjectManager.getLoadedObjectsOfType(MATERIAL_LOCK.getObjectIdentifier());
        for (IStorageObject materialLockObject : materialLockObjects) {
            Bukkit.getScheduler().runTaskLater(core, () -> materialLockObject.onTrigger(), 1);
        }

        if (event.getBlock().getType() == Material.TRAPPED_CHEST) {
            // Trapped Chests
            IStorageObject[] chestEntityObjects = ObjectManager.getLoadedObjectsOfType(TRAPPED_CHEST_ENTITY.getObjectIdentifier());
            for (IStorageObject chestEntity : chestEntityObjects) chestEntity.onTrigger("chest_break", event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void customEntityDeathEvent(EntityDeathEvent event) {
        // Trapped Chests
        IStorageObject[] chestEntityObjects = ObjectManager.getLoadedObjectsOfType(TRAPPED_CHEST_ENTITY.getObjectIdentifier());
        for (IStorageObject chestEntity : chestEntityObjects) chestEntity.onTrigger("entity_death", event.getEntity(), event.getLocation());
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
                    // Trapped Chests
                    IStorageObject[] chestEntityObjects = ObjectManager.getLoadedObjectsOfType(TRAPPED_CHEST_ENTITY.getObjectIdentifier());
                    for (IStorageObject chestEntity : chestEntityObjects) chestEntity.onTrigger("chest_open", event.getClickedBlock().getLocation());
                }

                // Locks
                IStorageObject[] runeLockObjects = ObjectManager.getLoadedObjectsOfType(RUNE_LOCK.getObjectIdentifier());
                for (IStorageObject lockObject : runeLockObjects) lockObject.onTrigger(event.getClickedBlock().getLocation(), event.getPlayer());

                IStorageObject[] itemLockObjects = ObjectManager.getLoadedObjectsOfType(ITEM_LOCK.getObjectIdentifier());
                for (IStorageObject lockObject : itemLockObjects) lockObject.onTrigger(event.getClickedBlock().getLocation(), event.getPlayer());
            }
        }
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }
}
