package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.CustomItem;
import se.fusion1013.plugin.cobaltmagick.util.HexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemManager extends Manager {

    private static final Map<String, ItemStack> INBUILT_ITEMS = new HashMap<>();

    // ----- TOOLS -----

    public static final CustomItem CRYSTAL_KEY = register(new CustomItem.CustomItemBuilder("crystal_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.WHITE + "The key is voiceless")
            .setCustomModel(3)
            .build());

    public static final CustomItem DUNGEON_COIN = register(new CustomItem.CustomItemBuilder("dungeon_coin", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Coin")
            .setCustomModel(1)
            .build());

    public static final CustomItem DUNGEON_KEY = register(new CustomItem.CustomItemBuilder("dungeon_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Key")
            .setCustomModel(2)
            .build());

    public static final CustomItem DREAMGLASS = register(new CustomItem.CustomItemBuilder("dreamglass", Material.SPYGLASS, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_AQUA + "Dreamglass")
            .addLoreLine(ChatColor.WHITE + "The lens distorts reality")
            .setCustomModel(1)
            .build());

    // ----- MATERIALS -----

    public static final CustomItem CRYSTAL_LENS = register(new CustomItem.CustomItemBuilder("crystal_lens", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&5Crystal Lens")).addLoreLine(HexUtils.colorify("&dLight shifts and distorts")).setCustomModel(4).build());

    public static final CustomItem DRAGONSTONE = register(new CustomItem.CustomItemBuilder("dragonstone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&dDragonstone")).setCustomModel(5).build());

    public static final CustomItem MANA_DIAMOND = register(new CustomItem.CustomItemBuilder("mana_diamond", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Diamond")).setCustomModel(6).build());

    public static final CustomItem MANA_PEARL = register(new CustomItem.CustomItemBuilder("mana_pearl", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Pearl")).setCustomModel(7).build());

    public static final CustomItem MANA_POWDER = register(new CustomItem.CustomItemBuilder("mana_powder", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Powder")).setCustomModel(8).build());

    public static final CustomItem RAINBOW_ROD = register(new CustomItem.CustomItemBuilder("rainbow_rod", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<r:.5:1>Rainbow Rod")).setCustomModel(9).build());

    public static final CustomItem AQUAMARINE = register(new CustomItem.CustomItemBuilder("aquamarine", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fAquamarine")).setCustomModel(10).build());

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

    private static CustomItem register(CustomItem item){
        INBUILT_ITEMS.put(item.getInternalName(), item.getItemStack());
        return item;
    }

    public static boolean isItem(ItemStack item, CustomItem compareTo){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(compareTo.getNamespacedKey(), PersistentDataType.INTEGER);
    }

    public static String[] getItemNames(){
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
    public static ItemStack getItem(String name){
        return INBUILT_ITEMS.get(name);
    }

    public CustomItemManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }
}
