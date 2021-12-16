package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs an operation on all entities in a spherical area
 */
public class AreaEffectModule extends AbstractSpellModule<AreaEffectModule> implements SpellModule {

    boolean cancelsCast;

    // Optional Variables
    PotionEffect effect;
    boolean freezing;
    int instantFreeze = 0;

    public AreaEffectModule(double radius, boolean cancelsCast){
        setRadius(radius);
        this.cancelsCast = cancelsCast;
    }

    public AreaEffectModule(AreaEffectModule target){
        super(target);
        this.cancelsCast = target.cancelsCast;

        this.effect = target.effect;
        this.freezing = target.freezing;
        this.instantFreeze = target.instantFreeze;
    }

    public AreaEffectModule setInstantFreeze(int ticks){
        this.instantFreeze = ticks;
        return this;
    }

    public AreaEffectModule setFreezing(){
        freezing = true;
        return this;
    }

    public AreaEffectModule setPotionEffect(PotionEffect effect){
        this.effect = effect;
        return this;
    }

    @Override
    public void executeOnCast(Player caster, Location location, Vector velocityVector) {
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnTick(Player caster, Location location, Vector velocityVector) {
        if (!canRun) return;
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnBlockHit(Player caster, Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(caster, location, velocityVector, blockHit, hitBlockFace);
        if (!canRun) return;
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnEntityHit(Player caster, Location location, Vector velocityVector, Entity entityHit) {
        super.executeOnEntityHit(caster, location, velocityVector, entityHit);
        if (!canRun) return;
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnDeath(Player caster, Location location, Vector velocityVector) {
        if (!canRun) return;
        giveEffectsInSphere(location);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    private void giveEffectsInSphere(Location location){
        World world = location.getWorld();
        if (world != null){
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, currentRadius, currentRadius, currentRadius));

            for (Entity e : nearbyEntities){
                if (e instanceof LivingEntity && e.getLocation().distance(location) <= currentRadius){
                    LivingEntity le = (LivingEntity)e;

                    if (effect != null) le.addPotionEffect(effect);
                    if (freezing) le.setFreezeTicks(le.getFreezeTicks() + 4);
                    if (le.getFreezeTicks() < instantFreeze) le.setFreezeTicks(instantFreeze);
                }
            }
        }
    }

    @Override
    public AreaEffectModule clone() {
        return new AreaEffectModule(this);
    }

    protected AreaEffectModule getThis() { return this; }
}