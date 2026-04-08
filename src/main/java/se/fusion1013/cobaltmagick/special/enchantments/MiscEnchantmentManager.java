package se.fusion1013.cobaltmagick.special.enchantments;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.special.enchantments.arrow_rain.ArrowRainUtil;
import se.fusion1013.cobaltmagick.special.enchantments.cold_aura.ColdAuraState;
import se.fusion1013.cobaltmagick.special.enchantments.cold_aura.ColdAuraUtil;
import se.fusion1013.cobaltmagick.special.enchantments.cold_snap.ColdSnapUtil;
import se.fusion1013.cobaltmagick.special.enchantments.duel.DuelUtil;
import se.fusion1013.cobaltmagick.special.enchantments.grapple.GrappleUtil;
import se.fusion1013.cobaltmagick.special.enchantments.pull.PullUtil;

public class MiscEnchantmentManager extends Manager<CobaltMagick> implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        tryTriggerFrostbite(event);
        ColdAuraUtil.tryTrigger(event);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        ColdSnapUtil.onPlayerShoot(event);
        PullUtil.onPlayerShoot(event);
        GrappleUtil.onPlayerShoot(event);
        ArrowRainUtil.onEntityShootBow(event);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        ColdSnapUtil.onProjectileHit(event);
        PullUtil.onProjectileHit(event);
        GrappleUtil.onProjectileHit(event);
        ArrowRainUtil.onProjectileHitBlock(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        DuelUtil.onPlayerInteract(event);
    }

    private static void tryTriggerFrostbite(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (!event.isCritical()) return;

        if (event.getDamager() instanceof LivingEntity living) {
            ItemStack item = living.getEquipment() != null ? living.getEquipment().getItemInMainHand() : null;

            if (item == null) return;

            if (!item.hasItemMeta()) return;

            ItemMeta meta = item.getItemMeta();

            NamespacedKey key = new NamespacedKey("fusion1013", "frostbite");
            Enchantment frostbite = Enchantment.getByKey(key);

            if (frostbite == null || !meta.hasEnchant(frostbite)) return;

            int level = meta.getEnchantLevel(frostbite);

            target.setFreezeTicks(Math.min(target.getFreezeTicks() + 40 * level, 100));

            Location loc = target.getLocation().add(0, target.getHeight() / 2, 0);
            World world = target.getWorld();

            world.spawnParticle(Particle.SNOWFLAKE, loc, 40, 0.5, 0.5, 0.5, 0.02);
            world.spawnParticle(Particle.ITEM_SNOWBALL, loc, 25, 0.4, 0.4, 0.4, 0.01);
            world.spawnParticle(Particle.CLOUD, loc, 15, 0.3, 0.3, 0.3, 0.01);

            world.spawnParticle(Particle.SNOWFLAKE, loc, 20, 0.2, 0.8, 0.2, 0.01);

            world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.2f);
            world.playSound(loc, Sound.ENTITY_PLAYER_HURT_FREEZE, 1.0f, 1.0f);
            world.playSound(loc, Sound.BLOCK_POWDER_SNOW_HIT, 1.0f, 0.8f);

            double damageIncrease = 1 + 0.2 * level;

            ColdAuraState coldAuraState = ColdAuraState.get(living.getEquipment().getLeggings());
            if (coldAuraState != null && coldAuraState.active()) {
                damageIncrease += 0.2 * coldAuraState.level();
            }
            event.setDamage(event.getDamage() * damageIncrease);
        }


    }

    public MiscEnchantmentManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            ColdAuraUtil.tick();
            DuelUtil.tick();
        }, 0, 1);
    }

    @Override
    public void disable() {

    }
}
