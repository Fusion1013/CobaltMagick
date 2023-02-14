package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.List;

public interface ISpell {
    void castSpell(Wand wand, LivingEntity caster);
    void castSpell(Wand wand, LivingEntity caster, Vector direction, Location location);

    List<String> getLore();
    int getId();

    boolean getHasCast();
    void setHasCast(boolean hasCast);
    int getManaDrain();

    /**
     * Gets the mana drain of the spell, including childrens mana drain
     * @return true mana drain of the spell
     */
    int getTrueManaDrain();
    double getCastDelay();

    /**
     * Gets the cast delay of the spell, including childrens cast delay
     * @return true cast delay of the spell
     */
    double getTrueCastDelay();
    double getRechargeTime();

    String getInternalSpellName();
    String getSpellName();
    ItemStack getSpellItem();
    ICustomItem getSpellCustomItem();
    int getCustomModelData();
    SpellType getSpellType();
    boolean getConsumeOnUse();
    void setConsumeOnUse(boolean consume);
    int getCount();
    void setCount(int count);
    String getDescription();
    List<Spell.DelayedSpell> getDelayedSpells();
    double getRadius();
    void setRadius(double radius);
    Wand getWand();
    LivingEntity getCaster();
    Location getLocation();
    List<String> getTags();
    void setCaster(LivingEntity caster);
    int[] getSpellTiers();
    double[] getSpellTierWeights();
    void setSpellTiers(int... spellTier);
    void setSpellTierWeights(double... spellTierWeights);
    String getHexIcon();

    /**
     * Performs operations that need to be done before a spell can be cast
     *
     * @param wandSpells the spells that are in the wand
     * @param casts
     * @param spellPos
     */
    void performPreCast(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos);

    Spell clone();
}
