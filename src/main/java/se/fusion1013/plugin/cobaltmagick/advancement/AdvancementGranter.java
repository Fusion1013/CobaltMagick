package se.fusion1013.plugin.cobaltmagick.advancement;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public class AdvancementGranter {

    // ----- VARIABLES -----

    String managerName;
    String advancementKey;
    int radius;

    // ----- CONSTRUCTORS -----

    public AdvancementGranter(String manager, String key, int radius) {
        this.managerName = manager;
        this.advancementKey = key;
        this.radius = radius;
    }

    // ----- EXECUTE -----

    public void execute(Location location) {
        MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);
        if (advancementManager == null) return;

        for (LivingEntity entity : location.getNearbyLivingEntities(radius)) {
            if (entity instanceof Player player) advancementManager.grantAdvancement(player, managerName, advancementKey);
        }
    }

}
