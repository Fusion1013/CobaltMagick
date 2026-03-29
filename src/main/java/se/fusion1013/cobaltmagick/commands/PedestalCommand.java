package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.pedestal.PedestalManager;
import se.fusion1013.cobaltmagick.pedestal.PedestalStyle;

import java.util.Arrays;

public class PedestalCommand {

    public static void register() {
        new CommandAPICommand("pedestal")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "pedestal"))
                .withSubcommand(PedestalCommand.createCreateCommand())
                .register();
    }

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "pedestal.create"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("style").replaceSuggestions(ArgumentSuggestions.strings((k) -> Arrays.stream(PedestalStyle.values()).map(PedestalStyle::getName).toArray(String[]::new))))
                .executes(PedestalCommand::createPedestal);
    }

    private static void createPedestal(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        String styleName = (String) args.get("style");
        if (location == null || styleName == null) return;

        PedestalStyle style = EnumUtils.findEnumInsensitiveCase(PedestalStyle.class, styleName);
        PedestalManager.getInstance().create(location, style);
    }

}
