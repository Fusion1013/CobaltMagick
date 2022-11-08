package se.fusion1013.plugin.cobaltmagick.entity.modules.ability;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;

import java.util.Collection;
import java.util.Random;

public class Enderport extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    private final int maxTeleportDistance;

    // ----- CONSTRUCTORS -----

    public Enderport(int maxTeleportDistance) {
        super(.1);
        this.maxTeleportDistance = maxTeleportDistance;
    }

    // ----- EXECUTE -----

    @Override
    public boolean attemptAbility(CustomEntity entity, ISpawnParameters spawnParameters) {
        execute(entity, spawnParameters);
        return false;
    }

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {
        if (!customEntity.isAlive()) return;

        Collection<Entity> nearbyEntities = customEntity.getLocation().getNearbyEntities(2, 2.6, 2);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Arrow || entity instanceof SpectralArrow || entity instanceof Trident) {
                // Teleport entity
                teleport(customEntity);
                // TODO: Give temporary effects to entity
                // Kill entity
                entity.remove();
                return;
            }
        }
    }

    private void teleport(CustomEntity customEntity) {
        if (!customEntity.getSummonedEntity().isValid()) return;

        // Create variables
        int attempts = 0;
        Random r = new Random();
        Location currentLocation = customEntity.getLocation();
        Vector offset;

        // Get a position that is not inside solid ground
        do {
            offset = new Vector(r.nextInt(maxTeleportDistance), r.nextInt(maxTeleportDistance), r.nextInt(maxTeleportDistance));
            attempts++;
        } while (currentLocation.clone().add(offset).getBlock().isSolid() || attempts > 500); // Only try to teleport 500 times

        // Teleport entity
        Location newLocation = moveLocationToGround(currentLocation.clone().add(offset).toCenterLocation());
        customEntity.getSummonedEntity().teleport(newLocation);

        // Play teleport sounds
        World world = currentLocation.getWorld();
        world.playSound(currentLocation, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.HOSTILE, 1, 1); // Entity location
        world.playSound(newLocation, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.HOSTILE, 1, 1); // New entity location

        // Particles
        world.spawnParticle(Particle.PORTAL, currentLocation.clone().add(0, 1, 0), 50, .5, 1, .5); // Entity location
        world.spawnParticle(Particle.PORTAL, newLocation.clone().add(0, 1, 0), 50, .5, 1, .5); // New entity location
    }

    private Location moveLocationToGround(Location location) {
        while (!location.clone().add(0, -1, 0).getBlock().isSolid()) location.add(0, -1, 0);
        return location;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getAbilityName() {
        return "Enderport";
    }

    @Override
    public String getAbilityDescription() {
        return "Teleports a short distance when close to a projectile";
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public Enderport(Enderport target) {
        super(target);

        this.maxTeleportDistance = target.maxTeleportDistance;
    }

    @Override
    public Enderport clone() {
        return new Enderport(this);
    }
}
