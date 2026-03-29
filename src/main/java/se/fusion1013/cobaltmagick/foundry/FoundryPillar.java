package se.fusion1013.cobaltmagick.foundry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.item.CustomItemManager;

public record FoundryPillar(Vector offset, String pedestalType, String requiredItem) {

    public ItemStack getItem() {
        return CustomItemManager.getItemStack(requiredItem);
    }

}
