package se.fusion1013.cobaltmagick.alchemy.ritual;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.variable.ActionVariable;
import se.fusion1013.cobaltCore.variable.ConditionVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.cauldron.AbstractCauldronRecipe;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RitualRecipe extends AbstractCauldronRecipe implements IRitualRecipe {

    private final StringVariable glyph = new StringVariable("glyph");
    private final ConditionVariable conditions = new ConditionVariable("conditions");
    private final ActionVariable actions = new ActionVariable("actions");
    private final StringVariable requiredItems = new StringVariable("items");

    public RitualRecipe(YamlConfiguration yaml) {
        super(yaml);
        glyph.load(yaml);
        requiredItems.load(yaml);
        conditions.load(yaml);
        actions.load(yaml);
    }

    public RitualRecipe(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getGlyph() {
        return glyph.getValue();
    }

    @Override
    public boolean validateConditions(Location cauldronLocation) {
        Map<String, Object> context = new HashMap<>();
        context.put("default_location", cauldronLocation);
        context.put("above_cauldron", cauldronLocation.clone().add(new Vector(0, 1, 0)));
        return conditions.getValueList().stream().allMatch(c -> c.evaluate(context));
    }

    @Override
    public boolean validateItems(List<ItemStack> items) {
        for (int j = requiredItems.getValueList().size() - 1; j >= 0; j--) {
            for (int i = items.size() - 1; i >= 0; i--) {
                ItemStack inputItem = items.get(i);
                String requiredItemName = requiredItems.getValueList().get(j);

                ItemStack requiredItem = CustomItemManager.getItemStack(requiredItemName);
                if (requiredItem == null) continue;

                if (!CustomItemManager.compare(inputItem, requiredItem)) continue;
                items.remove(i);
                break;
            }
        }

        return items.isEmpty();
    }

    @Override
    public void execute(Location location, CauldronState state, int count, RuleLogger ruleLogger) {
        World world = location.getWorld();

        CauldronEffectUtil.animateCauldron(location, (center) -> {
            Map<String, Object> context = new HashMap<>();
            context.put("default_location", center);

            // Final burst effect
            world.spawnParticle(Particle.FLASH, center.clone().add(0, 0.5, 0), 1, Color.WHITE);
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, 0.5, 0), 10, .1, .1, .1, 0);
            world.playSound(center, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1f, 1.2f);

            Sound ominousSound = Registry.SOUNDS.get(new NamespacedKey("thegreatwork", "sfx.ominous_amaze.1"));
            if (ominousSound != null) world.playSound(center, ominousSound, 1f, 1f);

            actions.getValueList().forEach(action -> {
                action.execute(context);
            });
        }, CobaltMagick.getInstance());
    }

    @Override
    public boolean allowExternalBlocks() {
        return false;
    }

    @Override
    public boolean validateFinalItem(ItemStack itemStack) {
        return itemStack.getPersistentDataContainer().has(new NamespacedKey(CobaltCore.getInstance(), "ritual_stone"));
    }
}
