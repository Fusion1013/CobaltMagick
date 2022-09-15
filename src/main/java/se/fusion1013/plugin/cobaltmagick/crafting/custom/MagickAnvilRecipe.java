package se.fusion1013.plugin.cobaltmagick.crafting.custom;

import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class MagickAnvilRecipe implements ICobaltCraftingRecipe {

    // ----- VARIABLES -----

    private ItemStack[] items;
    private ItemStack result;

    // ----- CONSTRUCTORS -----

    public MagickAnvilRecipe(ItemStack[] items, ItemStack result) {
        this.items = items;
        this.result = result;
    }

    public MagickAnvilRecipe(JsonObject jsonObject) {
        // TODO
    }

}
