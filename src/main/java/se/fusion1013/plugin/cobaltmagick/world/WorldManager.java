package se.fusion1013.plugin.cobaltmagick.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.world.chunk.ChunkBoundObjectManager;
import se.fusion1013.plugin.cobaltcore.world.chunk.IChunkBound;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.door.IDoorDao;
import se.fusion1013.plugin.cobaltmagick.database.hidden.IHiddenObjectDao;
import se.fusion1013.plugin.cobaltmagick.database.itemlock.IItemLockDao;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.database.runelock.IRuneLockDao;
import se.fusion1013.plugin.cobaltmagick.event.MusicBoxEvent;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.*;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenObject;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.RuneLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.*;

public class WorldManager extends Manager implements Listener {

    // ----- VARIABLES -----

    // Activatable
    private static final Map<UUID, IActivatable> ACTIVATABLE_MAP = new HashMap<>();

    // Music box
    private static final Map<Integer, MusicBox> musicBoxMap = new HashMap<>();
    private static Map<String, MusicBox> musicBoxLocations = new HashMap<>(); // TODO: Replace with uuid for identification
    private static int currentMusicBoxId = 0;

    // Magick Door
    private static Map<UUID, MagickDoor> doorMap = new HashMap<>();

    // ----- CONSTRUCTOR -----

