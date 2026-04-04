package se.fusion1013.cobaltmagick.spell.projectile;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.components.actions.IAction;
import se.fusion1013.cobaltmagick.spell.SpellTrigger;
import se.fusion1013.cobaltmagick.wand.cast.ShotProperty;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellProjectile extends AbstractSpellProjectile {

    // TODO: Move a lot of this to the projectile template
    private boolean isDead = false;
    private int ticksToIgnoreCollisions = 4;
    private boolean isStatic;

    private final double velocityLimit = 50;
    private Vector velocity = new Vector(0, 0, 0);
    private final Location location;
    private Location previousLocation;

    private final boolean collidesWithEntities = true;
    private final boolean collidesWithBlocks = true;
    private final boolean isPiercing;

    private final boolean hasGravity;
    private final double gravityMultiplier;

    private final boolean hasAirResistance;
    private final double airResistanceMultiplier;

    private final boolean isBouncy;
    private final Vector bounceFriction = new Vector(0.99, 0.75, 0.99);
    private int maxBounces;

    private int lifespan;

    private final double damage;

    private final ShotState shotState;
    private final IProjectileTemplate projectileTemplate;

    public SpellProjectile(LivingEntity caster, Location location, Vector initialVelocity, IProjectileTemplate projectileTemplate, ShotState shotState) {
        super(caster);
        this.location = location;
        this.velocity = initialVelocity;
        this.shotState = shotState;
        this.projectileTemplate = projectileTemplate;

        isPiercing = shotState.getPropertyOrDefault(ShotProperty.HAS_PIERCING, false);
        hasGravity = shotState.getPropertyOrDefault(ShotProperty.HAS_GRAVITY, false);
        gravityMultiplier = shotState.getPropertyOrDefault(ShotProperty.GRAVITY_MULTIPLIER, 1.0);
        hasAirResistance = shotState.getPropertyOrDefault(ShotProperty.HAS_AIR_RESISTANCE, false);
        airResistanceMultiplier = shotState.getPropertyOrDefault(ShotProperty.AIR_RESISTANCE_MULTIPLIER, 0.99);
        isBouncy = shotState.getPropertyOrDefault(ShotProperty.IS_BOUNCY, false);
        maxBounces = shotState.getPropertyOrDefault(ShotProperty.MAX_BOUNCES, 10);
        lifespan = shotState.getPropertyOrDefault(ShotProperty.LIFESPAN, 240);
        damage = shotState.getPropertyOrDefault(ShotProperty.DAMAGE, 0D);
    }

    @Override
    public void tick() {
        if (isStatic) return;

        if (hasGravity) applyGravity();
        if (hasAirResistance) applyAirResistance();

        performMovementStep();

        if (ticksToIgnoreCollisions > 0) ticksToIgnoreCollisions--;
        if (lifespan > 0) lifespan--;
        else {
            isDead = true;
            onDeath();
        }

        List<IAction> tickActions = shotState.getActions(SpellTrigger.OnTick);

        Map<String, Object> context = getContext();
        triggerActions(tickActions, context);
    }

    private void applyGravity() {
        velocity.subtract(new Vector(0, gravityMultiplier * 0.05, 0));
    }

    private void applyAirResistance() {
        velocity.multiply(airResistanceMultiplier);
    }

    private void performMovementStep() {
        previousLocation = location.clone();

        double distanceMoved = 0;
        double distanceToMove = velocity.length();

        if (distanceToMove > velocityLimit) {
            velocity = velocity.clone().normalize().multiply(velocityLimit);
            distanceToMove = velocityLimit;
        }

        while (distanceMoved < distanceToMove) {
            if (velocity.length() <= 0) return;
            if (isDead) return;

            World world = location.getWorld();
            if (world != null) {

                RayTraceResult entityRayTrace = world.rayTraceEntities(location, velocity, distanceToMove, Math.max(1, 1));
                RayTraceResult blockRayTrace = world.rayTraceBlocks(location, velocity, distanceToMove, FluidCollisionMode.NEVER);

                boolean blockCollision = false;
                if (collidesWithEntities && entityRayTrace != null) {
                    // Entity Collision Detection
                    Entity hitEntity = entityRayTrace.getHitEntity();
                    if (hitEntity != null && ticksToIgnoreCollisions <= 0 && hitEntity != caster)
                        onEntityCollide(hitEntity, entityRayTrace.getHitPosition());
                }

                if (collidesWithBlocks && blockRayTrace != null) {
                    // Block Collision Detection
                    Block hitBlock = blockRayTrace.getHitBlock();
                    BlockFace hitFace = blockRayTrace.getHitBlockFace();
                    Vector hitPos = blockRayTrace.getHitPosition();
                    double distanceToHit = hitPos.distance(location.toVector());
                    if (distanceMoved + distanceToHit < distanceToMove) { // If true, collision has occurred
                        distanceMoved += distanceToHit;

                        location.setX(hitPos.getX());
                        location.setY(hitPos.getY());
                        location.setZ(hitPos.getZ());

                        velocity.multiply(new Vector(.99, .99, .99));

                        if (hitBlock != null) onBlockCollide(hitBlock, hitFace);
                        blockCollision = true;
                    }
                }
                if (!blockCollision) {
                    location.add(velocity.clone().normalize().multiply(distanceToMove - distanceMoved));
                    distanceMoved = distanceToMove;
                }
            } else {
                break;
            }
        }
    }

    private void onEntityCollide(Entity hitEntity, @NotNull Vector hitPosition) {
        if (!isPiercing) {
            isDead = true;
            onDeath();
        }
        if (damage > 0 && hitEntity instanceof LivingEntity livingEntity) livingEntity.damage(damage, caster);

        List<IAction> entityHitActions = shotState.getActions(SpellTrigger.OnEntityHit);
        List<IAction> hitActions = shotState.getActions(SpellTrigger.OnHit);

        Map<String, Object> context = getContext();
        context.put("default_location", new Location(hitEntity.getWorld(), hitPosition.getX(), hitPosition.getY(), hitPosition.getZ()));
        triggerActions(entityHitActions, context);
        triggerActions(hitActions, context);
    }

    private void onBlockCollide(Block hitBlock, BlockFace hitBlockFace) {
        if (isBouncy && (maxBounces > 0 || maxBounces == -1)) {
            Vector n = hitBlockFace.getDirection();
            Vector d = velocity.clone();
            double dot = d.dot(n);
            velocity = d.subtract(n.multiply(2 * dot));
            velocity.multiply(bounceFriction);
            if (maxBounces > 0) maxBounces--;
        } else {
            isDead = true;
            onDeath();
        }

        List<IAction> blockHitActions = shotState.getActions(SpellTrigger.OnBlockHit);
        List<IAction> hitActions = shotState.getActions(SpellTrigger.OnHit);

        Map<String, Object> context = getContext();
        triggerActions(blockHitActions, context);
        triggerActions(hitActions, context);
    }

    private void triggerActions(List<IAction> actions, Map<String, Object> context) {
        for (IAction action : actions) {
            action.execute(context);
        }
    }

    private void onDeath() {
        triggerActions(shotState.getActions(SpellTrigger.OnDeath), getContext());
    }

    // TODO: Add things like hit block, etc for specific methods
    private Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();

        context.put("default_entity", caster);
        context.put("default_location", location);
        context.put("previous_location", previousLocation);

        return context;
    }

    public void display() {
        projectileTemplate.display(location);
    }

    public Location getLocation() {
        return location;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public boolean isDead() {
        return lifespan == 0 || isDead;
    }
}
