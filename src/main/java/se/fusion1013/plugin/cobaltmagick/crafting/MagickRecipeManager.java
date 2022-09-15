package se.fusion1013.plugin.cobaltmagick.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.ICobaltCraftingRecipe;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.MagickAnvilRecipe;

public class MagickRecipeManager extends Manager {

    // ----- REGISTER -----

    // Magick Anvil
    public static final ICobaltCraftingRecipe MAGICK_ANVIL_TEST = new MagickAnvilRecipe(new ItemStack[] {
            new ItemStack(Material.GOLD_INGOT)
    }, new ItemStack(Material.GOLD_BLOCK));

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
