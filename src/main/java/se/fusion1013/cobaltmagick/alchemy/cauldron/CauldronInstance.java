package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;
import se.fusion1013.cobaltmagick.alchemy.glyph.GlyphUtil;
import se.fusion1013.cobaltmagick.alchemy.glyph.GlyphValidatorUtil;

import java.util.*;

public final class CauldronInstance implements ICauldronInstance {

    private final Location location;
    private final List<ItemStack> heldItems = new ArrayList<>();
    private final Map<String, Double> veinDistances = new HashMap<>();

    private int ticks = 0;

    public CauldronInstance(Location location) {
        this.location = location;
        calculateVeinDistances();
    }

    private void calculateVeinDistances() {
        Collection<IElementalVein> veins = ElementalVeinManager.getElementalVeins();
        for (IElementalVein vein : veins) {
            ParametricSpline2D.Result result = vein.getClosestPoint(location.x(), location.z());
            veinDistances.put(vein.getInternalName(), result.distance());
        }
    }

    @Override
    public boolean hasValidGlyph() {
        return getGlyph() != null;
    }

    @Override
    public GlyphData getGlyph() {
        List<ICauldronRecipe> recipes = CauldronManager.getCauldronRecipes();
        for (ICauldronRecipe recipe : recipes) {
            String glyphName = recipe.getGlyph();
            GlyphData glyphData = GlyphManager.getGlyphFromName(glyphName);
            boolean isValid = GlyphValidatorUtil.hasValidGlyph(location, glyphName);
            if (isValid) return glyphData;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return heldItems.isEmpty();
    }

    @Override
    public void insertItem(ItemStack item) {
        if (heldItems.isEmpty()) {
            heldItems.add(item);
            return;
        }

        ItemStack previousItemStack = heldItems.getLast();
        if (previousItemStack.isEmpty()) {
            heldItems.add(item);
            return;
        }

        if (!previousItemStack.equals(item)) {
            heldItems.add(item);
            return;
        }

        previousItemStack.setAmount(previousItemStack.getAmount() + item.getAmount());
    }

    @Override
    public ItemStack[] getHeldItems() {
        return heldItems.toArray(new ItemStack[0]);
    }

    @Override
    public ICauldronRecipe getValidRecipe(ItemStack finalItem) {
        GlyphData glyph = getGlyph();
        if (glyph == null) return null;

        ICauldronRecipe[] recipes = CauldronManager.getRecipesMatchingGlyph(glyph);

        for (ICauldronRecipe recipe : recipes) {
            boolean isValid = recipe.validateConditions(location) && recipe.validateItems(heldItems) && recipe.validateFinalItem(finalItem);
            if (isValid) return recipe;
        }

        return null;
    }

    @Override
    public Set<Vector> getGlyphVectors(Location location) {
        GlyphData glyphData = getGlyph();
        if (glyphData == null) return new HashSet<>();
        return GlyphValidatorUtil.getValidGlyphOffsets(location, glyphData.category() + "." + glyphData.name());
    }

    @Override
    public void decay(Location location, int decayTotal) {
        GlyphUtil.decay(location, getGlyphVectors(location), decayTotal);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public int getTicks() {
        return ticks;
    }

    @Override
    public int getLowestItemCount() {
        int count = Integer.MAX_VALUE;
        for (ItemStack item : heldItems) {
            if (item.getAmount() < count) count = item.getAmount();
        }
        return count;
    }

    @Override
    public double getVeinDistance(String vein) {
        return veinDistances.get(vein);
    }

    @Override
    public int clearHeldItems() {
        int amount = heldItems.size();
        heldItems.clear();
        return amount;
    }


}
