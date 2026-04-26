package se.fusion1013.cobaltmagick.enchantments.pull;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Random;

public class PullUtil {

    private static final Random random = new Random();
    public static final NamespacedKey PULL_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "pull");
    public static final NamespacedKey PULL_ARROW_KEY = new NamespacedKey(CobaltMagick.getInstance(), "pull");

    public static void onPlayerShoot(EntityShootBowEvent event) {
        ItemStack item = event.getBow();
        if (item == null) return;
        ItemMeta itemMeta = item.getItemMeta();

        Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(PULL_ENCHANTMENT_KEY);
        if (enchantment == null) return;

        if (!itemMeta.hasEnchant(enchantment)) return;

        int level = itemMeta.getEnchantLevel(enchantment);

        Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow arrow) {
            PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
            persistentDataContainer.set(PULL_ARROW_KEY, PersistentDataType.INTEGER, level);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    public static void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer persistent = projectile.getPersistentDataContainer();
        if (!persistent.has(PULL_ARROW_KEY)) return;

        int level = persistent.getOrDefault(PULL_ARROW_KEY, PersistentDataType.INTEGER, 1);

        Entity hitEntity = event.getHitEntity();
        if (hitEntity != null) onProjectileHitEntityVortex(event, level);
    }

    private static void onProjectileHitEntity(ProjectileHitEvent event, int level) {
        if (!(event.getHitEntity() instanceof LivingEntity target)) return;
        if (event.getHitEntity() == event.getEntity().getShooter()) return;

        Projectile projectile = event.getEntity();

        if (!(projectile.getShooter() instanceof Entity shooter)) return;

        Location targetLoc = target.getLocation();
        Location shooterLoc = shooter.getLocation();

        Vector pullVector = shooterLoc.toVector().subtract(targetLoc.toVector());

        double strength = 1.3 + (0.7 * level);
        pullVector.normalize().multiply(strength);

        pullVector.setY(pullVector.getY() + 0.1 * level);

        target.setVelocity(target.getVelocity().add(pullVector));

        World world = target.getWorld();
        world.spawnParticle(Particle.PORTAL, targetLoc, 20, 0.3, 0.5, 0.3, 0.1);
        world.playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.4f);
    }

    private static void onProjectileHitEntityVortex(ProjectileHitEvent event, int level) {
        if (!(event.getHitEntity() instanceof LivingEntity target)) return;
        if (event.getHitEntity() == event.getEntity().getShooter()) return;

        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Entity shooter)) return;

        int duration = 20 + (level * 10);

        pullTowards(level, target, shooter, duration);
    }

    public static void pullTowards(int level, LivingEntity target, Entity shooter, int duration) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ > duration || target.isDead() || !shooter.isValid()) {
                    cancel();
                    return;
                }

                Location targetLoc = target.getLocation();
                Location shooterLoc = shooter.getLocation();

                Vector pull = shooterLoc.toVector().subtract(targetLoc.toVector());
                double distance = pull.length();

                if (distance < 1.5) {
                    cancel();
                    return;
                }

                pull.normalize();

                double strength = 0.08 + (0.03 * level);
                pull.multiply(strength);

                pull.setY(pull.getY() + 0.02 * level);

                target.setVelocity(target.getVelocity().add(pull));

                World world = target.getWorld();
                world.spawnParticle(Particle.PORTAL, targetLoc, 8, 0.3, 0.4, 0.3, 0.05);
                world.spawnParticle(Particle.ENCHANT, targetLoc, 5, 0.2, 0.3, 0.2, 0.01);

                if (ticks % 10 == 0) {
                    world.playSound(targetLoc, Sound.ENTITY_ENDERMAN_AMBIENT, 0.5f, 1.5f);
                }
            }
        }.runTaskTimer(CobaltMagick.getInstance(), 0L, 1L);
    }
}
