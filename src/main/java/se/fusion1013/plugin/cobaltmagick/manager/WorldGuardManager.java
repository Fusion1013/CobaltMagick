package se.fusion1013.plugin.cobaltmagick.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;

public class WorldGuardManager extends Manager {

    // ----- FLAGS -----
    public static StateFlag ALLOW_SPELLS_FLAG;
    public static StateFlag ALLOW_EDIT_WANDS_FLAG;
    public static StateFlag ALLOW_MANA_RECHARGE;

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
                initMagickFlags();
            }
        } catch (Throwable ex){
            ex.printStackTrace();
        }
    }

    private static void initMagickFlags(){
        StateFlag allowMagickSpellsFlag = new StateFlag("allow-magick-spells", true);
        ALLOW_SPELLS_FLAG = (StateFlag) initFlag(allowMagickSpellsFlag);

        StateFlag allowEditWandsFlag = new StateFlag("allow-edit-wands", true);
        ALLOW_EDIT_WANDS_FLAG = (StateFlag) initFlag(allowEditWandsFlag);

        StateFlag allowManaRecharge = new StateFlag("allow-mana-recharge", true);
        ALLOW_MANA_RECHARGE = (StateFlag) initFlag(allowManaRecharge);
    }

    private static Flag<?> initFlag(Flag<?> flag){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e){
            return registry.get(flag.getName());
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

    public boolean isEnabled(){
        return enabled;
    }

    @Override
    public void disable() {}

    public boolean isCastingAllowed(Player player, Location location){
        if (!enabled) return true;

        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, ALLOW_SPELLS_FLAG) != StateFlag.State.DENY;
    }

    public boolean isWandEditingAllowed(Player player, Location location){
        if (!enabled) return true;

        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, ALLOW_EDIT_WANDS_FLAG) != StateFlag.State.DENY;
    }

    public boolean isManaRechargeAllowed(Player player, Location location){
        if (!enabled) return true;

        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, ALLOW_MANA_RECHARGE) != StateFlag.State.DENY;
    }

    private ApplicableRegionSet getRegionSet(Location location){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(location));
    }
}
