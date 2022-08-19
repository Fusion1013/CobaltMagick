package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

public enum SpellType{
    PROJECTILE(ChatColor.RED, "Projectile", "spark_bolt"),
    STATIC_PROJECTILE(ChatColor.YELLOW, "Static Projectile", "sphere_of_buoyancy"),
    PASSIVE(ChatColor.GRAY, "Passive", "TODO"),
    UTILITY(ChatColor.DARK_PURPLE, "Utility", "all-seeing_eye"),
    PROJECTILE_MODIFIER(ChatColor.BLUE, "Projectile Modifier", "increase_lifetime"), // TODO: Change the display item
    MATERIAL(ChatColor.GREEN, "Material", "sea_of_water"),
    MULTICAST(ChatColor.AQUA, "Multicast", "triple_scatter_spell"),
    OTHER(ChatColor.GOLD, "Other", "ocarina_note_b");

    final ChatColor spellColor;
    final String localizedName;
    final String displaySpell;

    SpellType(ChatColor spellColor, String localizedName, String displaySpell){
        this.spellColor = spellColor;
        this.localizedName = localizedName;
        this.displaySpell = displaySpell;
    }

    public ChatColor getSpellColor() {
        return spellColor;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public ItemStack getDisplayItem() {
        ISpell spell = SpellManager.getSpell(displaySpell);
        if (spell != null) return spell.getSpellItem();
        else return ItemManager.BROKEN_SPELL.getItemStack();
    }
}
