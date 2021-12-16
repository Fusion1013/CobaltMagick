package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.AbstractSpellModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.SpellModifier;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class ProjectileModifierSpell extends Spell implements Cloneable {

    List<SpellModifier> spellModifiers = new ArrayList<>();

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

    /**
     * Creates a new <code>ProjectileModifierSpell</code> with the given <code>ProjectileModifierSpell</code> as a template
     *
     * @param spell <code>ProjectileModifierSpell</code> to copy the parameters of
     */
    public ProjectileModifierSpell(ProjectileModifierSpell spell){
        super(spell);

        this.spellModifiers = AbstractSpellModifier.cloneList(spell.spellModifiers);
    }

    public ISpell modifySpell(ISpell spellToModify){

        if (spellToModify instanceof MulticastSpell){
            MulticastSpell multicastSpell = (MulticastSpell)spellToModify;
            multicastSpell.addModifierSpell(this);
            return spellToModify;
        }

        MovableSpell movableSpell = null;
        ProjectileSpell projectileSpell = null;
        StaticProjectileSpell staticProjectileSpell = null;

        if (spellToModify instanceof MovableSpell) movableSpell = (MovableSpell)spellToModify;
        if (spellToModify instanceof ProjectileSpell) projectileSpell = (ProjectileSpell)spellToModify;
        if (spellToModify instanceof StaticProjectileSpell) staticProjectileSpell = (StaticProjectileSpell)spellToModify;

        for (SpellModifier sm : spellModifiers){
            if (movableSpell != null) sm.modifyMovableSpell(movableSpell);
            if (projectileSpell != null) sm.modifyProjectileSpell(projectileSpell);
            if (staticProjectileSpell != null) sm.modifyStaticProjectileSpell(staticProjectileSpell);
        }
        return spellToModify;
    }

    @Override
    public void performPreCast(List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(wandSpells, casts, spellPos);
    }

    @Override
    public void castSpell(Wand wand, Player caster, Vector direction, Location location) { super.castSpell(wand, caster); }

    @Override
    public void cancelTask() { }

    @Override
    public Spell clone() {
        return new ProjectileModifierSpell(this);
    }

    public static class ProjectileModifierSpellBuilder extends SpellBuilder<ProjectileModifierSpell, ProjectileModifierSpellBuilder> {

        List<SpellModifier> spellModifiers = new ArrayList<>();

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id                id of the spell
         * @param internalSpellName internal name of the spell
         */
        public ProjectileModifierSpellBuilder(int id, String internalSpellName) {
            super(id, internalSpellName);
        }

        @Override
        protected ProjectileModifierSpell createObj() {
            return new ProjectileModifierSpell(id, internalSpellName, spellName);
        }

        @Override
        protected ProjectileModifierSpellBuilder getThis() {
            return this;
        }

        @Override
        public ProjectileModifierSpell build() {
            obj.setSpellModifiers(spellModifiers);

            return super.build();
        }

        public ProjectileModifierSpellBuilder addSpellModifier(SpellModifier modifier){
            this.spellModifiers.add(modifier);
            return getThis();
        }
    }

    public void setSpellModifiers(List<SpellModifier> spellModifiers) { this.spellModifiers = new ArrayList<>(spellModifiers); }

    public List<SpellModifier> getSpellModifiers() { return this.spellModifiers; }
}