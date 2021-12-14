package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.spells.spellmodifiers.SpellModifier;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.List;

public interface ISpell {
    void castSpell(Wand wand, Player caster);
    void castSpell(Wand wand, Player caster, Vector direction, Location location);

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
    int getCustomModelData();
    Spell.SpellType getSpellType();
    int getUses();
    String getDescription();
    List<Spell.DelayedSpell> getDelayedSpells();

    /**
     * Performs operations that need to be done before a spell can be cast
     *
     * @param wandSpells the spells that are in the wand
     * @param casts
     * @param spellPos
     */
    void performPreCast(List<ISpell> wandSpells, int casts, int spellPos);

    Spell clone();
}
