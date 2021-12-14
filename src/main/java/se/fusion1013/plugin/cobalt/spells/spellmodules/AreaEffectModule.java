package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs an operation on all entities in a spherical area
 */
public class AreaEffectModule implements SpellModule {

    double radius;
    boolean cancelsCast;

    // Optional Variables
    PotionEffect effect;
    boolean freezing;

    public AreaEffectModule(double radius, boolean cancelsCast){
        this.radius = radius;
        this.cancelsCast = cancelsCast;
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
    public void executeOnCast(Location location, Vector velocityVector) {
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        giveEffectsInSphere(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        giveEffectsInSphere(location);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    private void giveEffectsInSphere(Location location){
        World world = location.getWorld();
        if (world != null){
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, radius, radius, radius));

            for (Entity e : nearbyEntities){
                if (e instanceof LivingEntity && e.getLocation().distance(location) <= radius){
                    LivingEntity le = (LivingEntity)e;

                    if (effect != null) le.addPotionEffect(effect);
                    if (freezing) le.setFreezeTicks(le.getFreezeTicks() + 4);
                }
            }
        }
    }
}
