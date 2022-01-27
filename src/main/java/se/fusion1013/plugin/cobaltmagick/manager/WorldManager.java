package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.MusicBoxEvent;
import se.fusion1013.plugin.cobaltmagick.world.WorldEvents;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;

import java.util.HashMap;
import java.util.Map;

public class WorldManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static Map<String, MusicBox> musicBoxMap = new HashMap<>();

    // ----- INSTANCE -----

    private static WorldManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WorldManager</code>.
     *
     * @return The object of this class
     */
    public static WorldManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WorldManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    // ----- CONSTRUCTOR -----

    public WorldManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
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
    }

    // ----- REGISTER -----

    public static void registerMusicBox(Location location, String sound) {
        MusicBox box = new MusicBox(location.getBlock().getLocation(), sound, musicBoxMap.size());
        musicBoxMap.put(getFormattedLocation(location.getBlock().getLocation()), box);
        CobaltMagick.getInstance().getRDatabase().insertMusicBox(box);
    }

    // ----- GETTERS / SETTERS -----

    public static String getFormattedLocation(Location location) {
        return location.getX() + "::" + location.getY() + "::" + location.getZ() + "::" + location.getWorld().getName();
    }

    public MusicBox getMusicBox(Location location) {
        return musicBoxMap.get(location);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        // Load Music Boxes from Database
        musicBoxMap = CobaltMagick.getInstance().getRDatabase().getMusicBoxes();
    }

    @Override
    public void disable() { }
}
