package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltmagick.alchemy.potion.IPotionRecipe;

public interface ICauldronInstance {

    IPotionRecipe potionRecipe();

    void incrementProgress();

    boolean isRecipeDone();

    ItemStack getCurrentRequiredItem();

}
