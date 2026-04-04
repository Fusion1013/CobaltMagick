package se.fusion1013.cobaltmagick.spell.projectile;

import org.bukkit.entity.LivingEntity;

public abstract class AbstractSpellProjectile implements ISpellProjectile {

    protected final LivingEntity caster;

    public AbstractSpellProjectile(LivingEntity caster) {
        this.caster = caster;
    }

    public abstract void tick();

}
