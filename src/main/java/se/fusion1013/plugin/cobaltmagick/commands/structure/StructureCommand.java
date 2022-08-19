package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;

public class StructureCommand {

    public static CommandAPICommand register() {

        // Standalone command
        new CommandAPICommand("cstructure")
                .withPermission("cobalt.magick.commands.structure")
                .withSubcommand(DoorCommand.createDoorCommand())
                .withSubcommand(MusicBoxCommand.createMusicBoxCommand())
                .withSubcommand(ItemLockCommand.createItemLockCommand())
                .withSubcommand(RuneLockCommand.createRuneLockCommand())
                .withSubcommand(LaserCommand.createLaserCommand())
                .withSubcommand(ActivatableCommand.createActivatableCommand())
                .withSubcommand(HiddenObjectCommand.createHiddenObjectCommand())
                .register();

        // Command for /magick
        return new CommandAPICommand("structure")
                .withPermission("cobalt.magick.commands.structure")
                .withSubcommand(DoorCommand.createDoorCommand())
                .withSubcommand(MusicBoxCommand.createMusicBoxCommand())
                .withSubcommand(ItemLockCommand.createItemLockCommand())
                .withSubcommand(RuneLockCommand.createRuneLockCommand())
                .withSubcommand(LaserCommand.createLaserCommand())
                .withSubcommand(ActivatableCommand.createActivatableCommand())
                .withSubcommand(HiddenObjectCommand.createHiddenObjectCommand());
    }

}
