package se.fusion1013.cobaltmagick.special.misc;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ArrowRainManager extends Manager<CobaltMagick> implements Listener {

    private static final Random random = new Random();
    private static final NamespacedKey ARROW_RAIN_ENTITY_TAG = new NamespacedKey(CobaltMagick.getInstance(), "arrow_rain");
    private static final NamespacedKey ARROW_RAIN_FLAME_ENTITY_TAG = new NamespacedKey(CobaltMagick.getInstance(), "arrow_rain_flame");

    public ArrowRainManager(CobaltMagick plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerShootEvent(EntityShootBowEvent event) {
        Map<Enchantment, Integer> enchantments = event.getBow().getEnchantments();
        boolean hasEnchantment = false;
        int flameLevel = event.getBow().getEnchantmentLevel(Enchantment.FLAME);
        int level = 0;
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase("arrow_rain")) {
                hasEnchantment = true;
                level = enchantments.get(enchantment);
            }
        }
        if (!hasEnchantment) return;

        Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow arrow) {
            PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
            persistentDataContainer.set(ARROW_RAIN_ENTITY_TAG, PersistentDataType.INTEGER, level);
            if (flameLevel > 0) persistentDataContainer.set(ARROW_RAIN_FLAME_ENTITY_TAG, PersistentDataType.INTEGER, 1);
        }
    }

    @EventHandler
    public void onProjectileHitBlock(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer persistentDataContainer = projectile.getPersistentDataContainer();
        if (!persistentDataContainer.has(ARROW_RAIN_ENTITY_TAG)) return;

        int level = persistentDataContainer.get(ARROW_RAIN_ENTITY_TAG, PersistentDataType.INTEGER);

        start(projectile.getLocation(), 3 + level, level, 60, 8 + level * 2, persistentDataContainer.has(ARROW_RAIN_FLAME_ENTITY_TAG));
        projectile.remove();
    }

    public static void start(Location location, double radius, int arrowsPerTick, int durationInTicks, double height, boolean flame) {
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
                        world.spawn(arrow, Arrow.class, a -> {
                            if (flame) a.setFireTicks(20 * 60);
                        });
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

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }
}
