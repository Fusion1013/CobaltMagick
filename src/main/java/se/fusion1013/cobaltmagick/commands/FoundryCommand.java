package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.foundry.FoundryManager;
import se.fusion1013.cobaltmagick.foundry.IFoundryRecipe;

public class FoundryCommand {

    public static void register() {
        new CommandAPICommand("foundry")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "foundry"))
                .withSubcommand(createTemplateCommand())
                .register();
    }

    private static CommandAPICommand createTemplateCommand() {
        return new CommandAPICommand("template")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "foundry.template"))
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                        .withArguments(new StringArgument("template").replaceSuggestions(ArgumentSuggestions.strings(k -> FoundryManager.getRecipeNames())))
                        .withArguments(new BooleanArgument("cinematic"))
                        .executes(FoundryCommand::placeTemplate));
    }

    private static void placeTemplate(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        String templateName = (String) args.get("template");
        boolean cinematic = (boolean) args.get("cinematic");

        IFoundryRecipe recipe = FoundryManager.getRecipe(templateName);
        if (recipe == null) return;

        FoundryManager.clearFoundryHousings(location);
        recipe.placeTemplate(location, cinematic);
    }

}
