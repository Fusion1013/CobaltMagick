package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface MagickItem {
    NamespacedKey getNamespacedKey();
    String getInternalName();
    ItemStack getItemStack();
}
