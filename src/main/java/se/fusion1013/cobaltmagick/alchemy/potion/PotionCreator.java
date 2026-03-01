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
import se.fusion1013.cobaltCore.util.PotionUtil;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;

import java.util.ArrayList;
import java.util.List;

public class PotionCreator {

    private final PotionEffectType effectType;
    private int variance = 0;
    private int potency = 0;
    private int duration = 0;
    private int wild = 0;
    private int decay = 0;
    private List<String> elementalAffinities = new ArrayList<>();

    public PotionCreator(PotionEffectType effectType) {
        this.effectType = effectType;
    }

    public ItemStack getItem(Location location) {
        ItemStack potionItem = new ItemStack(Material.POTION);

        int affinity = getElementalAffinityLevel(location);

        int duration = (int) ((160 + Math.max(this.duration, 1) * 40) * Math.pow(1.5, affinity)); // TODO ???

        PotionEffectTypeCategory resultCategory = effectType.getCategory();
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

        // VARIANCE
        int extraPotionsCount = (this.variance / 50) - affinity * 2;
        for (int i = 0; i < extraPotionsCount; i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.BENEFICIAL : PotionEffectTypeCategory.HARMFUL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (duration * 1.2f), 0), false);
        }

        // WILD
        int wildPotionsCount = Math.max(0, this.wild) / 50;
        for (int i = 0; i < wildPotionsCount; i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.HARMFUL : PotionEffectTypeCategory.BENEFICIAL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (duration * 0.2f), 0), false);
        }

        int amplifier = Math.max(0, this.potency) / 100;

        potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);

        potionMeta.customName(
                Component.text("Potion of ")
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.translatable(effectType.translationKey())
                                .decoration(TextDecoration.ITALIC, false))
        );

        potionItem.setItemMeta(potionMeta);

        potionItem.setData(DataComponentTypes.MAX_STACK_SIZE, 8);

        return potionItem;
    }

    private int getElementalAffinityLevel(Location location) {
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
}
