package se.fusion1013.cobaltmagick.special.enchantments.arrow_rain;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ArrowRainUtil {

    private static final Random random = new Random();
    private static final NamespacedKey ARROW_RAIN_ENTITY_TAG = new NamespacedKey(CobaltMagick.getInstance(), "arrow_rain");

    // ##%%##%%## ON ENTITY SHOOT BOW ##%%##%%## //

    public static void onEntityShootBow(EntityShootBowEvent event) {
        Map<Enchantment, Integer> enchantments = event.getBow().getEnchantments();
        boolean hasEnchantment = false;

        int level = 0;

        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("arrow_rain")) {
                hasEnchantment = true;
                level = enchantments.get(enchantment);
            }
        }
        if (!hasEnchantment) return;

        Entity projectile = event.getProjectile();
        if (projectile instanceof Projectile arrow) {
            PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
            persistentDataContainer.set(ARROW_RAIN_ENTITY_TAG, PersistentDataType.INTEGER, level);
        }
    }

    // ##%%##%%## PROJECTILE HIT BLOCK ##%%##%%## //

    public static void onProjectileHitBlock(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer persistentDataContainer = projectile.getPersistentDataContainer();
        if (!persistentDataContainer.has(ARROW_RAIN_ENTITY_TAG)) return;

        int level = persistentDataContainer.getOrDefault(ARROW_RAIN_ENTITY_TAG, PersistentDataType.INTEGER, 0);
        if (level == 0) return;

        start(projectile, projectile.getLocation(), 3 + level, level, 60, 8 + level * 2);
        projectile.remove();
    }

    public static void start(Projectile parentProjectile, Location location, double radius, int arrowsPerTick, int durationInTicks, double height) {

        parentProjectile.getPersistentDataContainer().remove(ARROW_RAIN_ENTITY_TAG);

        int waitTime = durationInTicks / 3;
        Location arrowLocation = location.clone().add(0, height, 0);
        World world = arrowLocation.getWorld();

        new BukkitRunnable() {

            private int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationInTicks) {
                    cancel();
                    return;
                }

                if (ticks < waitTime) {
                    double t = ticks / (double) waitTime;
                    List<Vector> points = ShapeUtils.generateCircle(radius * t, 10, true);
                    displayParticles(location, points);
                } else {
                    List<Vector> points = ShapeUtils.generateCircle(radius, 10, true);
                    displayParticles(location, points);

                    for (int i = 0; i < arrowsPerTick; i++) {
                        Location arrow = arrowLocation.clone().add(randomPointInCircle(0, 0, radius));

                        Entity copy = parentProjectile.copy(arrow);
                        copy.setVelocity(new Vector(0, -1, 0));

                        if (copy instanceof AbstractArrow copiedProjectile) {
                            copiedProjectile.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        }
                        world.spawnParticle(Particle.CRIT, arrow, 4, .1, .1, .1, 0.1);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(CobaltMagick.getInstance(), 0, 1);
    }

    public static Vector randomPointOnCircle(double centerX, double centerY, double radius) {
        double angle = 2 * Math.PI * random.nextDouble();

        double x = centerX + radius * Math.cos(angle);
        double y = centerY + radius * Math.sin(angle);

        return new Vector(x, 0, y);
    }

    public static Vector randomPointInCircle(double centerX, double centerY, double radius) {
        double angle = 2 * Math.PI * random.nextDouble();

        double r = radius * Math.sqrt(random.nextDouble());

        double x = centerX + r * Math.cos(angle);
        double y = centerY + r * Math.sin(angle);

        return new Vector(x, 0, y);
    }

    private static void displayParticles(Location location, List<Vector> points) {
        World world = location.getWorld();
        for (Vector v : points) {
            Location particleLocation = location.clone().add(v);
            world.spawnParticle(Particle.CRIT, particleLocation, 1, 0, 0, 0, 0);
        }
    }

}
