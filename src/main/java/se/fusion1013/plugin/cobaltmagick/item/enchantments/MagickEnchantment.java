package se.fusion1013.plugin.cobaltmagick.item.enchantments;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.enchantment.ICobaltEnchantment;

public enum MagickEnchantment implements ICobaltEnchantment {

    // -- Pickaxe
    AUTOSMELT("autosmelt", "Autosmelt", "Automatically smelts mined blocks", 1, EnchantmentTarget.PICKAXE, EnchantmentTarget.SHOVEL, EnchantmentTarget.AXE),

    // -- Sword
    FROSTBITE("frostbite", "Frostbite", "Inflicts a few seconds of slowness & frost", 5),

    // -- Passive
    ADRENALINE("adrenaline", "Adrenaline", "When the holder or wearer kills a mob, they gain a decaying speed buff", 1),
    AGILITY("agility", "Agility", "Allows the wearer to jump an extra block per level", 2),
    EVASION("evasion", "Evasion", "When the holder or wearer takes damage, they have a chance to dodge. Dodging the attack negates all damage it would do", 5),
    REGENERATION("regeneration", "Regeneration", "The holder or wearer regenerates 1 health every 4 seconds. Every level of regeneration decreases the time interval by 1/3 second", 3),

    // -- Curses
    CURSE_OF_NEUTRALIZED_SPELLS("curse_of_neutralized_spells", "Curse of Neutralized Spells", "Spells cast by the wearer or holder have no effect", 1, true),
    CURSE_OF_WEAKENING("curse_of_weakening", "Curse of Weakening", "Halves your health for each level", 1, true),
    CURSE_OF_SLIPPERINESS("curse_of_slipperiness", "Curse of Slipperiness", "Risk of dropping the item when switching to it in the hotbar", 1, true);

    // ----- VARIABLES -----

    final String internalName;
    final Component name;
    final Component description;
    final int maxLevel;

    final boolean isCurse;

    EnchantmentTarget[] enchantmentTargets = {EnchantmentTarget.ANY};

    // ----- CONSTRUCTORS -----

    MagickEnchantment(String internalName, String name, String description, int maxLevel) {
        this.internalName = internalName;
        this.name = Component.text(name);
        this.description = Component.text(description);
        this.maxLevel = maxLevel;
        this.isCurse = false;
    }

    MagickEnchantment(String internalName, String name, String description, int maxLevel, boolean isCurse) {
        this.internalName = internalName;
        this.name = Component.text(name);
        this.description = Component.text(description);
        this.maxLevel = maxLevel;
        this.isCurse = isCurse;
    }

    MagickEnchantment(String internalName, String name, String description, int maxLevel, EnchantmentTarget... enchantmentTargets) {
        this.internalName = internalName;
        this.name = Component.text(name);
        this.description = Component.text(description);
        this.maxLevel = maxLevel;
        this.enchantmentTargets = enchantmentTargets;
        this.isCurse = false;
    }

    MagickEnchantment(String internalName, String name, String description, int maxLevel, boolean isCurse, EnchantmentTarget... enchantmentTargets) {
        this.internalName = internalName;
        this.name = Component.text(name);
        this.description = Component.text(description);
        this.maxLevel = maxLevel;
        this.isCurse = isCurse;
        this.enchantmentTargets = enchantmentTargets;
    }

    // ----- GETTERS / SETTERS ----

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltCore.getInstance(), "enchantment." + internalName);
    }

    @Override
    public boolean hasEnchantment(ItemStack stack) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(getNamespacedKey());
    }

    @Override
    public int getLevel(ItemStack stack) {
        if (!hasEnchantment(stack)) return 0;
        else return stack.getItemMeta().getPersistentDataContainer().get(getNamespacedKey(), PersistentDataType.INTEGER);
    }

    @Override
    public EnchantmentTarget[] getEnchantmentTargets() {
        return enchantmentTargets;
    }

    @Override
    public boolean isCurse() {
        return isCurse;
    }

}
