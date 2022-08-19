package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;

import java.util.UUID;

public class ActivatableCommand {

    public static CommandAPICommand createActivatableCommand() {
        return new CommandAPICommand("activatable")
                .withPermission("cobalt.magick.commands.structure.activatable")
                .withSubcommand(createToggleCommand());
    }

    // ----- TOGGLE ACTIVATABLE -----

    private static CommandAPICommand createToggleCommand() {
        return new CommandAPICommand("activate")
                .withPermission("cobalt.magick.commands.structure.activatable.toggle")
                .withArguments(new StringArgument("activatable").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getActivatableStrings())))
                .executes(ActivatableCommand::toggleActivatable);
    }

    private static void toggleActivatable(CommandSender executor, Object[] args) {
        UUID activatable = UUID.fromString((String) args[0]);
        boolean activated = WorldManager.activateActivatable(activatable);

        if (executor instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("uuid", activatable.toString())
                    .build();
            if (activated) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.activatable.toggle.success", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.activatable.toggle.failed", placeholders);
        }
    }
}
