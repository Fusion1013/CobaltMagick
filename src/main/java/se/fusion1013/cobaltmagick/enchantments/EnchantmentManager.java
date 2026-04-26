package se.fusion1013.cobaltmagick.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.enchantments.arrow_rain.ArrowRainUtil;
import se.fusion1013.cobaltmagick.enchantments.cleanse.CleanseUtil;
import se.fusion1013.cobaltmagick.enchantments.cold_aura.ColdAuraUtil;
import se.fusion1013.cobaltmagick.enchantments.cold_snap.ColdSnapUtil;
import se.fusion1013.cobaltmagick.enchantments.duel.DuelUtil;
import se.fusion1013.cobaltmagick.enchantments.frostbite.FrostbiteUtil;
import se.fusion1013.cobaltmagick.enchantments.grapple.GrappleUtil;
import se.fusion1013.cobaltmagick.enchantments.pull.PullUtil;

public class EnchantmentManager extends Manager<CobaltMagick> implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        FrostbiteUtil.onEntityDamageByEntity(event);
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

    public EnchantmentManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            ColdAuraUtil.tick();
            DuelUtil.tick();
            CleanseUtil.tick();
        }, 0, 1);
    }

    @Override
    public void disable() {

    }
}
