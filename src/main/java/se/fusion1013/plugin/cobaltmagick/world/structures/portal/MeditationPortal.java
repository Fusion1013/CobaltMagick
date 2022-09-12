package se.fusion1013.plugin.cobaltmagick.world.structures.portal;

import org.apache.commons.io.input.AbstractCharacterFilterReader;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeditationPortal extends AbstractMagickPortal implements Runnable, Listener {

    // ----- VARIABLES -----

    private BukkitTask task;

    private static final ParticleGroup SMALL_PARTICLE = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.SPELL_WITCH)
                    .setCount(1)
                    .setSpeed(.6)
                    .setDensity(2)
                    .setRadius(.1)
                    .build())
            .build();

    private static final ParticleGroup SMALL_PORTAL_PARTICLE = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.SPELL_WITCH)
                    .setCount(1)
                    .setSpeed(0)
                    .setDensity(1)
                    .setRadius(.5)
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setExtra(new Particle.DustTransition(Color.PURPLE, Color.MAROON, 1))
                    .setCount(3)
                    .setSpeed(.8)
                    .setDensity(4)
                    .setRadius(1)
                    .build())
            .build();

    private static final float MEDITATION_DISTANCE = 10f;
    private final Map<UUID, Location> nearbyPlayerLocations = new HashMap<>();
    private static final int tickDelay = 2;
    private static final int meditationTime = 20*20/tickDelay; // Player has to meditate for 20 seconds

    private int currentTick = 0;

    // ----- CONSTRUCTORS -----

    public MeditationPortal(Location portalLocation, Location exitLocation) {
        super(portalLocation, exitLocation);
    }

    public MeditationPortal(Location portalLocation, Location exitLocation, UUID uuid) {
        super(portalLocation, exitLocation, uuid);
    }

    // ----- PORTAL TICK -----

    @Override
    public void run() {
        // Check if portal is active, if so, portal tick
        if (currentTick >= meditationTime) {
            tickPortal();
            return;
        }

        boolean playerMeditating = true;
        boolean playerNearby = false;

        // Check if all players nearby are meditating
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != portalLocation.getWorld()) continue;
            if (player.getLocation().distanceSquared(portalLocation) > MEDITATION_DISTANCE*MEDITATION_DISTANCE) continue;

            playerNearby = true;

            Location oldPlayerLocation = nearbyPlayerLocations.get(player.getUniqueId());
            if (oldPlayerLocation == null) oldPlayerLocation = player.getLocation();

            // Update player location
            nearbyPlayerLocations.put(player.getUniqueId(), player.getLocation());

            if (!oldPlayerLocation.equals(player.getLocation())) {
                playerMeditating = false;
                break;
            }
        }

        // Update tick
        if (playerMeditating && playerNearby) {
            currentTick++;

            // Display small particle
            if (currentTick > 20) SMALL_PARTICLE.display(portalLocation);
            if (currentTick > 40) SMALL_PORTAL_PARTICLE.display(portalLocation);
        }
        else currentTick = 0;

        // Activates on first activation
        if (currentTick >= meditationTime) {
            MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);
            if (advancementManager == null) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld() != portalLocation.getWorld()) continue;
                if (player.getLocation().distanceSquared(portalLocation) > MEDITATION_DISTANCE * MEDITATION_DISTANCE) continue;

                Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> advancementManager.grantAdvancement(player, "progression", "meditation_cube"), 10);
            }
        }
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        super.onLoad();

        // TODO: Play portal emerge effects

        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 20, tickDelay);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.task != null) this.task.cancel();
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getObjectIdentifier() {
        return "meditation_portal";
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MeditationPortal(MeditationPortal target) {
        super(target);

        this.task = target.task;
        this.currentTick = target.currentTick;
    }

    @Override
    public MeditationPortal clone() {
        return new MeditationPortal(this);
    }
}
