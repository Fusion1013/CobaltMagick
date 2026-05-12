package se.fusion1013.cobaltmagick.alchemy.cauldron.effect;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.shape.ShapeUtils;

import java.util.List;
import java.util.function.Consumer;

public class CauldronEffectUtil {

    public static void animateCauldron(Location location, Consumer<Location> onDone, Plugin plugin) {
        Block block = location.getBlock();

        if (block.getType() != Material.CAULDRON
                && block.getType() != Material.WATER_CAULDRON
                && block.getType() != Material.LAVA_CAULDRON
                && block.getType() != Material.POWDER_SNOW_CAULDRON) {
            return;
        }

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.2, 0.5);

        new BukkitRunnable() {

            double radius = 0.6;
            double height = 0;
            double angle = 0;
            int ticks = 0;
            final int maxTicks = 30; // 30 ticks = 1.5 seconds

            @Override
            public void run() {

                if (ticks >= maxTicks) {

                    onDone.accept(center.clone().add(0, 2, 0));

                    cancel();
                    return;
                }

                // Spiral effect (multiple particles per tick for smoothness)
                for (int i = 0; i < 3; i++) {
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLoc = center.clone().add(x, height, z);

                    world.spawnParticle(Particle.PORTAL, particleLoc, 2, 0, 0, 0, 0);
                    world.spawnParticle(Particle.ENCHANT, particleLoc, 1, 0, 0, 0, 0);

                    angle += Math.PI / 8;
                    height += 0.03;
                    radius -= 0.015;
                }

                // Subtle ambient particles inside cauldron
                world.spawnParticle(Particle.WITCH, center, 3, 0.3, 0.1, 0.3, 0.01);

                ticks++;
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void displayCraftingEffect(Location location) {
        Block block = location.getBlock();

        if (block.getType() != Material.CAULDRON
                && block.getType() != Material.WATER_CAULDRON
                && block.getType() != Material.LAVA_CAULDRON
                && block.getType() != Material.POWDER_SNOW_CAULDRON) {
            return;
        }

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.3, 0.5);

        long time = System.currentTimeMillis();

        // Time-based animation values
        double seconds = time / 1000.0;
        double rotation = seconds * 2; // rotation speed
        double pulse = (Math.sin(seconds * 3) + 1) / 2; // 0 -> 1 pulse

        double baseRadius = 0.8;
        double radius = baseRadius + (pulse * 0.15);

        int points = 16;

        // Rotating particle ring
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i + rotation;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, 0, z);

            world.spawnParticle(
                    Particle.ENCHANT,
                    particleLoc,
                    1,
                    0, 0.05, 0,
                    0
            );
        }

        // Vertical energy column
        for (int i = 0; i < 5; i++) {
            double height = i * 0.25;
            world.spawnParticle(
                    Particle.PORTAL,
                    center.clone().add(0, height, 0),
                    2,
                    0.2, 0.05, 0.2,
                    0.01
            );
        }

        // Occasional magical spark burst
        if (Math.random() < 0.3) {
            world.spawnParticle(
                    Particle.WITCH,
                    center.clone().add(0, 0.2, 0),
                    5,
                    0.4, 0.2, 0.4,
                    0.02
            );
        }

