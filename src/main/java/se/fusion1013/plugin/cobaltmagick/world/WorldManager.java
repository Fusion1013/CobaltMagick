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
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.*;

public class WorldManager extends Manager implements Listener, Runnable {

    // ----- VARIABLES -----

    // Activatable
    private static final Map<UUID, IActivatable> ACTIVATABLE_MAP = new HashMap<>();

    // Music box
    private static final Map<Integer, MusicBox> musicBoxMap = new HashMap<>();
    private static Map<String, MusicBox> musicBoxLocations = new HashMap<>(); // TODO: Replace with uuid for identification
    private static int currentMusicBoxId = 0;

    // Magick Door
    private static Map<UUID, MagickDoor> doorMap = new HashMap<>();

    // Item Lock
    private static Map<UUID, ItemLock> itemLockMap = new HashMap<>();

    // Rune Lock
    private static Map<Integer, RuneLock> runeLockMap = new HashMap<>();
    private static final Map<Location, RuneLock> runeLockLocations = new HashMap<>();
    private static int currentRuneLockId = 0;

    // Hidden Particles
    private static final Map<UUID, HiddenObject> HIDDEN_OBJECT_MAP = new HashMap<>();

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
        for (IChunkBound<?> chunkBound : ChunkBoundObjectManager.getLoadedOfType(HiddenObject.class)) {
            if (chunkBound instanceof HiddenObject message) {
                if (message.isActive()) message.tick();
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().getEnvironment().equals(World.Environment.NETHER) && (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
                p.setFireTicks(100);
            }
        }
    }

    // ----- ACTIVATABLE -----

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

    // ----- ITEM LOCK -----

    /**
     * Places a new item lock at the location that unlocks when given the correct item.
     *
     * @param location the location of the lock.
     * @param item the item that unlocks the lock.
     */
    public static void registerItemLock(Location location, CustomItem item, IActivatable activatable) {
        ItemLock lock = new ItemLock(location, item, activatable);
        itemLockMap.put(lock.getUuid(), lock);
        DataManager.getInstance().getDao(IItemLockDao.class).insertLockAsync(lock);
    }

    /**
     * Removes an <code>ItemLock</code> with matching <code>UUID</code>.
     *
     * @param uuid the <code>UUID</code> of the <code>ItemLock</code> to remove.
     */
    public static void removeItemLock(UUID uuid) {
        itemLockMap.remove(uuid);
        DataManager.getInstance().getDao(IItemLockDao.class).removeLockAsync(uuid);
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

    // ----- HIDDEN OBJECTS -----

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

    public static String[] getRevealMethods() {
        RevealMethod[] methods = RevealMethod.values();
        List<String> revealMethodStrings = new ArrayList<>();
        for (RevealMethod method : methods) revealMethodStrings.add(method.toString());
        return revealMethodStrings.toArray(new String[0]);
    }

    private static void revealNearbyHiddenParticles(RevealMethod revealMethod, Location location) {
        List<IChunkBound<?>> chunkBounds = ChunkBoundObjectManager.getLoadedOfType(HiddenObject.class);

        for (IChunkBound<?> chunkBound : chunkBounds) {
            if (chunkBound instanceof HiddenObject hiddenObject) {
                Location hiddenLocation = hiddenObject.getLocation();

                if (hiddenLocation.getWorld() != location.getWorld()) continue;

                if (hiddenLocation.distanceSquared(location) <= HiddenObject.MAX_REVEAL_DISTANCE* HiddenObject.MAX_REVEAL_DISTANCE && revealMethod == hiddenObject.getRevealMethod()) {
                    hiddenObject.activate();
                }
            }
        }
    }

    /**
     * Creates a new <code>HiddenObject</code>.
     *
     * @param location the <code>Location</code> of the <code>HiddenObject</code>.
     * @param revealMethod the method used to reveal the <code>HiddenObject</code>.
     */
    public static UUID createHiddenObject(Location location, RevealMethod revealMethod) {
        HiddenObject hidden = new HiddenObject(location, revealMethod);
        HIDDEN_OBJECT_MAP.put(hidden.getUUID(), hidden);
        ChunkBoundObjectManager.addChunkLoadableObject(location.getChunk(), hidden);
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IHiddenObjectDao.class).insertHiddenObjectAsync(hidden);
        return hidden.getUUID();
    }

