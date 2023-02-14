package se.fusion1013.plugin.cobaltmagick.crafting.custom;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.crafting.ICobaltRecipe;

public record MagickAnvilRecipe(
        NamespacedKey key,
        ItemStack[] input,
        ItemStack[] output
) implements ICobaltRecipe {

    //region GETTERS/SETTERS

    public ItemStack[] getInput() {
        return input;
    }

    public ItemStack[] getOutput() {
        return output;
    }

    @Override
    public String getRecipeType() {
        return "magick_anvil";
    }

    @Override
    public String getInternalName() {
        return key.getKey();
    }

    //endregion
}
