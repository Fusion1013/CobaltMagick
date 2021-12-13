package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.Random;

public class DamageModule implements SpellModule {

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
    public void executeOnCast(Location location, Vector directionVector) { }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) { }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) { }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        if (entityHit instanceof LivingEntity){
            LivingEntity entity = (LivingEntity)entityHit;

            if (setsFire) entity.setFireTicks(fireTicks);
            if (knockback) entity.setVelocity(entity.getVelocity().add(velocityVector.clone().normalize().multiply(knockbackForce)));

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
    public void executeOnDeath(Location location, Vector velocityVector) { }

    @Override
    public boolean cancelsCast() {
        return false;
    }
}
