package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;

import java.util.List;

public interface ICauldronRecipe extends IRegistryItem {

    String getGlyph();

    boolean hasSpecificGlyph();

    boolean validateConditions(Location location);

    boolean validateItems(List<ItemStack> items);

    void execute(Location location, CauldronState state, int count, RuleLogger ruleLogger);

    IElementalAffinity getElementalAffinity();

    boolean allowExternalBlocks();

}
