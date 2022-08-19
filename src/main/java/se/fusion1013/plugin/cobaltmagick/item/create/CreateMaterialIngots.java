package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ShapedRecipe;
import se.fusion1013.plugin.cobaltcore.item.AbstractCustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;

public class CreateMaterialIngots {

    public static void create() {}

    public static ICustomItem ARDITE_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("ardite_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fArdite Ingot"))
            .setCustomModel(16).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem ARDITE_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("ardite_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fArdite Nugget"))
            .setCustomModel(17).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem COBALT_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("cobalt_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fCobalt Ingot"))
            .setCustomModel(18).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem COBALT_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("cobalt_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fCobalt Nugget"))
            .setCustomModel(19).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem HEPATIZON_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("hepatizon_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fHepatizon Ingot"))
            .setCustomModel(20).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem HEPATIZON_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("hepatizon_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fHepatizon Nugget"))
            .setCustomModel(21).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem HOLLOW_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("hollow_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fHollow Ingot"))
            .setCustomModel(22).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem HOLLOW_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("hollow_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fHollow Nugget"))
            .setCustomModel(23).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem KNIGHTSLIME_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("knightslime_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fKnightslime Ingot"))
            .setCustomModel(24).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem KNIGHTSLIME_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("knightslime_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fKnightslime Nugget"))
            .setCustomModel(25).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem MANYULLYN_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("manyullyn_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fManyullyn Ingot"))
            .setCustomModel(26).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem MANYULLYN_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("manyullyn_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fManyullyn Nugget"))
            .setCustomModel(27).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem PIGIRON_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("pigiron_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fPigiron Ingot"))
            .setCustomModel(28).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem PIGIRON_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("pigiron_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fPigiron Nugget"))
            .setCustomModel(29).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem QUEENS_SLIME_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("queens_slime_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fQueens Slime Ingot"))
            .setCustomModel(30).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem QUEENS_SLIME_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("queens_slime_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fQueens Slime Nugget"))
            .setCustomModel(31)
            .build());

    public static ICustomItem ROSE_GOLD_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("rose_gold_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fRose Gold Ingot"))
            .setCustomModel(32).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem ROSE_GOLD_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("rose_gold_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fRose Gold Nugget"))
            .setCustomModel(33).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem SLIMESTEEL_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("slimesteel_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fSlimesteel Ingot"))
            .setCustomModel(34).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem SLIMESTEEL_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("slimesteel_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fSlimesteel Nugget"))
            .setCustomModel(35).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem ARCANE_ALLOY = CustomItemManager.register(new CustomItem.CustomItemBuilder("arcane_alloy", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&bArcane Alloy"))
            .addLoreLine(HexUtils.colorify("&9&oEnergy pulses within..."))
            .setItemMetaEditor((itemMeta -> {
                itemMeta.addEnchant(Enchantment.MENDING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return itemMeta;
            }))
            .setCustomModel(36).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem ARCANE_ALLOY_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("arcane_alloy_nugget", Material.EMERALD, 9)
            .setCustomName(HexUtils.colorify("&bArcane Alloy Nugget"))
            .addLoreLine(HexUtils.colorify("&9&oEnergy pulses within..."))
            .setItemMetaEditor((itemMeta -> {
                itemMeta.addEnchant(Enchantment.MENDING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return itemMeta;
            }))
            .addShapelessRecipe(new AbstractCustomItem.ShapelessIngredient(1, ARCANE_ALLOY.getItemStack()))
            .setCustomModel(37).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ShapedRecipe ARCANE_ALLOY_RECIPE = CustomItemManager.addShapedRecipe(ARCANE_ALLOY.getItemStack(), "---", "---", "---", new AbstractCustomItem.ShapedIngredient('-', ARCANE_ALLOY_NUGGET.getItemStack()));

    public static ICustomItem TINKERS_BRONZE_INGOT = CustomItemManager.register(new CustomItem.CustomItemBuilder("tinkers_bronze_ingot", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fTinkers Bronze Ingot"))
            .setCustomModel(38).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static ICustomItem TINKERS_BRONZE_NUGGET = CustomItemManager.register(new CustomItem.CustomItemBuilder("tinkers_bronze_nugget", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("&fTinkers Bronze Nugget"))
            .setCustomModel(39).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

}
