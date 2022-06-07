package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.DatabaseHook;
import se.fusion1013.plugin.cobaltmagick.event.MusicBoxEvent;
import se.fusion1013.plugin.cobaltmagick.world.WorldEvents;
import se.fusion1013.plugin.cobaltmagick.world.structures.HiddenMessage;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.*;

public class WorldManager extends Manager implements Listener, Runnable {

    // ----- VARIABLES -----

    private static Map<String, MusicBox> musicBoxMap = new HashMap<>();
    private static Map<UUID, MagickDoor> doorMap = new HashMap<>();
    private static Map<UUID, ItemLock> itemLockMap = new HashMap<>();

    private static List<HiddenMessage> hiddenMessages = new ArrayList<>();

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
            MusicBox box = musicBoxMap.get(getFormattedLocation(location));

            if (box != null) {
                MusicBoxEvent boxEvent = new MusicBoxEvent(location, box.getSound(), box.getId(), event.getPlayer());
                Bukkit.getPluginManager().callEvent(boxEvent);
                if (!boxEvent.isCancelled()) box.playMusic();
            }
        }

        // ----- LOCKS -----

        for (ItemLock lock : itemLockMap.values()) {
            // CobaltMagick.getInstance().getLogger().info("Comparing location " + lock.getLocation().getBlock().getLocation() + " with " + location);
            if (lock.getLocation().getBlock().getLocation().equals(location) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                lock.onClick(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    // ----- RUNNABLE -----

    @Override
    public void run() {
        for (HiddenMessage message : hiddenMessages) {
            message.tick(); // TODO: Some messages should only be visible if player is holding thing
        }
    }

    // ----- REGISTER -----

    /**
     * Places a new item lock at the location that unlocks when given the correct item.
     *
     * @param location the location of the lock.
     * @param item the item that unlocks the lock.
     */
    public static void registerLock(Location location, CustomItem item, Unlockable unlockable) {
        ItemLock lock = new ItemLock(location, item, unlockable);
        itemLockMap.put(lock.getUuid(), lock);
        DatabaseHook.insertLock(lock);
    }

    /**
     * Places a new door at the location with a width, height and depth.
     *
     * @param location the corner location of the door.
     * @param width the width of the door.
     * @param height the height of the door.
     * @param depth the depth of the door.
     */
    public static void registerDoor(Location location, int width, int height, int depth) {
        MagickDoor door = new MagickDoor(location, width, height, depth);
        doorMap.put(door.getUuid(), door);
        DatabaseHook.insertDoor(door);
    }

    /**
     * Places a new music box in the world and inserts it into the database.
     *
     * @param location the location to place the box at.
     * @param sound the sound the box makes when played.
     */
    public static void registerMusicBox(Location location, String sound) {
        MusicBox box = new MusicBox(location.getBlock().getLocation(), sound, musicBoxMap.size());
        musicBoxMap.put(getFormattedLocation(location.getBlock().getLocation()), box);
        DatabaseHook.insertMusicBox(box);
    }

    // ----- REMOVE -----

    // TODO: Remove music box

    public static void removeLock(UUID uuid) {
        itemLockMap.remove(uuid);
        DatabaseHook.removeLock(uuid);
    }

    public static void removeDoor(UUID uuid) {
        doorMap.remove(uuid);
        DatabaseHook.removeDoor(uuid);
    }

    // ----- GETTERS / SETTERS -----

    public static ItemLock[] getLocks() {
        List<ItemLock> locks = new ArrayList<>(itemLockMap.values());
        return locks.toArray(new ItemLock[0]);
    }

    public static ItemLock getLock(UUID uuid) {
        return itemLockMap.get(uuid);
    }

    public static String[] getLockKeys() {
        List<String> keys = new ArrayList<>();
        for (UUID uuid : itemLockMap.keySet()) {
            keys.add(uuid.toString());
        }
        return keys.toArray(new String[0]);
    }

    public static void addHiddenMessage(HiddenMessage message) {
        hiddenMessages.add(message);
    }

    public static MagickDoor getDoor(UUID uuid) {
        return doorMap.get(uuid);
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

    /**
     * Returns the music box at the given location.
     *
     * @param location the location of the box.
     * @return the music box at the location.
     */
    public MusicBox getMusicBox(Location location) {
        return musicBoxMap.get(getFormattedLocation(location));
    }

    /**
     * Returns an array of all registered music boxes.
     *
     * @return an array of music boxes.
     */
    public MusicBox[] getMusicBoxes() {
        return musicBoxMap.values().toArray(new MusicBox[0]);
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

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);

        // Load Music Boxes from Database
        musicBoxMap = DatabaseHook.getMusicBoxes();
        doorMap = DatabaseHook.getDoors();
        itemLockMap = DatabaseHook.getLocks();
    }

    @Override
    public void disable() {
        DatabaseHook.updateDoorsStatus(doorMap.values().toArray(new MagickDoor[0]));
        doorMap.forEach((uuid, magickDoor) -> magickDoor.close());
    }
}
