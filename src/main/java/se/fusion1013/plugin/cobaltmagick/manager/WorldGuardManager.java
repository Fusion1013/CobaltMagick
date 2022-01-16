package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.protection.CustomWorldGuardFlags;

public class WorldGuardManager extends Manager {

    private static WorldGuardManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WorldGuardManager</code>.
     *
     * @return The object of this class
     */
    public static WorldGuardManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WorldGuardManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    public static void initialize(){
        try {
            Plugin wgPlugin = CobaltMagick.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
            if (wgPlugin != null){
                enabled = true;
                CustomWorldGuardFlags.initMagickFlags();
            } else {
                enabled = false;
            }
        } catch (Throwable ex){
            ex.printStackTrace();
        }
    }

    private static boolean enabled = false; // TODO: Add to config file

    public WorldGuardManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
        INSTANCE = this;
    }

    @Override
    public void reload() {
    }

    public static boolean isEnabled(){
        return enabled;
    }

    @Override
    public void disable() {}

    public boolean isCastingAllowed(Player player, Location location){
        if (!enabled) return true;

        return CustomWorldGuardFlags.isCastingAllowed(player, location);
    }

    public boolean isWandEditingAllowed(Player player, Location location){
        if (!enabled) return true;

        return CustomWorldGuardFlags.isWandEditingAllowed(player, location);
    }

    public boolean isManaRechargeAllowed(Player player, Location location){
        if (!enabled) return true;

        return CustomWorldGuardFlags.isManaRechargeAllowed(player, location);
    }
}
