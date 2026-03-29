package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltmagick.alchemy.cauldron.ICauldronRecipe;

public interface IPotionRecipe extends ICauldronRecipe {

    String getInternalName();

    PotionEffectType getPotionEffectType();

    void placeTemplate(Location location, boolean cinematic);

    String getMetalName();

    String getBindingName();

    String[] getItemNames();

    ICondition[] getConditions();
}
