package se.fusion1013.cobaltmagick.alchemy.transmutation.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.Response;
import se.fusion1013.cobaltmagick.alchemy.transmutation.service.TransmutationService;

public class TransmutationAddCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("add")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.transmutation.add"))
                .withArguments(new StringArgument("input").replaceSuggestions(ArgumentSuggestions.strings(a -> CustomItemManager.getItemNames())))
                .withArguments(new StringArgument("output").replaceSuggestions(ArgumentSuggestions.strings(a -> CustomItemManager.getItemNames())))
                .withArguments(new StringArgument("catalyst").replaceSuggestions(ArgumentSuggestions.strings(a -> CustomItemManager.getItemNames())))
                .withArguments(new IntegerArgument("cost"))
                .executesPlayer(TransmutationAddCommand::addTransmutation);
    }

    private static void addTransmutation(Player player, CommandArguments args) {
        String input = (String) args.get("input");
        String output = (String) args.get("output");
        String catalyst = (String) args.get("catalyst");
        int cost = (int) args.get("cost");

        Response response = TransmutationService.getInstance().addTransmutation(input, output, catalyst, cost);

        if (response.ok()) {

        } else {

        }
    }

}
