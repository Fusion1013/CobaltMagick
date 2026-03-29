package se.fusion1013.cobaltmagick.alchemy.potion;

import com.google.gson.JsonObject;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltCore.variable.ConditionVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.PotionTypeVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectUtil;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;
import se.fusion1013.cobaltmagick.alchemy.glyph.GlyphValidatorUtil;
import se.fusion1013.cobaltmagick.util.AdvancementUtil;

import java.util.*;

public class PotionRecipe implements IPotionRecipe, IElementalAffinity {

    private static final Random random = new Random();

    private final StringVariable internalName = new StringVariable("internal_name");
    private final PotionTypeVariable effectType = new PotionTypeVariable("potion");
    private final StringVariable metalItem = new StringVariable("metal");
    private final StringVariable bindingItem = new StringVariable("binding");
    private final StringVariable requiredItems = new StringVariable("items");
    private final StringVariable glyph = new StringVariable("glyph");
    private final StringVariable elementalAffinities = new StringVariable("elemental_affinity");
    private final ConditionVariable conditions = new ConditionVariable("conditions");
    private final IntVariable amountMultiplier = new IntVariable("count_multiplier", 1);

    public static IPotionRecipe create(ConfigurationSection yaml) {
        PotionRecipe recipe = new PotionRecipe();
        recipe.load(yaml);
        return recipe;
    }

    public static IPotionRecipe create(JsonObject json) {
        throw new NotImplementedException();
    }

    private void load(ConfigurationSection yaml) {
        internalName.load(yaml);
        effectType.load(yaml);
        metalItem.load(yaml);
        bindingItem.load(yaml);
        requiredItems.load(yaml);
        glyph.load(yaml);
        elementalAffinities.load(yaml);
        conditions.load(yaml);
        amountMultiplier.load(yaml);
    }

    @Override
    public String getGlyph() {
        return glyph.getValue();
    }

    @Override
    public String[] getElementalAffinities() {
        return elementalAffinities.getValueList().toArray(new String[0]);
    }

    public boolean validateConditions(Location cauldronLocation) {
        Map<String, Object> context = new HashMap<>();
        context.put("default_location", cauldronLocation);
        context.put("above_cauldron", cauldronLocation.clone().add(new Vector(0, 1, 0)));
        return conditions.getValueList().stream().allMatch(c -> c.evaluate(context));
    }

    @Override
    public boolean validateItems(List<ItemStack> containedItems) {
        List<ItemStack> reduceList = new ArrayList<>(containedItems);

        if (validateInitialItems(containedItems)) return false;

        reduceList.removeFirst();
        reduceList.removeFirst();

        removeModifierItems(reduceList);
        if (reduceList.size() < requiredItems.getValueList().size()) return false;

        for (int j = requiredItems.getValueList().size() - 1; j >= 0; j--) {
            for (int i = reduceList.size() - 1; i >= 0; i--) {
                ItemStack inputItem = reduceList.get(i);
                String requiredItemName = requiredItems.getValueList().get(j);

                ItemStack requiredItem = CustomItemManager.getItemStack(requiredItemName);
                if (requiredItem == null) continue;

                if (!CustomItemManager.compare(inputItem, requiredItem)) continue;
                reduceList.remove(i);
                break;
            }
        }

        return reduceList.isEmpty();
    }

    private boolean validateInitialItems(List<ItemStack> containedItems) {
        if (containedItems.size() < 2) return true;

        ItemStack item1 = containedItems.get(0);
        ItemStack item2 = containedItems.get(1);

        if (metalItem.getValue() == null || bindingItem.getValue() == null) return true;

        ItemStack requiredMetalItem = CustomItemManager.getItemStack(metalItem.getValue());
        ItemStack requiredBindingItem = CustomItemManager.getItemStack(bindingItem.getValue());

        if (requiredMetalItem == null || requiredBindingItem == null) return true;

        if (!CustomItemManager.compare(item1, requiredMetalItem)) return true;
        return !CustomItemManager.compare(item2, requiredBindingItem);
    }

    private void removeModifierItems(List<ItemStack> reduceList) {
        for (int i = reduceList.size() - 1; i >= 0; i--) {
            ItemStack item = reduceList.get(i);
            PersistentDataContainerView pc = item.getPersistentDataContainer();
            if (!pc.has(new NamespacedKey(CobaltCore.getInstance(), "alchemy_extra"))) continue;
            reduceList.remove(i);
        }
    }

    @Override
    public void execute(Location location, CauldronState state, int count) {
        ItemStack item = getPotionItem(this, state, location);
        item.setAmount(count);

        CauldronEffectUtil.animateCauldron(location, item, CobaltMagick.getInstance());

        grantAdvancement(location);
    }

    private void grantAdvancement(Location location) {
        Advancement alchemy = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "alchemy/root"));
        Advancement potions = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "alchemy/potions"));
        Advancement root = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "potions/root"));
        Advancement advancement = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "potions/" + internalName.getValue()));

        AdvancementUtil.grantInRange(location, alchemy, 16);
        AdvancementUtil.grantInRange(location, potions, 16);
        AdvancementUtil.grantInRange(location, root, 16);
        AdvancementUtil.grantInRange(location, advancement, 16);
    }


    private ItemStack getPotionItem(IPotionRecipe recipe, CauldronState state, Location location) {
        PotionCreator potionCreator = new PotionCreator(recipe.getPotionEffectType())
                .variance(state.getVariance())
                .potency(state.getPotency())
                .duration(state.getDuration())
                .wild(state.getWild())
                .decay(state.getDecay())
                .elementalAffinity(elementalAffinities.getValueList())
                .amount(amountMultiplier.getValue());
        return potionCreator.getItem(location);
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    @Override
    public PotionEffectType getPotionEffectType() {
        return effectType.getValue();
    }

    @Override
    public void placeTemplate(Location cauldronLocation, boolean cinematic) {
        GlyphData glyphData = GlyphManager.getGlyphFromName(getGlyph());
        if (glyphData == null) return;

        Set<Vector> vectors = GlyphValidatorUtil.getGlyphOffsets(glyphData.category() + "." + glyphData.name());
        if (vectors == null) return;

        World world = cauldronLocation.getWorld();

        for (Vector vector : vectors) {
            Location currentLocation = cauldronLocation.clone().add(vector);
            if (AlchemyManager.getProperties(currentLocation.getBlock().getType()) != null) continue;
            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(),
                    () -> {
                        currentLocation.getBlock().setBlockData(Material.WAXED_OXIDIZED_COPPER.createBlockData());
                        world.playSound(currentLocation, Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1, 1);
                        world.spawnParticle(Particle.BLOCK, currentLocation.toCenterLocation(), 10, .6, .6, .6, 0, Material.WAXED_OXIDIZED_COPPER.createBlockData());
                    },
                    random.nextInt(0, Math.min(vectors.size(), 30)));
        }
    }

    @Override
    public String getMetalName() {
        return metalItem.getValue();
    }

    @Override
    public String getBindingName() {
        return bindingItem.getValue();
    }

    @Override
    public String[] getItemNames() {
        return requiredItems.getValueList().toArray(new String[0]);
    }

    @Override
    public ICondition[] getConditions() {
        return conditions.getValueList().toArray(new ICondition[0]);
    }

    @Override
    public IElementalAffinity getElementalAffinity() {
        return this;
    }

}
