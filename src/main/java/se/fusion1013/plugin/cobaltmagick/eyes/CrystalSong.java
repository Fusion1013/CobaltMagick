package se.fusion1013.plugin.cobaltmagick.eyes;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.manager.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;

import java.util.*;

public class CrystalSong implements Listener {

    // public static final NamespacedKey crystalKey = new NamespacedKey(CobaltMagick.getInstance(), "crystal_key");
    public static final NamespacedKey darkSong1 = new NamespacedKey(CobaltMagick.getInstance(), "dark_song_part_1");
    public static final NamespacedKey darkSong2 = new NamespacedKey(CobaltMagick.getInstance(), "dark_song_part_2");

    private final Map<UUID, List<String>> noteList = new HashMap<>();

    private final static String[] songOfKeysPart1 = new String[]{"ocarina_note_e", "ocarina_note_c", "ocarina_note_b", "ocarina_note_g#", "ocarina_note_f"};
    private final static String[] songOfKeysPart2 = new String[]{"kantele_note_g", "kantele_note_d#", "kantele_note_g", "kantele_note_e", "kantele_note_a"};

    @EventHandler
    public void onSpellCastEvent(SpellCastEvent event){
        if (isNote(event.getSpell())){
            UUID uuid = event.getSpell().getCaster().getUniqueId();
            List<String> currentNotes = noteList.get(uuid);
            if (currentNotes == null) currentNotes = new ArrayList<>();
            currentNotes.add(event.getSpell().getInternalSpellName());
            noteList.put(uuid, currentNotes);

            checkSongs();
        }
    }

    private void checkSongs(){
        List<UUID> keys = new ArrayList<>(noteList.keySet());

        for (UUID id : keys){
            List<String> currentSong = noteList.get(id);

            Player player = Bukkit.getPlayer(id);
            if (player != null){
                if (matchesSong(songOfKeysPart1, currentSong)) executeSongOfKeys(player, darkSong1, darkSong2);
                if (matchesSong(songOfKeysPart2, currentSong)) executeSongOfKeys(player, darkSong2, darkSong1);
            }
        }
    }

    // KEY LORE FOR MUSIC BOXES::
    // 1 Box: The key remembers a song
    // 2 Boxes: The key remembers two songs
    // 3 Boxes: The key remembers three songs
    // 4 Boxes: The key is ready

    private void executeSongOfKeys(Player player, NamespacedKey key, NamespacedKey other){
        CobaltMagick.getInstance().getLogger().info("Executing song of keys: " + key.getKey());

        World world = player.getWorld();
        List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(player.getLocation(), 10, 10, 10));

        for (Entity e : nearbyEntities){
            if (e instanceof Item item){
                ItemStack stack = item.getItemStack();
                ItemMeta meta = stack.getItemMeta();

                if (meta != null){
                    PersistentDataContainer cont = meta.getPersistentDataContainer();

                    if (!cont.has(key, PersistentDataType.INTEGER) && cont.has(CustomItemManager.CRYSTAL_KEY.getNamespacedKey(), PersistentDataType.INTEGER)){
                        cont.set(key, PersistentDataType.INTEGER, 1);

                        player.playSound(item.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                        List<String> newLore = new ArrayList<>();
                        if (cont.has(other, PersistentDataType.INTEGER)) {
                            player.sendTitle(ChatColor.GOLD + "The Key Begins to Whisper!", ChatColor.YELLOW + "I can give you so much in exchange for...", 20, 70, 20);
                            newLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "The key whispers secrets and promises; it is ready");
                        }
                        else {
                            player.sendTitle(ChatColor.GOLD + "The Key Begins to Hum!", ChatColor.YELLOW + "Something is still missing...", 20, 70, 20);
                            newLore.add(ChatColor.RESET + "" + ChatColor.WHITE + "The key hums, but something is missing...");
                        }
                        meta.setLore(newLore);

                        stack.setItemMeta(meta);
                    }
                }
            }
        }
    }

    private boolean matchesSong(String[] song, List<String> currentSong){
        if (currentSong.size() < song.length) return false;
        int lengthDiff = currentSong.size() - song.length;

        for (int i = song.length; i > 0; i--){
            String songNote = song[i-1];
            String playerNote = currentSong.get(i-1+lengthDiff);
            if (!songNote.equalsIgnoreCase(playerNote)) return false;
        }

        // Clear song list
        currentSong.clear();

        return true;
    }

    private boolean isNote(ISpell spell){
        List<String> tags = spell.getTags();
        for (String s : tags){
            if (s.equalsIgnoreCase("note")) return true;
        }
        return false;
    }
}
