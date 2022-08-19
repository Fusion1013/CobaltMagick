package se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers;

import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.spells.ProjectileSpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;
import se.fusion1013.plugin.cobaltmagick.spells.StaticProjectileSpell;

import java.util.ArrayList;
import java.util.List;

public class ValueSpellModifier extends AbstractSpellModifier<ValueSpellModifier> {

    // ----- VARIABLES -----

    // Projectile

    double addRadius;
    double addSpread;
    double addVelocity;
    double addLifetime;

    double multiplyRadius = 1;
    double multiplySpread = 1;
    double multiplyVelocity = 1;
    double multiplyLifetime = 1;

    // Movable

    boolean setBounce = false;
    boolean isBouncy;
    Vector bounceFriction;
    int maxBouncesIncrease = 0;

    public ValueSpellModifier(){ }

    public ValueSpellModifier(ValueSpellModifier target){
        super(target);

        // Projectile

        this.multiplyRadius = target.multiplyRadius;
        this.multiplySpread = target.multiplySpread;
        this.multiplyVelocity = target.multiplyVelocity;
        this.multiplyLifetime = target.multiplyLifetime;

        this.addRadius = target.addRadius;
        this.addSpread = target.addSpread;
        this.addVelocity = target.addVelocity;
        this.addLifetime = target.addLifetime;

        // Movable
        this.setBounce = target.setBounce;
        this.isBouncy = target.isBouncy;
        this.bounceFriction = target.bounceFriction;
        this.maxBouncesIncrease = target.maxBouncesIncrease;
    }

    // ----- VALUE OVERRIDES -----

    public ValueSpellModifier setIsBouncy(boolean isBouncy, Vector bounceFriction) {
        this.setBounce = true;
        this.isBouncy = isBouncy;
        this.bounceFriction = bounceFriction;
        return getThis();
    }

    public ValueSpellModifier setIsBouncy(boolean isBouncy, Vector bounceFriction, int maxBounces) {
        this.setBounce = true;
        this.isBouncy = isBouncy;
        this.bounceFriction = bounceFriction;
        this.maxBouncesIncrease = maxBounces;
        return getThis();
    }
    // ----- VALUE MULTIPLIERS -----

    public ValueSpellModifier addLifetimeMultiplier(double lifetimeMultiplier){
        this.multiplyLifetime = lifetimeMultiplier;
        return getThis();
    }

    public ValueSpellModifier addVelocityMultiplier(double velocityMultiplier){
        this.multiplyVelocity = velocityMultiplier;
        return getThis();
    }

    public ValueSpellModifier addSpreadMultiplier(double spreadMultiplier){
        this.multiplySpread = spreadMultiplier;
        return getThis();
    }

    public ValueSpellModifier addRadiusMultiplier(double radiusMultiplier){
        this.multiplyRadius = radiusMultiplier;
        return getThis();
    }

    // ----- VALUE ADDERS / SUBTRACTS -----

    public ValueSpellModifier addLifetimeModifier(double lifetimeModifier){
        this.addLifetime = lifetimeModifier;
        return getThis();
    }

    public ValueSpellModifier addVelocityModifier(double velocityModifier){
        this.addVelocity = velocityModifier;
        return getThis();
    }

    public ValueSpellModifier addSpreadModifier(double spreadModifier){
        this.addSpread = spreadModifier;
        return getThis();
    }

    public ValueSpellModifier addRadiusModifier(double radiusModifier){
        this.addRadius = radiusModifier;
        return getThis();
    }

    @Override
    public void modifyProjectileSpell(ProjectileSpell spellToModify) {
        spellToModify.setLifetime(spellToModify.getLifetime() + addLifetime);
        spellToModify.setVelocity(spellToModify.getVelocity() + addVelocity);
        spellToModify.setSpread(spellToModify.getSpread() + addSpread);
        spellToModify.setRadius(spellToModify.getRadius() + addRadius);

        spellToModify.setLifetime(spellToModify.getLifetime() * multiplyLifetime);
        spellToModify.setVelocity(spellToModify.getVelocity() * multiplyVelocity);
        spellToModify.setSpread(spellToModify.getSpread() * multiplySpread);
        spellToModify.setRadius(spellToModify.getRadius() * multiplyRadius);
    }

    @Override
    public void modifyStaticProjectileSpell(StaticProjectileSpell spellToModify) {
        spellToModify.setLifetime(spellToModify.getLifetime() + addLifetime);
        spellToModify.setRadius(spellToModify.getRadius() + addRadius);

        spellToModify.setLifetime(spellToModify.getLifetime() * multiplyLifetime);
        spellToModify.setRadius(spellToModify.getRadius() * multiplyRadius);
    }

    @Override
    public void modifyMovableSpell(MovableSpell spellToModify) {
        if (setBounce) {
            spellToModify.setIsBouncy(isBouncy);
            spellToModify.setBounceFriction(bounceFriction);
            spellToModify.setMaxBounces(spellToModify.getMaxBounces() + maxBouncesIncrease);
        }
    }

    @Override
    public List<String> getExtraLore() {
        List<String> extraLore = new ArrayList<>();

        // Add operations
        if (addLifetime > 0) extraLore.add(Spell.colorizeValue("Lifetime: +", addLifetime, "s"));
        if (addLifetime < 0) extraLore.add(Spell.colorizeValue("Lifetime: ", addLifetime, "s"));

        if (addVelocity > 0) extraLore.add(Spell.colorizeValue("Velocity: +", addVelocity, ""));
        if (addVelocity < 0) extraLore.add(Spell.colorizeValue("Velocity: ", addVelocity, ""));

        if (addSpread > 0) extraLore.add(Spell.colorizeValue("Spread: +", addSpread, " DEG"));
        if (addSpread < 0) extraLore.add(Spell.colorizeValue("Spread: ", addSpread, " DEG"));

        if (addRadius > 0) extraLore.add(Spell.colorizeValue("Radius: +", addRadius, ""));
        if (addRadius < 0) extraLore.add(Spell.colorizeValue("Radius: ", addRadius, ""));

        // Multiply operations
        if (multiplyVelocity != 0) extraLore.add(Spell.colorizeValue("Velocity: x", multiplyVelocity, ""));

        // Bounce operations
        if (maxBouncesIncrease > 0) extraLore.add(Spell.colorizeValue("Bounces: +", maxBouncesIncrease, ""));
        if (maxBouncesIncrease < 0) extraLore.add(Spell.colorizeValue("Bounces: -", maxBouncesIncrease, ""));

        return extraLore;
    }

    @Override
    public AbstractSpellModifier<ValueSpellModifier> clone() {
        return new ValueSpellModifier(this);
    }

    @Override
    protected ValueSpellModifier getThis() {
        return this;
    }
}
