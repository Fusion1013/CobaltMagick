package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.AbstractSpellModule;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.SpellModule;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaticProjectileSpell extends MovableSpell implements Cloneable, Runnable {

    StaticProjectileShape staticProjectileShape;

    double lifetime;
    ParticleGroup particleGroup;

    List<SpellModule> executeOnCast = new ArrayList<>();
    List<SpellModule> executeOnTick = new ArrayList<>();
    List<SpellModule> executeOnDeath = new ArrayList<>();

    private BukkitTask staticProjectileTask;

    /**
     * Creates a new <code>StaticProjectileSpell</code> with an id, internalSpellName and a spellName
     *
     * @param id id of the Static Projectile
     * @param internalSpellName internal name of the Static Projectile. Example: "horizontal_barrier"
     * @param spellName display name of the Static Projectile. Example: "Horizontal Barrier"
     */
    public StaticProjectileSpell(int id, String internalSpellName, String spellName) {
        super(id, internalSpellName, spellName, SpellType.STATIC_PROJECTILE);
    }

    /**
     * Creates a new <code>StaticProjectileSpell</code> with the given <code>StaticProjectileSpell</code> as a template
     *
     * @param spell <code>StaticProjectileSpell</code> to copy the parameters of
     */
    public StaticProjectileSpell(StaticProjectileSpell spell) {
        super(spell);
        this.staticProjectileShape = spell.getStaticProjectileShape();
        this.lifetime = spell.getLifetime();
        this.particleGroup = spell.getParticleGroup();

        this.executeOnCast = spell.getExecuteOnCast();
        this.executeOnTick = spell.getExecuteOnTick();
        this.executeOnDeath = spell.getExecuteOnDeath();
    }

    @Override
    public void castSpell(Wand wand, Player caster) {
        Location currentLocation = caster.getEyeLocation();
        castSpell(wand, caster, new Vector(0, 0, 0), currentLocation);
    }

    @Override
    public void castSpell(Wand wand, Player caster, Vector direction, Location location) {
        super.castSpell(wand, caster);
        this.currentLocation = location;

        // Special Things Here
        for (SpellModule module : executeOnCast){
            module.executeOnCast(wand, caster, this);
        }

        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            long period = 1;
            this.staticProjectileTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this, 0, period);
            SpellManager.getInstance().addActiveSpell(this, staticProjectileTask, this.hashCode());
        }, 0);
    }

    @Override
    public void run() {
        if (lifetime <= 0) onProjectileDeath();
        lifetime -= .05;

        display(); // TODO: Replace with onTick particles

        // Special Things Here
        for (SpellModule module : executeOnTick){
            module.executeOnTick(wand, caster, this);
        }

        if (movementStopped) killParticle();

        resetModules();
    }

    private void resetModules(){
        for (SpellModule module : executeOnCast) module.update();
        for (SpellModule module : executeOnTick) module.update();
        for (SpellModule module : executeOnDeath) module.update();
    }

    private void killParticle(){
        SpellManager.getInstance().removeActiveSpell(this.hashCode());
        staticProjectileTask.cancel();
        movementStopped = true;
    }

    @Override
    public void cancelTask() {
        killParticle();
    }

    private void display(){
        if (particleGroup != null) particleGroup.display(currentLocation);
    }

    private void onProjectileDeath(){
        // Special Things Here
        for (SpellModule module : executeOnDeath){
            module.executeOnDeath(wand, caster, this);
        }
        killParticle();
    }

    @Override
    public StaticProjectileSpell clone() {
        return new StaticProjectileSpell(this);
    }

    public static class StaticProjectileSpellBuilder extends MovableSpellBuilder<StaticProjectileSpell, StaticProjectileSpellBuilder> {
        StaticProjectileShape staticProjectileShape;

        double lifetime;
        ParticleGroup particleGroup;

        List<SpellModule> executeOnCast = new ArrayList<>();
        List<SpellModule> executeOnTick = new ArrayList<>();
        List<SpellModule> executeOnDeath = new ArrayList<>();

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "horizontal_barrier".
         *
         * @param id id of the spell
         * @param internalSpellName internal name of the spell
         */
        public StaticProjectileSpellBuilder(int id, String internalSpellName){
            super(id, internalSpellName);
        }

        /**
         * Builds a new <code>StaticProjectileSpell</code> object
         *
         * @return a new <code>StaticProjectileSpell</code> object
         */
        protected StaticProjectileSpell createObj() { return new StaticProjectileSpell(id, internalSpellName, spellName); }

        @Override
        public StaticProjectileSpell build() {
            obj.setLifetime(lifetime);
            obj.setStaticProjectileShape(staticProjectileShape);
            obj.setParticleGroup(particleGroup);

            obj.setExecuteOnCast(executeOnCast);
            obj.setExecuteOnTick(executeOnTick);
            obj.setExecuteOnDeath(executeOnDeath);

            return super.build();
        }

        protected StaticProjectileSpellBuilder getThis() { return this; }

        public StaticProjectileSpellBuilder addExecuteOnCast(SpellModule executeThis){
            executeOnCast.add(executeThis);
            return getThis();
        }

        public StaticProjectileSpellBuilder addExecuteOnTick(SpellModule executeThis){
            executeOnTick.add(executeThis);
            return getThis();
        }

        public StaticProjectileSpellBuilder addExecuteOnDeath(SpellModule executeThis){
            executeOnDeath.add(executeThis);
            return getThis();
        }

        public StaticProjectileSpellBuilder addParticle(ParticleGroup group){
            this.particleGroup = group;
            return getThis();
        }

        /**
         * Sets the lifetime of the spell
         *
         * @param lifetime lifetime of the spell measured in seconds
         * @return
         */
        public StaticProjectileSpellBuilder setLifetime(double lifetime){
            Random r = new Random();
            this.lifetime = lifetime;
            return getThis();
        }

        public StaticProjectileSpellBuilder setStaticProjectileShape(StaticProjectileShape shape){
            this.staticProjectileShape = shape;
            return getThis();
        }
    }

    public enum StaticProjectileShape {
        SPHERE
    }

    // ------ GETTERS / SETTERS -----

    // TODO: Clone all objects inside executeOn lists

    public void setStaticProjectileShape(StaticProjectileShape shape) { this.staticProjectileShape = shape; }

    public void setLifetime(double lifetime) { this.lifetime = lifetime; }

    public void setExecuteOnCast(List<SpellModule> executeThis) { this.executeOnCast = new ArrayList<>(executeThis); }

    public void setExecuteOnTick(List<SpellModule> executeThis) { this.executeOnTick = new ArrayList<>(executeThis); }

    public void setExecuteOnDeath(List<SpellModule> executeThis) { this.executeOnDeath = new ArrayList<>(executeThis); }

    public void addExecuteOnCast(SpellModule executeThis) { this.executeOnCast.add(executeThis); }

    public void addExecuteOnTick(SpellModule executeThis) { this.executeOnTick.add(executeThis); }

    public void addExecuteOnDeath(SpellModule executeThis) { this.executeOnDeath.add(executeThis); }

    public void setParticleGroup(ParticleGroup particleGroup) { this.particleGroup = particleGroup; }

    public StaticProjectileShape getStaticProjectileShape() { return staticProjectileShape; }

    public double getLifetime() { return lifetime; }

    public List<SpellModule> getExecuteOnCast() {
        return AbstractSpellModule.cloneList(executeOnCast);
    }

    public List<SpellModule> getExecuteOnTick() { return AbstractSpellModule.cloneList(executeOnTick); }

    public List<SpellModule> getExecuteOnDeath() { return AbstractSpellModule.cloneList(executeOnDeath); }

    public ParticleGroup getParticleGroup() {
        if (particleGroup != null) return particleGroup.clone();
        else return null;
    }
}
