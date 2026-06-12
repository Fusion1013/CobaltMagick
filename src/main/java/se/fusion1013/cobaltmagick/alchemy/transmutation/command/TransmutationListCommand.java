package se.fusion1013.cobaltmagick.alchemy.transmutation.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.Transmutation;
import se.fusion1013.cobaltmagick.alchemy.transmutation.service.TransmutationService;

import java.util.List;

public class TransmutationListCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("list")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.transmutation.list"))
                .executesPlayer(TransmutationListCommand::listTransmutations);
    }

    private static void listTransmutations(Player player, CommandArguments args) {

        List<Transmutation> transmutations = TransmutationService.getInstance().getTransmutations();

        LocaleManager.getInstance().sendMessage("", player, "commands.alchemy.transmutation.list.header");

        for (Transmutation transmutation : transmutations) {
            LocaleManager.getInstance().sendMessage("", player, "commands.alchemy.transmutation.list.item", StringPlaceholders.builder()
                    .addPlaceholder("catalyst", transmutation.getCatalyst())
                    .addPlaceholder("input", transmutation.getInputItem())
                    .addPlaceholder("output", transmutation.getOutputItem())
                    .addPlaceholder("cost", transmutation.getCost())
                    .build());
        }

    }

}
