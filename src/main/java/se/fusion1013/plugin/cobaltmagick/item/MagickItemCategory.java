package se.fusion1013.plugin.cobaltmagick.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public enum MagickItemCategory implements IItemCategory {

    HAT("hat", "Hats", "Hat items", NamedTextColor.YELLOW),
    KEY("key", "Keys", "Key items", NamedTextColor.YELLOW),
    MATERIAL("material", "Materials", "Material items", NamedTextColor.DARK_GRAY),
    POTION("potion", "Potions", "Potion items", NamedTextColor.DARK_AQUA),
    RUNE("rune", "Runes", "Rune items", NamedTextColor.DARK_GREEN),
    SPELL("spell", "Spells", "Spell items", NamedTextColor.LIGHT_PURPLE),
    TOOL("tool", "Tools", "Tool items", NamedTextColor.GRAY),
    WEAPON("weapon", "Weapon", "Weapon items", NamedTextColor.GRAY),
    ARMOR("armor", "Armor", "Armor items", NamedTextColor.YELLOW),
    WAND("wand", "Wands", "Wand items", NamedTextColor.BLUE),
    UNKNOWN("unknown", "???", "Items of unknown origin", NamedTextColor.DARK_PURPLE);

    final String internalName;
    final String name;
    final String description;

    // -- Color
    final NamedTextColor color;

    MagickItemCategory(String internalName, String name, String description, NamedTextColor color) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    @Override
    public Component getFormattedName() {
        return Component.text(name).color(color).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltMagick.getInstance(), "magick_item_category." + internalName);
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Material getBoxMaterial() {
        return BlockUtil.getColoredShulkerBox(color);
    }


}
