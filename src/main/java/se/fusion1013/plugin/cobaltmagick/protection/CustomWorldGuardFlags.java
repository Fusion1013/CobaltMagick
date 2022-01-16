package se.fusion1013.plugin.cobaltmagick.protection;

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

public class CustomWorldGuardFlags {

    // ----- FLAGS -----
    public static StateFlag ALLOW_SPELLS_FLAG;
    public static StateFlag ALLOW_EDIT_WANDS_FLAG;
    public static StateFlag ALLOW_MANA_RECHARGE;

    public static void initMagickFlags(){
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

    public static boolean isCastingAllowed(Player player, Location location){
        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, CustomWorldGuardFlags.ALLOW_SPELLS_FLAG) != StateFlag.State.DENY;
    }

    public static boolean isWandEditingAllowed(Player player, Location location){
        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, CustomWorldGuardFlags.ALLOW_EDIT_WANDS_FLAG) != StateFlag.State.DENY;
    }

    public static boolean isManaRechargeAllowed(Player player, Location location){
        ApplicableRegionSet checkSet = getRegionSet(location);
        if (checkSet == null) return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        return checkSet.queryState(localPlayer, CustomWorldGuardFlags.ALLOW_MANA_RECHARGE) != StateFlag.State.DENY;
    }

    private static ApplicableRegionSet getRegionSet(Location location){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(location));
    }
}
