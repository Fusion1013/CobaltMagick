package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltmagick.alchemy.cauldron.ICauldronRecipe;

public interface IPotionRecipe extends ICauldronRecipe {

    String getInternalName();

    PotionEffectType getPotionEffectType();

}
