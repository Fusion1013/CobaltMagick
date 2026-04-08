package se.fusion1013.cobaltmagick.special.enchantments.cold_aura;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

import java.util.*;

public class ColdAuraUtil {

    public static final int MAX_CHARGE = 50;
    private static final Map<UUID, BossBar> COLD_AURA_BOSS_BARS = new HashMap<>();

    // ##%%##%%## ON ENTITY HIT ##%%##%%## //

    public static void tryTrigger(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) return;

        trigger(event, equipment.getLeggings());
    }

    private static void trigger(EntityDamageByEntityEvent event, ItemStack item) {
        if (item == null) return;

        ColdAuraState state = ColdAuraState.get(item);
        if (state == null) return;

        state.setCharge(state.charge() + event.getDamage());
        state.setLastHit(System.currentTimeMillis());
        state.save(item);
    }

    // ##%%##%%## ON TICK ##%%##%%## //

    public static void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            tick(player);
        }
    }

    private static void tick(Player player) {
        EntityEquipment equipment = player.getEquipment();

        ColdAuraState state = ColdAuraState.get(equipment.getLeggings());
        if (state == null) return;

        boolean shouldReduce = System.currentTimeMillis() - state.lastHit() > 8000 || state.active();
        if (shouldReduce) {
            state.setCharge(state.charge() - 1 / (3f + state.level()));
        }

        BossBar bossBar = getBossBar(player);
        bossBar.progress(Math.clamp((float) state.charge() / MAX_CHARGE, 0, 1));
        if (state.charge() > 0) {
            player.showBossBar(bossBar);
            tryTriggerColdAuraEffects(player, state);
        } else {
            player.hideBossBar(bossBar);
            state.setActive(false);
        }

        if (state.charge() >= MAX_CHARGE) {
            if (!state.active()) {
                state.setActive(true);
                triggerOnActiveEffects(player);
            }
        }

        state.save(equipment.getLeggings());
    }

    private static void triggerOnActiveEffects(Player player) {
        World world = player.getWorld();
        Location center = player.getLocation();
        world.playSound(center, Sound.BLOCK_BEACON_AMBIENT, 1.2f, 0.6f);
        world.playSound(center, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 0.8f);
        world.playSound(center, Sound.BLOCK_POWDER_SNOW_STEP, 1.5f, 0.5f);
    }

    private static void tryTriggerColdAuraEffects(Player player, ColdAuraState state) {
        if (state.charge() <= 0) return;
        if (!state.active()) return;

        double range = Math.min(state.charge(), 16);

        World world = player.getWorld();
        Collection<Entity> entities = world.getNearbyEntities(player.getLocation(), range, range, range);
        entities.forEach(entity -> {
            if (entity.getUniqueId() == player.getUniqueId()) return;

            double distance = entity.getLocation().distanceSquared(player.getLocation());
            if (distance > 256) return;

            entity.setFreezeTicks(Math.min(entity.getFreezeTicks() + 1, 100));
        });

        List<Vector> vectors = ShapeUtils.generateSphere(range, (int) range * 10);
        vectors.forEach(v -> {
            world.spawnParticle(Particle.SNOWFLAKE, player.getLocation().clone().add(v), 1, .05, .05, .05, 0);
        });
    }

    private static BossBar getBossBar(Player player) {
        return COLD_AURA_BOSS_BARS.computeIfAbsent(player.getUniqueId(), uuid -> createBossBar());
    }

    private static BossBar createBossBar() {
        final Component name = Component.text("Cold Aura");
        return BossBar.bossBar(name, 0, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }
}
