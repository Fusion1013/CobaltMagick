package se.fusion1013.plugin.cobaltmagick.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;

public class EditCommand {

    public static CommandAPICommand register() {

        // Standalone command
        new CommandAPICommand("cedit")
                .withSubcommand(EditItemCommand.register())
                .withSubcommand(EditPlayerCommand.register())
                .register();

        // Command for /magick
        return new CommandAPICommand("edit")
                .withSubcommand(EditPlayerCommand.register())
                .withSubcommand(EditItemCommand.register());
    }
}
