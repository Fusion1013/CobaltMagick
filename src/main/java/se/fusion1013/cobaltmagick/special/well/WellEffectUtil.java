package se.fusion1013.cobaltmagick.special.well;

import org.bukkit.Location;
import org.bukkit.Particle;

public class WellEffectUtil {

    public static void display(Location center, int currentTick, double radius) {
        int durationTicks = 100;

        double progress = (double) currentTick / durationTicks;
        double angleStep = Math.PI / 8;

        for (double angle = 0; angle < Math.PI * 2; angle += angleStep) {
            double currentAngle = angle + currentTick * 0.15;

            double x = Math.cos(currentAngle) * radius * (1.0 - progress);
            double z = Math.sin(currentAngle) * radius * (1.0 - progress);

            double y = 0.8 - progress * 1.2; // spiral slowly downward

            Location particleLoc = center.clone().add(x, y, z);

            center.getWorld().spawnParticle(
                    Particle.ENCHANT,
                    particleLoc,
                    1,
                    0, 0, 0,
                    0
            );
        }

        // Extra burst near the end
        if (currentTick > durationTicks * 0.8) {
            center.getWorld().spawnParticle(
                    Particle.END_ROD,
                    center.clone().add(0, 0.6, 0),
                    3,
                    0.2, 0.2, 0.2,
                    0
            );
        }
    }

}
