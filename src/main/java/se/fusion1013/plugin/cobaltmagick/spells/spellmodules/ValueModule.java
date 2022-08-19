package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class ValueModule extends AbstractSpellModule<ValueModule> implements SpellModule {

    // ----- VARIABLES -----

    double acceleration = 1;

    boolean cancelsCast;

    // ----- CONSTRUCTORS -----

    public ValueModule(boolean cancelsCast) {
        this.cancelsCast = cancelsCast;
    }

    // ----- BUILDER METHODS -----

    public ValueModule setAcceleration(double acceleration) {
        this.acceleration = acceleration;
        return getThis();
    }

    // ----- EXECUTE -----

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {

    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (spell instanceof MovableSpell movableSpell) {
            Vector velocityVector = movableSpell.getVelocityVectorNoClone();
            if (velocityVector != null) velocityVector.multiply(acceleration);
        }
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {

    }

    // ----- UTILITY METHODS -----

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    protected ValueModule getThis() {
        return this;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public ValueModule(ValueModule target) {
        this.cancelsCast = target.cancelsCast;

        this.acceleration = target.acceleration;
    }

    @Override
    public AbstractSpellModule<ValueModule> clone() {
        return new ValueModule(this);
    }
}
