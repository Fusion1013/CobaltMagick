package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.AbstractSpellModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.ISpellModifier;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class ProjectileModifierSpell extends Spell implements Cloneable {

    List<ISpellModifier> spellModifiers = new ArrayList<>();

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

        if (spellToModify instanceof MovableSpell movableSpell) {
            for (ISpellModifier sm : spellModifiers) sm.modifyMovableSpell(movableSpell);
        }

        if (spellToModify instanceof ProjectileSpell projectileSpell) {
            for (ISpellModifier sm : spellModifiers) sm.modifyProjectileSpell(projectileSpell);
        }

        if (spellToModify instanceof StaticProjectileSpell staticProjectileSpell) {
            for (ISpellModifier sm : spellModifiers) sm.modifyStaticProjectileSpell(staticProjectileSpell);
        }

        return spellToModify;
    }

    @Override
    public void performPreCast(List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(wandSpells, casts, spellPos);
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster, Vector direction, Location location) { super.castSpell(wand, caster); }

    @Override
    public void cancelTask() { }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();

        for (ISpellModifier modifier : spellModifiers){
            lore.addAll(modifier.getExtraLore());
        }

        return lore;
    }

    @Override
    public Spell clone() {
        return new ProjectileModifierSpell(this);
    }

    public static class ProjectileModifierSpellBuilder extends SpellBuilder<ProjectileModifierSpell, ProjectileModifierSpellBuilder> {

        List<ISpellModifier> spellModifiers = new ArrayList<>();

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

        public ProjectileModifierSpellBuilder addSpellModifier(ISpellModifier modifier){
            this.spellModifiers.add(modifier);
            return getThis();
        }
    }

    public void setSpellModifiers(List<ISpellModifier> spellModifiers) { this.spellModifiers = new ArrayList<>(spellModifiers); }

    public List<ISpellModifier> getSpellModifiers() { return this.spellModifiers; }

    @Override
    public Location getLocation() {
        return caster.getLocation().clone();
    }
}
