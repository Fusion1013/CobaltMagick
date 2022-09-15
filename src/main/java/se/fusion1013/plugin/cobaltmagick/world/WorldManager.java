package se.fusion1013.plugin.cobaltmagick.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.event.MusicBoxEvent;
import se.fusion1013.plugin.cobaltmagick.world.structures.*;

import java.util.*;

public class WorldManager extends Manager implements Listener {

    // ----- VARIABLES -----

    // Music box
    private static final Map<Integer, MusicBox> musicBoxMap = new HashMap<>();
    private static Map<String, MusicBox> musicBoxLocations = new HashMap<>(); // TODO: Replace with uuid for identification
    private static int currentMusicBoxId = 0;

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

    public static String getFormattedLocation(Location location) {
        return location.getX() + "::" + location.getY() + "::" + location.getZ() + "::" + location.getWorld().getName();
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Load Music Boxes from Database
        musicBoxLocations = DataManager.getInstance().getDao(IMusicBoxDao.class).getMusicBoxes();
        for (MusicBox box : musicBoxLocations.values()) musicBoxMap.put(box.getId(), box);
    }

    @Override
    public void disable() {
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
