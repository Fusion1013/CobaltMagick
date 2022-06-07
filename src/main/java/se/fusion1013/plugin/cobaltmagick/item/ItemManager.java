package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ShapedRecipe;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.AbstractCustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;

/**
 * Holds all <code>CustomItems</code> registered by the <code>CobaltMagick</code> plugin.
 * Items are registered through <code>CobaltCore</code>.
 */
public class ItemManager extends Manager {

    // ----- CONSTRUCTORS -----

    public ItemManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- TOOLS -----

    public static final CustomItem CRYSTAL_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("crystal_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.WHITE + "The key is voiceless")
            .setCustomModel(3)
            .build());

    public static final CustomItem CRYSTAL_KEY_LIGHT_ACTIVE = CustomItemManager.register(new CustomItem.CustomItemBuilder("crystal_key_light_active", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.RESET + "" + ChatColor.WHITE + "The key is ready")
            .setItemMetaEditor((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .setCustomModel(3)
            .build());

    public static final CustomItem CRYSTAL_KEY_DARK_ACTIVE = CustomItemManager.register(new CustomItem.CustomItemBuilder("crystal_key_dark_active", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Crystal Key")
            .addLoreLine(ChatColor.RESET + "" + ChatColor.WHITE + "The key whispers secrets and promises; it is ready")
            .setItemMetaEditor((meta -> {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return meta;
            }))
            .setCustomModel(3)
            .build());

    public static final CustomItem DUNGEON_COIN = CustomItemManager.register(new CustomItem.CustomItemBuilder("dungeon_coin", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Coin")
            .setCustomModel(1)
            .build());

    public static final CustomItem DREAMGLASS = CustomItemManager.register(new CustomItem.CustomItemBuilder("dreamglass", Material.SPYGLASS, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_AQUA + "Dreamglass")
            .addLoreLine(ChatColor.WHITE + "The lens distorts reality")
            .setCustomModel(1)
            .build());

    // ----- KEYS -----

    public static final CustomItem DUNGEON_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("dungeon_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Key")
            .setCustomModel(2)
            .build());

    public static final CustomItem RUSTY_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("rusty_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GRAY + "Rusty Key")
            .setCustomModel(11)
            .build());

    public static final CustomItem RED_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("red_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Red Key")
            .setCustomModel(12)
            .build());

    public static final CustomItem GREEN_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("green_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Green Key")
            .setCustomModel(13)
            .build());

    public static final CustomItem BLUE_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("blue_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.BLUE + "Blue Key")
            .setCustomModel(14)
            .build());

    // ----- MISCELLANEOUS -----

    public static final CustomItem BROKEN_SPELL = CustomItemManager.register(new CustomItem.CustomItemBuilder("broken_spell", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Broken Spell")
            .addLoreLine("A malfunctioning spell")
            .setCustomModel(61)
            .build());

    public static final CustomItem BROKEN_WAND = CustomItemManager.register(new CustomItem.CustomItemBuilder("broken_wand", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Broken Wand")
            .addLoreLine("This wand has snapped in half")
            .addLoreLine("but it still crackles with magical energy")
            .setCustomModel(15)
            .build());

    // ----- COINS -----

    public static final CustomItem IRON_COIN = CustomItemManager.register(new CustomItem.CustomItemBuilder("iron_coin", Material.IRON_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&f&lIron Coin"))
            .addLoreLine(HexUtils.colorify("&r&7It glimmers"))
            .setCustomModel(1)
            .build());

    public static final CustomItem GOLD_COIN = CustomItemManager.register(new CustomItem.CustomItemBuilder("gold_coin", Material.GOLD_NUGGET, 1)
            .setCustomName(HexUtils.colorify("&6&lGold Coin"))
            .addLoreLine(HexUtils.colorify("&r&eIt glimmers"))
            .setCustomModel(1)
            .build());

    // ----- MATERIALS -----

    public static final CustomItem CRYSTAL_LENS = CustomItemManager.register(new CustomItem.CustomItemBuilder("crystal_lens", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&5Crystal Lens")).addLoreLine(HexUtils.colorify("&dLight shifts and distorts")).setCustomModel(4).build());

    public static final CustomItem DRAGONSTONE = CustomItemManager.register(new CustomItem.CustomItemBuilder("dragonstone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&dDragonstone")).setCustomModel(5).build());

    public static final CustomItem MANA_DIAMOND = CustomItemManager.register(new CustomItem.CustomItemBuilder("mana_diamond", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Diamond")).setCustomModel(6).build());

    public static final CustomItem MANA_PEARL = CustomItemManager.register(new CustomItem.CustomItemBuilder("mana_pearl", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Pearl")).setCustomModel(7).build());

    public static final CustomItem MANA_POWDER = CustomItemManager.register(new CustomItem.CustomItemBuilder("mana_powder", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fMana Powder")).setCustomModel(8).build());

    public static final CustomItem RAINBOW_ROD = CustomItemManager.register(new CustomItem.CustomItemBuilder("rainbow_rod", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<r:.5:1>Rainbow Rod")).setCustomModel(9).build());

    public static final CustomItem AQUAMARINE = CustomItemManager.register(new CustomItem.CustomItemBuilder("aquamarine", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fAquamarine")).setCustomModel(10).build());

    // ----- RUNES -----

    public static final CustomItem RUNE_OF_AIR = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_air", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.AQUA + "Rune of Air").setCustomModel(1001).addTag("rune").build());

    public static final CustomItem RUNE_OF_AUTUMN = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_autumn", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Rune of Autumn").setCustomModel(1002).addTag("rune").build());

    public static final CustomItem RUNE_OF_EARTH = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_earth", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_GREEN + "Rune of Earth").setCustomModel(1003).addTag("rune").build());

    public static final CustomItem RUNE_OF_ENVY = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_envy", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Rune of Envy").setCustomModel(1004).addTag("rune").build());

    public static final CustomItem RUNE_OF_FIRE = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_fire", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_RED + "Rune of Fire").setCustomModel(1005).addTag("rune").build());

    public static final CustomItem RUNE_OF_GLUTTONY = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_gluttony", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Rune of Gluttony").setCustomModel(1006).addTag("rune").build());

    public static final CustomItem RUNE_OF_GREED = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_greed", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Rune of Greed").setCustomModel(1007).addTag("rune").build());

    public static final CustomItem RUNE_OF_LUST = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_lust", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "Rune of Lust").setCustomModel(1008).addTag("rune").build());

    public static final CustomItem RUNE_OF_MANA = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_mana", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_AQUA + "Rune of Mana").setCustomModel(1009).addTag("rune").build());

    public static final CustomItem RUNE_OF_PRIDE = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_pride", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Rune of Pride").setCustomModel(1010).addTag("rune").build());

    public static final CustomItem RUNE_OF_SLOTH = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_sloth", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.BLUE + "Rune of Sloth").setCustomModel(1011).addTag("rune").build());

    public static final CustomItem RUNE_OF_SPRING = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_spring", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_GREEN + "Rune of Spring").setCustomModel(1012).addTag("rune").build());

    public static final CustomItem RUNE_OF_SUMMER = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_summer", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.YELLOW + "Rune of Summer").setCustomModel(1013).addTag("rune").build());

    public static final CustomItem RUNE_OF_WATER = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_water", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.DARK_BLUE + "Rune of Water").setCustomModel(1014).addTag("rune").build());

    public static final CustomItem RUNE_OF_WINTER = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_winter", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.WHITE + "Rune of Winter").setCustomModel(1015).addTag("rune").build());

    public static final CustomItem RUNE_OF_WRATH = CustomItemManager.register(new CustomItem.CustomItemBuilder("rune_of_wrath", Material.CLOCK, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Rune of Wrath").setCustomModel(1016).addTag("rune").build());

    // ----- CUSTOM RECIPES -----

    public static final ShapedRecipe SPARKBOLT_RECIPE = CustomItemManager.addShapedRecipe(SpellManager.SPARK_BOLT.getSpellItem(),
            "-*-", "*%*", "-*-",
            new AbstractCustomItem.ShapedIngredient('%', MANA_PEARL.getItemStack()),
            new AbstractCustomItem.ShapedIngredient('*', Material.AMETHYST_SHARD),
            new AbstractCustomItem.ShapedIngredient('-', AQUAMARINE.getItemStack()));

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ItemManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomItemManager</code>.
     *
     * @return The object of this class
     */
    public static ItemManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ItemManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
