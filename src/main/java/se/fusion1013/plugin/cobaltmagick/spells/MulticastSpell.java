package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.wand.CastParser;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class MulticastSpell extends Spell implements Cloneable, Runnable {

    int numberSpellsToCast;
    double increaseSpread;
    Formation formation = Formation.NONE;
    List<ISpell> spellsToCast = new ArrayList<>();

    List<ProjectileModifierSpell> modifierSpells = new ArrayList<>();

    public MulticastSpell(int id, String internalSpellName, String spellName) {
        super(id, internalSpellName, spellName, SpellType.MULTICAST);
    }

    /**
     * Creates a copy of a <code>MulticastSpell</code>
     * @param multicastSpell the <code>MulticastSpell</code> to copy
     */
    public MulticastSpell(MulticastSpell multicastSpell) {
        super(multicastSpell);
        this.numberSpellsToCast = multicastSpell.getNumberSpellsToCast();
        this.increaseSpread = multicastSpell.getIncreaseSpread();
        this.formation = multicastSpell.getFormation();
    }

    @Override
    public void performPreCast(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(caster, wand, wandSpells, casts, spellPos);

        if (formation == Formation.NONE){
            spellsToCast = new CastParser(caster, wand.getId(), wandSpells, numberSpellsToCast, spellPos+1).addModifiers(modifierSpells).prepareCast();
        } else {
            spellsToCast = new CastParser(caster, wand.getId(), wandSpells, formation.getDirectionModifiers().length, spellPos+1).addModifiers(modifierSpells).prepareCast();
            int pos = 0;
            for (ISpell spell : spellsToCast) {
                if (spell instanceof ProjectileSpell ps) {
                    ps.setDirectionModifier(formation.getDirectionModifiers()[pos]);
                    pos++;
                }
            }
        }

        // Increase spread of spell
        for (ISpell spell : spellsToCast){
            if (spell instanceof ProjectileSpell ps){
                ps.setSpread(ps.getSpread() + increaseSpread);
            }
        }
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster) {
        Vector direction = caster.getEyeLocation().getDirection();
        Location currentLocation = caster.getEyeLocation();

        for (ISpell s : spellsToCast){
            s.setCaster(caster);
            // s.castSpell(wand, caster, direction.clone(), currentLocation.clone());
            SpellCastEvent event = new SpellCastEvent(s);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                s.castSpell(wand, caster);
            }
        }
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster, Vector direction, Location location) {
        super.castSpell(wand, caster);
        for (ISpell s : spellsToCast) {
            s.setCaster(caster);
            SpellCastEvent event = new SpellCastEvent(s);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                s.castSpell(wand, caster, direction.clone(), location.clone());
            }
        }
    }

    @Override
    public void run() { }

    @Override
    public void cancelTask() { }

    @Override
    public Spell clone() {
        return new MulticastSpell(this);
    }

    /**
     * Used to build a new <code>MulticastSpell</code>
     */
    public static class MulticastSpellBuilder extends Spell.SpellBuilder<MulticastSpell, MulticastSpellBuilder>{

        int spellsToCast;
        double increaseSpread;
        Formation formation = Formation.NONE;

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id                id of the spell
         * @param internalSpellName internal name of the spell
         */
        public MulticastSpellBuilder(int id, String internalSpellName) {
            super(id, internalSpellName);
        }

        @Override
        protected MulticastSpell createObj() {
            return new MulticastSpell(id, internalSpellName, spellName);
        }

        @Override
        protected MulticastSpellBuilder getThis() {
            return this;
        }

        @Override
        public MulticastSpell build() {
            obj.setNumberSpellsToCast(spellsToCast);
            obj.setIncreaseSpread(increaseSpread);
            obj.setFormation(formation);

            return super.build();
        }

        public MulticastSpellBuilder setFormation(Formation formation){
            this.formation = formation;
            return getThis();
        }

        public MulticastSpellBuilder setIncreaseSpread(double increaseSpread){
            this.increaseSpread = increaseSpread;
            return getThis();
        }

        public MulticastSpellBuilder setNumberSpellsToCast(int spellsToCast){
            this.spellsToCast = spellsToCast;
            return getThis();
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setNumberSpellsToCast(int numberSpellsToCast) { this.numberSpellsToCast = numberSpellsToCast; }

    public void setIncreaseSpread(double increaseSpread) { this.increaseSpread = increaseSpread; }

    public void setFormation(Formation formation) { this.formation = formation; }

    public void setModifierSpells(List<ProjectileModifierSpell> modifierSpells) { this.modifierSpells = new ArrayList<>(modifierSpells); }

    public void addModifierSpell(ProjectileModifierSpell modifierSpell) { this.modifierSpells.add(modifierSpell); }

    public int getNumberSpellsToCast() { return this.numberSpellsToCast; }

    public double getIncreaseSpread() { return increaseSpread; }

    public Formation getFormation() { return this.formation; }

    @Override
    public Location getLocation() {
        return caster.getLocation().clone();
    }

    @Override
    public int getTrueManaDrain() {
        int manaDrain = 0;
        for (ISpell s : spellsToCast){
            manaDrain += s.getTrueManaDrain();
        }
        return super.getTrueManaDrain() + manaDrain;
    }

    @Override
    public double getTrueCastDelay() {
        double castDelay = 0;
        for (ISpell s : spellsToCast){
            castDelay += s.getTrueCastDelay();
        }
        return super.getTrueCastDelay() + castDelay;
    }

    public enum Formation {
        NONE(new Vector[]{}),
        BEHIND_BACK(new Vector[] { new Vector(0, 0, 0), new Vector(180, 0, 0) }),
        ABOVE_AND_BELOW(new Vector[] { new Vector(0, 90, 0), new Vector(0, 0, 0), new Vector(0, -90, 0) }),
        PENTAGON(new Vector[]{ new Vector(0, 0, 0), new Vector(72, 0, 0), new Vector(144, 0, 0), new Vector(216, 0, 0), new Vector(288, 0, 0) }),
        HEXAGON(new Vector[]{ new Vector(0, 0, 0), new Vector(60, 0, 0), new Vector(120, 0, 0), new Vector(180, 0, 0), new Vector(240, 0, 0), new Vector(300, 0, 0) }),
        BIFURCATED(new Vector[]{ new Vector(-22.5, 0, 0), new Vector(22.5, 0, 0) }),
        TRIFURCATED(new Vector[]{ new Vector(-20, 0, 0), new Vector(0, 0, 0), new Vector(20, 0, 0) });

        Vector[] directionModifiers;

        Formation(Vector[] directionModifiers){
            this.directionModifiers = directionModifiers;
        }

        public Vector[] getDirectionModifiers() { return directionModifiers; }
    }
}