    public WorldManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        if (INSTANCE == null) INSTANCE = this;
    }

    // ----- EVENTS -----

    /**
     * Called when an entity interacts with an entity or a block.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        EquipmentSlot slot = event.getHand();

        if (clickedBlock == null || slot == null) return;

        Location location = clickedBlock.getLocation();
        World world = location.getWorld();

        if (world == null) return;

        // ----- MUSIC BOX -----

        if (slot == EquipmentSlot.HAND && clickedBlock.getType() == Material.NOTE_BLOCK) {
            MusicBox box = musicBoxLocations.get(getFormattedLocation(location));

            if (box != null) {
                MusicBoxEvent boxEvent = new MusicBoxEvent(location, box.getSound(), box.getId(), event.getPlayer());
                Bukkit.getPluginManager().callEvent(boxEvent);
                if (!boxEvent.isCancelled()) box.playMusic();
            }
        }
    }

    // ----- ACTIVATABLE -----

    /**
     * Adds an <code>IActivatable</code> to the map.
     *
     * @return the <code>IActivatable</code>.
     */
    public static IActivatable addActivatable(IActivatable activatable) {
        ACTIVATABLE_MAP.put(activatable.getUuid(), activatable);
        return activatable;
    }

    /**
     * Removes an <code>IActivatable</code> from the map.
     *
     * @param uuid the <code>UUID</code> of the <code>IActivatable</code>.
     * @return the <code>IActivatable</code>, or null if it does not exist.
     */
    public static IActivatable removeActivatable(UUID uuid) {
        return ACTIVATABLE_MAP.remove(uuid);
    }

    /**
     * Gets an <code>IActivatable</code> from the map.
     *
     * @param uuid the <code>UUID</code> of the <code>IActivatable</code>.
     * @return the <code>IActivatable</code>, or null if it does not exist.
     */
    public static IActivatable getActivatable(UUID uuid) {
        return ACTIVATABLE_MAP.get(uuid);
    }

    /**
     * Activates the <code>IActivatable</code> with the given <code>UUID</code> if it exists.
     *
     * @param uuid the <code>UUID</code> of the <code>IActivatable</code>.
     */
    public static boolean activateActivatable(UUID uuid) {
        IActivatable activatable = ACTIVATABLE_MAP.get(uuid);
        if (activatable != null) {
            activatable.activate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets all <code>IActivatable</code> <code>UUID</code>'s as strings.
     *
     * @return an array of <code>IActivatable</code> <code>UUID</code>'s as strings.
     */
    public static String[] getActivatableStrings() {
        List<String> activatables = new ArrayList<>();
        for (UUID uuid : ACTIVATABLE_MAP.keySet()) {
            activatables.add(uuid.toString());
        }
        return activatables.toArray(new String[0]);
    }

    // ----- MUSIC BOX -----

    /**
     * Places a new music box in the world and inserts it into the database.
     *
     * @param location the location to place the box at.
     * @param sound the sound the box makes when played.
     */
    public static void registerMusicBox(Location location, String sound) {
        MusicBox box = new MusicBox(location.getBlock().getLocation(), sound, getMusicBoxId());
        musicBoxMap.put(box.getId(), box);
        musicBoxLocations.put(getFormattedLocation(location.getBlock().getLocation()), box);
        DataManager.getInstance().getDao(IMusicBoxDao.class).insertMusicBoxAsync(box);
    }

    /**
     * Gets an id that is not currently held by a <code>MusicBox</code>.
     *
     * @return a unique id.
     */
    private static int getMusicBoxId() {
        while (musicBoxMap.get(currentMusicBoxId) != null) currentMusicBoxId++;
        return currentMusicBoxId;
    }

    /**
     * Returns the music box at the given location.
     *
     * @param location the location of the box.
     * @return the music box at the location.
     */
    public MusicBox getMusicBox(Location location) {
        return musicBoxLocations.get(getFormattedLocation(location));
    }

    /**
     * Returns an array of all registered music boxes.
     *
     * @return an array of music boxes.
     */
    public MusicBox[] getMusicBoxes() {
        return musicBoxLocations.values().toArray(new MusicBox[0]);
    }

    /**
     * Removes a <code>MusicBox</code>.
     *
     * @param id the id of the <code>MusicBox</code> to remove.
     * @return the <code>MusicBox</code> that was removed.
     */
    public MusicBox removeMusicBox(int id) {
        // Remove the box from both maps
        MusicBox box = musicBoxMap.remove(id);
        musicBoxLocations.remove(getFormattedLocation(box.getLocation()));
        DataManager.getInstance().getDao(IMusicBoxDao.class).removeMusicBoxSync(box);
        return box;
    }

    public String[] getMusicBoxIdStrings() {
        MusicBox[] boxes = getMusicBoxes();
        String[] boxIds = new String[boxes.length];

        for (int i = 0; i < boxes.length; i++) {
            boxIds[i] = Integer.toString(boxes[i].getId());
        }

        return boxIds;
    }

    public MusicBox getMusicBox(int id) {
        MusicBox[] boxes = getMusicBoxes();

        for (MusicBox box : boxes) {
            if (box.getId() == id) return box;
        }

        return null;
    }

    // ----- DOOR -----

    /**
     * Places a new door at the location with a width, height and depth.
     *
     * @param location the corner location of the door.
     * @param width the width of the door.
     * @param height the height of the door.
     * @param depth the depth of the door.
     * @return the created <code>MagickDoor</code>.
     */
    public static MagickDoor registerDoor(Location location, int width, int height, int depth) {
        MagickDoor door = new MagickDoor(location, width, height, depth);
        doorMap.put(door.getUuid(), door);
        DataManager.getInstance().getDao(IDoorDao.class).insertDoorAsync(door);
        ACTIVATABLE_MAP.put(door.getUuid(), door);

        return door;
    }

    /**
     * Removes a <code>MagickDoor</code>.
     *
     * @param uuid the <code>UUID</code> of the <code>MagickDoor</code>.
     * @return the <code>MagickDoor</code> that was removed.
     */
    public static MagickDoor removeDoor(UUID uuid) {
        MagickDoor door = doorMap.remove(uuid);
        DataManager.getInstance().getDao(IDoorDao.class).removeDoorAsync(uuid);
        return door;
    }

    public static MagickDoor getDoor(UUID uuid) {
        return doorMap.get(uuid);
    }

    public static MagickDoor[] getDoors() {
        return doorMap.values().toArray(new MagickDoor[0]);
    }

    public static String[] getDoorKeys() {
        List<String> keys = new ArrayList<>();
        for (UUID uuid : doorMap.keySet()) {
            keys.add(uuid.toString());
        }
        return keys.toArray(new String[0]);
    }

    public static UUID[] getDoorUUIDS() {
        return doorMap.keySet().toArray(new UUID[0]);
    }

    public static String getFormattedLocation(Location location) {
        return location.getX() + "::" + location.getY() + "::" + location.getZ() + "::" + location.getWorld().getName();
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Load Music Boxes from Database
        musicBoxLocations = DataManager.getInstance().getDao(IMusicBoxDao.class).getMusicBoxes();
        for (MusicBox box : musicBoxLocations.values()) musicBoxMap.put(box.getId(), box);

        // Load Doors from database
        doorMap = DataManager.getInstance().getDao(IDoorDao.class).getDoors();
        for (MagickDoor door : doorMap.values()) ACTIVATABLE_MAP.put(door.getUuid(), door);
    }

    @Override
    public void disable() {
        DataManager.getInstance().getDao(IDoorDao.class).updateDoorStatusSync(doorMap.values().toArray(new MagickDoor[0]));
        doorMap.forEach((uuid, magickDoor) -> magickDoor.setDoorBlocks(false));
    }

    // ----- INSTANCE -----

    private static WorldManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WorldManager</code>.
     *
     * @return The object of this class
     */
    public static WorldManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WorldManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
