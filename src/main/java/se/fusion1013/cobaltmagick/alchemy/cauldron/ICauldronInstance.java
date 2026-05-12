package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;

import java.util.Set;

public interface ICauldronInstance {

    boolean hasValidGlyph();

    GlyphData getGlyph();

    boolean isEmpty();

    void insertItem(ItemStack item);

    ItemStack[] getHeldItems();

    int clearHeldItems();

    ICauldronRecipe getValidRecipe(ItemStack finalItem);

    Set<Vector> getGlyphVectors(Location location);

    void decay(Location location, int decayTotal);

    Location getLocation();

    void tick();

    int getTicks();

    int getLowestItemCount();

    double getVeinDistance(String vein);
}
