package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;

public class CreateEssenceStones {

    public static void create() {}

    public static final CustomItem THUNDER_STONE = CustomItemManager.register(new CustomItem.CustomItemBuilder("thunder_stone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#067591:#0bccd6>The Thunder Stone"))
            .addLoreLine(HexUtils.colorify("&7&oThis small rock makes you"))
            .addLoreLine(HexUtils.colorify("&7&ofeel very charged")).setCustomModel(1014).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static final CustomItem FIRE_STONE = CustomItemManager.register(new CustomItem.CustomItemBuilder("fire_stone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#ad0707:#d4750f>The Brimstone"))
            .addLoreLine(HexUtils.colorify("&7&oThis tiny rock looks most"))
            .addLoreLine(HexUtils.colorify("&7&ofiery, but when touched only"))
            .addLoreLine(HexUtils.colorify("&7&oa pleasant warmth can be felt")).setCustomModel(1015).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static final CustomItem WATER_STONE = CustomItemManager.register(new CustomItem.CustomItemBuilder("water_stone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#125cdb:#40a2db>The Water Stone"))
            .addLoreLine(HexUtils.colorify("&7&oThis small rock is hard and solid,"))
            .addLoreLine(HexUtils.colorify("&7&oyet seems to be gushing with water")).setCustomModel(1016).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

    public static final CustomItem EARTH_STONE = CustomItemManager.register(new CustomItem.CustomItemBuilder("earth_stone", Material.EMERALD, 1)
            .setCustomName(HexUtils.colorify("<g:#0b750b:#0bd64f>The Earth Stone"))
            .addLoreLine(HexUtils.colorify("&7&oIt looks like it could stand"))
            .addLoreLine(HexUtils.colorify("&7&othe test of aeons")).setCustomModel(1017).setItemCategory(MagickItemCategory.MATERIAL)
            .build());

}
