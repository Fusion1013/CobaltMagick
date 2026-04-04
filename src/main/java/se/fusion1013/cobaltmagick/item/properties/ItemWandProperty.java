package se.fusion1013.cobaltmagick.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import static se.fusion1013.cobaltmagick.wand.WandUtil.*;

public class ItemWandProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private boolean isWand = false;
    private boolean shuffle = false;
    private int spellsPerCast = 1;
    private double castDelay = 0.8;
    private double rechargeTime = 4.5;
    private int manaMax = 100;
    private int manaChargeSpeed = 10;
    private int capacity = 4;
    private double spread = 34.5;

    @Override
    public String getId() {
        return "item_wand";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (!isWand) return;

        obj.persistent.set(WandKey, PersistentDataType.BOOLEAN, true);
        obj.persistent.set(ShuffleKey, PersistentDataType.BOOLEAN, shuffle);
        obj.persistent.set(SpellsPerCastKey, PersistentDataType.INTEGER, spellsPerCast);
        obj.persistent.set(CastDelayKey, PersistentDataType.DOUBLE, castDelay);
        obj.persistent.set(RechargeTimeKey, PersistentDataType.DOUBLE, rechargeTime);
        obj.persistent.set(ManaMaxKey, PersistentDataType.INTEGER, manaMax);
        obj.persistent.set(ManaChargeSpeedKey, PersistentDataType.INTEGER, manaChargeSpeed);
        obj.persistent.set(CapacityKey, PersistentDataType.INTEGER, capacity);
        obj.persistent.set(SpreadKey, PersistentDataType.DOUBLE, spread);

        obj.lore.add("&7Shuffle: &3" + shuffle);
        obj.lore.add("&7Spells Per Cast: &3" + spellsPerCast);
        obj.lore.add("&7Cast Delay: &3" + castDelay);
        obj.lore.add("&7Recharge Time: &3" + rechargeTime);
        obj.lore.add("&7Mana Max: &3" + manaMax);
        obj.lore.add("&7Mana Charge Speed: &3" + manaChargeSpeed);
        obj.lore.add("&7Capacity: &3" + capacity);
        obj.lore.add("&7Spread: &3" + spread);
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

        builder.setItemToggle(ItemToggleType.NO_FIX, true);
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
