package se.fusion1013.cobaltmagick.alchemy.potion;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.logger.SetValue;
import se.fusion1013.cobaltCore.util.PotionUtil;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionCreator {

    private static final Random random = new Random();
    private final PotionEffectType effectType;
    private final RuleLogger ruleLogger;

    private int variance = 0;
    private int potency = 0;
    private int duration = 0;
    private int wild = 0;
    private int decay = 0;
    private List<String> elementalAffinities = new ArrayList<>();
    private int amount;

    private final SetValue<Integer> affinity;
    private final SetValue<Integer> resultingDuration;
    private final SetValue<Integer> varianceExtraPotionsCount;
    private final SetValue<Integer> wildPotionsCount;
    private final SetValue<Integer> resultingAmplifier;

    public PotionCreator(PotionEffectType effectType, RuleLogger ruleLogger) {
        this.effectType = effectType;
        this.ruleLogger = ruleLogger;

        this.affinity = new SetValue<>("affinity", ruleLogger);
        this.resultingDuration = new SetValue<>("resultingDuration", ruleLogger);
        this.varianceExtraPotionsCount = new SetValue<>("varianceExtraPotionsCount", ruleLogger);
        this.wildPotionsCount = new SetValue<>("wildPotionsCount", ruleLogger);
        this.resultingAmplifier = new SetValue<>("resultingAmplifier", ruleLogger);
    }

    public ItemStack getItem(Location location) {
        ruleLogger.logMessage("Creating Potion Item");

        ItemStack potionItem = new ItemStack(Material.POTION);

        affinity.setValue(getElementalAffinityLevel(location, elementalAffinities));

        resultingDuration.setValue((int) ((160 + Math.max(this.duration, 1) * 40) * Math.pow(1.5, affinity.getValue()))); // TODO ???
        if (affinity.getValue() <= 0 && resultingDuration.getValue() > 40) {
            resultingDuration.setValue(random.nextInt((int) (resultingDuration.getValue() * 0.3), resultingDuration.getValue()));
        }

        PotionEffectTypeCategory resultCategory = effectType.getCategory();
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

        // VARIANCE
        varianceExtraPotionsCount.setValue((this.variance / 50) - affinity.getValue() * 2);
        for (int i = 0; i < varianceExtraPotionsCount.getValue(); i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.BENEFICIAL : PotionEffectTypeCategory.HARMFUL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (resultingDuration.getValue() * 0.2f), 0), false);
            ruleLogger.logMessage("Added Variance Potion Effect: " + type.key().key());
        }

        // WILD
        wildPotionsCount.setValue(Math.max(0, this.wild) / 50);
        for (int i = 0; i < wildPotionsCount.getValue(); i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.HARMFUL : PotionEffectTypeCategory.BENEFICIAL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (resultingDuration.getValue() * 0.2f), 0), false);
            ruleLogger.logMessage("Added Wild Potion Effect: " + type.key().key());
        }

        resultingAmplifier.setValue(Math.max(0, this.potency) / 100);

        potionMeta.addCustomEffect(new PotionEffect(effectType, resultingDuration.getValue(), resultingAmplifier.getValue()), false);
        ruleLogger.logMessage("Added Potion Effect: " + effectType.key().key());

        potionMeta.customName(
                Component.text("Potion of ")
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.translatable(effectType.translationKey())
                                .decoration(TextDecoration.ITALIC, false))
        );

        potionItem.setItemMeta(potionMeta);

        potionItem.setData(DataComponentTypes.MAX_STACK_SIZE, 8);
        potionItem.setAmount(amount);

        return potionItem;
    }

    public static int getElementalAffinityLevel(Location location, List<String> elementalAffinities) {
        int affinity = 0;

        for (String veinName : elementalAffinities) {
            IElementalVein vein = ElementalVeinManager.getElementalVein(veinName);
            if (vein == null) continue;

            ParametricSpline2D.Result result = vein.getClosestPoint(location.x(), location.z());
            if (result.distance() < 4) affinity++;
        }

        return affinity;
    }

    public PotionCreator variance(int variance) {
        this.variance = variance;
        return this;
    }

    public PotionCreator potency(int potency) {
        this.potency = potency;
        return this;
    }

    public PotionCreator duration(int duration) {
        this.duration = duration;
        return this;
    }

    public PotionCreator wild(int wild) {
        this.wild = wild;
        return this;
    }

    public PotionCreator decay(int decay) {
        this.decay = decay;
        return this;
    }

    public PotionCreator elementalAffinity(List<String> affinities) {
        this.elementalAffinities = affinities;
        return this;
    }

    public PotionCreator amount(int amount) {
        this.amount = amount;
        return this;
    }
}
