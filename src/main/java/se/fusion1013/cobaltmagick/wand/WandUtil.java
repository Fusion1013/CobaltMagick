package se.fusion1013.cobaltmagick.wand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.ISpellInstance;
import se.fusion1013.cobaltmagick.spell.SpellManager;

import java.util.ArrayList;
import java.util.List;

public class WandUtil {

    public static final NamespacedKey WandKey = new NamespacedKey(CobaltMagick.getInstance(), "wand");
    public static final NamespacedKey ShuffleKey = new NamespacedKey(CobaltMagick.getInstance(), "shuffle");
    public static final NamespacedKey SpellsPerCastKey = new NamespacedKey(CobaltMagick.getInstance(), "spells_per_cast");
    public static final NamespacedKey CastDelayKey = new NamespacedKey(CobaltMagick.getInstance(), "cast_delay");
    public static final NamespacedKey RechargeTimeKey = new NamespacedKey(CobaltMagick.getInstance(), "recharge_time");
    public static final NamespacedKey ManaMaxKey = new NamespacedKey(CobaltMagick.getInstance(), "mana_max");
    public static final NamespacedKey ManaChargeSpeedKey = new NamespacedKey(CobaltMagick.getInstance(), "mana_charge_speed");
    public static final NamespacedKey CapacityKey = new NamespacedKey(CobaltMagick.getInstance(), "capacity");
    public static final NamespacedKey SpreadKey = new NamespacedKey(CobaltMagick.getInstance(), "spread");

    public static final NamespacedKey LastRechargeTimestamp = new NamespacedKey(CobaltMagick.getInstance(), "last_recharge_timestamp");
    public static final NamespacedKey LastRechargeLength = new NamespacedKey(CobaltMagick.getInstance(), "last_recharge_length");
    public static final NamespacedKey LastCastDelayTimestamp = new NamespacedKey(CobaltMagick.getInstance(), "last_cast_delay_timestamp");
    public static final NamespacedKey LastCastDelayLength = new NamespacedKey(CobaltMagick.getInstance(), "last_cast_delay_length");
    public static final NamespacedKey ManaTimestamp = new NamespacedKey(CobaltMagick.getInstance(), "mana_timestamp");
    public static final NamespacedKey LastMana = new NamespacedKey(CobaltMagick.getInstance(), "last_mana");

    public static final NamespacedKey WandSpellsKey = new NamespacedKey(CobaltMagick.getInstance(), "spells");
    public static final NamespacedKey SpellIdKey = new NamespacedKey(CobaltMagick.getInstance(), "spell_id");
    public static final NamespacedKey SpellStateKey = new NamespacedKey(CobaltMagick.getInstance(), "spell_state");

    public static List<ISpellInstance> getSpells(ItemStack wand) {
        ItemMeta meta = wand.getItemMeta();
        PersistentDataContainer persistent = meta.getPersistentDataContainer();

        if (!persistent.has(WandSpellsKey)) return List.of();
        if (!persistent.has(CapacityKey)) return List.of();

        int wandCapacity = persistent.get(CapacityKey, PersistentDataType.INTEGER);

        PersistentDataContainer persistentSpells = persistent.get(WandSpellsKey, PersistentDataType.TAG_CONTAINER);
        if (persistentSpells == null) return List.of();

        List<ISpellInstance> spells = new ArrayList<>();

        for (int i = 0; i < wandCapacity; i++) {
            ISpellInstance template = getSpellInSlot(persistentSpells, i);
            spells.add(template);
        }

        return spells;
    }

    public static ISpellInstance getSpellInSlot(PersistentDataContainer persistentSpells, int spellSlot) {
        NamespacedKey key = getWandSlotKey(spellSlot);
        if (!persistentSpells.has(key)) return null;

        PersistentDataContainer persistentSpell = persistentSpells.get(key, PersistentDataType.TAG_CONTAINER);
        if (persistentSpell == null) return null;

        String spellId = persistentSpell.get(SpellIdKey, PersistentDataType.STRING);
        String spellState = persistentSpell.get(SpellStateKey, PersistentDataType.STRING);

        return SpellManager.getSpellInstance(spellId, spellState, spellSlot);
    }

