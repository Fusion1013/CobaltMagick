package se.fusion1013.cobaltmagick.util;

import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.Collection;

public class AdvancementUtil {

    public static void grantInRange(Location location, Advancement advancement, int range) {
        if (advancement == null) return;

        Collection<Player> players = location.getNearbyPlayers(range);
        players.forEach(p -> {
            AdvancementProgress progress = p.getAdvancementProgress(advancement);

            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        });
    }

}
