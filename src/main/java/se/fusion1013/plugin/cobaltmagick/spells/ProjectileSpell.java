package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.action.system.*;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.IParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.AbstractSpellModule;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.DamageModule;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.SpellModule;
import se.fusion1013.plugin.cobaltmagick.wand.CastParser;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProjectileSpell extends MovableSpell implements Cloneable, Runnable {

    //region FIELDS

    // -- Generic Values
    private double spread;
    private double velocity;
    private double lifetime;
    private double currentLifetime;

    private ParticleGroup particleGroup;

    // -- Actions
    private final List<IAction> castActions = new ArrayList<>();
    private final List<IAction> tickActions = new ArrayList<>(); // TODO
    private final List<IAction> blockCollisionActions = new ArrayList<>();
    private final List<IAction> entityCollisionActions = new ArrayList<>();
    private final List<IAction> deathActions = new ArrayList<>();

    // -- Executors
    private List<SpellModule> executeOnCast = new ArrayList<>();
    private List<SpellModule> executeOnTick = new ArrayList<>();
    private List<SpellModule> executeOnBlockCollision = new ArrayList<>();
    private List<SpellModule> executeOnEntityCollision = new ArrayList<>();
    private List<SpellModule> executeOnDeath = new ArrayList<>();

    private Vector directionModifier = new Vector(0, 0, 0);

    private BukkitTask projectileTask;

    private List<TriggerType> triggerTypes = new ArrayList<>();

    //endregion

    //region CONSTRUCTORS

    public ProjectileSpell(int id, String internalSpellName, Map<?, ?> data) {
        super(id, internalSpellName, SpellType.PROJECTILE, data);

        if (data.containsKey("spread")) spread = (double) data.get("spread");
        if (data.containsKey("velocity")) velocity = (double) data.get("velocity");
        if (data.containsKey("lifetime")) lifetime = (double) data.get("lifetime");

        // Main Particle
        if (data.containsKey("particles")) {
            ParticleGroup.ParticleGroupBuilder particleBuilder = new ParticleGroup.ParticleGroupBuilder();
            for (ParticleStyle style : getParticles((List<Map<?,?>>) data.get("particles"))) {
                particleBuilder.addStyle(style);
            }
            particleGroup = particleBuilder.build();
        }

        // Actions
        if (data.containsKey("cast_actions")) castActions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("cast_actions")));
        if (data.containsKey("tick_actions")) tickActions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("tick_actions")));
        if (data.containsKey("block_collision_actions")) blockCollisionActions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("block_collision_actions")));
        if (data.containsKey("entity_collision_actions")) entityCollisionActions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("entity_collision_actions")));
        if (data.containsKey("death_actions")) deathActions.addAll(ActionManager.getActions((List<Map<?,?>>) data.get("death_actions")));

        // Triggers
        if (data.containsKey("triggers")) {
            List<String> triggers = (List<String>) data.get("triggers");
            for (String s : triggers) triggerTypes.add(EnumUtils.findEnumInsensitiveCase(TriggerType.class, s));
        }
    }

    protected List<ParticleStyle> getParticles(List<Map<?, ?>> particleData) {
        List<ParticleStyle> newStyles = new ArrayList<>();

        for (Map<?, ?> styleData : particleData) {
            for (var key : styleData.keySet()) {
                ParticleStyle newStyle = ParticleStyleManager.createParticleStyleSilent((String) key, (Map<?, ?>) styleData.get(key));
                newStyles.add(newStyle);
            }
        }

        return newStyles;
    }

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
        this.spread = projectileSpell.getSpread();
        this.velocity = projectileSpell.getVelocity();
        this.lifetime = projectileSpell.getLifetime();
        this.particleGroup = projectileSpell.getParticleGroup();

        this.triggerTypes = projectileSpell.getTriggerTypes();

        this.directionModifier = projectileSpell.getDirectionModifier();

        this.castActions.addAll(projectileSpell.castActions);
        this.tickActions.addAll(projectileSpell.tickActions);
        this.blockCollisionActions.addAll(projectileSpell.blockCollisionActions);
        this.entityCollisionActions.addAll(projectileSpell.entityCollisionActions);
        this.deathActions.addAll(projectileSpell.deathActions);

        this.executeOnCast = projectileSpell.getExecuteOnCast();
        this.executeOnTick = projectileSpell.getExecuteOnTick();
        this.executeOnBlockCollision = projectileSpell.getExecuteOnBlockCollision();
        this.executeOnEntityCollision = projectileSpell.getExecuteOnEntityCollision();
        this.executeOnDeath = projectileSpell.getExecuteOnDeath();
    }

    //endregion

    @Override
    public void performPreCast(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(caster, wand, wandSpells, casts, spellPos);

        for (TriggerType t : triggerTypes){
            List<ISpell> spellsToCast = new CastParser(caster, wand.getId(), wandSpells, 1, spellPos+1).prepareCast();
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
    public void castSpell(Wand wand, LivingEntity caster) {
        Vector velocityVector = caster.getEyeLocation().getDirection();
        Location currentLocation = caster.getEyeLocation();

        // Offset from casting center
        Vector offsetFromPlayer = new Vector(0, 0, 0);
        offsetFromPlayer.add(velocityVector.clone().normalize().multiply(Math.max(1, radius)));

        currentLocation.add(offsetFromPlayer);

        castSpell(wand, caster, velocityVector, currentLocation);
    }

    private static Vector getRightVector(Vector vector){
        Vector direction = vector.clone().normalize();
        return new Vector(direction.getZ(), 0, -direction.getX()).normalize();
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster, Vector direction, Location location){
        super.castSpell(wand, caster);
        this.wand = wand;
        this.velocityVector = direction;
        this.currentLocation = location;
        this.lastLocation = location.clone();
        this.currentLifetime = lifetime;

        // Apply spread
        double actualSpread;
        if (wand == null) actualSpread = spread;
        else actualSpread = wand.getSpread() + spread;

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
        if (caster != null) {
            Vector casterVelocity = caster.getVelocity().clone();
            this.velocityVector.add(casterVelocity.setY(0));
        }

        // Actions
        if (castActions != null && caster != null) {
            for (IAction action : castActions) {
                if (action instanceof ILivingEntityAction livingEntityAction) livingEntityAction.activate(caster); // TODO: Audio action does not work if caster is null, ie. spell was cast by non-entity
                else if (action instanceof IEntityAction entityAction) entityAction.activate(caster);
            }
        }

        // Special Things Here
        for (SpellModule module : executeOnCast){
            module.executeOnCast(wand, caster, this);
        }

        // Start projectile tick
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            long period = 1;
            this.projectileTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this, 0, period);
            SpellManager.getInstance().addActiveSpell(this, projectileTask, this.hashCode());
        }, 0);
    }

    @Override
    public void run() {
        if (currentLifetime <= 0 || movementStopped) {
            onProjectileDeath();
            return;
        }
        currentLifetime -= .05;

        move();
        display();

        for (IAction action : tickActions) {
            // TODO
        }

        // Special Things Here
        for (SpellModule module : executeOnTick){
            module.executeOnTick(wand, caster, this);
        }
        // Particle After Movement Handle
        if (currentLifetime * 2 <= lifetime) executeTrigger(TriggerType.TIMER);

        updateModules();
    }

    private void updateModules(){
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
        for (IAction action : deathActions) {
            if (action instanceof ILocationAction locationAction) locationAction.activate(currentLocation);
        }

        for (SpellModule module : executeOnDeath){
            module.executeOnDeath(wand, caster, this);
        }
        executeTrigger(TriggerType.EXPIRATION);
        executeTrigger(TriggerType.COLLISIONOREXPIRATION);

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

        // Execute actions
        for (IAction action : entityCollisionActions) {
            if (action instanceof ILivingEntityAction livingEntityAction) livingEntityAction.activate(hitEntity);
            else if (action instanceof IEntityAction entityAction) entityAction.activate(hitEntity);

            if (action.isCancelAction()) killParticle();
        }

        // Old system
        for (SpellModule module : executeOnEntityCollision){
            module.executeOnEntityHit(wand, caster, this, hitEntity);

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

        // Actions
        for (IAction action : blockCollisionActions) {
            if (action instanceof ILocationAction locationAction) locationAction.activate(hitBlock.getLocation());
        }

        for (SpellModule module : executeOnBlockCollision){
            module.executeOnBlockHit(wand, caster, this, hitBlock, hitBlockFace);

            if (module.cancelsCast()) killParticle();
        }
        executeTrigger(TriggerType.COLLISION);
        executeTrigger(TriggerType.COLLISIONOREXPIRATION);
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
                    sp.castSpell(wand, caster, velocityVector.clone().normalize(), currentLocation.clone());
                }
            }
        }
    }

    private void display(){
        if (particleGroup != null) {
            particleGroup.display(currentLocation.clone());
            particleGroup.display(lastLocation.clone(), currentLocation.clone());
        }
    }

    /*
    @Override
    public ItemStack getSpellItem(){
        return getSpellItem(getLore());
    }
     */

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();

        if (spread != 0) lore.add(colorizeValue("Spread: ", spread, ""));
        if (velocity != 0) lore.add(colorizeValue("Velocity: ", velocity, ""));

        double damage = 0;
        double criticalChance = 0;

        for (SpellModule m : executeOnEntityCollision){
            if (m instanceof DamageModule module){
                damage += module.getDamage();
                criticalChance +=  module.getCriticalChance();
            }
        }

        damage = damage / 2.0;

        if (damage != 0) lore.add(colorizeValue("Damage: ", damage, "♥"));
        if (criticalChance != 0) lore.add(colorizeValue("Critical Chance: ", criticalChance, "%"));

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

        /**
         * Adds a spell to be executed both on entity and block collision
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
        public ProjectileSpellBuilder addExecuteOnCollision(SpellModule executeThis){
            executeOnBlockCollision.add(executeThis);
            executeOnEntityCollision.add(executeThis);
            return getThis();
        }

        /**
         * Adds a spell to be executed both on cast
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
        public ProjectileSpellBuilder addExecuteOnCast(SpellModule executeThis){
            executeOnCast.add(executeThis);
            return getThis();
        }

        /**
         * Adds a spell to be executed both on tick
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
        public ProjectileSpellBuilder addExecuteOnTick(SpellModule executeThis){
            executeOnTick.add(executeThis);
            return getThis();
        }

        /**
         * Adds a spell to be executed both on block collision
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
        public ProjectileSpellBuilder addExecuteOnBlockCollision(SpellModule executeThis){
            executeOnBlockCollision.add(executeThis);
            return getThis();
        }

        /**
         * Adds a spell to be executed both on entity collision
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
        public ProjectileSpellBuilder addExecuteOnEntityCollision(SpellModule executeThis){
            executeOnEntityCollision.add(executeThis);
            return getThis();
        }

        /**
         * Adds a spell to be executed both on death
         *
         * @param executeThis the <code>SpellModule</code> to execute
         * @return the builder
         */
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

        public ProjectileSpellBuilder setSpread(double spread){
            this.spread = spread;
            return getThis();
        }

        public ProjectileSpellBuilder setVelocity(double velocity){
            this.velocity = velocity / 20;
            return getThis();
        }

        public ProjectileSpellBuilder setLifetime(double lifetime){
            this.lifetime = lifetime;
            return getThis();
        }
    }

    // ------ GETTERS / SETTERS -----

    // TODO: Clone all objects inside executeOn lists

    public void addDelayedSpell(DelayedSpell delayedSpell) { this.delayedSpells.add(delayedSpell); }

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

    public double getSpread(){
        return spread;
    }

    public double getVelocity(){
        return velocity;
    }

    public double getLifetime(){
        return lifetime;
    }

    public ParticleGroup getParticleGroup() { // TODO: Replace with on tick particles
        if (particleGroup != null) return particleGroup.clone();
        else return null;
    }

    public List<TriggerType> getTriggerTypes() { return triggerTypes; }

    public Vector getDirectionModifier() { return directionModifier.clone(); }

    public List<SpellModule> getExecuteOnCast() { return AbstractSpellModule.cloneList(executeOnCast); }

    public List<SpellModule> getExecuteOnTick() { return AbstractSpellModule.cloneList(executeOnTick); }

    public List<SpellModule> getExecuteOnBlockCollision() { return AbstractSpellModule.cloneList(executeOnBlockCollision); }

    public List<SpellModule> getExecuteOnEntityCollision() { return AbstractSpellModule.cloneList(executeOnEntityCollision); }

    public List<SpellModule> getExecuteOnDeath() { return AbstractSpellModule.cloneList(executeOnDeath); }
}
