package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public abstract class MovableSpell extends Spell implements Cloneable {

    boolean movementStopped = false;
    int ticksToIgnoreCollisions = 4; // This is to prevent the projectile to collide with the caster

    // Basic Movement Variables
    boolean moves;
    Vector velocityVector = new Vector(0, 0, 0);
    Location currentLocation;

    // Collision
    boolean collidesWithEntities;
    boolean collidesWithBlocks;
    boolean piercesEntities;
    double colliderRadius;

    // Gravity Variables
    boolean affectedByGravity;
    double gravityMultiplier;

    // Air Resistance
    boolean affectedByAirResistance;
    double airResistanceMultiplier = .99;

    // Bounce Variables
    boolean isBouncy;
    Vector bounceFriction; // Values should be between 0 & 1

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
        this.moves = movableSpell.getMoves();

        this.collidesWithEntities = movableSpell.getCollidesWithEntities();
        this.collidesWithBlocks = movableSpell.getCollidesWithBlocks();
        this.piercesEntities = movableSpell.getPiercesEntities();
        this.colliderRadius = movableSpell.getColliderRadius();

        this.affectedByGravity = movableSpell.isAffectedByGravity();
        this.gravityMultiplier = movableSpell.getGravityMultiplier();

        this.affectedByAirResistance = movableSpell.isAffectedByAirResistance();
        this.airResistanceMultiplier = movableSpell.getAirResistanceMultiplier();

        this.isBouncy = movableSpell.isBouncy();
        this.bounceFriction = movableSpell.getBounceFriction();
    }

    public void move(){
        if (!moves) return;

        // PERFORM DIRECTION CHANGING OPERATIONS
        if (affectedByGravity) applyGravity();
        if (affectedByAirResistance) applyAirResistance();

        performMovementStep();

        if (ticksToIgnoreCollisions > 0) ticksToIgnoreCollisions--;
    }

    /**
     * Moves the projectile one step in the velocityDirection
     * Fires onEntityCollide and onBlockCollide
     */
    public void performMovementStep(){
        double distanceMoved = 0;
        double distanceToMove = velocityVector.length();

        while (distanceMoved < distanceToMove){
            if (velocityVector.length() <= 0) return;
            if (movementStopped) return;

            World world = currentLocation.getWorld();
            if (world != null && distanceToMove > 0){

                RayTraceResult result = null;
                if (collidesWithBlocks || collidesWithEntities) result = world.rayTrace(currentLocation, velocityVector, distanceToMove, FluidCollisionMode.NEVER, true, colliderRadius, null);

                boolean blockCollision = false;
                if (result != null) {

                    if (collidesWithEntities){
                        // Entity Collision Detection
                        Entity hitEntity = result.getHitEntity();
                        if (hitEntity != null && ticksToIgnoreCollisions <= 0) onEntityCollide(hitEntity);
                    }

                    if (collidesWithBlocks){
                        // Block Collision Detection
                        Block hitBlock = result.getHitBlock();
                        BlockFace hitFace = result.getHitBlockFace();
                        Vector hitPos = result.getHitPosition();
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

    private void applyAirResistance(){
        velocityVector.multiply(airResistanceMultiplier);
    }

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

    // MOVABLE BUILDER
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

            return super.build();
        }

        public B setMoves(boolean moves){
            this.moves = moves;
            return getThis();
        }

        // GRAVITY

        public B setAffectedByGravity(boolean affectedByGravity){
            this.affectedByGravity = affectedByGravity;
            return getThis();
        }

        public B addGravity(double gravityMultiplier){
            this.affectedByGravity = true;
            this.gravityMultiplier = gravityMultiplier;
            return getThis();
        }

        // COLLISION

        public B setCollidesWithEntities(boolean collidesWithEntities){
            this.collidesWithEntities = collidesWithEntities;
            return getThis();
        }

        public B setCollidesWithBlocks(boolean collidesWithBlocks){
            this.collidesWithBlocks = collidesWithBlocks;
            return getThis();
        }

        public B setPiercesEntities(boolean piercesEntities){
            this.piercesEntities = piercesEntities;
            return getThis();
        }

        public B setColliderRadius(double colliderRadius){
            this.colliderRadius = colliderRadius;
            return getThis();
        }

        // AIR RESISTANCE

        public B setAffectedByAirResistance(boolean affectedByAirResistance){
            this.affectedByAirResistance = affectedByAirResistance;
            return getThis();
        }

        public B setAirResistance(double airResistanceMultiplier){
            this.affectedByAirResistance = true;
            this.airResistanceMultiplier = airResistanceMultiplier;
            return getThis();
        }

        // BOUNCE

        public B setIsBouncy(boolean isBouncy){
            this.isBouncy = isBouncy;
            return getThis();
        }

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

    public double getColliderRadius() {
        return colliderRadius;
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
}
