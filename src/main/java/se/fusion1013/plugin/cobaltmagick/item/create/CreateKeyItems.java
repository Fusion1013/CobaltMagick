package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

public class CreateKeyItems {

    public static void create() {}

    public static final ICustomItem DUNGEON_KEY = register(new CustomItem.CustomItemBuilder("dungeon_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GOLD + "Dungeon Key")
            .setCustomModel(2).setItemCategory(MagickItemCategory.KEY)
            .build());

    public static final ICustomItem RUSTY_KEY = register(new CustomItem.CustomItemBuilder("rusty_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GRAY + "Rusty Key")
            .setCustomModel(11).setItemCategory(MagickItemCategory.KEY)
            .build());

    public static final ICustomItem RED_KEY = register(new CustomItem.CustomItemBuilder("red_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.RED + "Red Key")
            .setCustomModel(12).setItemCategory(MagickItemCategory.KEY)
            .build());

    public static final ICustomItem GREEN_KEY = register(new CustomItem.CustomItemBuilder("green_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.GREEN + "Green Key")
            .setCustomModel(13).setItemCategory(MagickItemCategory.KEY)
            .build());

    public static final ICustomItem BLUE_KEY = register(new CustomItem.CustomItemBuilder("blue_key", Material.EMERALD, 1)
            .setCustomName(ChatColor.RESET + "" + ChatColor.BLUE + "Blue Key")
            .setCustomModel(14).setItemCategory(MagickItemCategory.KEY)
            .build());

    public static ICustomItem GOLD_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("gold_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&6Gold Key"))
            .setCustomModel(5000).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem SHADOW_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("shadow_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&5Shadow Key"))
            .setCustomModel(5001).setItemCategory(MagickItemCategory.KEY).build());

    // ----- BIOME KEYS -----

    public static ICustomItem JUNGLE_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("jungle_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#014002:#1ae81d>Jungle Key"))
            .setCustomModel(5002).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem CORRUPTION_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("corruption_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#7b1087:#ab50b5>Corrupted Key"))
            .setCustomModel(5003).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem CRIMSON_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("crimson_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#5e0000:#b80000>Crimson Key"))
            .setCustomModel(5004).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem HALLOWED_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("hallowed_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#FFD700:#82774e>Hallowed Key"))
            .setCustomModel(5005).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem FROZEN_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("frozen_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#2329a6:#2ecae6>Frozen Key"))
            .setCustomModel(5006).setItemCategory(MagickItemCategory.KEY).build());

    public static ICustomItem DESERT_KEY = CustomItemManager.register(new CustomItem.CustomItemBuilder("desert_key", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#381d04:#c2771b>Desert Key"))
            .setCustomModel(5007).setItemCategory(MagickItemCategory.KEY).build());

}
