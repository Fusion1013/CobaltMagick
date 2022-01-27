package se.fusion1013.plugin.cobaltmagick.spells.movementmodifier;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HomingMovementModifier implements IMovementModifier, Cloneable {

    // Homing Type
    HomingType type;

    // General Attributes
    double maxEnemyDistance;

    public HomingMovementModifier() { }

    public HomingMovementModifier(HomingMovementModifier target) {
        this.type = target.type;

        this.maxEnemyDistance = target.maxEnemyDistance;
    }

    // ----- BUILDER METHODS -----

    public HomingMovementModifier setRotateTowards(double maxEnemyDistance) {
        type = HomingType.ROTATE_TOWARDS;
        this.maxEnemyDistance = maxEnemyDistance;
        return this;
    }

    public HomingMovementModifier setAccelerateTowards(double maxEnemyDistance) {
        type = HomingType.ACCELERATE_TOWARDS;
        this.maxEnemyDistance = maxEnemyDistance;
        return this;
    }

    // ----- MODIFY METHODS -----

    @Override
    public Vector modifyVelocityVector(LivingEntity caster, Vector currentVelocity) { return currentVelocity; }

    public Vector modifyVelocityVector(LivingEntity caster, Vector currentVelocity, Location currentLocation, LivingEntity... filter) {
        switch (type) {
            case ROTATE_TOWARDS -> {
                return rotateTowardsEnemy(caster, currentVelocity, currentLocation, maxEnemyDistance, filter);
            }
            case ACCELERATE_TOWARDS -> {
                return accelerateTowardsEnemy(currentVelocity, maxEnemyDistance, filter);
            }
        }

        return currentVelocity;
    }

    // ----- HOMING MODIFIER METHODS -----

    private Vector rotateTowardsEnemy(LivingEntity caster, Vector velocity, Location currentLocation, double maxEnemyDistance, LivingEntity... filter) {
        LivingEntity targetEntity = findNearbyEntity(caster, currentLocation, maxEnemyDistance, filter);
        if (targetEntity == null) return velocity;
        Vector target = targetEntity.getLocation().toVector();

        Vector delta = new Vector(target.getX() - currentLocation.getX(), target.getY() - currentLocation.getY() + 1, target.getZ() - currentLocation.getZ()).normalize();
        delta.multiply(velocity.length());

        return delta;
    }

    private Vector accelerateTowardsEnemy(Vector velocity, double maxEnemyDistance, LivingEntity... filter) {
        return null;
    }

    // ----- FINDING NEARBY ENTITY METHODS ----- // TODO: Move this to util

    private LivingEntity findNearbyEntity(LivingEntity caster, Location location, double maxDistance, LivingEntity... filter) {
        World world = location.getWorld();
        if (world == null) return null;

        List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, maxEnemyDistance, maxEnemyDistance, maxEnemyDistance));

        if (nearbyEntities.size() > 0) {
            LivingEntity closest = null;
            double distance = Double.MAX_VALUE;
            for (Entity e : nearbyEntities) {
                if (e instanceof LivingEntity living) {
                    if (living.getLocation().distanceSquared(location) < distance && !contains(living, filter) && living.getLocation().distanceSquared(location) < maxDistance*maxDistance) {
                        if (caster instanceof Player || living instanceof Player) closest = living;
                    }
                }
            }
            return closest;
        }
        return null;
    }

    private boolean contains(LivingEntity check, LivingEntity... filter) {
        for (LivingEntity living : filter) {
            if (living == check) return true;
        }
        return false;
    }

    @Override
    public HomingMovementModifier clone() {
        return new HomingMovementModifier(this);
    }

    public enum HomingType {
        ROTATE_TOWARDS,
        ACCELERATE_TOWARDS
    }
}
