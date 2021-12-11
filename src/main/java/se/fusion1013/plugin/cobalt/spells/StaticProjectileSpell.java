package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class StaticProjectileSpell extends MovableSpell implements Cloneable, Runnable {

    StaticProjectileShape staticProjectileShape;
    double radius;
    double lifetime;
    PotionEffect givesEffect;

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
        super(spell); // TODO: Copy all variables
        this.staticProjectileShape = spell.getStaticProjectileShape();
        this.radius = spell.getRadius();
        this.lifetime = spell.getLifetime();
        this.givesEffect = spell.getGivesEffect();
    }

    @Override
    public void castSpell(Wand wand, Player caster) {
        Location currentLocation = caster.getEyeLocation();
        castSpell(wand, caster, new Vector(0, 0, 0), currentLocation);
    }

    @Override
    public void castSpell(Wand wand, Player caster, Vector direction, Location location) {
        this.currentLocation = location;

        Bukkit.getScheduler().runTaskLater(Cobalt.getInstance(), () -> {
            long period = 1;
            this.staticProjectileTask = Bukkit.getScheduler().runTaskTimer(Cobalt.getInstance(), this, 0, period);
        }, 0);
    }

    @Override
    public void run() {
        if (lifetime <= 0) onProjectileDeath();
        lifetime -= .05;

        display();
        giveEffect();
    }

    private void display(){
        ParticleStyleSphere sphereStyle = new ParticleStyleSphere(Particle.TOWN_AURA);
        sphereStyle.setRadius(radius);
        sphereStyle.setDensity((int)Math.round(4 * Math.PI * Math.pow(radius, 2)));
        List<PParticle> particles = sphereStyle.getParticles(currentLocation);

        for (PParticle part : particles){
            for (Player p : Bukkit.getOnlinePlayers()){
                p.spawnParticle(sphereStyle.getParticle(), part.getLocation(), 1, 0, 0, 0, 0);
            }
        }
    }

    private void onProjectileDeath(){
        staticProjectileTask.cancel();
    }

    private void giveEffect(){
        if (givesEffect != null){
            World world = currentLocation.getWorld();
            if (world != null){
                List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(currentLocation, radius, radius, radius));

                for (Entity e : nearbyEntities){
                    if (e instanceof LivingEntity && e.getLocation().distance(currentLocation) <= radius){
                        LivingEntity le = (LivingEntity)e;
                        le.addPotionEffect(givesEffect);
                    }
                }
            }
        }
    }

    @Override
    public StaticProjectileSpell clone() {
        return new StaticProjectileSpell(this);
    }

    public static class StaticProjectileSpellBuilder extends MovableSpellBuilder<StaticProjectileSpell, StaticProjectileSpellBuilder> {
        StaticProjectileShape staticProjectileShape;
        double radius;
        double lifetime;
        PotionEffect givesEffect;

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
            obj.setRadius(radius);
            obj.setStaticProjectileShape(staticProjectileShape);
            obj.setGivesEffect(givesEffect);

            return super.build();
        }

        protected StaticProjectileSpellBuilder getThis() { return this; }

        public StaticProjectileSpellBuilder setGivesEffect(PotionEffect effect){
            this.givesEffect = effect;
            return getThis();
        }

        /**
         * Sets the lifetime of the spell
         *
         * @param lifetime lifetime of the spell measured in seconds
         * @return
         */
        public StaticProjectileSpellBuilder setLifetime(double lifetime){
            this.lifetime = lifetime;
            return getThis();
        }

        public StaticProjectileSpellBuilder setRadius(double radius){
            this.radius = radius;
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

    public void setStaticProjectileShape(StaticProjectileShape shape) { this.staticProjectileShape = shape; }

    public void setRadius(double radius) { this.radius = radius; }

    public void setLifetime(double lifetime) { this.lifetime = lifetime; }

    public void setGivesEffect(PotionEffect effect) { this.givesEffect = effect; }

    public StaticProjectileShape getStaticProjectileShape() { return staticProjectileShape; }

    public double getRadius() { return radius; }

    public double getLifetime() { return lifetime; }

    public PotionEffect getGivesEffect() { return givesEffect; }
}
