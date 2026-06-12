package se.fusion1013.cobaltmagick.alchemy.transmutation.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.Response;
import se.fusion1013.cobaltmagick.alchemy.transmutation.service.TransmutationService;

public class TransmutationCreateCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("create")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.transmutation.create"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(TransmutationCreateCommand::createTransmutation);
    }

    private static void createTransmutation(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        if (location == null) return;

        Response response = TransmutationService.getInstance().createTransmutationLocation(location.toCenterLocation());

        if (response.ok()) {

        } else {

        }
    }

}
