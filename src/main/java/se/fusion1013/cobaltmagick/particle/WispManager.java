package se.fusion1013.cobaltmagick.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WispManager extends Manager<CobaltMagick> {

    public WispManager(CobaltMagick plugin) {
        super(plugin);
    }


    public static void tickWisps(
            World world,

            // Region
            Location corner1,
            Location corner2,

            // Persistent wisps
            List<Wisp> wisps,

            // Spawn settings
            int targetWispCount,
            double spawnChance,

            // Movement
            double horizontalDrift,
            double verticalDrift,
            double randomAcceleration,

            // Lifetime
            int minLifetimeTicks,
            int maxLifetimeTicks,

            // Particle appearance
            Particle particle,
            float particleSize,
            int particlesPerWisp,

            // Rendering
            double trailLength,
            int trailParticles
    ) {
        Random random = ThreadLocalRandom.current();

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());

        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());

        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        // Spawn new wisps
        while (wisps.size() < targetWispCount
                && random.nextDouble() < spawnChance) {

            double x = random.nextDouble(minX, maxX);
            double y = random.nextDouble(minY, maxY);
            double z = random.nextDouble(minZ, maxZ);

            Vector velocity = new Vector(
                    (random.nextDouble() - 0.5) * horizontalDrift,
                    (random.nextDouble() - 0.5) * verticalDrift,
                    (random.nextDouble() - 0.5) * horizontalDrift
            );

            wisps.add(new Wisp(
                    new Location(world, x, y, z),
                    velocity,
                    random.nextInt(
                            minLifetimeTicks,
                            maxLifetimeTicks + 1
                    )
            ));
        }

        Iterator<Wisp> iterator = wisps.iterator();

        while (iterator.hasNext()) {
            Wisp wisp = iterator.next();

            wisp.age++;

            if (wisp.age >= wisp.maxAge) {
                iterator.remove();
                continue;
            }

            // Gentle wandering acceleration
            wisp.velocity.add(new Vector(
                    (random.nextDouble() - 0.5) * randomAcceleration,
                    (random.nextDouble() - 0.5) * randomAcceleration * 0.3,
                    (random.nextDouble() - 0.5) * randomAcceleration
            ));

            // Soft velocity damping
            wisp.velocity.multiply(0.98);

            // Move
            wisp.location.add(wisp.velocity);

            // Keep inside bounds
            if (wisp.location.getX() < minX || wisp.location.getX() > maxX)
                wisp.velocity.setX(-wisp.velocity.getX());

            if (wisp.location.getY() < minY || wisp.location.getY() > maxY)
                wisp.velocity.setY(-wisp.velocity.getY());

            if (wisp.location.getZ() < minZ || wisp.location.getZ() > maxZ)
                wisp.velocity.setZ(-wisp.velocity.getZ());

            double lifeProgress =
                    (double) wisp.age / wisp.maxAge;

            float alpha =
                    (float) Math.sin(lifeProgress * Math.PI);

            // Core particles
            for (int i = 0; i < particlesPerWisp; i++) {
                world.spawnParticle(
                        particle,
                        wisp.location,
                        1,
                        0.02,
                        0.02,
                        0.02,
                        0
                );
            }

            // Trail
            for (int i = 0; i < trailParticles; i++) {

                double t =
                        (double) i / trailParticles;

                Location trail =
                        wisp.location.clone()
                                .subtract(
                                        wisp.velocity.clone()
                                                .multiply(trailLength * t)
                                );

                world.spawnParticle(
                        particle,
                        trail,
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }
    }


    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    private static WispManager INSTANCE;

    public static WispManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WispManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
