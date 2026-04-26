package se.fusion1013.cobaltmagick.enchantments.cold_aura;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Objects;

public final class ColdAuraState {

    private static final NamespacedKey ColdAuraEnchantmentKey = new NamespacedKey("fusion1013", "cold_aura");
    private static final NamespacedKey ColdAuraLastHitKey = new NamespacedKey(CobaltMagick.getInstance(), "cold_aura_hit_timestamp");
    private static final NamespacedKey ColdAuraChargeKey = new NamespacedKey(CobaltMagick.getInstance(), "cold_aura_charge");
    private static final NamespacedKey ColdAuraActiveKey = new NamespacedKey(CobaltMagick.getInstance(), "cold_aura_active");

    private final int level;
    private long lastHit;
    private double charge;
    private boolean active;

    public ColdAuraState(int level, long lastHit, double charge, boolean active) {
        this.level = level;
        this.lastHit = lastHit;
        this.charge = charge;
        this.active = active;
    }

    public static ColdAuraState get(ItemStack item) {
        if (item == null) return null;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = enchantmentRegistry.get(ColdAuraEnchantmentKey);
        if (enchantment == null) return null;

        if (!itemMeta.hasEnchant(enchantment)) return null;
        int level = itemMeta.getEnchantLevel(enchantment);

        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();

        long lastHit = persistent.getOrDefault(ColdAuraLastHitKey, PersistentDataType.LONG, 0L);
        double charge = persistent.getOrDefault(ColdAuraChargeKey, PersistentDataType.DOUBLE, 0D);
        boolean active = persistent.getOrDefault(ColdAuraActiveKey, PersistentDataType.BOOLEAN, false);
        return new ColdAuraState(level, lastHit, charge, active);
    }

    public void save(ItemStack item) {
        if (item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();
        persistent.set(ColdAuraLastHitKey, PersistentDataType.LONG, lastHit);
        persistent.set(ColdAuraChargeKey, PersistentDataType.DOUBLE, Math.clamp(charge, 0, ColdAuraUtil.MAX_CHARGE));
        persistent.set(ColdAuraActiveKey, PersistentDataType.BOOLEAN, active);

        item.setItemMeta(itemMeta);
    }

    public long lastHit() {
        return lastHit;
    }

    public double charge() {
        return charge;
    }

    public boolean active() {
        return active;
    }

    public void setLastHit(long lastHit) {
        this.lastHit = lastHit;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int level() {
        return level;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ColdAuraState) obj;
        return this.lastHit == that.lastHit &&
                Double.doubleToLongBits(this.charge) == Double.doubleToLongBits(that.charge) &&
                this.active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastHit, charge, active);
    }

    @Override
    public String toString() {
        return "ColdAuraState[" +
                "lastHit=" + lastHit + ", " +
                "charge=" + charge + ", " +
                "active=" + active + ']';
    }


}
