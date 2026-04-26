package se.fusion1013.cobaltmagick.enchantments.cleanse;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltmagick.CobaltMagick;

public final class CleanseState {

    private static final NamespacedKey CleanseEnchantmentKey = new NamespacedKey("fusion1013", "cleanse");
    private static final NamespacedKey CleanseStoredPotionsBeneficialKey = new NamespacedKey(CobaltMagick.getInstance(), "cleanse_stored_potions_beneficial");
    private static final NamespacedKey CleanseStoredPotionsNeutralKey = new NamespacedKey(CobaltMagick.getInstance(), "cleanse_stored_potions_neutral");
    private static final NamespacedKey CleanseStoredPotionsHarmfulKey = new NamespacedKey(CobaltMagick.getInstance(), "cleanse_stored_potions_harmful");

    private final int level;
    private int storedPotionLevelsBeneficial;
    private int storedPotionLevelsNeutral;
    private int storedPotionLevelsHarmful;

    public CleanseState(int level, int storedPotionLevelsBeneficial, int storedPotionLevelsNeutral, int storedPotionLevelsHarmful) {
        this.level = level;
        this.storedPotionLevelsBeneficial = storedPotionLevelsBeneficial;
        this.storedPotionLevelsNeutral = storedPotionLevelsNeutral;
        this.storedPotionLevelsHarmful = storedPotionLevelsHarmful;
    }

    public static CleanseState get(ItemStack item) {
        if (item == null) return null;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = enchantmentRegistry.get(CleanseEnchantmentKey);
        if (enchantment == null) return null;

        if (!itemMeta.hasEnchant(enchantment)) return null;
        int level = itemMeta.getEnchantLevel(enchantment);

        int storedPotionLevelsBeneficial = itemMeta.getPersistentDataContainer().getOrDefault(CleanseStoredPotionsBeneficialKey, PersistentDataType.INTEGER, 0);
        int storedPotionLevelsNeutral = itemMeta.getPersistentDataContainer().getOrDefault(CleanseStoredPotionsNeutralKey, PersistentDataType.INTEGER, 0);
        int storedPotionLevelsHarmful = itemMeta.getPersistentDataContainer().getOrDefault(CleanseStoredPotionsHarmfulKey, PersistentDataType.INTEGER, 0);

        return new CleanseState(level, storedPotionLevelsBeneficial, storedPotionLevelsNeutral, storedPotionLevelsHarmful);
    }

    public void save(ItemStack item) {
        if (item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();
        persistent.set(CleanseStoredPotionsBeneficialKey, PersistentDataType.INTEGER, storedPotionLevelsBeneficial);
        persistent.set(CleanseStoredPotionsNeutralKey, PersistentDataType.INTEGER, storedPotionLevelsNeutral);
        persistent.set(CleanseStoredPotionsHarmfulKey, PersistentDataType.INTEGER, storedPotionLevelsHarmful);

        item.setItemMeta(itemMeta);
    }

    public int getLevel() {
        return level;
    }

    public int getStoredPotionLevelsBeneficial() {
        return storedPotionLevelsBeneficial;
    }

    public int getStoredPotionLevelsNeutral() {
        return storedPotionLevelsNeutral;
    }

    public int getStoredPotionLevelsHarmful() {
        return storedPotionLevelsHarmful;
    }

    public void setStoredPotionLevelsBeneficial(int storedPotionLevelsBeneficial) {
        this.storedPotionLevelsBeneficial = storedPotionLevelsBeneficial;
    }

    public void setStoredPotionLevelsNeutral(int storedPotionLevelsNeutral) {
        this.storedPotionLevelsNeutral = storedPotionLevelsNeutral;
    }

    public void setStoredPotionLevelsHarmful(int storedPotionLevelsHarmful) {
        this.storedPotionLevelsHarmful = storedPotionLevelsHarmful;
    }

    public void increment(PotionEffectTypeCategory type, int amount) {
        switch (type) {
            case BENEFICIAL -> {
                storedPotionLevelsBeneficial += amount;
            }
            case HARMFUL -> {
                storedPotionLevelsHarmful += amount;
            }
            case NEUTRAL -> {
                storedPotionLevelsNeutral += amount;
            }
        }
    }
}
