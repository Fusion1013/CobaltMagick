package se.fusion1013.cobaltmagick.spell.projectile;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.ParticleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.wand.cast.ShotProperty;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProjectileTemplate implements IProjectileTemplate {

    private static final Random random = new Random();
    private final StringVariable internalName = new StringVariable("internal_name");
    private final ParticleVariable particle = new ParticleVariable("particle");
    private final Map<ShotProperty, Object> defaultProperties = new HashMap<>();

    public ProjectileTemplate(ConfigurationSection yaml) {
        internalName.load(yaml);
        particle.load(yaml);

        if (!yaml.contains("properties")) return;
        ConfigurationSection propertiesYaml = yaml.getConfigurationSection("properties");
        if (propertiesYaml == null) return;

        for (ShotProperty<?> property : ShotProperty.values) {
            if (!propertiesYaml.contains(property.getInternalName())) continue;

            Object value = propertiesYaml.get(property.getInternalName());
            defaultProperties.put(property, value);
        }
    }

    public ProjectileTemplate(JsonObject json) {
    }

    @Override
    public void display(Location location) {
        World world = location.getWorld();
        world.spawnParticle(particle.getParticle(), location, 5, .1, .1, .1, 0, null, true);
    }

    @Override
    public SpellProjectile create(LivingEntity caster, ShotState context) {
        context = new ShotState(context);

        // Velocity
        Vector velocity = caster.getEyeLocation().getDirection().clone().multiply(context.getPropertyOrDefault(ShotProperty.SPEED, 1D));
        applySpread(velocity, context.getPropertyOrDefault(ShotProperty.SPREAD, 0D));

        // Set default projectile properties, if they do not already exist
        for (Map.Entry<ShotProperty, Object> entry : defaultProperties.entrySet()) {
            ShotProperty property = entry.getKey();
            if (context.hasProperty(property)) continue;
            context.add(property, entry.getValue());
        }


        // Create projectile
        return new SpellProjectile(caster,
                caster.getEyeLocation().clone().add(caster.getEyeLocation().getDirection()),
                velocity,
                this,
                context);
    }

    private void applySpread(Vector velocity, double spread) {

        // Rotate vector around head
        Vector rightVector = getRightVector(velocity);
        Vector upVector = velocity.clone().rotateAroundAxis(rightVector, Math.toRadians(90));
//        velocity.rotateAroundAxis(upVector, Math.toRadians(directionModifier.getX()));

        // Rotate vector up/down
//        velocity.rotateAroundAxis(rightVector, Math.toRadians(directionModifier.getY()));

        // Apply spread
        velocity.rotateAroundAxis(upVector, Math.toRadians(2 * (random.nextDouble() - .5) * Math.max(0, spread)));
        velocity.rotateAroundAxis(rightVector, Math.toRadians(2 * (random.nextDouble() - .5) * Math.max(0, spread)));
    }

    private static Vector getRightVector(Vector vector) {
        Vector direction = vector.clone().normalize();
        return new Vector(direction.getZ(), 0, -direction.getX()).normalize();
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