    public static void setSpells(ItemStack wandItem, ISpellInstance[] spells, RuleLogger ruleLogger) {
        ruleLogger.logMessage("Persisting " + spells.length + " spells in item");

        ItemMeta meta = wandItem.getItemMeta();
        PersistentDataContainer persistent = meta.getPersistentDataContainer();

        PersistentDataContainer persistentSpells = persistent.getAdapterContext().newPersistentDataContainer();
        ruleLogger.evaluate("Set Spells", () -> setSpells(spells, persistentSpells, ruleLogger));
        persistent.set(WandSpellsKey, PersistentDataType.TAG_CONTAINER, persistentSpells);

        wandItem.setItemMeta(meta);
    }

    private static void setSpells(ISpellInstance[] spells, PersistentDataContainer persistentSpells, RuleLogger ruleLogger) {
        for (ISpellInstance spell : spells) {
            if (spell == null) continue;

            NamespacedKey slotKey = getWandSlotKey(spell.slot());
            PersistentDataContainer persistentSpell = persistentSpells.getAdapterContext().newPersistentDataContainer();

            persistentSpell.set(SpellIdKey, PersistentDataType.STRING, spell.getInternalName());
            persistentSpell.set(SpellStateKey, PersistentDataType.STRING, spell.state());

            persistentSpells.set(slotKey, PersistentDataType.TAG_CONTAINER, persistentSpell);

            ruleLogger.logMessage("[" + spell.slot() + "]: " + spell.state() + " - " + spell.getInternalName());
        }
    }

    public static @Nullable List<? extends Component> getLore(ItemStack wandItem) {
        ItemMeta itemMeta = wandItem.getItemMeta();
        PersistentDataContainer persistent = itemMeta.getPersistentDataContainer();

        return List.of(
                getLoreLine("Shuffle", "" + persistent.get(ShuffleKey, PersistentDataType.BOOLEAN)),
                getLoreLine("Spells Per Cast", "" + persistent.get(SpellsPerCastKey, PersistentDataType.INTEGER)),
                getLoreLine("Cast Delay", "" + persistent.get(CastDelayKey, PersistentDataType.DOUBLE)),
                getLoreLine("Recharge Time", "" + persistent.get(RechargeTimeKey, PersistentDataType.DOUBLE)),
                getLoreLine("Mana Max", "" + persistent.get(ManaMaxKey, PersistentDataType.INTEGER)),
                getLoreLine("Mana Charge Speed", "" + persistent.get(ManaChargeSpeedKey, PersistentDataType.INTEGER)),
                getLoreLine("Capacity", "" + persistent.get(CapacityKey, PersistentDataType.INTEGER)),
                getLoreLine("Spread", "" + persistent.get(SpreadKey, PersistentDataType.DOUBLE))
        );
    }

    private static Component getLoreLine(String title, String value) {
        Component titleComponent = Component.text(title + ": ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
        Component valueComponent = Component.text(value).color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        return titleComponent.append(valueComponent);
    }

    public static NamespacedKey getWandSlotKey(int id) {
        return new NamespacedKey(CobaltMagick.getInstance(), "wand_slot_" + id);
    }

    public static int getCurrentMana(long manaTimestamp, int lastMana, int manaChargeSpeed, int manaMax) {
        long now = System.currentTimeMillis();

        // Time passed in milliseconds
        long elapsedMs = now - manaTimestamp;

        // Convert to seconds (as double for precision)
        double elapsedSeconds = elapsedMs / 1000.0;

        // Mana regenerated
        double manaGained = elapsedSeconds * manaChargeSpeed;

        return Math.min((int) (lastMana + manaGained), manaMax);
    }
}
