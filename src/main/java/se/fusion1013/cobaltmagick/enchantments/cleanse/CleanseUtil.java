package se.fusion1013.cobaltmagick.enchantments.cleanse;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.util.Vector;

public class CleanseUtil {

    public static void tick() {
        Bukkit.getOnlinePlayers().forEach(CleanseUtil::tick);
    }

    private static void tick(Player player) {
        ItemStack chestplateItem = player.getEquipment().getChestplate();
        CleanseState state = CleanseState.get(chestplateItem);
        if (state == null) return;

        tryIncrementFromPotions(player, state);
        tryTrigger(player, state);

        state.save(player.getEquipment().getChestplate());
    }

    private static void tryTrigger(Player player, CleanseState state) {
        if (state.getStoredPotionLevelsBeneficial() > 10) {
            triggerBeneficialEffect(player);
            state.setStoredPotionLevelsBeneficial(0);
        }
        if (state.getStoredPotionLevelsNeutral() > 10) {
            triggerNeutralEffect(player);
            state.setStoredPotionLevelsNeutral(0);
        }
        if (state.getStoredPotionLevelsHarmful() > 10) {
            triggerHarmfulEffect(player);
            state.setStoredPotionLevelsHarmful(0);
        }
    }

    private static void tryIncrementFromPotions(Player player, CleanseState state) {
        player.getActivePotionEffects().forEach(p -> {
            state.increment(p.getType().getCategory(), p.getAmplifier() + 1);
            displayPotionConsumeEffect(player, p.getType());
        });
        player.clearActivePotionEffects();
    }

    private static void displayPotionConsumeEffect(Player player, PotionEffectType type) {
        Location loc = player.getLocation().add(0, 1, 0);
        World world = player.getWorld();

        Particle particle;
        Sound sound;
        float pitch;

        // Choose visuals based on category
        if (type.getCategory() == PotionEffectTypeCategory.HARMFUL) {
            particle = Particle.SMOKE;
            sound = Sound.ENTITY_WITHER_HURT;
            pitch = 0.6f;
        } else if (type.getCategory() == PotionEffectTypeCategory.BENEFICIAL) {
            particle = Particle.HAPPY_VILLAGER;
            sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            pitch = 1.4f;
        } else {
            particle = Particle.ENCHANT;
            sound = Sound.BLOCK_AMETHYST_BLOCK_RESONATE;
            pitch = 1.0f;
        }

        // Particle burst
        world.spawnParticle(particle, loc, 25, 0.5, 0.8, 0.5, 0.1);

        // Subtle spiral effect (optional but looks nice)
        for (double t = 0; t < Math.PI * 2; t += Math.PI / 8) {
            double x = Math.cos(t) * 0.7;
            double z = Math.sin(t) * 0.7;
            world.spawnParticle(particle, loc.clone().add(x, 0.2, z), 1, 0, 0, 0, 0);
        }

        // Sound feedback
        world.playSound(loc, sound, 0.8f, pitch);
    }

    private static void triggerHarmfulEffect(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();

        // AoE damage + debuffs
        for (Entity entity : world.getNearbyEntities(loc, 6, 6, 6)) {
            if (entity instanceof LivingEntity target && entity != player) {

                target.damage(6.0, player); // direct damage
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 12, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 6, 0));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 24, 0));
            }
        }

        world.spawnParticle(Particle.LARGE_SMOKE, loc, 50, 1, 1, 1, 0.05);
        world.playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.8f);
    }

    private static void triggerBeneficialEffect(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();

        for (Entity entity : world.getNearbyEntities(loc, 8, 8, 8)) {
            if (entity instanceof Player ally && entity != player) {

                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 240, 1));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 160, 0));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1));
            }
        }

        world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 50, 1.5, 1.5, 1.5, 0.2);
        world.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.2f);
    }

    private static void triggerNeutralEffect(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();

        // Radial knockback (control space)
        for (Entity entity : world.getNearbyEntities(loc, 6, 6, 6)) {
            if (entity instanceof LivingEntity && entity != player) {
                Vector knockback = entity.getLocation().toVector()
                        .subtract(loc.toVector())
                        .normalize()
                        .multiply(1.4)
                        .setY(0.4);

                entity.setVelocity(knockback);
            }
        }

        // Temporary "utility zone" using particles (visual + optional future logic hook)
        world.spawnParticle(Particle.CLOUD, loc, 80, 2, 1, 2, 0.05);
        world.spawnParticle(Particle.CRIT, loc, 40, 1, 1, 1, 0.2);

        world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.1f);
    }

}
