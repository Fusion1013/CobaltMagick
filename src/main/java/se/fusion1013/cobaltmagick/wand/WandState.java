package se.fusion1013.cobaltmagick.wand;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.ISpellInstance;
import se.fusion1013.cobaltmagick.spell.SpellManager;
import se.fusion1013.cobaltmagick.spell.projectile.SpellProjectile;
import se.fusion1013.cobaltmagick.wand.cast.CastResult;
import se.fusion1013.cobaltmagick.wand.cast.CastState;

import java.util.List;

import static se.fusion1013.cobaltmagick.wand.WandUtil.WandKey;

public class WandState {

    private final ItemStack itemStack;
    private final WandStats wandStats;

    private final boolean isOnRecharge;
    private final double rechargeTime;

    private final boolean isOnCastDelay;
    private final double castDelayTime;

    private final int currentMana;

    private final List<ISpellInstance> spells;

    public WandState(WandStats wandStats, ItemStack itemStack) {
        this.wandStats = wandStats;
        this.itemStack = itemStack;

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();

        long currentTimeMillis = System.currentTimeMillis();

        long lastRechargeTimestamp = persistent.getOrDefault(WandUtil.LastRechargeTimestamp, PersistentDataType.LONG, 0L);
        double lastRechargeLength = persistent.getOrDefault(WandUtil.LastRechargeLength, PersistentDataType.DOUBLE, 0D);

        isOnRecharge = lastRechargeTimestamp + lastRechargeLength > currentTimeMillis;
        rechargeTime = (lastRechargeTimestamp + lastRechargeLength) - currentTimeMillis;

        long lastCastDelayTimestamp = persistent.getOrDefault(WandUtil.LastCastDelayTimestamp, PersistentDataType.LONG, 0L);
        double lastCastDelayLength = persistent.getOrDefault(WandUtil.LastCastDelayLength, PersistentDataType.DOUBLE, 0D);

        isOnCastDelay = lastCastDelayTimestamp + lastCastDelayLength > currentTimeMillis;
        castDelayTime = (lastCastDelayTimestamp + lastCastDelayLength) - currentTimeMillis;

        long manaTimestamp = persistent.getOrDefault(WandUtil.ManaTimestamp, PersistentDataType.LONG, 0L);
        int lastMana = persistent.getOrDefault(WandUtil.LastMana, PersistentDataType.INTEGER, wandStats.getManaMax());

        currentMana = WandUtil.getCurrentMana(manaTimestamp, lastMana, wandStats.getManaChargeSpeed(), wandStats.getManaMax());

        spells = WandUtil.getSpells(itemStack);
    }

    public static WandState getWand(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();
        if (!persistent.has(WandKey)) return null;

        return new WandState(WandStats.create(persistent), itemStack);
    }

    public void castSpells(Player player) {
        RuleLogger ruleLogger = RuleLogger.create("WandState::castSpells");

        CastState castState = ruleLogger.evaluate("CastState::new", () -> new CastState(this, ruleLogger));

        ISpellInstance[] spellResult = castState.getSpells().toArray(new ISpellInstance[0]);

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();

        // Update cast delay / recharge time
        if (castState.castResult == CastResult.Recharge) {
            ruleLogger.logMessage("Recharging wand!");
            persistent.set(WandUtil.LastRechargeTimestamp, PersistentDataType.LONG, System.currentTimeMillis());
            persistent.set(WandUtil.LastRechargeLength, PersistentDataType.DOUBLE, wandStats.getRechargeTime() * 1000 + 0); // TODO: Increase by cast amount
            for (ISpellInstance instance : spellResult) {
                instance.setState("deck");
            }
        } else if (castState.castResult == CastResult.CastDelay) {
            persistent.set(WandUtil.LastCastDelayTimestamp, PersistentDataType.LONG, System.currentTimeMillis());
            persistent.set(WandUtil.LastCastDelayLength, PersistentDataType.DOUBLE, wandStats.getCastDelay() * 1000 + 0); // TODO: Increase by cast amount
        }

        // Reduce mana
        if (castState.getManaUsed() > currentMana) return;
        persistent.set(WandUtil.ManaTimestamp, PersistentDataType.LONG, System.currentTimeMillis());
        persistent.set(WandUtil.LastMana, PersistentDataType.INTEGER, currentMana - castState.getManaUsed());

        castState.shotState.getProjectileTemplates().forEach(
                pt -> {
                    SpellProjectile projectile = pt.create(player, castState.shotState);
                    SpellManager.getInstance().createSpell(projectile);
                }
        );

        itemStack.setItemMeta(itemMeta);

        // Update wand spells
        ruleLogger.evaluate("Set Spells", () -> WandUtil.setSpells(itemStack, spellResult, ruleLogger));

        ruleLogger.print(CobaltMagick.getInstance());
    }

    public WandStats getStats() {
        return wandStats;
    }

    public boolean isOnRecharge() {
        return isOnRecharge;
    }

    public boolean isOnCastDelay() {
        return isOnCastDelay;
    }

    public double getRechargeTime() {
        return rechargeTime;
    }

    public double getCastDelayTime() {
        return castDelayTime;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public List<ISpellInstance> getSpells() {
        return spells;
    }
}
