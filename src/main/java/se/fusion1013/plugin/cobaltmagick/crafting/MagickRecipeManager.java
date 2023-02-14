package se.fusion1013.plugin.cobaltmagick.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.crafting.IRecipeWrapper;
import se.fusion1013.plugin.cobaltcore.item.crafting.RecipeManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.ICobaltCraftingRecipe;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.MagickAnvilRecipe;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.MagickAnvilRecipeWrapper;

public class MagickRecipeManager extends Manager {

    //region REGISTER

    private static final IRecipeWrapper MAGICK_ANVIL_WRAPPER = RecipeManager.registerWrapper(new MagickAnvilRecipeWrapper(null, null, null));

    //endregion

    public MagickRecipeManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