    /**
     * Removes a <code>HiddenObject</code>.
     *
     * @param uuid the <code>UUID</code> of the <code>HiddenObject</code>.
     */
    public static void removeHiddenObject(UUID uuid) {
        HIDDEN_OBJECT_MAP.remove(uuid);
        ChunkBoundObjectManager.removeChunkBound(HiddenObject.class, uuid);
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IHiddenObjectDao.class).removeHiddenObjectAsync(uuid);
    }

    /**
     * Gets a list of <code>HiddenObject</code> identifiers.
     *
     * @return a list of <code>HiddenObject</code> identifiers.
     */
    public static String[] getHiddenObjectIdentifiers() {
        List<String> hiddenParticleIds = new ArrayList<>();
        for (UUID uuid : HIDDEN_OBJECT_MAP.keySet()) {
            hiddenParticleIds.add(uuid.toString());
        }
        return hiddenParticleIds.toArray(new String[0]);
    }

    public static void setHiddenObjectParticleGroup(UUID uuid, ParticleGroup particleGroup) {
        HiddenObject hiddenObject = HIDDEN_OBJECT_MAP.get(uuid);
        if (hiddenObject == null) return;
        hiddenObject.setParticleGroup(particleGroup);
    }

    public static void setHiddenObjectItemSpawn(UUID uuid, String item) {
        HiddenObject hiddenObject = HIDDEN_OBJECT_MAP.get(uuid);
        if (hiddenObject == null) return;
        hiddenObject.setItemSpawn(item);
    }

    public static void setHiddenObjectWandSpawn(UUID uuid, int wandTier) {
        HiddenObject hiddenObject = HIDDEN_OBJECT_MAP.get(uuid);
        if (hiddenObject == null) return;
        hiddenObject.setWandSpawn(wandTier);
    }

    public static void setHiddenObjectDeleteOnActivation(UUID uuid, boolean deleteOnActivation) {
        HiddenObject hiddenObject = HIDDEN_OBJECT_MAP.get(uuid);
        if (hiddenObject == null) return;
        hiddenObject.setDeleteOnActivation(deleteOnActivation);
    }

    // ----- RUNE LOCK -----

    public static RuneLock registerRuneLock(Location location, IActivatable activatable, String item) {
        // Create the Rune Lock
        int id = getRuneLockId();
        Location formattedLocation = location.getBlock().getLocation();
        RuneLock runeLock = new RuneLock(formattedLocation, activatable, id, item);

        // Store the Rune Lock
        runeLockMap.put(id, runeLock);
        runeLockLocations.put(formattedLocation, runeLock);
        DataManager.getInstance().getDao(IRuneLockDao.class).insertRuneLockAsync(runeLock);

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
        DataManager.getInstance().getDao(IRuneLockDao.class).updateRuneLockItemsAsync(runeLock);
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
        DataManager.getInstance().getDao(IRuneLockDao.class).updateRuneLockItemsAsync(runeLock);
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
        CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class).getDao(IRuneLockDao.class).removeRuneLockAsync(id);
        return runeLock;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 2);

        // Load Music Boxes from Database
        musicBoxLocations = DataManager.getInstance().getDao(IMusicBoxDao.class).getMusicBoxes();
        for (MusicBox box : musicBoxLocations.values()) musicBoxMap.put(box.getId(), box);

        // Load Doors from database
        doorMap = DataManager.getInstance().getDao(IDoorDao.class).getDoors();
        for (MagickDoor door : doorMap.values()) ACTIVATABLE_MAP.put(door.getUuid(), door);

        // Load Item Locks from database
        itemLockMap = DataManager.getInstance().getDao(IItemLockDao.class).getLocks();

        // Load Rune Locks from database
        runeLockMap = DataManager.getInstance().getDao(IRuneLockDao.class).getRuneLocks();
        for (RuneLock lock : runeLockMap.values()) runeLockLocations.put(lock.getLocation(), lock);

        // Load hidden particles from database
        List<HiddenObject> hiddenObjects = core.getManager(core, DataManager.class).getDao(IHiddenObjectDao.class).getHiddenParticles();
        for (HiddenObject particle : hiddenObjects) {
            // Add to hidden particle map & chunk bound
            HIDDEN_OBJECT_MAP.put(particle.getUUID(), particle);
            ChunkBoundObjectManager.addChunkLoadableObject(particle.getLocation().getChunk(), particle);
        }
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
