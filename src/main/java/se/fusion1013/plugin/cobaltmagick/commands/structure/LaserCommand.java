package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.laser.LaserManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.laser.SimpleLaser;

import java.util.UUID;

public class LaserCommand {

    public static CommandAPICommand createLaserCommand() {
        return new CommandAPICommand("laser")
                .withPermission("cobalt.magick.commands.structure.laser")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createRemoveCommand());
    }

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> LaserManager.getInstance().getLaserIdentifiers())))
                .executes(LaserCommand::removeLaser);
    }

    private static void removeLaser(CommandSender sender, Object[] args) {
        UUID laserUUID = UUID.fromString((String) args[0]);
        SimpleLaser laser = LaserManager.getInstance().removeLaser(laserUUID);

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("uuid", laserUUID.toString())
                    .build();

            if (laser != null) {
                placeholders.addPlaceholder("location", laser.getStartLocation());
                LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.laser.remove", placeholders);
            } else {
                LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.laser.not_found", placeholders);
            }
        }
    }

    // ----- PLACE COMMAND -----

    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(LaserCommand::placeLaser);
    }

    private static void placeLaser(CommandSender sender, Object[] args) {
        Location location = (Location) args[0];
        SimpleLaser laser = LaserManager.getInstance().createLaserEmitter(location);
        UUID uuid = laser.getUUID();

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("uuid", uuid.toString())
                    .addPlaceholder("location", location)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.laser.place", placeholders);
        }
    }

}
