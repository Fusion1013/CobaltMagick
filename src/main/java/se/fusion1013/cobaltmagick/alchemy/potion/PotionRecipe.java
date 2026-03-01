package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.variable.ConditionVariable;
import se.fusion1013.cobaltCore.variable.PotionTypeVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectUtil;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionRecipe implements IPotionRecipe, IElementalAffinity {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final PotionTypeVariable effectType = new PotionTypeVariable("potion");
    private final StringVariable inputItems = new StringVariable("items");
    private final StringVariable glyph = new StringVariable("glyph");
    private final StringVariable elementalAffinities = new StringVariable("elemental_affinity");
    private final ConditionVariable conditions = new ConditionVariable("conditions");

    public static IPotionRecipe create(ConfigurationSection yaml) {
        PotionRecipe recipe = new PotionRecipe();
        recipe.load(yaml);
        return recipe;
    }

    private void load(ConfigurationSection yaml) {
        internalName.load(yaml);
        effectType.load(yaml);
        inputItems.load(yaml);
        glyph.load(yaml);
        elementalAffinities.load(yaml);
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
        return conditions.getValueList().stream().allMatch(c -> c.evaluate(context));
    }

    @Override
    public boolean validateItems(List<ItemStack> items) {
        if (items.size() != this.inputItems.getValueList().size()) return false;

        for (int i = 0; i < items.size(); i++) {
            ItemStack inputItem = items.get(i);
            String required = this.inputItems.getValueList().get(i);

            ItemStack requiredItem = CustomItemManager.getItemStack(required);

            if (!CustomItemManager.compare(inputItem, requiredItem)) return false;
        }
        return true;
    }

    @Override
    public void execute(Location location, CauldronState state, int count) {
        ItemStack item = getPotionItem(this, state, location);
        item.setAmount(count);

        CauldronEffectUtil.animateCauldron(location, item, CobaltMagick.getInstance());

        grantAdvancement(location);
    }

    private void grantAdvancement(Location location) {
        Advancement root = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "potions/root"));
        Advancement advancement = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "potions/" + internalName.getValue()));

        grantAdvancement(location, root);
        grantAdvancement(location, advancement);
    }

    private static void grantAdvancement(Location location, Advancement advancement) {
        if (advancement == null) return;

        Collection<Player> players = location.getNearbyPlayers(16);
        players.forEach(p -> {
            AdvancementProgress progress = p.getAdvancementProgress(advancement);

            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        });
    }

    private ItemStack getPotionItem(IPotionRecipe recipe, CauldronState state, Location location) {
        PotionCreator potionCreator = new PotionCreator(recipe.getPotionEffectType())
                .variance(state.getVariance())
                .potency(state.getPotency())
                .duration(state.getDuration())
                .wild(state.getWild())
                .decay(state.getDecay())
                .elementalAffinity(elementalAffinities.getValueList());
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
    public IElementalAffinity getElementalAffinity() {
        return this;
    }

}
