package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager extends Manager {

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>();

    private static CustomItemManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomItemManager</code>.
     *
     * @return The object of this class
     */
    public static CustomItemManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomItemManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    private static ItemStack getItemWithModelData(Material material, int count, int data){
        ItemStack is = new ItemStack(material, count);
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(data);
            is.setItemMeta(meta);
        }
        return is;
    }

    private void registerItems(){

        // CRYSTAL KEY
        ItemStack CRYSTAL_KEY = getItemWithModelData(Material.EMERALD, 1, 3);
        ItemMeta meta = CRYSTAL_KEY.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(new NamespacedKey(CobaltMagick.getInstance(), "crystal_key"), PersistentDataType.INTEGER, 1);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key");
            List<String> keyLore = new ArrayList<>();
            keyLore.add("The key is voiceless");
            meta.setLore(keyLore);
            CRYSTAL_KEY.setItemMeta(meta);
        }
        INBUILT_ITEMS.put("crystal_key", CRYSTAL_KEY);

        // DUNGEON COIN
        ItemStack DUNGEON_COIN = getItemWithModelData(Material.EMERALD, 1, 1);
        INBUILT_ITEMS.put("dungeon_coin", DUNGEON_COIN);

        // DUNGEON KEY
        ItemStack DUNGEON_KEY = getItemWithModelData(Material.EMERALD, 1, 2);
        INBUILT_ITEMS.put("dungeon_key", DUNGEON_KEY);
    }

    public String[] getItemNames(){
        String[] names = new String[INBUILT_ITEMS.size()];
        List<String> keys = new ArrayList<>(INBUILT_ITEMS.keySet());
        for (int i = 0; i < keys.size(); i++){
            names[i] = keys.get(i);
        }
        return names;
    }

    /**
     * Returns a new item from the given name
     *
     * @param name the name of the item to get
     * @return a new item
     */
    public ItemStack getItem(String name){
        return INBUILT_ITEMS.get(name);
    }

    public CustomItemManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
    }

    @Override
    public void reload() {
        registerItems();
    }

    @Override
    public void disable() {

    }
}
