package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.AbstractMagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MeditationPortal;

public class MagickPortalCommand {

    public static CommandAPICommand createMagickPortalCommand() {
        return new CommandAPICommand("magick_portal")
                .withPermission("cobalt.magick.commands.structure.magick_portal")
                .withSubcommand(createCreateCommand());
    }

    // ----- CREATE COMMAND -----

    private static CommandAPICommand createCreateCommand() {
        return new CommandAPICommand("create")
                .withArguments(new LocationArgument("portal_location", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("exit_location", LocationType.BLOCK_POSITION))
                .executes(MagickPortalCommand::createPortal);
    }

    private static void createPortal(CommandSender sender, Object[] args) {
        Location portalLocation = (Location) args[0];
        Location exitLocation = (Location) args[1];

        AbstractMagickPortal portal = new MeditationPortal(portalLocation.clone().add(.5, .5, .5), exitLocation.clone().add(.5, .5, .5));
        ObjectManager.insertStorageObject(portal, portalLocation.getChunk());
        // TODO: Move portal creation to abstract class
        // TODO: Implement automatic command generation for objects
    }

}
