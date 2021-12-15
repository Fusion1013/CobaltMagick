package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.SpellManager;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;
import se.fusion1013.plugin.cobalt.spells.spellmodules.AbstractSpellModule;
import se.fusion1013.plugin.cobalt.spells.spellmodules.SpellModule;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProjectileSpell extends MovableSpell implements Cloneable, Runnable {

    double radius;
    double spread;
    double velocity;
    double lifetime;
    double currentLifetime;
    ParticleGroup particleGroup;

    List<SpellModule> executeOnCast = new ArrayList<>();
    List<SpellModule> executeOnTick = new ArrayList<>();
    List<SpellModule> executeOnBlockCollision = new ArrayList<>();
    List<SpellModule> executeOnEntityCollision = new ArrayList<>();
    List<SpellModule> executeOnDeath = new ArrayList<>();

    Vector directionModifier = new Vector(0, 0, 0);

    private BukkitTask projectileTask;

    List<TriggerType> triggerTypes = new ArrayList<>();

    Wand wand;
    Player caster;

    /**
     * Creates a new <code>ProjectileSpell</code> with an id, internalSpellName and a spellName
     *
     * @param id id of the Projectile
     * @param internalSpellName internal name of the Projectile. Example: "spark_bolt"
     * @param spellName display name of the Projectile. Example: "Spark Bolt"
     */
    public ProjectileSpell(int id, String internalSpellName, String spellName) {
        super(id, internalSpellName, spellName, SpellType.PROJECTILE);
    }

    /**
     * Creates a new <code>ProjectileSpell</code> with the given <code>ProjectileSpell</code> as a template
     *
     * @param projectileSpell <code>ProjectileSpell</code> to copy the parameters of
     */
    public ProjectileSpell(ProjectileSpell projectileSpell){
        super(projectileSpell);
        this.radius = projectileSpell.getRadius();
        this.spread = projectileSpell.getSpread();
        this.velocity = projectileSpell.getVelocity();
        this.lifetime = projectileSpell.getLifetime();
        this.particleGroup = projectileSpell.getParticleGroup();

        this.triggerTypes = projectileSpell.getTriggerTypes();

        this.directionModifier = projectileSpell.getDirectionModifier();

        this.executeOnCast = projectileSpell.getExecuteOnCast();
        this.executeOnTick = projectileSpell.getExecuteOnTick();
        this.executeOnBlockCollision = projectileSpell.getExecuteOnBlockCollision();
        this.executeOnEntityCollision = projectileSpell.getExecuteOnEntityCollision();
        this.executeOnDeath = projectileSpell.getExecuteOnDeath();
    }

    @Override
    public void performPreCast(List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(wandSpells, casts, spellPos);

        for (TriggerType t : triggerTypes){
            List<ISpell> spellsToCast = new CastParser(wandSpells, 1, spellPos+1).prepareCast();
            DelayedSpell trigger = new DelayedSpell(spellsToCast, t);
            this.delayedSpells.add(trigger);
        }
    }

    /**
     * Creates a new projectile instance in the world. This should not be performed on a spell that is inside a wand
     * as this would break if the same spell was cast more than once
     *
     * @param wand the wand this spell was cast from
     * @param caster the caster that cast the spell
     */
    @Override
    public void castSpell(Wand wand, Player caster) {
        Vector velocityVector = caster.getEyeLocation().getDirection();
        Location currentLocation = caster.getEyeLocation();

        // Offset from casting center
        Vector offsetFromPlayer = new Vector(0, 0, 0);
        offsetFromPlayer.add(velocityVector.clone().multiply(radius * 1.2));

        currentLocation.add(offsetFromPlayer);

        castSpell(wand, caster, velocityVector, currentLocation);
    }

    private static Vector getRightVector(Vector vector){
        Vector direction = vector.clone().normalize();
        return new Vector(direction.getZ(), 0, -direction.getX()).normalize();
    }

    @Override
    public void castSpell(Wand wand, Player caster, Vector direction, Location location){
        super.castSpell(wand, caster);
        this.wand = wand;
        this.caster = caster;
        this.velocityVector = direction;
        this.currentLocation = location;
        this.currentLifetime = lifetime;

        // Apply spread
        double actualSpread = wand.getSpread() + spread;

        Random r = new Random();

        // Rotate vector around head
        Vector rightVector = getRightVector(direction);
        Vector upVector = direction.clone().rotateAroundAxis(rightVector, Math.toRadians(90));
        this.velocityVector.rotateAroundAxis(upVector, Math.toRadians(directionModifier.getX()));

        // Rotate vector up/down
        this.velocityVector.rotateAroundAxis(rightVector, Math.toRadians(directionModifier.getY()));

        // Apply spread
        this.velocityVector.rotateAroundAxis(upVector, Math.toRadians(2 * (r.nextDouble() - .5) * Math.max(0, actualSpread)));
        this.velocityVector.rotateAroundAxis(rightVector, Math.toRadians(2 * (r.nextDouble() - .5) * Math.max(0, actualSpread)));

        // Add Velocity
        this.velocityVector.multiply(velocity);
        Vector casterVelocity = caster.getVelocity().clone();
        this.velocityVector.add(casterVelocity.setY(0));

        // Special Things Here
        for (SpellModule module : executeOnCast){
            module.executeOnCast(currentLocation, velocityVector.clone());
        }

        // Start projectile tick
        Bukkit.getScheduler().runTaskLater(Cobalt.getInstance(), () -> {
            long period = 1;
            this.projectileTask = Bukkit.getScheduler().runTaskTimer(Cobalt.getInstance(), this, 0, period);
            SpellManager.getInstance().addActiveSpell(this, projectileTask, this.hashCode());
        }, 0);
    }

    @Override
    public void run() {
        if (currentLifetime <= 0) onProjectileDeath();
        currentLifetime -= .05;

        move();
        display();

        // Special Things Here
        for (SpellModule module : executeOnTick){
            module.executeOnTick(currentLocation, velocityVector.clone());
        }
        // Particle After Movement Handle
        if (currentLifetime * 2 <= lifetime) executeTrigger(TriggerType.TIMER);

        if (movementStopped) killParticle();

        resetModules();
    }

    private void resetModules(){
        for (SpellModule module : executeOnCast) module.update();
        for (SpellModule module : executeOnTick) module.update();
        for (SpellModule module : executeOnDeath) module.update();
        for (SpellModule module : executeOnBlockCollision) module.update();
        for (SpellModule module : executeOnEntityCollision) module.update();
    }

    /**
     * Called when a projectile dies
     */
    public void onProjectileDeath(){
        for (SpellModule module : executeOnDeath){
            module.executeOnDeath(currentLocation, velocityVector);
        }
        executeTrigger(TriggerType.EXPIRATION);

        killParticle();
    }

    /**
     * Called when an entity is hit by a projectile
     *
     * @param hitEntity the entity that was hit by the projectile
     */
    @Override
    public void onEntityCollide(Entity hitEntity){
        super.onEntityCollide(hitEntity);

        for (SpellModule module : executeOnEntityCollision){
            module.executeOnEntityHit(currentLocation, velocityVector, hitEntity);

            if (module.cancelsCast()) killParticle();
        }

        executeTrigger(TriggerType.COLLISION);
    }

    /**
     * Called when a projectile collides with a block
     *
     * @param hitBlock the block that was hit
     * @param hitBlockFace the block face that was hit
     */
    @Override
    public void onBlockCollide(Block hitBlock, BlockFace hitBlockFace){
        super.onBlockCollide(hitBlock, hitBlockFace);

        for (SpellModule module : executeOnBlockCollision){
            module.executeOnBlockHit(currentLocation, velocityVector, hitBlock, hitBlockFace);

            if (module.cancelsCast()) killParticle();
        }
        executeTrigger(TriggerType.COLLISION);
    }

    private void killParticle(){
        SpellManager.getInstance().removeActiveSpell(this.hashCode());
        projectileTask.cancel();
        movementStopped = true;
    }

    @Override
    public void cancelTask() {
        killParticle();
    }

    private void executeTrigger(TriggerType type){
        for (DelayedSpell ds : delayedSpells){
            if (ds.getWhenToCast() == type && !ds.getHasCast()){
                ds.setHasCast(true);
                List<ISpell> spellsToCast = ds.getSpellsToCast();

                for (ISpell sp : spellsToCast){
                    sp.castSpell(wand, caster, velocityVector.clone(), currentLocation.clone());
                }
            }
        }
    }

    private void display(){
        if (particleGroup != null) particleGroup.display(currentLocation);
    }

    @Override
    public ItemStack getSpellItem(){
        return getSpellItem(getLore());
    }

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();

        // TODO: Make lore search SpellModules for values
        if (spread != 0) lore.add(colorizeValue("Spread: ", spread, ""));
        if (velocity != 0) lore.add(colorizeValue("Velocity: ", velocity, ""));
        // if (damage != 0) lore.add(colorizeValue("Damage: ", damage, "♥"));
        // if (criticalChance != 0) lore.add(colorizeValue("Critical Chance: ", criticalChance, "%"));

        return lore;
    }

    @Override
    public ProjectileSpell clone(){
        return new ProjectileSpell(this);
    }

    /**
     * Builds a new <code>ParticleSpell</code>
     */
    public static class ProjectileSpellBuilder extends MovableSpellBuilder<ProjectileSpell, ProjectileSpellBuilder> {

        double radius = 0.1;
        double spread = 0;
        double velocity = 800;
        double lifetime = 50; // Measured in 1/50 seconds
        ParticleGroup particleGroup;

        List<SpellModule> executeOnCast = new ArrayList<>();
        List<SpellModule> executeOnTick = new ArrayList<>();
        List<SpellModule> executeOnBlockCollision = new ArrayList<>();
        List<SpellModule> executeOnEntityCollision = new ArrayList<>();
        List<SpellModule> executeOnDeath = new ArrayList<>();

        List<TriggerType> triggerType = new ArrayList<>();

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id id of the spell
         * @param internalSpellName internal name of the spell
         */
        public ProjectileSpellBuilder(int id, String internalSpellName) {
            super(id, internalSpellName);
        }

        /**
         * Builds a new <code>ProjectileSpell</code>
         *
         * @return a new <code>ProjectileSpell</code>
         */
        protected ProjectileSpell createObj(){
            return new ProjectileSpell(id, internalSpellName, spellName);
        }

        protected ProjectileSpellBuilder getThis() { return this; }

        /**
         * Builds a new <code>ProjectileSpell</code>
         *
         * @return a new <code>ProjectileSpell</code>
         */
        @Override
        public ProjectileSpell build(){
            obj.setRadius(radius);
            obj.setSpread(spread);
            obj.setVelocity(velocity);
            obj.setLifetime(lifetime);
            obj.setParticleGroup(particleGroup);

            obj.setExecuteOnCast(executeOnCast);
            obj.setExecuteOnTick(executeOnTick);
            obj.setExecuteOnBlockCollision(executeOnBlockCollision);
            obj.setExecuteOnEntityCollision(executeOnEntityCollision);
            obj.setExecuteOnDeath(executeOnDeath);

            obj.setTrigger(triggerType);

            return super.build();
        }

        public ProjectileSpellBuilder addExecuteOnCast(SpellModule executeThis){
            executeOnCast.add(executeThis);
            return getThis();
        }

        public ProjectileSpellBuilder addExecuteOnTick(SpellModule executeThis){
            executeOnTick.add(executeThis);
            return getThis();
        }

        public ProjectileSpellBuilder addExecuteOnBlockCollision(SpellModule executeThis){
            executeOnBlockCollision.add(executeThis);
            return getThis();
        }

        public ProjectileSpellBuilder addExecuteOnEntityCollision(SpellModule executeThis){
            executeOnEntityCollision.add(executeThis);
            return getThis();
        }

        public ProjectileSpellBuilder addExecuteOnDeath(SpellModule executeThis){
            executeOnDeath.add(executeThis);
            return getThis();
        }

        public ProjectileSpellBuilder addTrigger(TriggerType triggerType){
            this.triggerType.add(triggerType);
            return getThis();
        }

        public ProjectileSpellBuilder setParticle(ParticleGroup group){
            this.particleGroup = group;
            return getThis();
        }

        /**
         * Sets the radius of the projectile
         *
         * @param radius the radius of the projectile in blocks
         * @return the builder
         */
        public ProjectileSpellBuilder setRadius(double radius){
            this.radius = radius;
            return getThis();
        }

        public ProjectileSpellBuilder setSpread(double spread){
            this.spread = spread;
            return getThis();
        }

        public ProjectileSpellBuilder setVelocity(double velocity){
            this.velocity = velocity / 20;
            return getThis();
        }

        public ProjectileSpellBuilder setLifetime(double lifetime){
            Random r = new Random();
            this.lifetime = (lifetime / 50.0) + r.nextDouble() * .5;
            return getThis();
        }
    }

    // ------ GETTERS / SETTERS -----

    // TODO: Clone all objects inside executeOn lists

    public void addDelayedSpell(DelayedSpell delayedSpell) { this.delayedSpells.add(delayedSpell); }

    public void setRadius(double radius){
        this.radius = radius;
    }

    public void setSpread(double spread){
        this.spread = spread;
    }

    public void setVelocity(double velocity){
        this.velocity = velocity;
    }

    public void setLifetime(double lifetime){
        this.lifetime = lifetime;
    }

    public void setParticleGroup(ParticleGroup particleGroup) { this.particleGroup = particleGroup; }

    public void setTrigger(List<TriggerType> triggerType) { this.triggerTypes = triggerType; }

    /**
     * Sets the direction modifier. The x value represents yaw and the y value represents pitch
     *
     * @param directionModifier vector to modify the direction by
     */
    public void setDirectionModifier(Vector directionModifier) { this.directionModifier = directionModifier; }

    public void setExecuteOnCast(List<SpellModule> executeThis) { this.executeOnCast = new ArrayList<>(executeThis); }

    public void setExecuteOnTick(List<SpellModule> executeThis) { this.executeOnTick = new ArrayList<>(executeThis); }

    public void setExecuteOnBlockCollision(List<SpellModule> executeThis) { this.executeOnBlockCollision = new ArrayList<>(executeThis); }

    public void setExecuteOnEntityCollision(List<SpellModule> executeThis) { this.executeOnEntityCollision = new ArrayList<>(executeThis); }

    public void setExecuteOnDeath(List<SpellModule> executeThis) { this.executeOnDeath = new ArrayList<>(executeThis); }

    public void addExecuteOnCast(SpellModule executeThis) { this.executeOnCast.add(executeThis); }

    public void addExecuteOnTick(SpellModule executeThis) { this.executeOnTick.add(executeThis); }

    public void addExecuteOnEntityCollision(SpellModule executeThis) { this.executeOnEntityCollision.add(executeThis); }

    public void addExecuteOnBlockCollision(SpellModule executeThis) { this.executeOnBlockCollision.add(executeThis); }

    public void addExecuteOnDeath(SpellModule executeThis) { this.executeOnDeath.add(executeThis); }

    public double getRadius(){
        return radius;
    }

    public double getSpread(){
        return spread;
    }

    public double getVelocity(){
        return velocity;
    }

    public double getLifetime(){
        return lifetime;
    }

    public ParticleGroup getParticleGroup() { return particleGroup.clone(); }

    public List<TriggerType> getTriggerTypes() { return triggerTypes; }

    public Vector getDirectionModifier() { return directionModifier.clone(); }

    public List<SpellModule> getExecuteOnCast() { return AbstractSpellModule.cloneList(executeOnCast); }

    public List<SpellModule> getExecuteOnTick() { return AbstractSpellModule.cloneList(executeOnTick); }

    public List<SpellModule> getExecuteOnBlockCollision() { return AbstractSpellModule.cloneList(executeOnBlockCollision); }

    public List<SpellModule> getExecuteOnEntityCollision() { return AbstractSpellModule.cloneList(executeOnEntityCollision); }

    public List<SpellModule> getExecuteOnDeath() { return AbstractSpellModule.cloneList(executeOnDeath); }
}
