package se.fusion1013.cobaltmagick.enchantments.duel;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DuelUtil {

    private static final NamespacedKey DUEL_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "duel");
    private static final List<DuelState> ACTIVE_DUELS = new ArrayList<>();
    private static final Random random = new Random();

    // ##%%##%%## START DUEL ##%%##%%## //

    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem.isEmpty() || mainHandItem.getType() == Material.AIR) return;

        ItemMeta itemMeta = mainHandItem.getItemMeta();

        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(DUEL_ENCHANTMENT_KEY);

        if (enchantment == null) return;

        int level = itemMeta.getEnchantLevel(enchantment);
        if (level == 0) return;

        for (DuelState duelState : ACTIVE_DUELS) {
            if (duelState.owner() == player || duelState.victim() == player) return;
        }

        // Find player to duel with
        Entity targetPlayer = getEntityLookingAt(player, 12 + level * 2);
        if (targetPlayer == null) return;

        player.swingMainHand();

        startDuel(player, targetPlayer, level);
    }

    private static void startDuel(Entity owner, Entity victim, int level) {
        ACTIVE_DUELS.add(
                new DuelState(owner,
                        victim,
                        level,
                        System.currentTimeMillis(),
                        BossBar.bossBar(Component.text("Duel - " + owner.getName() + " VS " + victim.getName()), 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS)
                )
        );

        try {
            if (owner instanceof Player ownerPlayer) {
                playDuelStartSounds(ownerPlayer);
                CobaltMagick.getGlowingEntities().setGlowing(victim, ownerPlayer, ChatColor.RED);
            }
            if (victim instanceof Player victimPlayer) {
                playDuelStartSounds(victimPlayer);
                CobaltMagick.getGlowingEntities().setGlowing(owner, victimPlayer, ChatColor.RED);
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }

    }

    private static void playDuelStartSounds(Player player) {
        player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 1, 1);
    }

    // ##%%##%%## TICK ##%%##%%## //

    public static void tick() {
        for (int i = ACTIVE_DUELS.size() - 1; i >= 0; i--) {
            boolean isActive = tick(ACTIVE_DUELS.get(i));
            if (!isActive) {
                stopDuel(ACTIVE_DUELS.get(i));
                ACTIVE_DUELS.remove(i);
            }
        }
    }

    private static boolean tick(DuelState duelState) {
        // Check if any of the duelers are dead
        if (duelState.owner().isDead()) return false;
        if (duelState.victim().isDead()) return false;

        double ownerHeight = duelState.owner().getHeight();
        double victimHeight = duelState.victim().getHeight();

        // Bossbar
        int durationSeconds = duelState.level() * 60 + 30 * duelState.level();
        long timeElapsedSeconds = (System.currentTimeMillis() - duelState.startTimestamp()) / 1000;
        float progress = Math.clamp(timeElapsedSeconds / (float) durationSeconds, 0, 1);

        duelState.bossBar().progress(progress);

        if (duelState.owner() instanceof Player ownerPlayer) ownerPlayer.showBossBar(duelState.bossBar());
        if (duelState.victim() instanceof Player victimPlayer) victimPlayer.showBossBar(duelState.bossBar());

        // Particles
        float maxDistance = 24 - duelState.level() - progress * 8;
        List<Vector> sphereVectors = ShapeUtils.generateSphere(maxDistance, 100);
        List<Vector> lineVectors = ShapeUtils.generateLine(
                duelState.owner().getLocation().toVector().clone().add(new Vector(0, ownerHeight / 2f, 0)),
                duelState.victim().getLocation().toVector().clone().add(new Vector(0, victimHeight / 2f, 0)), 1
        );

        displayParticles(Particle.FLAME, false, duelState, sphereVectors, ownerHeight / 2f);
        displayParticles(Particle.CRIT, true, duelState, lineVectors, ownerHeight / 2f);

        // Pull victim
        pullEntityTowards(duelState.victim(), duelState.owner(), maxDistance, 1);

        return progress < 1;
    }

    private static void stopDuel(DuelState duelState) {
        try {
            if (duelState.owner() instanceof Player ownerPlayer) {
                ownerPlayer.hideBossBar(duelState.bossBar());
                ownerPlayer.playSound(ownerPlayer, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1, 1);
                CobaltMagick.getGlowingEntities().unsetGlowing(duelState.victim(), ownerPlayer);
            }
            if (duelState.victim() instanceof Player victimPlayer) {
                victimPlayer.hideBossBar(duelState.bossBar());
                victimPlayer.playSound(victimPlayer, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1, 1);
                CobaltMagick.getGlowingEntities().unsetGlowing(duelState.owner(), victimPlayer);
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private static void displayParticles(Particle particle, boolean doRandom, DuelState duelState, List<Vector> vectors, double heightOffset) {
        for (Vector vector : vectors) {
            Location location = duelState.owner().getLocation().clone().add(vector).add(0, heightOffset, 0);
            if (random.nextInt(0, 8) != 0 && doRandom) continue;

            if (duelState.owner() instanceof Player p1) {
                p1.spawnParticle(particle, location, 1, 0.1, 0.1, 0.1, 0);
            }

            if (duelState.victim() instanceof Player p2) {
                p2.spawnParticle(particle, location, 1, 0.1, 0.1, 0.1, 0);
            }
        }
    }

    // ##%%##%%## UTIL ##%%##%%## //

    private static Entity getEntityLookingAt(Player player, double maxRange) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                maxRange,
                entity -> entity != player
        );

        if (result == null) return null;

        return result.getHitEntity();
    }

    private static void pullEntityTowards(Entity victim, Entity target, double radius, double strength) {
        if (victim == null || target == null) return;
        if (!victim.getWorld().equals(target.getWorld())) return;

        Location vLoc = victim.getLocation();
        Location tLoc = target.getLocation();

        double distance = vLoc.distance(tLoc);

        if (distance <= radius) return;

        Vector direction = tLoc.toVector().subtract(vLoc.toVector()).normalize();

        double pullStrength = strength * (distance - radius);

        pullStrength = Math.min(pullStrength, 1.5);

        Vector velocity = direction.multiply(pullStrength);
        velocity.setY(victim.getVelocity().getY() * 0.5 + velocity.getY());

        victim.setVelocity(velocity);
    }

}
