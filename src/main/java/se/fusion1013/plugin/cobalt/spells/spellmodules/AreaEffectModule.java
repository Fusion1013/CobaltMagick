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

public class AreaEffectModule implements SpellModule {

    PotionEffect effect;
    double radius;
    boolean cancelsCast;

    public AreaEffectModule(PotionEffect effect, double radius, boolean cancelsCast){
        this.effect = effect;
        this.radius = radius;
        this.cancelsCast = cancelsCast;
    }

    @Override
    public void executeOnCast(Location location, Vector velocityVector) {
        giveEffectInSphere(location);
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        giveEffectInSphere(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        giveEffectInSphere(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        giveEffectInSphere(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        giveEffectInSphere(location);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    private void giveEffectInSphere(Location location){
        World world = location.getWorld();
        if (world != null){
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, radius, radius, radius));

            for (Entity e : nearbyEntities){
                if (e instanceof LivingEntity && e.getLocation().distance(location) <= radius){
                    LivingEntity le = (LivingEntity)e;
                    le.addPotionEffect(effect);
                }
            }
        }
    }
}
