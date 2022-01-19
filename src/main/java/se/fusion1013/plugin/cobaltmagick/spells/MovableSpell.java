package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.movementmodifier.HomingMovementModifier;
import se.fusion1013.plugin.cobaltmagick.spells.movementmodifier.IMovementModifier;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public abstract class MovableSpell extends Spell implements Cloneable {

    private static final double velocityLimit = 50;

    boolean movementStopped = false;
    int ticksToIgnoreCollisions = 4; // This is to prevent the projectile to collide with the caster

    // Basic Movement Variables
    boolean moves;
    Vector velocityVector = new Vector(0, 0, 0);
    Location currentLocation;

    // Collision
    boolean collidesWithEntities = true;
    boolean collidesWithBlocks = true;
    boolean piercesEntities = false;
    // double colliderRadius;

    // Gravity Variables
    boolean affectedByGravity;
    double gravityMultiplier;

    // Air Resistance
    boolean affectedByAirResistance;
    double airResistanceMultiplier = .99;

    // Bounce Variables
    boolean isBouncy;
    Vector bounceFriction; // Values should be between 0 & 1

    // Movement Modifiers
    List<IMovementModifier> movementModifiers = new ArrayList<>();

    public MovableSpell(int id, String internalSpellName, String spellName, SpellType type) {
        super(id, internalSpellName, spellName, type);
    }

    /**
     * Creates a new <code>MovableSpell</code> with the given <code>MovableSpell</code> as a template
     *
     * @param movableSpell <code>MovableSpell</code> to copy the parameters of
     */
    public MovableSpell(MovableSpell movableSpell) {
        super(movableSpell);
        this.movementStopped = movableSpell.movementStopped;
        this.ticksToIgnoreCollisions = movableSpell.ticksToIgnoreCollisions;

        this.moves = movableSpell.getMoves();
        this.velocityVector = movableSpell.getVelocityVector();
        this.currentLocation = movableSpell.getLocation();

        this.collidesWithEntities = movableSpell.getCollidesWithEntities();
        this.collidesWithBlocks = movableSpell.getCollidesWithBlocks();
        this.piercesEntities = movableSpell.getPiercesEntities();

        this.affectedByGravity = movableSpell.isAffectedByGravity();
        this.gravityMultiplier = movableSpell.getGravityMultiplier();

        this.affectedByAirResistance = movableSpell.isAffectedByAirResistance();
        this.airResistanceMultiplier = movableSpell.getAirResistanceMultiplier();

        this.isBouncy = movableSpell.isBouncy();
        this.bounceFriction = movableSpell.getBounceFriction();

        this.movementModifiers = new ArrayList<>(movableSpell.movementModifiers);
    }

    /**
     * Applies all modifiers to the direction of the spell, and then performs a movement step
     */
    public void move(){
        if (!moves) return;

        // PERFORM DIRECTION CHANGING OPERATIONS
        for (IMovementModifier movMod : movementModifiers) {
            if (movMod instanceof HomingMovementModifier homing) velocityVector = homing.modifyVelocityVector(velocityVector, currentLocation, caster);
            else velocityVector = movMod.modifyVelocityVector(velocityVector);
        }

        if (affectedByGravity) applyGravity();
        if (affectedByAirResistance) applyAirResistance();

        performMovementStep();

        if (ticksToIgnoreCollisions > 0) ticksToIgnoreCollisions--;
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster) {
        super.castSpell(wand, caster);
    }

    /**
     * Moves the projectile one step in the velocityDirection
     * Fires onEntityCollide and onBlockCollide
     */
    public void performMovementStep(){
        double distanceMoved = 0;
        double distanceToMove = velocityVector.length();

        if (distanceToMove > velocityLimit){
            velocityVector = velocityVector.clone().normalize().multiply(50);
            distanceToMove = 50;
        }

        while (distanceMoved < distanceToMove){
            if (velocityVector.length() <= 0) return;
            if (movementStopped) return;

            World world = currentLocation.getWorld();
            if (world != null){

                RayTraceResult entityRayTrace = world.rayTraceEntities(currentLocation, velocityVector, distanceToMove, Math.max(radius, 1));
                RayTraceResult blockRayTrace = world.rayTraceBlocks(currentLocation, velocityVector, distanceToMove, FluidCollisionMode.NEVER);

                boolean blockCollision = false;
                if (collidesWithEntities && entityRayTrace != null){
                    // Entity Collision Detection
                    Entity hitEntity = entityRayTrace.getHitEntity();
                    if (hitEntity != null && ticksToIgnoreCollisions <= 0 && hitEntity != caster) onEntityCollide(hitEntity);
                }

                if (collidesWithBlocks && blockRayTrace != null){
                    // Block Collision Detection
                    Block hitBlock = blockRayTrace.getHitBlock();
                    BlockFace hitFace = blockRayTrace.getHitBlockFace();
                    Vector hitPos = blockRayTrace.getHitPosition();
                    double distanceToHit = hitPos.distance(currentLocation.toVector());
                    if (distanceMoved + distanceToHit < distanceToMove){ // If true, collision has occurred
                        distanceMoved += distanceToHit;

                        currentLocation.setX(hitPos.getX());
                        currentLocation.setY(hitPos.getY());
                        currentLocation.setZ(hitPos.getZ());

                        velocityVector.multiply(new Vector(.99, .99, .99));

                        if (hitBlock != null) onBlockCollide(hitBlock, hitFace);
                        blockCollision = true;
                    }
                }
                if (!blockCollision) {
                    currentLocation.add(velocityVector.clone().normalize().multiply(distanceToMove - distanceMoved));
                    distanceMoved = distanceToMove;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Applies air resistance to the spell
     */
    private void applyAirResistance(){
        velocityVector.multiply(airResistanceMultiplier);
    }

    /**
     * Applies gravity to the spell
     */
    private void applyGravity(){
        velocityVector.subtract(new Vector(0, gravityMultiplier * .05, 0));
    }

    /**
     * Called when an entity is hit by a projectile
     *
     * @param hitEntity the entity that was hit by the projectile
     */
    public void onEntityCollide(Entity hitEntity){
        if (!piercesEntities) movementStopped = true;
    }

    /**
     * Called when a projectile collides with a block
     *
     * @param hitBlock the block that was hit
     * @param hitBlockFace the block face that was hit
     */
    public void onBlockCollide(Block hitBlock, BlockFace hitBlockFace){
        if (isBouncy){
            Vector n = hitBlockFace.getDirection();
            Vector d = velocityVector.clone();
            double dot = d.dot(n);
            velocityVector = d.subtract(n.multiply(2*dot));
            velocityVector.multiply(bounceFriction);
        } else {
            movementStopped = true;
        }
    }

    @Override
    public abstract MovableSpell clone();

    /**
     * Builds a new <code>MovableSpell</code>
     */
    protected static abstract class MovableSpellBuilder<T extends MovableSpell, B extends MovableSpellBuilder> extends Spell.SpellBuilder<T, B> {

        // Basic Movement Variables
        boolean moves = true;

        // Collision
        boolean collidesWithEntities = true;
        boolean collidesWithBlocks = true;
        boolean piercesEntities = false;
        double colliderRadius = .1;

        // Gravity Variables
        boolean affectedByGravity = false;
        double gravityMultiplier = 2;

        // Air Resistance
        boolean affectedByAirResistance = false;
        double airResistanceMultiplier = .99;

        // Bounce Variables
        boolean isBouncy = false;
        Vector bounceFriction = new Vector(.99, .75, .99); // Values should be between 0 & 1

        // Movement modifiers
        List<IMovementModifier> movementModifiers = new ArrayList<>();

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id                id of the spell
         * @param internalSpellName internal name of the spell
         */
        public MovableSpellBuilder(int id, String internalSpellName) {
            super(id, internalSpellName);
        }

        /**
         * Builds the <code>MovableSpell</code>
         * @return a new <code>MovableSpell</code>
         */
        @Override
        public T build() {
            obj.setMoves(moves);

            obj.setCollidesWithEntities(collidesWithEntities);
            obj.setCollidesWithBlocks(collidesWithBlocks);
            obj.setPiercesEntities(piercesEntities);

            obj.setAffectedByGravity(affectedByGravity);
            obj.setGravityMultiplier(gravityMultiplier);

            obj.setAffectedByAirResistance(affectedByAirResistance);
            obj.setAirResistanceMultiplier(airResistanceMultiplier);

            obj.setIsBouncy(isBouncy);
            obj.setBounceFriction(bounceFriction);

            obj.movementModifiers = movementModifiers;

            return super.build();
        }

        public B addMovementModifier(IMovementModifier modifier) {
            movementModifiers.add(modifier);
            return getThis();
        }

        /**
         * Sets if the spell moves or not
         *
         * @param moves if the spell moves or not
         * @return the builder
         */
        public B setMoves(boolean moves){
            this.moves = moves;
            return getThis();
        }

        // ----- GRAVITY -----

        /**
         * Sets if the spell is affected by gravity or not
         *
         * @param affectedByGravity if the spell is affected by gravity or not
         * @return the builder
         */
        public B setAffectedByGravity(boolean affectedByGravity){
            this.affectedByGravity = affectedByGravity;
            return getThis();
        }

        /**
         * Sets the gravity multiplier, or the acceleration towards the gravity direction the spell experiences every tick
         *
         * @param gravityMultiplier the gravity multiplier, measured in blocks/second^2
         * @return the builder
         */
        public B addGravity(double gravityMultiplier){
            this.affectedByGravity = true;
            this.gravityMultiplier = gravityMultiplier;
            return getThis();
        }

        // ----- COLLISION -----

        /**
         * Sets if the spell collides with entities or not. If it doesn't, <code>onEntityCollision</code> will not be called
         *
         * @param collidesWithEntities if the spell collides with entities
         * @return the builder
         */
        public B setCollidesWithEntities(boolean collidesWithEntities){
            this.collidesWithEntities = collidesWithEntities;
            return getThis();
        }

        /**
         * Sets if the spell collides with blocks or not. If it doesn't, <code>onBlockCollision</code> will not be called
         *
         * @param collidesWithBlocks if the spell collides with entities
         * @return the builder
         */
        public B setCollidesWithBlocks(boolean collidesWithBlocks){
            this.collidesWithBlocks = collidesWithBlocks;
            return getThis();
        }

        /**
         * Sets if the spell pierces entities or not. If it doesn't, the spell will be cancelled on collision
         *
         * @param piercesEntities if the spell pierces entities
         * @return the builder
         */
        public B setPiercesEntities(boolean piercesEntities){
            this.piercesEntities = piercesEntities;
            return getThis();
        }

        /**
         * Sets the radius of the collider
         *
         * @param colliderRadius the radius of the collider
         * @return the builder
         */
        public B setColliderRadius(double colliderRadius){
            this.colliderRadius = colliderRadius;
            return getThis();
        }

        // AIR RESISTANCE

        /**
         * Sets if the spell is affected by air resistance
         *
         * @param affectedByAirResistance if the spell is affected by air resistance
         * @return the builder
         */
        public B setAffectedByAirResistance(boolean affectedByAirResistance){
            this.affectedByAirResistance = affectedByAirResistance;
            return getThis();
        }

        /**
         * Sets the air resistance multiplier
         *
         * @param airResistanceMultiplier the air resistance multiplier
         * @return the builder
         */
        public B setAirResistance(double airResistanceMultiplier){
            this.affectedByAirResistance = true;
            this.airResistanceMultiplier = airResistanceMultiplier;
            return getThis();
        }

        // BOUNCE

        /**
         * Sets if the spell is bouncy. If true, the spell will bounce upon collision with a block
         *
         * @param isBouncy if the spell is bouncy
         * @return the builder
         */
        public B setIsBouncy(boolean isBouncy){
            this.isBouncy = isBouncy;
            return getThis();
        }

        /**
         * Sets the friction the bounce induces on the spell
         *
         * @param bounceFriction the bounce friction
         * @return the builder
         */
        public B setBounceFriction(Vector bounceFriction){
            this.isBouncy = true;
            this.bounceFriction = bounceFriction.clone();
            return getThis();
        }
    }

    public void setMoves(boolean moves){
        this.moves = moves;
    }

    public void setCollidesWithEntities(boolean collidesWithEntities){
        this.collidesWithEntities = collidesWithEntities;
    }

    public void setCollidesWithBlocks(boolean collidesWithBlocks){
        this.collidesWithBlocks = collidesWithBlocks;
    }

    public void setPiercesEntities(boolean piercesEntities){
        this.piercesEntities = piercesEntities;
    }

    public void setAffectedByGravity(boolean affectedByGravity){
        this.affectedByGravity = affectedByGravity;
    }

    public void setGravityMultiplier(double gravityMultiplier){
        this.gravityMultiplier = gravityMultiplier;
    }

    public void setAffectedByAirResistance(boolean affectedByAirResistance){
        this.affectedByAirResistance = affectedByAirResistance;
    }

    public void setAirResistanceMultiplier(double airResistanceMultiplier){
        this.airResistanceMultiplier = airResistanceMultiplier;
    }

    public void setIsBouncy(boolean isBouncy){
        this.isBouncy = isBouncy;
    }

    public void setBounceFriction(Vector bounceFriction){
        this.bounceFriction = bounceFriction;
    }

    public boolean getMoves() {
        return moves;
    }

    public boolean getCollidesWithEntities() {
        return collidesWithEntities;
    }

    public boolean getCollidesWithBlocks() {
        return collidesWithBlocks;
    }

    public boolean getPiercesEntities() {
        return piercesEntities;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public double getGravityMultiplier() {
        return gravityMultiplier;
    }

    public boolean isAffectedByAirResistance() {
        return affectedByAirResistance;
    }

    public double getAirResistanceMultiplier() {
        return airResistanceMultiplier;
    }

    public boolean isBouncy() {
        return isBouncy;
    }

    public Vector getBounceFriction() {
        return bounceFriction.clone();
    }

    public Vector getVelocityVector() {
        if (velocityVector != null) return velocityVector.clone();
        else return  null;
    }

    @Override
    public Location getLocation() {
        if (currentLocation != null) return currentLocation.clone();
        return null;
    }
}