        // Subtle ambient smoke
        world.spawnParticle(
                Particle.SMOKE,
                center,
                3,
                0.3, 0.1, 0.3,
                0.01
        );
    }

    public static void displayEarthVeinEffect(Location location) {

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.1, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        // Slow heavy rotation
        double rotation = seconds * 0.6;

        // Large radius
        double baseRadius = 4.0;
        double radius = baseRadius + Math.sin(seconds * 0.8) * 0.3;

        int points = 32;

        // === Large rotating dust ring ===
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i + rotation;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location ringLoc = center.clone().add(x, 0, z);

            BlockData blockData = Math.random() < 0.5 ? Bukkit.createBlockData(Material.DIRT) : Bukkit.createBlockData(Material.STONE);

            world.spawnParticle(
                    Particle.FALLING_DUST,
                    ringLoc,
                    2,
                    0.1, 0.05, 0.1,
                    0,
                    blockData
            );
        }

        // === Rising debris chunks around cauldron ===
        for (int i = 0; i < 6; i++) {
            double angle = Math.random() * Math.PI * 2;
            double distance = 1.0 + Math.random() * 1.5;

            double x = distance * Math.cos(angle);
            double z = distance * Math.sin(angle);

            Location debrisLoc = center.clone().add(x, 0.2, z);

            world.spawnParticle(
                    Particle.BLOCK_CRUMBLE,
                    debrisLoc,
                    3,
                    0.2, 0.3, 0.2,
                    0.1,
                    Bukkit.createBlockData(Material.DEEPSLATE)
            );
        }

        // === Expanding ground pulse ===
        double pulseProgress = (seconds % 1.5) / 1.5; // loop every 2.5 sec
        double pulseRadius = pulseProgress * 4.5;

        for (int i = 0; i < 24; i++) {
            double angle = (2 * Math.PI / 24) * i;

            double x = pulseRadius * Math.cos(angle);
            double z = pulseRadius * Math.sin(angle);

            Location pulseLoc = center.clone().add(x, 0.05, z);

            world.spawnParticle(
                    Particle.BLOCK,
                    pulseLoc,
                    1,
                    0, 0, 0,
                    0,
                    Bukkit.createBlockData(Material.MUD)
            );
        }
    }

    public static void displayAirVeinEffect(Location location) {

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.2, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        // Fast rotation for wind
        double rotation = seconds * 2.5;

        // Light pulsing radius
        double baseRadius = 4.5;
        double radius = baseRadius + Math.sin(seconds * 2) * 0.4;

        int points = 12;

        // === Large Fast Wind Ring ===
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i + rotation;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location windLoc = center.clone().add(x, 0.1, z);

            world.spawnParticle(
                    Particle.CLOUD,
                    windLoc,
                    1,
                    0.15, 0.05, 0.15,
                    0.02
            );
        }

        // === Vertical Spiral Wind Column ===
        int spiralPoints = 16;
        double spiralHeight = 2.5;

        List<Vector> spiralVectors = ShapeUtils.getAnimatedCircle(radius + 1, spiralPoints, 1, 1, .5);

        for (Vector sp : spiralVectors) {
            Location spiralLoc = center.clone().add(sp.getX(), sp.getY() + 1, sp.getZ());
            world.spawnParticle(
                    Particle.END_ROD,
                    spiralLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static void displayFireVeinEffect(Location location) {

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.2, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        // Fast chaotic rotation
        double rotation = seconds * 3.0;

        // Large aggressive radius
        double baseRadius = 4.8;
        double radius = baseRadius + Math.sin(seconds * 3) * 0.5;

        int points = 40;

        // === Large Rotating Flame Ring ===
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i + rotation;

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location flameLoc = center.clone().add(x, 0.1, z);

            world.spawnParticle(
                    Particle.FLAME,
                    flameLoc,
                    1,
                    0.05, 0.05, 0.05,
                    0.02
            );
        }

        // === Rising Flame Vortex ===
        int spiralPoints = 20;
        double spiralHeight = 4.5;

        for (int i = 0; i < spiralPoints; i++) {

            double progress = (double) i / spiralPoints;
            double height = progress * spiralHeight;

            double spiralAngle = rotation * 0.7 + progress * 8;

            double spiralRadius = 1.2 + progress * 0.8;

            double x = spiralRadius * Math.cos(spiralAngle);
            double z = spiralRadius * Math.sin(spiralAngle);

            Location vortexLoc = center.clone().add(x, height, z);

            world.spawnParticle(
                    Particle.SMALL_FLAME,
                    vortexLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // === Ember Scatter ===
        double randomAngle = Math.random() * Math.PI * 2;
        double emberDistance = Math.random() * 2.5;

        double emberX = emberDistance * Math.cos(randomAngle);
        double emberZ = emberDistance * Math.sin(randomAngle);

        Location emberLoc = center.clone().add(emberX, 0.5 + Math.random() * 1.5, emberZ);

        world.spawnParticle(
                Particle.LAVA,
                emberLoc,
                1,
                0.1, 0.2, 0.1,
                0
        );

        // === Heat Shimmer Layer ===
        for (int i = 0; i < 10; i++) {

            double angle = Math.random() * Math.PI * 2;
            double distance = Math.random() * 3.5;

            double x = distance * Math.cos(angle);
            double z = distance * Math.sin(angle);

            Location smokeLoc = center.clone().add(x, 0.1, z);

            world.spawnParticle(
                    Particle.SMOKE,
                    smokeLoc,
                    1,
                    0.1, 0.2, 0.1,
                    0.01
            );
        }
    }

    public static void displayWaterVeinEffect(Location location) {

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.2, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        // Smooth rotation
        double rotation = seconds * 1.5;

        double outerRadius = 3.8 + Math.sin(seconds * 1.2) * 0.3;
        double innerRadius = 1.8 + Math.cos(seconds * 1.5) * 0.2;

        int outerPoints = 40;
        int innerPoints = 24;

        // === Dripping Droplets ===
        for (int i = 0; i < 6; i++) {

            double randomAngle = Math.random() * Math.PI * 2;
            double distance = Math.random() * 2.5;

            double x = distance * Math.cos(randomAngle);
            double z = distance * Math.sin(randomAngle);

            Location dripLoc = center.clone().add(x, 3.5 + Math.random(), z);

            world.spawnParticle(
                    Particle.DRIPPING_WATER,
                    dripLoc,
                    1,
                    0, 0, 0,
                    0
            );
            world.spawnParticle(
                    Particle.CLOUD,
                    dripLoc.clone().add(new Vector(0, .2, 0)),
                    1,
                    0, 0, 0,
                    0
            );
        }

        // === Periodic Expanding Wave Pulse ===
        double pulseCycle = 3.0;
        double progress = (seconds % pulseCycle) / pulseCycle;
        double pulseRadius = progress * 4.0;

        for (int i = 0; i < 30; i++) {

            double angle = (2 * Math.PI / 30) * i;

            double x = pulseRadius * Math.cos(angle);
            double z = pulseRadius * Math.sin(angle);

            Location pulseLoc = center.clone().add(x, 0.05, z);

            world.spawnParticle(
                    Particle.SPLASH,
                    pulseLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    public static void displayAetherVeinEffect(Location location) {

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.3, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        double rotation = seconds * 0.8;

        /*
         * === 1. Floating Celestial Halo (above cauldron) ===
         */
        double haloRadius = 2.3;
        int haloPoints = 12;

        for (int i = 0; i < haloPoints; i++) {

            double angle = (2 * Math.PI / haloPoints) * i + rotation;

            double x = haloRadius * Math.cos(angle);
            double z = haloRadius * Math.sin(angle);

            Location haloLoc = center.clone().add(x, 4.2, z);

            world.spawnParticle(
                    Particle.END_ROD,
                    haloLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }

        /*
         * === 2. Arcane Sigil (Hexagram Star Pattern) ===
         */
        double sigilRadius = 2.4;

        for (int i = 0; i < 6; i++) {

            double angle1 = rotation + (Math.PI / 3) * i;
            double angle2 = rotation + (Math.PI / 3) * (i + 2);

            double x1 = sigilRadius * Math.cos(angle1);
            double z1 = sigilRadius * Math.sin(angle1);

            double x2 = sigilRadius * Math.cos(angle2);
            double z2 = sigilRadius * Math.sin(angle2);

            // Draw faint line between star points
            for (double t = 0; t < 1; t += 0.1) {

                double x = x1 + (x2 - x1) * t;
                double z = z1 + (z2 - z1) * t;

                Location sigilLoc = center.clone().add(x, 0.05, z);

                world.spawnParticle(
                        Particle.ENCHANT,
                        sigilLoc,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }

        // === Vertical Spiral Wind Column ===
        int spiralPoints = 16;

        List<Vector> spiralVectors = ShapeUtils.getAnimatedCircle(6, spiralPoints, 0.5, 0.001, .3);

        for (Vector sp : spiralVectors) {
            Location spiralLoc = center.clone().add(sp.getX(), sp.getY(), sp.getZ());
            world.spawnParticle(
                    Particle.END_ROD,
                    spiralLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }

        /*
         * === 4. Orbiting Light Shards ===
         */
        for (int i = 0; i < 3; i++) {

            double offset = i * (Math.PI * 2 / 3);
            double angle = rotation * 1.5 + offset;

            double x = 0.9 * Math.cos(angle);
            double z = 0.9 * Math.sin(angle);

            Location shardLoc = center.clone().add(x, 1.2 + Math.sin(seconds * 2 + i) * 0.3, z);

            world.spawnParticle(
                    Particle.CRIT,
                    shardLoc,
                    2,
                    0.05, 0.05, 0.05,
                    0
            );
        }

        /*
         * === 5. Rare Celestial Pulse ===
         */
        if (Math.random() < 0.015) {
            world.spawnParticle(
                    Particle.FLASH,
                    center.clone().add(0, 1.5, 0),
                    1,
                    0, 0, 0,
                    0, Color.WHITE
            );
            world.playSound(
                    center,
                    Sound.ENTITY_ILLUSIONER_CAST_SPELL,
                    0.4f,
                    1.4f + (float) (Math.sin(seconds * 2) * 0.1)
            );
        }
    }

    public static void playCraftingSounds(Location location) {

        if (Math.random() > 0.05) return;

        World world = location.getWorld();
        Location center = location.clone().add(0.5, 0.5, 0.5);

        long time = System.currentTimeMillis();
        double seconds = time / 1000.0;

        // === 1. Ambient Magical Hum (every second) ===
        world.playSound(
                center,
                Sound.BLOCK_BEACON_AMBIENT,
                0.4f,
                1.4f + (float) (Math.sin(seconds * 2) * 0.1)
        );

        // === 2. Subtle Brewing Bubble ===
        if (Math.random() < 0.6) {
            world.playSound(
                    center,
                    Sound.BLOCK_BREWING_STAND_BREW,
                    0.3f,
                    1.0f + (float) (Math.random() * 0.3)
            );
        }

        // === 3. Arcane Spark Accent ===
        if (Math.random() < 0.35) {
            world.playSound(
                    center,
                    Sound.ENTITY_EVOKER_CAST_SPELL,
                    0.5f,
                    1.5f + (float) (Math.random() * 0.3)
            );
        }

        // === 4. Low Ritual Pulse (every ~3 seconds) ===
        if (((int) seconds) % 3 == 0) {
            world.playSound(
                    center,
                    Sound.BLOCK_CONDUIT_AMBIENT_SHORT,
                    0.6f,
                    0.8f
            );
        }
    }

}
