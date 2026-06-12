package se.fusion1013.cobaltmagick.alchemy.transmutation.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

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

}
