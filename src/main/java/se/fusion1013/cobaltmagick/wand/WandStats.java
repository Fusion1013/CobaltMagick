package se.fusion1013.cobaltmagick.wand;

import com.google.gson.JsonObject;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.variable.BooleanVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import static se.fusion1013.cobaltmagick.wand.WandUtil.*;

public class WandStats {

    private final BooleanVariable shuffle = new BooleanVariable("shuffle");
    private final IntVariable spellsPerCast = new IntVariable("spells_per_cast");
    private final DoubleVariable castDelay = new DoubleVariable("cast_delay");
    private final DoubleVariable rechargeTime = new DoubleVariable("recharge_time");
    private final IntVariable manaMax = new IntVariable("mana_max");
    private final IntVariable manaChargeSpeed = new IntVariable("mana_charge_speed");
    private final IntVariable capacity = new IntVariable("capacity");
    private final DoubleVariable spread = new DoubleVariable("spread");

    public WandStats(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread) {
        this.shuffle.setValue(shuffle);
        this.spellsPerCast.setValue(spellsPerCast);
        this.castDelay.setValue(castDelay);
        this.rechargeTime.setValue(rechargeTime);
        this.manaMax.setValue(manaMax);
        this.manaChargeSpeed.setValue(manaChargeSpeed);
        this.capacity.setValue(capacity);
        this.spread.setValue(spread);
    }

    public static WandStats create(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        return create(itemStack.getPersistentDataContainer());
    }

    public static WandStats create(PersistentDataContainerView persistent) {
        if (!persistent.has(WandKey)) return null;

        boolean shuffle = persistent.getOrDefault(ShuffleKey, PersistentDataType.BOOLEAN, false);
        int spellsPerCast = persistent.getOrDefault(SpellsPerCastKey, PersistentDataType.INTEGER, 1);
        double castDelay = persistent.getOrDefault(CastDelayKey, PersistentDataType.DOUBLE, 1D);
        double rechargeTime = persistent.getOrDefault(RechargeTimeKey, PersistentDataType.DOUBLE, 1D);
        int manaMax = persistent.getOrDefault(ManaMaxKey, PersistentDataType.INTEGER, 200);
        int manaChargeSpeed = persistent.getOrDefault(ManaChargeSpeedKey, PersistentDataType.INTEGER, 70);
        int capacity = persistent.getOrDefault(CapacityKey, PersistentDataType.INTEGER, 9);
        double spread = persistent.getOrDefault(SpreadKey, PersistentDataType.DOUBLE, 1D);

        return new WandStats(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread);
    }

    public void applyToItem(ItemCreationContext obj) {

    }

    public void fromJson(JsonObject json) {
        throw new NotImplementedException();
    }

    public void toJson(JsonObject json) {
        throw new NotImplementedException();
    }

    public void fromYaml(ConfigurationSection yaml) {
        shuffle.load(yaml);
        spellsPerCast.load(yaml);
        castDelay.load(yaml);
        rechargeTime.load(yaml);
        manaMax.load(yaml);
        manaChargeSpeed.load(yaml);
        capacity.load(yaml);
        spread.load(yaml);
    }

    public void toYaml(ConfigurationSection yaml) {
        throw new NotImplementedException();
    }

    public boolean getShuffle() {
        return shuffle.getValue();
    }

    public int getSpellsPerCast() {
        return spellsPerCast.getValue();
    }

    public double getCastDelay() {
        return castDelay.getValue();
    }

    public double getRechargeTime() {
        return rechargeTime.getValue();
    }

    public int getManaMax() {
        return manaMax.getValue();
    }

    public int getManaChargeSpeed() {
        return manaChargeSpeed.getValue();
    }

    public int getCapacity() {
        return capacity.getValue();
    }

    public double getSpread() {
        return spread.getValue();
    }
}
