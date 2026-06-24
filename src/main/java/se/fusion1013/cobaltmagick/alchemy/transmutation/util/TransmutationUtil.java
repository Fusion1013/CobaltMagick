package se.fusion1013.cobaltmagick.alchemy.transmutation.util;

import org.bukkit.*;
import se.fusion1013.cobaltmagick.alchemy.Element;

import java.util.List;

public class TransmutationUtil {

    public static void playTransmutationSounds(Location location) {

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

    public static void playTransmutationSounds(
            Location location,
            long animationTick,
            float volume,
            float pitchBase,
            long interval
    ) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        // Main magical pulse (~4 times per second)
        if (animationTick % (5 * 20) == 0) {
            world.playSound(location, "thegreatwork:sfx.transmute_loop", volume, pitchBase);
        }
    }

    public static void displayElementOrbit(
            Location center,
            List<Element> elements,
            long animationTick,
            double baseRadius,
            double radiusVariation,
            double radiusFrequency,
            double rotationSpeed,
            double height,
            int particlesPerElement
    ) {
        World world = center.getWorld();
        if (world == null || elements.isEmpty()) {
            return;
        }

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);

            // Spread elements evenly around the circle
            double angleOffset = (Math.PI * 2.0 * i) / elements.size();

            // Rotation over time
            double angle = animationTick * rotationSpeed + angleOffset;

            // Radius oscillates over time
            double radius = baseRadius
                    + Math.sin(animationTick * radiusFrequency + angleOffset)
                    * radiusVariation;

            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            double y = center.getY() + Math.cos(angle / 4.0) * height;

            Color color = element.getColor();

            Particle.DustOptions dust = new Particle.DustOptions(
                    color,
                    1.5f // particle size
            );

            for (int p = 0; p < particlesPerElement; p++) {
                world.spawnParticle(
                        Particle.DUST,
                        x,
                        y,
                        z,
                        1,
                        0,
                        0,
                        0,
                        0,
                        dust
                );
            }
        }
    }

}
