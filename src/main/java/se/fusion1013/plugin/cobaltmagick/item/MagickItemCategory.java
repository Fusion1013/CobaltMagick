package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

public enum MagickItemCategory implements IItemCategory {

    HAT("hat", "&6Hats", "Hat items", Material.YELLOW_SHULKER_BOX),
    KEY("key", "&6Keys", "Key items", Material.YELLOW_SHULKER_BOX),
    MATERIAL("material", "&8Materials", "Material items", Material.GRAY_SHULKER_BOX),
    POTION("potion", "&3Potions", "Potion items", Material.CYAN_SHULKER_BOX),
    RUNE("rune", "&2Runes", "Rune items", Material.GREEN_SHULKER_BOX),
    SPELL("spell", "&dSpells", "Spell items", Material.MAGENTA_SHULKER_BOX),
    TOOL("tool", "&7Tools", "Tool items", Material.LIGHT_GRAY_SHULKER_BOX),
    WAND("wand", "&bWands", "Wand items", Material.LIGHT_BLUE_SHULKER_BOX);

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
    public String getName() {
        return HexUtils.colorify(name);
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
