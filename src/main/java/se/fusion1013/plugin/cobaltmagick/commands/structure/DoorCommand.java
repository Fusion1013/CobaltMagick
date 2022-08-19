package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MagickDoor;

import java.util.UUID;

public class DoorCommand {

    // ----- MAIN COMMAND -----

    public static CommandAPICommand createDoorCommand() {
        return new CommandAPICommand("door")
                .withPermission("cobalt.magick.commands.structure.door")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createToggleCommand())
                .withSubcommand(createInfoCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createRemoveCommand());
    }

    // ----- PLACE COMMAND -----

    // TODO: Add place command that takes two locations as corners
    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withArguments(new LocationArgument("position", LocationType.BLOCK_POSITION))
                .withArguments(new IntegerArgument("width"))
                .withArguments(new IntegerArgument("height"))
                .withArguments(new IntegerArgument("depth"))
                .executesPlayer(DoorCommand::placeDoor);
    }

    private static void placeDoor(Player p, Object[] args) {
        Location location = (Location)args[0];
        int dx = (Integer)args[1];
        int dy = (Integer)args[2];
        int dz = (Integer)args[3];

        MagickDoor door = WorldManager.registerDoor(location, dx, dy, dz);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("location", location)
                .addPlaceholder("uuid", door.getUuid().toString())
                .build();
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.door.place", placeholders);
    }

    // ----- TOGGLE COMMAND -----

    private static CommandAPICommand createToggleCommand() {
        return new CommandAPICommand("toggle")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getDoorKeys())))
                .executesPlayer(DoorCommand::toggleDoor);
    }

    private static void toggleDoor(Player p, Object[] args) {
        UUID id = UUID.fromString((String)args[0]);

        MagickDoor door = WorldManager.getDoor(id);
        if (door.isClosed()) door.open();
        else door.close();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", id.toString())
                .addPlaceholder("location", door.getCorner())
                .build();
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.door.toggle", placeholders);
    }

    // ----- INFO COMMAND -----

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getDoorKeys())))
                .executesPlayer(DoorCommand::doorInfo);
    }

    private static void doorInfo(Player p, Object[] args) {
        UUID uuid = UUID.fromString((String) args[0]);
        MagickDoor door = WorldManager.getDoor(uuid);
        if (door == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.not_found");
        }

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", uuid.toString())
                .addPlaceholder("location", door.getCorner())
                .addPlaceholder("x_size", door.getWidth())
                .addPlaceholder("y_size", door.getHeight())
                .addPlaceholder("z_size", door.getDepth())
                .build();
        LocaleManager.getInstance().sendMessage("", p, "commands.magick.structure.door.info", placeholders);
    }

    // ----- LIST COMMAND -----

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .executesPlayer(DoorCommand::listDoors);
    }

    private static void listDoors(Player p, Object[] args) {
        StringPlaceholders placeholders1 = StringPlaceholders.builder()
                .addPlaceholder("header", "Doors")
                .build();
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "list-header", placeholders1);

        for (MagickDoor door : WorldManager.getDoors()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("uuid", door.getUuid().toString())
                    .addPlaceholder("location", door.getCorner())
                    .addPlaceholder("x_size", door.getWidth())
                    .addPlaceholder("y_size", door.getHeight())
                    .addPlaceholder("z_size", door.getDepth())
                    .build();
            LocaleManager.getInstance().sendMessage("", p, "commands.magick.structure.door.info", placeholders);
        }
    }

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getDoorKeys())))
                .executesPlayer(DoorCommand::removeDoor);
    }

    private static void removeDoor(Player p, Object[] args) {
        UUID id = UUID.fromString((String)args[0]);
        MagickDoor door = WorldManager.removeDoor(id);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("structure", "Door")
                .addPlaceholder("location", door.getCorner())
                .build();
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.remove", placeholders);
    }

}
