package se.fusion1013.plugin.cobaltmagick.world.structures;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicBox implements Listener {

    // ----- VARIABLES -----

    Location location;
    String sound;
    String message = "";
    int id;

    // ----- CONSTRUCTORS -----

    public MusicBox(Location location, String sound, int id) {
        this.location = location;
        this.sound = sound;
        this.id = id;

        World world = location.getWorld();
        this.message = getBiomeMessage(world, location);

        location.getBlock().setType(Material.NOTE_BLOCK);
    }

    // ----- LOGIC -----

    /**
     * Plays the music this box contains.
     */
    public void playMusic() {
        World world = location.getWorld();

        if (world != null) {
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, 20, 20, 20));
            for (Entity e : nearbyEntities) {
                if (e instanceof Player p) {
                    p.stopSound(sound, SoundCategory.AMBIENT); // Stop the song from playing if it is already playing
                    p.playSound(location, sound, SoundCategory.AMBIENT, 1, 1);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(HexUtils.colorify(message)));
                }
            }
        }
    }

    // TODO: Move to locale. Base color of temperature of biome (fade from blue to green)
    private static String getBiomeMessage(World world, Location location) {
        Biome biome = world.getBiome(location);
        String biomeName = biome.toString();
        return ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "The " + biomeName.toLowerCase().replaceAll("_", " ") + " vibes with the music";
    }

    // ----- GETTERS / SETTERS -----

    public Location getLocation() {
        return location;
    }

    public String getSound() {
        return sound;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}
