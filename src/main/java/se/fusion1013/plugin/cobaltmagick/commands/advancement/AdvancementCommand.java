package se.fusion1013.plugin.cobaltmagick.commands.advancement;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;

public class AdvancementCommand {

    public static CommandAPICommand register() {
        new CommandAPICommand("cadvancement")
                .withPermission("cobalt.magick.commands.advancement")
                .withSubcommand(createAllAdvancementsCommand())
                .withSubcommand(createGrantAdvancementCommand())
                .register();

        return new CommandAPICommand("advancement")
                .withPermission("cobalt.magick.commands.advancement")
                .withSubcommand(createAllAdvancementsCommand())
                .withSubcommand(createGrantAdvancementCommand());
    }

    // ----- ONE ADVANCEMENT -----

    private static CommandAPICommand createGrantAdvancementCommand() {
        return new CommandAPICommand("grant")
                .withPermission("cobalt.magick.commands.advancement.grant")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("manager_name").replaceSuggestions(ArgumentSuggestions.strings(info -> MagickAdvancementManager.getManagerNames())))
                .withArguments(new StringArgument("advancement_name").replaceSuggestions(ArgumentSuggestions.strings(info -> MagickAdvancementManager.getInstance().getAdvancementNames((String) info.previousArgs()[1]))))
                .executesPlayer(AdvancementCommand::grantAdvancement);
    }

    private static void grantAdvancement(Player player, Object[] args) {
        MagickAdvancementManager manager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);

        Player toPlayer = (Player) args[0];
        String advancementManager = (String) args[1];
        String advancementName = (String) args[2];

        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("advancement", advancementName)
                .addPlaceholder("player", toPlayer.getName())
                .build();

        // If the manager does not exist, return (If this happens something has gone very wrong)
        if (manager == null) return;

        // Grant advancement and send feedback
        boolean granted = manager.grantAdvancement(toPlayer, advancementManager, advancementName); // TODO
        if (granted) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.advancement.grant.success", placeholders);
        else LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.advancement.grant.failed", placeholders);
    }

    // ----- ALL ADVANCEMENTS -----

    private static CommandAPICommand createAllAdvancementsCommand() {
        return new CommandAPICommand("all")
                .withPermission("cobalt.magick.commands.advancement.all")
                .executesPlayer(AdvancementCommand::grantAllAdvancements);
    }

    private static void grantAllAdvancements(Player player, Object[] args) {
        MagickAdvancementManager manager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);

        // Create locale placeholders
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player", player.getName())
                .build();

        // If the manager does not exist, return
        if (manager == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.advancement.grant_all.failed", placeholders);
            return;
        }
        manager.grantAll(player);
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.advancement.grant_all.success", placeholders);
    }

}
