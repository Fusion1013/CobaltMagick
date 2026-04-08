package se.fusion1013.cobaltmagick.special.enchantments.grapple;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.special.enchantments.pull.PullUtil;

import java.util.Random;

public class GrappleUtil {

    private static final Random random = new Random();
    public static final NamespacedKey GRAPPLE_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "grapple");
    public static final NamespacedKey GRAPPLE_ARROW_KEY = new NamespacedKey(CobaltMagick.getInstance(), "grapple");

    public static void onPlayerShoot(EntityShootBowEvent event) {
        ItemStack item = event.getBow();
        if (item == null) return;
        ItemMeta itemMeta = item.getItemMeta();

        Registry<Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(GRAPPLE_ENCHANTMENT_KEY);
        if (enchantment == null) return;

        if (!itemMeta.hasEnchant(enchantment)) return;

        int level = itemMeta.getEnchantLevel(enchantment);

        Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow arrow) {
            PersistentDataContainer persistentDataContainer = arrow.getPersistentDataContainer();
            persistentDataContainer.set(GRAPPLE_ARROW_KEY, PersistentDataType.INTEGER, level);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    public static void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        PersistentDataContainer persistent = projectile.getPersistentDataContainer();
        if (!persistent.has(GRAPPLE_ARROW_KEY)) return;

        int level = persistent.getOrDefault(GRAPPLE_ARROW_KEY, PersistentDataType.INTEGER, 1);

        Block hitBlock = event.getHitBlock();
        if (hitBlock != null) onProjectileHitBlockVortex(event, level);
    }

    private static void onProjectileHitBlockVortex(ProjectileHitEvent event, int level) {
        if (event.getHitEntity() == event.getEntity().getShooter()) return;

        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

        int duration = 20 + (level * 10);

        PullUtil.pullTowards(level, shooter, projectile, duration);
    }

}
