package se.fusion1013.cobaltmagick.alchemy.potion;

import com.google.gson.JsonObject;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltCore.variable.ConditionVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.PotionTypeVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.AbstractCauldronRecipe;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectUtil;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;
import se.fusion1013.cobaltmagick.alchemy.glyph.GlyphValidatorUtil;
import se.fusion1013.cobaltmagick.util.AdvancementUtil;

import java.util.*;

public class PotionRecipe extends AbstractCauldronRecipe implements IPotionRecipe, IElementalAffinity {

    private final PotionTypeVariable effectType = new PotionTypeVariable("potion");
    private final StringVariable metalItem = new StringVariable("metal");
    private final StringVariable bindingItem = new StringVariable("binding");
    private final StringVariable requiredItems = new StringVariable("items");
    private final StringVariable glyph = new StringVariable("glyph");
    private final ConditionVariable conditions = new ConditionVariable("conditions");
    private final IntVariable amountMultiplier = new IntVariable("count_multiplier", 1);

    public PotionRecipe(YamlConfiguration yaml) {
        super(yaml);
        load(yaml);
    }

    public PotionRecipe(JsonObject json) {
        super(json);
    }

    @Override
    public boolean validateFinalItem(ItemStack itemStack) {
        return itemStack.getType() == Material.GLASS_BOTTLE;
    }

    private void load(ConfigurationSection yaml) {
        effectType.load(yaml);
        metalItem.load(yaml);
        bindingItem.load(yaml);
        requiredItems.load(yaml);
        glyph.load(yaml);
        conditions.load(yaml);
        amountMultiplier.load(yaml);
    }

    @Override
    public String getGlyph() {
        return glyph.getValue();
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
    public void execute(Location location, CauldronState state, int count, RuleLogger ruleLogger) {
        ItemStack item = ruleLogger.evaluate("Get Potion Item", () -> getPotionItem(this, state, location, ruleLogger));
        item.setAmount(count);
        World world = location.getWorld();

        CauldronEffectUtil.animateCauldron(location, (center) -> {
            // Final burst effect
            world.spawnParticle(Particle.FLASH, center.clone().add(0, 2.5, 0), 1, Color.WHITE);
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, 2.5, 0), 10, .1, .1, .1, 0);
            world.playSound(center, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1.2f);

            Item dropped = world.spawn(center.clone().add(0, 2, 0), Item.class, spawnedItem -> {
                spawnedItem.setItemStack(item.clone());
                spawnedItem.setGravity(false);
                spawnedItem.setGlowing(true);
                spawnedItem.setVelocity(new Vector());
            });
        }, CobaltMagick.getInstance());

        grantAdvancement(location);
    }

    @Override
    public boolean allowExternalBlocks() {
        return false;
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


    private ItemStack getPotionItem(IPotionRecipe recipe, CauldronState state, Location location, RuleLogger ruleLogger) {
        PotionCreator potionCreator = new PotionCreator(recipe.getPotionEffectType(), ruleLogger)
                .variance(state.getVariance())
                .potency(state.getPotency())
                .duration(state.getDuration())
                .wild(state.getWild())
                .decay(state.getDecay())
                .elementalAffinity(elementalAffinities.getValueList())
                .amount(amountMultiplier.getValue());
        return ruleLogger.evaluate("Get Item", () -> potionCreator.getItem(location));
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

}
