package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltmagick.alchemy.potion.IPotionRecipe;

public final class CauldronInstance implements ICauldronInstance {

    private final IPotionRecipe potionRecipe;
    private int progress = 0;

    public CauldronInstance(IPotionRecipe potionRecipe) {
        this.potionRecipe = potionRecipe;
    }

    public IPotionRecipe potionRecipe() {
        return potionRecipe;
    }

    public void incrementProgress() {
        progress++;
    }

    public boolean isRecipeDone() {
        String[] required = potionRecipe.getInputItemOrder();
        return required.length <= progress;
    }

    public ItemStack getCurrentRequiredItem() {
        String[] required = potionRecipe.getInputItemOrder();
        if (required.length <= progress) return null; // Recipe is done
        return CustomItemManager.getItemStack(required[progress]);
    }


}
