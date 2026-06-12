package se.fusion1013.cobaltmagick.alchemy.transmutation.command;

import dev.jorel.commandapi.CommandAPICommand;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class TransmutationCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("transmutation")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.transmutation"))
                .withSubcommand(TransmutationCreateCommand.register())
                .withSubcommand(TransmutationListCommand.register())
                .withSubcommand(TransmutationAddCommand.register());
    }

}
