package se.fusion1013.cobaltmagick.enchantments.frostbite;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltmagick.enchantments.cold_aura.ColdAuraState;

public class FrostbiteUtil {

    private static final NamespacedKey FROSTBITE_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "frostbite");

    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (!event.isCritical()) return;

        if (!(event.getDamager() instanceof LivingEntity living)) return;

        ItemStack item = living.getEquipment() != null ? living.getEquipment().getItemInMainHand() : null;

        if (item == null) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();

        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment frostbiteEnchantment = registry.get(FROSTBITE_ENCHANTMENT_KEY);

        if (frostbiteEnchantment == null || !meta.hasEnchant(frostbiteEnchantment)) return;

        int level = meta.getEnchantLevel(frostbiteEnchantment);

        target.setFreezeTicks(Math.min(target.getFreezeTicks() + 40 * level, 100));

        Location loc = target.getLocation().add(0, target.getHeight() / 2, 0);
        World world = target.getWorld();

        displayFrostbiteParticles(world, loc);

        double damageIncrease = 1 + 0.2 * level;

        ColdAuraState coldAuraState = ColdAuraState.get(living.getEquipment().getLeggings());
        if (coldAuraState != null && coldAuraState.active()) {
            damageIncrease += 0.2 * coldAuraState.level();
        }
        event.setDamage(event.getDamage() * damageIncrease);
    }

    private static void displayFrostbiteParticles(World world, Location loc) {
        world.spawnParticle(Particle.SNOWFLAKE, loc, 40, 0.5, 0.5, 0.5, 0.02);
        world.spawnParticle(Particle.ITEM_SNOWBALL, loc, 25, 0.4, 0.4, 0.4, 0.01);
        world.spawnParticle(Particle.CLOUD, loc, 15, 0.3, 0.3, 0.3, 0.01);

        world.spawnParticle(Particle.SNOWFLAKE, loc, 20, 0.2, 0.8, 0.2, 0.01);

        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.2f);
        world.playSound(loc, Sound.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 1.0f);
        world.playSound(loc, Sound.BLOCK_POWDER_SNOW_HIT, 1.0f, 0.8f);
    }

}
