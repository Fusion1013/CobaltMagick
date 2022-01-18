package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.manager.EntityManager;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.EffectModule;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlchemistEntity extends AbstractCustomEntity {

    int currentTick = 0;
    int stateCooldown = 200;
    AlchemistState currentState = AlchemistState.NORMAL;

    // Shield
    double shieldRadius = 5;
    ParticleGroup shieldGroup = new ParticleGroup();
    double damageFieldRadius = 2;
    ParticleGroup damageFieldGroup = new ParticleGroup();

    public AlchemistEntity() {
        super(EntityType.ZOMBIE, "alchemist");

        List<ParticleStyle> shieldStyles = new ArrayList<>();
        shieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setRadius(shieldRadius).setDensity(100).build());
        shieldGroup.setParticleStyles(shieldStyles);

        List<ParticleStyle> damageFieldStyles = new ArrayList<>();
        damageFieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMALL_FLAME).setRadius(damageFieldRadius).setDensity(80).build());
        damageFieldGroup.setParticleStyles(damageFieldStyles);
    }

    public AlchemistEntity(AlchemistEntity target) {
        super(target);

        this.currentTick = target.currentTick;
        this.stateCooldown = target.stateCooldown;
        this.currentState = target.currentState;
        this.shieldGroup = target.shieldGroup.clone();
        this.shieldRadius = target.shieldRadius;
        this.damageFieldGroup = target.damageFieldGroup.clone();
        this.damageFieldRadius = target.damageFieldRadius;
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);

        summonedEntity.setSilent(true);
        LivingEntity living = (LivingEntity)summonedEntity;
        living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
        living.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        living.getEquipment().setItemInOffHand(new ItemStack(Material.NETHERITE_SWORD));
        living.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 0, true, false));

        summonedEntity.getWorld().playSound(summonedEntity.getLocation(), "minecraft:cobalt.akali", SoundCategory.HOSTILE, 1, 1);
    }

    @Override
    public void tick() {
        super.tick();

        currentTick++;

        // Random chance to summon an attacking wand
        Random r = new Random();
        if (r.nextInt(0, 101) == 0) summonWand();
        if (r.nextInt(0, 21) == 0) ambientSounds();
        if (r.nextInt(0, 41) == 0) mainAttack();

        // Switch State
        if (currentTick >= stateCooldown) {
            if (currentState == AlchemistState.NORMAL) {
                currentState = AlchemistState.SHIELDED;
                ParticleStylePoint point = new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(30).setOffset(new Vector(.4, 1, .4)).build();
                ParticleGroup group = new ParticleGroup();
                List<ParticleStyle> styles = new ArrayList<>();
                styles.add(point);
                group.setParticleStyles(styles);
                group.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));
                group.display(spawnLocation.add(new Vector(0, 1, 0)));

                World world = spawnLocation.getWorld();
                if (world != null) {
                    world.playSound(summonedEntity.getLocation(), "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
                    world.playSound(spawnLocation, "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
                }
            }
            else currentState = AlchemistState.NORMAL;

            currentTick = 0;
        }

        // Perform Operations Based on State
        switch (currentState) {
            case SHIELDED -> shieldedStateTick();
            case NORMAL -> normalStateTick();
        }
    }

    private void mainAttack() {
        Wand wand = new Wand(false, 1, .2, 2, 1000, 1000, 9, 0, new ArrayList<>(), 0);
        List<ISpell> spells = new ArrayList<>();
        spells.add(SpellManager.getSpell(94));
        wand.setSpells(spells);

        // Find target
        Location target = null;
        Location castLocation = summonedEntity.getLocation().clone().add(new Vector(0, 2.2, 0));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (castLocation.distanceSquared(p.getLocation()) < castLocation.distanceSquared(p.getLocation()) || target == null) {
                target = p.getLocation();
            }
        }

        if (target != null) {
            Vector delta = new Vector(target.getX() - castLocation.getX(), target.getY() - castLocation.getY() + 1, target.getZ() - castLocation.getZ()).normalize();
            // Cast spell
            if (summonedEntity instanceof LivingEntity living) wand.castSpells(living, delta, castLocation);
        }
    }

    private void ambientSounds() {
        Location currentLocation = summonedEntity.getLocation();
        World currentWorld = currentLocation.getWorld();
        if (currentWorld == null) return;
        currentWorld.playSound(currentLocation, "minecraft:cobalt.brain", SoundCategory.HOSTILE, 1, 1);
    }

    private void normalStateTick() {
        summonedEntity.setGravity(true);
        damageFieldGroup.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));
        new EffectModule(damageFieldRadius, false).setFire(20).giveEffectsInSphere(summonedEntity.getLocation());
    }

    private void shieldedStateTick() {
        summonedEntity.setGravity(false);
        summonedEntity.teleport(spawnLocation);
        shieldGroup.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));

        // Get all arrow entities and multiplies velocity by -1
        List<Entity> nearbyEntities = summonedEntity.getNearbyEntities(shieldRadius, shieldRadius, shieldRadius);
        for (Entity e : nearbyEntities) {
            if (e instanceof Arrow a && e.getLocation().distanceSquared(summonedEntity.getLocation()) < shieldRadius * shieldRadius) {
                Vector arrowVelocity = a.getVelocity();
                arrowVelocity.multiply(-1);
                a.setVelocity(arrowVelocity);
            }
        }
    }

    /**
     * Summons a new wand, randomly chosen from four presets
     */
    private void summonWand() { // TODO: Wands (3) should be summoned when the alchemist does his attack
        Random r = new Random();
        int wandType = r.nextInt(0, 4); // Three different kinds of wands that can possibly be summoned by alchemy boi
        Wand wand = null;
        List<ISpell> wandSpells = new ArrayList<>();

        switch (wandType) {
            case 0 -> {
                wand = new Wand(false, 1, .2, 10, 1000, 1000, 9, 0, new ArrayList<>(), 0);
                for (int i = 0; i < 4; i++) wandSpells.add(SpellManager.getSpell(90));
            }
            case 1 -> {
                wand = new Wand(false, 1, .2, 1, 1000, 1000, 4, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(91));
            }
            case 2 -> {
                wand = new Wand(false, 1, .2, 100, 1000, 1000, 20, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(92));
            }
            case 3 -> {
                wand = new Wand(false, 1, .2, 100, 1000, 1000, 20, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(93));
            }
        }
        if (wand != null) {
            wand.setSpells(wandSpells);

            WandEntity wandEntity = new WandEntity(wand);
            wandEntity.addLifespan(130);
            wandEntity.setXpDropAmount(0);
            EntityManager.getInstance().spawnCustomEntity(wandEntity, summonedEntity.getLocation()); // TODO: Change to a location near the entity
        }
    }

    @Override
    public void kill() {
        super.kill();
    }

    @Override
    public AlchemistEntity clone() {
        return new AlchemistEntity(this);
    }

    public static class AlchemistBuilder extends AbstractCustomEntityBuilder<AlchemistEntity, AlchemistBuilder> {

        @Override
        protected AlchemistEntity createObj() {
            return new AlchemistEntity();
        }

        @Override
        protected AlchemistBuilder getThis() {
            return this;
        }
    }

    enum AlchemistState {
        SHIELDED,
        NORMAL
    }
}
