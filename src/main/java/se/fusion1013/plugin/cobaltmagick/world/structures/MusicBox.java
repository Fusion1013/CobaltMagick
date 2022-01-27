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

import java.util.ArrayList;
import java.util.List;

public class MusicBox implements Listener {

    // ----- VARIABLES -----

    Location location;
    String sound;
    int id;

    // ----- CONSTRUCTORS -----

    public MusicBox(Location location, String sound, int id) {
        this.location = location;
        this.sound = sound;
        this.id = id;

        location.getBlock().setType(Material.NOTE_BLOCK);
    }

    // ----- LOGIC -----

    public void playMusic() {
        World world = location.getWorld();
        if (world != null) {
            world.playSound(location, sound, SoundCategory.AMBIENT, 1, 1);
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, 20, 20, 20));
            for (Entity e : nearbyEntities) {
                if (e instanceof Player p) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getBiomeMessage(world, location)));
                }
            }
        }
    }

    // TODO: Move to locale
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
}
