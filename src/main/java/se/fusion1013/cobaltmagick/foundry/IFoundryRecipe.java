package se.fusion1013.cobaltmagick.foundry;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

public interface IFoundryRecipe extends IRegistryItem {

    boolean validate(Location anvilLocation);

    ItemStack getResult();

    ItemStack getMold();

    boolean isCorrectMold(ItemStack itemStack);

    void execute(Location location);

    void placeTemplate(Location center, boolean cinematic);

    String getDisplayName();

}
