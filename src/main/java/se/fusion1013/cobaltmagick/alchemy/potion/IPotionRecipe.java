package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;

public interface IPotionRecipe {

    boolean validateConditions(Location cauldronLocation);

    void addGlyphToCauldronState(Location cauldronLocation, CauldronState state);

    boolean validateGlyph(Location cauldronLocation);

    String getInternalName();

    String getFirstInputItem();

    String[] getInputItemOrder();

    PotionEffectType getPotionEffectType();

    void decay(Location location, int decay);
}
