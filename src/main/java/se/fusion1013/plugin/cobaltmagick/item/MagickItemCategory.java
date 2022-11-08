package se.fusion1013.plugin.cobaltmagick.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public enum MagickItemCategory implements IItemCategory {

    HAT("hat", "Hats", "Hat items", Material.YELLOW_SHULKER_BOX),
    KEY("key", "Keys", "Key items", Material.YELLOW_SHULKER_BOX),
    MATERIAL("material", "Materials", "Material items", Material.GRAY_SHULKER_BOX),
    POTION("potion", "Potions", "Potion items", Material.CYAN_SHULKER_BOX),
    RUNE("rune", "Runes", "Rune items", Material.GREEN_SHULKER_BOX),
    SPELL("spell", "Spells", "Spell items", Material.MAGENTA_SHULKER_BOX),
    TOOL("tool", "Tools", "Tool items", Material.LIGHT_GRAY_SHULKER_BOX),
    WEAPON("weapon", "Weapon", "Weapon items", Material.LIGHT_GRAY_SHULKER_BOX),
    ARMOR("armor", "Armor", "Armor items", Material.YELLOW_SHULKER_BOX),
    WAND("wand", "Wands", "Wand items", Material.LIGHT_BLUE_SHULKER_BOX),
    UNKNOWN("unknown", "???", "Items of unknown origin", Material.PURPLE_SHULKER_BOX);

    final String internalName;
    final String name;
    final String description;
    final Material boxMaterial;

    MagickItemCategory(String internalName, String name, String description, Material boxMaterial) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.boxMaterial = boxMaterial;
    }

    @Override
    public Component getFormattedName() {
        return Component.text(name);
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
        return boxMaterial;
    }


}
