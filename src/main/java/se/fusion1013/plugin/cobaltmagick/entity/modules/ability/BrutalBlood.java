package se.fusion1013.plugin.cobaltmagick.entity.modules.ability;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;

import java.util.Collection;
import java.util.Random;

public class BrutalBlood extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    private final double range;
    private final int healAmount;
    private final int strengthIncrease;
    private final int strengthDuration;

    // Particles
    private static final ParticleGroup LINE_GROUP = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder()
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setExtra(new Particle.DustTransition(Color.RED, Color.ORANGE, 1))
                    .setCount(1)
                    .setOffset(new Vector(.1, .1, .1))
                    .setDensity(8)
                    .build())
            .build();

    // ----- CONSTRUCTORS -----

    public BrutalBlood(double range, int healAmount, int strengthIncrease, int strengthDuration) {
        super(0);
        this.range = range;
        this.healAmount = healAmount;
        this.strengthIncrease = strengthIncrease;
        this.strengthDuration = strengthDuration;
    }

    // ----- EXECUTE -----

    @Override
    public boolean attemptAbility(CustomEntity entity, ISpawnParameters spawnParameters) {
        return false;
    }

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {}

    @Override
    public void onEntityDeath(CustomEntity entity, ISpawnParameters spawnParameters, Location location, Entity dyingEntity) {
        Collection<LivingEntity> nearbyEntities = location.getNearbyLivingEntities(range);
        Random r = new Random();
        for (LivingEntity living : nearbyEntities) {
            if (!(living instanceof Player)) {

                if (dyingEntity.getUniqueId() == living.getUniqueId()) continue; // Do not heal itself

                // Add strength
                PotionEffect currentStrength = living.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                if (currentStrength == null) living.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, strengthDuration, strengthIncrease-1));
                else living.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, currentStrength.getDuration(), currentStrength.getAmplifier()+strengthIncrease));

                // Increase health
                AttributeInstance attributeInstance = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attributeInstance == null) continue;
                living.setHealth(Math.min(living.getHealth() + healAmount, attributeInstance.getValue()));

                // Play effect
                for (int i = 0; i < 10; i++) location.getWorld().spawnParticle(Particle.SPELL_MOB, living.getLocation().clone().add(r.nextDouble(), r.nextDouble(), r.nextDouble()), 0, 1.000, 0.078, 0.078, 1);
                location.getWorld().spawnParticle(Particle.HEART, living.getLocation(), 3, .1, .1, .1);
                location.getWorld().playSound(living.getLocation(), Sound.ENTITY_WITCH_DRINK, SoundCategory.HOSTILE, 1, 1);
                LINE_GROUP.display(living.getLocation().clone().add(0, 1, 0), location.clone().add(0, 1, 0));
            }
        }

        // Play death effect
        location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 5, .3, .3, .3, .1);
        location.getWorld().playSound(location, Sound.ENTITY_WITCH_DRINK, SoundCategory.HOSTILE, 1, 1);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getAbilityName() {
        return "Brutal Blood";
    }

    @Override
    public String getAbilityDescription() {
        return "Heals nearby allies on death, & gives them a slight increase to their meelee attack.";
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public BrutalBlood(BrutalBlood target) {
        super(target);

        this.range = target.range;
        this.healAmount = target.healAmount;
        this.strengthIncrease = target.strengthIncrease;
        this.strengthDuration = target.strengthDuration;
    }

    @Override
    public BrutalBlood clone() {
        return new BrutalBlood(this);
    }
}
