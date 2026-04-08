package se.fusion1013.cobaltmagick.special.enchantments.cold_snap;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;
import java.util.Random;

public class ColdSnapUtil {

    private static final Random random = new Random();
    public static final NamespacedKey COLD_SNAP_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "cold_snap");
    public static final NamespacedKey COLD_SNAP_ARROW_KEY = new NamespacedKey(CobaltMagick.getInstance(), "cold_snap");

    public static void onPlayerShoot(EntityShootBowEvent event) {
        ItemStack item = event.getBow();
        if (item == null) return;
        ItemMeta itemMeta = item.getItemMeta();

        Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(COLD_SNAP_ENCHANTMENT_KEY);
        if (enchantment == null) return;

        if (!itemMeta.hasEnchant(enchantment)) return;

        int level = itemMeta.getEnchantLevel(enchantment);

        Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow arrow) {
            PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
            persistentDataContainer.set(COLD_SNAP_ARROW_KEY, PersistentDataType.INTEGER, level);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    public static void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer persistent = projectile.getPersistentDataContainer();
        if (!persistent.has(COLD_SNAP_ARROW_KEY)) return;

        int level = persistent.getOrDefault(COLD_SNAP_ARROW_KEY, PersistentDataType.INTEGER, 1);

        Block hitBlock = event.getHitBlock();
        if (hitBlock != null) onProjectileHitBlock(event, level);

        Entity hitEntity = event.getHitEntity();
        if (hitEntity != null) onProjectileHitEntity(event, level);
    }

    private static void onProjectileHitBlock(ProjectileHitEvent event, int level) {
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), bt -> explodeArrow(event, level), 60 - 17L * level + random.nextInt(0, 3));
    }

    private static void onProjectileHitEntity(ProjectileHitEvent event, int level) {
        explodeArrow(event, level);
    }

    private static void explodeArrow(ProjectileHitEvent event, int level) {
        Projectile entity = event.getEntity();
        World world = entity.getWorld();
        Location loc = entity.getLocation();

        world.spawnParticle(Particle.SNOWFLAKE, loc, 40, 0.3, 0.3, 0.3, 0.02);
        world.spawnParticle(Particle.ITEM_SNOWBALL, loc, 25, 0.2, 0.2, 0.2, 0.01);
        world.spawnParticle(Particle.CLOUD, loc, 15, 0.3, 0.3, 0.3, 0.01);

        Collection<LivingEntity> entities = world.getNearbyLivingEntities(loc, 1.5, 1.5, 1.5);
        entities.forEach(e -> {
            e.damage(level * 2, DamageSource.builder(DamageType.FREEZE).withCausingEntity(entity).withDirectEntity(entity).build());
        });

        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.2f);
        world.playSound(loc, Sound.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 1.0f);
        world.playSound(loc, Sound.BLOCK_POWDER_SNOW_HIT, 1.0f, 0.8f);

        entity.remove();
    }

}
