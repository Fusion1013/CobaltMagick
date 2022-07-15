package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.door.IDoorDao;
import se.fusion1013.plugin.cobaltmagick.database.lock.ILockDao;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.event.MusicBoxEvent;
import se.fusion1013.plugin.cobaltmagick.world.structures.*;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.*;

public class WorldManager extends Manager implements Listener, Runnable {

    // ----- VARIABLES -----

    // Music box
    private static final Map<Integer, MusicBox> musicBoxMap = new HashMap<>();
    private static Map<String, MusicBox> musicBoxLocations = new HashMap<>(); // TODO: Replace with uuid for identification
    private static int currentMusicBoxId = 0;

    // Magick Door
    private static Map<UUID, MagickDoor> doorMap = new HashMap<>();

    // Item Lock
    private static Map<UUID, ItemLock> itemLockMap = new HashMap<>();

    // Rune Lock
    private static final Map<Integer, RuneLock> runeLockMap = new HashMap<>();
    private static Map<Location, RuneLock> runeLockLocations = new HashMap<>();
    private static int currentRuneLockId = 0;

    // Hidden Message
    private static List<HiddenMessage> hiddenMessages = new ArrayList<>();

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

        // ----- LOCKS -----

        for (ItemLock lock : itemLockMap.values()) {
            if (lock.getLocation().getBlock().getLocation().equals(location) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (lock.onClick(event.getPlayer())) event.setCancelled(true);
            }
        }

        // ----- RUNE LOCK -----

        RuneLock runeLock = runeLockLocations.get(location);
        if (runeLock != null) {
            if (runeLock.onClick(event.getPlayer())) event.setCancelled(true);
        }

    }

    // ----- RUNNABLE -----

    @Override
    public void run() {
        for (HiddenMessage message : hiddenMessages) {
            message.tick(); // TODO: Some messages should only be visible if player is holding thing
        }
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

    // ----- ITEM LOCK -----

    /**
     * Places a new item lock at the location that unlocks when given the correct item.
     *
     * @param location the location of the lock.
     * @param item the item that unlocks the lock.
     */
    public static void registerItemLock(Location location, CustomItem item, Unlockable unlockable) {
        ItemLock lock = new ItemLock(location, item, unlockable);
        itemLockMap.put(lock.getUuid(), lock);
        DataManager.getInstance().getDao(ILockDao.class).insertLockAsync(lock);
    }

    /**
     * Removes an <code>ItemLock</code> with matching <code>UUID</code>.
     *
     * @param uuid the <code>UUID</code> of the <code>ItemLock</code> to remove.
     */
    public static void removeItemLock(UUID uuid) {
        itemLockMap.remove(uuid);
        DataManager.getInstance().getDao(ILockDao.class).removeLockAsync(uuid);
    }

    /**
     * Gets all <code>ItemLock</code>'s
     *
     * @return an array of <code>ItemLock</code>'s.
     */
    public static ItemLock[] getItemLocks() {
        List<ItemLock> locks = new ArrayList<>(itemLockMap.values());
        return locks.toArray(new ItemLock[0]);
    }

    /**
     * Gets an <code>ItemLock</code> with matching <code>UUID</code>.
     *
     * @param uuid the <code>UUID</code> of the <code>ItemLock</code>.
     * @return an <code>ItemLock</code>.
     */
    public static ItemLock getItemLock(UUID uuid) {
        return itemLockMap.get(uuid);
    }

    /**
     * Gets all <code>ItemLock</code> keys.
     *
     * @return an array of <code>ItemLock</code> keys.
     */
    public static String[] getItemLockKeys() {
        List<String> keys = new ArrayList<>();
        for (UUID uuid : itemLockMap.keySet()) {
            keys.add(uuid.toString());
        }
        return keys.toArray(new String[0]);
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

    // ----- HIDDEN MESSAGE -----

    public static void addHiddenMessage(HiddenMessage message) {
        hiddenMessages.add(message);
    }

    // ----- RUNE LOCK -----

    public static RuneLock registerRuneLock(Location location, Unlockable unlockable, String item) {
        // Create the Rune Lock
        int id = getRuneLockId();
        Location formattedLocation = location.getBlock().getLocation();
        RuneLock runeLock = new RuneLock(formattedLocation, unlockable, id, item);

        // Store the Rune Lock
        runeLockMap.put(id, runeLock);
        runeLockLocations.put(formattedLocation, runeLock);
        // TODO: Insert into database

        return runeLock;
    }

    /**
     * Adds an item to the <code>RuneLock</code>.
     *
     * @param id the id of the <code>RuneLock</code>.
     * @param item the item to add.
     * @return weather an item was added or not.
     */
    public static boolean addRuneLockItem(int id, String item) {
        RuneLock runeLock = runeLockMap.get(id);
        if (runeLock == null) return false;

        runeLock.addItem(item);
        return true;
    }

    /**
     * Removes the first item in the <code>RuneLock</code> queue.
     *
     * @param id the id of the <code>RuneLock</code>.
     * @return weather an item was removed or not.
     */
    public static boolean removeRuneLockItem(int id) {
        RuneLock runeLock = runeLockMap.get(id);
        if (runeLock == null) return false;

        runeLock.removeItem();
        return true;
    }

    private static int getRuneLockId() {
        while (runeLockMap.get(currentRuneLockId) != null) currentRuneLockId++;
        return currentRuneLockId;
    }

    public static String[] getRuneLockIds() {
        List<String> ids = new ArrayList<>();
        for (int id : runeLockMap.keySet()) {
            ids.add(Integer.toString(id));
        }
        return ids.toArray(new String[0]);
    }

    public static RuneLock getRuneLock(int id) {
        return runeLockMap.get(id);
    }

    /**
     * Removes a <code>RuneLock</code>.
     *
     * @param id the id of the <code>RuneLock</code>.
     * @return the <code>RuneLock</code> that was removed.
     */
    public static RuneLock removeRuneLock(int id) {
        RuneLock runeLock = runeLockMap.remove(id);
        if (runeLock == null) return null;

        runeLockLocations.remove(runeLock.getLocation());
        // TODO: Remove from database
        return runeLock;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);

        // Load Music Boxes from Database
        musicBoxLocations = DataManager.getInstance().getDao(IMusicBoxDao.class).getMusicBoxes();
        for (MusicBox box : musicBoxLocations.values()) musicBoxMap.put(box.getId(), box);

        // Load Doors from database
        doorMap = DataManager.getInstance().getDao(IDoorDao.class).getDoors();

        // Load Item Locks from database
        itemLockMap = DataManager.getInstance().getDao(ILockDao.class).getLocks();
        CobaltMagick.getInstance().getLogger().info("Loaded " + itemLockMap.size() + " locks from database");

        // Load Rune Locks from database
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
