package se.fusion1013.cobaltmagick.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class ItemWandProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private boolean isWand = false;

    public static final NamespacedKey ShuffleKey = new NamespacedKey(CobaltMagick.getInstance(), "shuffle");
    private boolean shuffle = false;

    public static final NamespacedKey SpellsPerCastKey = new NamespacedKey(CobaltMagick.getInstance(), "spells_per_cast");
    private int spellsPerCast = 1;

    public static final NamespacedKey CastDelayKey = new NamespacedKey(CobaltMagick.getInstance(), "cast_delay");
    private double castDelay = 0.8;

    public static final NamespacedKey RechargeTimeKey = new NamespacedKey(CobaltMagick.getInstance(), "recharge_time");
    private double rechargeTime = 4.5;

    public static final NamespacedKey ManaMaxKey = new NamespacedKey(CobaltMagick.getInstance(), "mana_max");
    private int manaMax = 100;

    public static final NamespacedKey ManaChargeSpeedKey = new NamespacedKey(CobaltMagick.getInstance(), "mana_charge_speed");
    private int manaChargeSpeed = 10;

    public static final NamespacedKey CapacityKey = new NamespacedKey(CobaltMagick.getInstance(), "capacity");
    private int capacity = 4;

    public static final NamespacedKey SpreadKey = new NamespacedKey(CobaltMagick.getInstance(), "spread");
    private double spread = 34.5;

    @Override
    public String getId() {
        return "item_wand";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (!isWand) return;
        obj.persistent.set(ShuffleKey, PersistentDataType.BOOLEAN, shuffle);
        obj.persistent.set(SpellsPerCastKey, PersistentDataType.INTEGER, spellsPerCast);
        obj.persistent.set(CastDelayKey, PersistentDataType.DOUBLE, castDelay);
        obj.persistent.set(RechargeTimeKey, PersistentDataType.DOUBLE, rechargeTime);
        obj.persistent.set(ManaMaxKey, PersistentDataType.INTEGER, manaMax);
        obj.persistent.set(ManaChargeSpeedKey, PersistentDataType.INTEGER, manaChargeSpeed);
        obj.persistent.set(CapacityKey, PersistentDataType.INTEGER, capacity);
        obj.persistent.set(SpreadKey, PersistentDataType.DOUBLE, spread);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {

    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("wand")) return;
        ConfigurationSection wandYaml = yaml.getConfigurationSection("wand");

        isWand = true;
        if (wandYaml.contains("shuffle")) shuffle = wandYaml.getBoolean("shuffle");
        if (wandYaml.contains("spells_per_cast")) spellsPerCast = wandYaml.getInt("spells_per_cast");
        if (wandYaml.contains("cast_delay")) castDelay = wandYaml.getDouble("cast_delay");
        if (wandYaml.contains("recharge_time")) rechargeTime = wandYaml.getDouble("recharge_time");
        if (wandYaml.contains("mana_max")) manaMax = wandYaml.getInt("mana_max");
        if (wandYaml.contains("mana_charge_speed")) manaChargeSpeed = wandYaml.getInt("mana_charge_speed");
        if (wandYaml.contains("capacity")) capacity = wandYaml.getInt("capacity");
        if (wandYaml.contains("spread")) spread = wandYaml.getDouble("spread");
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
