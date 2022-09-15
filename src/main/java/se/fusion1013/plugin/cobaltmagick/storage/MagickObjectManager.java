package se.fusion1013.plugin.cobaltmagick.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.world.chunk.ChunkBoundObjectManager;
import se.fusion1013.plugin.cobaltcore.world.chunk.IChunkBound;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenObject;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenParticle;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.MaterialLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.RuneLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MeditationPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.MultiActivatable;
import se.fusion1013.plugin.cobaltmagick.world.structures.trap.TrappedChestEntity;

import java.util.List;

public class MagickObjectManager extends Manager implements Listener {

    // ----- OBJECT REGISTER -----

    // Portals
    public static IStorageObject MAGICK_PORTAL_STORAGE = ObjectManager.registerDefaultStorage(new MagickPortal(null, null));
    public static IStorageObject MEDITATION_PORTAL_STORAGE = ObjectManager.registerDefaultStorage(new MeditationPortal(null, null));

    // Trapped Chests
    public static IStorageObject TRAPPED_CHEST_ENTITY = ObjectManager.registerDefaultStorage(new TrappedChestEntity(null, null));

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

    // ----- CONSTRUCTORS -----

    public MagickObjectManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- EVENTS -----

    @EventHandler
    public void onSpellCast(SpellCastEvent event) {
        if (event.getSpell().getId() == SpellManager.ALL_SEEING_EYE.getId()) {
            revealNearbyHiddenParticles(RevealMethod.ALL_SEEING_EYE, event.getSpell().getCaster().getLocation()); // TODO: Replace with actual spell location
        }
    }

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        revealNearbyHiddenParticles(RevealMethod.PROXIMITY, event.getPlayer().getLocation());
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
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
                // Trapped Chests
                IStorageObject[] chestEntityObjects = ObjectManager.getLoadedObjectsOfType(TRAPPED_CHEST_ENTITY.getObjectIdentifier());
                for (IStorageObject chestEntity : chestEntityObjects) chestEntity.onTrigger(event.getClickedBlock().getLocation());
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
