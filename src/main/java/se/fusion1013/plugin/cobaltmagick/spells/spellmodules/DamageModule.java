package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.Random;

public class DamageModule extends AbstractSpellModule<DamageModule> implements SpellModule {

    // ----- VARIABLES -----

    int damage;
    boolean cancelsCast;

    // Optional Variables
    boolean setsFire = false;
    int fireTicks;
    boolean knockback = false;
    double knockbackForce;
    double criticalChance;

    public DamageModule(int damage, boolean cancelsCast){
        this.damage = damage;
        this.cancelsCast = cancelsCast;
    }

    public DamageModule(DamageModule target){
        super(target);
        this.damage = target.damage;
        this.cancelsCast = target.cancelsCast;

        this.setsFire = target.setsFire;
        this.fireTicks = target.fireTicks;
        this.knockback = target.knockback;
        this.knockbackForce = target.knockbackForce;
        this.criticalChance = target.criticalChance;
    }

    public DamageModule setCriticalChance(double criticalChance){
        this.criticalChance = criticalChance;
        return this;
    }

    public DamageModule setsFire(int ticks){
        this.setsFire = true;
        this.fireTicks = ticks;
        return this;
    }

    public DamageModule setKnockback(double force){
        this.knockback = true;
        this.knockbackForce = force;
        return this;
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) { }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) { }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        if (entityHit instanceof LivingEntity){
            LivingEntity entity = (LivingEntity)entityHit;

            if (setsFire) entity.setFireTicks(fireTicks);
            if (knockback) entity.setVelocity(entity.getVelocity().add(spell.getVelocityVector().normalize().multiply(knockbackForce)));

            entity.damage(getDamageWithCrit());
        }
    }

    private int getDamageWithCrit(){
        int critIncrease = (int)criticalChance;
        Random r = new Random();
        double chance = r.nextDouble();
        if (criticalChance-critIncrease >= chance){
            critIncrease++;
        }
        critIncrease++;

        return damage * critIncrease;
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) { }

    @Override
    public boolean cancelsCast() {
        return false;
    }

    @Override
    public DamageModule clone() {
        return new DamageModule(this);
    }

    protected DamageModule getThis() { return this; }

    public int getDamage() { return damage; }

    public double getCriticalChance() { return criticalChance; }
}
