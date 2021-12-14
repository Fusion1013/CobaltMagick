package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.wand.Wand;

public class ProjectileModifierSpell extends Spell implements Cloneable {

    /**
     * Creates a new <code>ProjectileModifierSpell</code> with an id, internalSpellname and a spellName
     *
     * @param id id of the modifier
     * @param internalSpellName internal name of the modifier. Example: "add_mana"
     * @param spellName display name of the modifier. Example: "Add Mana"
     */
    public ProjectileModifierSpell(int id, String internalSpellName, String spellName){
        super(id, internalSpellName, spellName, SpellType.PROJECTILE_MODIFIER);
    }

    @Override
    public void castSpell(Wand wand, Player caster, Vector direction, Location location) {

    }

    @Override
    public void cancelTask() {

    }

    @Override
    public Spell clone() {
        return new ProjectileModifierSpell(this);
    }
}
