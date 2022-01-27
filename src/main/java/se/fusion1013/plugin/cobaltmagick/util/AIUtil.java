package se.fusion1013.plugin.cobaltmagick.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class AIUtil {

    public static LivingEntity findNearbyPlayerHealthWeighted(Entity center, double maxDistance) {
        List<Entity> nearbyEntities = center.getNearbyEntities(maxDistance, maxDistance, maxDistance);

        if (nearbyEntities.size() <= 0) return null;

        double healthPool = 0;
        for (Entity e : nearbyEntities) if (e instanceof Player living) healthPool += living.getHealth();

        RandomCollection<LivingEntity> rc = new RandomCollection<>();
        for (Entity e : nearbyEntities) if (e instanceof Player living) rc.add(living.getHealth() / healthPool, living);

        return rc.next();
    }
}
