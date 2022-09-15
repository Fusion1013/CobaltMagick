package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;

public class StructureCommand {

    public static CommandAPICommand register() {

        // Standalone command
        new CommandAPICommand("cstructure")
                .withPermission("cobalt.magick.commands.structure")
                .withSubcommand(MusicBoxCommand.createMusicBoxCommand())
                .withSubcommand(LaserCommand.createLaserCommand())
                .withSubcommand(MagickPortalCommand.createMagickPortalCommand())
                .register();

        // Command for /magick
        return new CommandAPICommand("structure")
                .withPermission("cobalt.magick.commands.structure")
                .withSubcommand(MusicBoxCommand.createMusicBoxCommand())
                .withSubcommand(LaserCommand.createLaserCommand())
                .withSubcommand(MagickPortalCommand.createMagickPortalCommand());
    }

}
