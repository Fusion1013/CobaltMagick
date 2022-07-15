package se.fusion1013.plugin.cobaltmagick.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.util.AIUtil;

public class TntThrowerAbility extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    double velocity;
    double maxTargetDistance;

    // ----- CONSTRUCTORS -----

    public TntThrowerAbility(double cooldown, double velocity, double maxTargetDistance) {
        super(cooldown);

        this.velocity = velocity;
        this.maxTargetDistance = maxTargetDistance;
    }

    @Override
    public String getAbilityName() {
        return "TNT Thrower";
    }

    @Override
    public String getAbilityDescription() {
        return "Allows an entity to throw tnt";
    }

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {
        // Get a target entity
        Entity summonedEntity = customEntity.getSummonedEntity();
        LivingEntity targetEntity = AIUtil.findNearbyPlayerHealthWeighted(summonedEntity, maxTargetDistance);
        Location throwLocation = summonedEntity.getLocation().clone();
        World world = throwLocation.getWorld();

        // If the target is not null, calculate the direction towards it and cast the spells
        if (targetEntity != null) {
            if (summonedEntity instanceof LivingEntity living) {
                // Throw TNT
                world.spawn(throwLocation, TNTPrimed.class, tntEntity -> {
                    Vector launchVelocity = living.getEyeLocation().getDirection().clone().add(new Vector(0, 1, 0));
                    launchVelocity.multiply(velocity);
                    tntEntity.setVelocity(launchVelocity);
                });

                // Play effects
                throwLocation.getWorld().playSound(throwLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                throwLocation.getWorld().spawnParticle(Particle.SMOKE_NORMAL, throwLocation, 10, 1, 1, 1, 0);
            }
        }
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public TntThrowerAbility(TntThrowerAbility target) {
        super(target);
        this.velocity = target.velocity;
        this.maxTargetDistance = target.maxTargetDistance;
    }

    public TntThrowerAbility clone() {
        return new TntThrowerAbility(this);
    }
}
