package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;

public class StructureCommand {

    public static CommandAPICommand createStructureCommand() {
        return new CommandAPICommand("structure")
                .withSubcommand(DoorCommand.createDoorCommand())
                .withSubcommand(MusicBoxCommand.createMusicBoxCommand())
                .withSubcommand(ItemLockCommand.createItemLockCommand())
                .withSubcommand(RuneLockCommand.createRuneLockCommand());
    }

}
