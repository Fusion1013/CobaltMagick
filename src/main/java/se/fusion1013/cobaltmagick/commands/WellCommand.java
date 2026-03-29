package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.special.well.WellManager;

public class WellCommand {

    public static void register() {
        new CommandAPICommand("well")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "well"))
                .withArguments(new LocationArgument("hopper", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("center", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("reward", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("door", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("reward_group").replaceSuggestions(ArgumentSuggestions.strings(k -> WellManager.getRewardGroupNames())))
                .executes(WellCommand::tickWell)
                .register();
    }

    private static void tickWell(CommandSender sender, CommandArguments args) {
        Location hopperLocation = (Location) args.get("hopper");
        Location centerLocation = (Location) args.get("center");
        Location rewardLocation = (Location) args.get("reward");
        Location doorLocation = (Location) args.get("door");
        String group = (String) args.get("reward_group");

        if (hopperLocation == null || centerLocation == null || rewardLocation == null || doorLocation == null) return;

        WellManager.getInstance().tickWell(hopperLocation, centerLocation, rewardLocation, doorLocation, group);
    }

}
