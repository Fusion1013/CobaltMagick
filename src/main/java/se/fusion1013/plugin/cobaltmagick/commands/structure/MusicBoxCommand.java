package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;

public class MusicBoxCommand {

    public static CommandAPICommand createMusicBoxCommand() {
        return new CommandAPICommand("music_box")
                .withPermission("cobalt.magick.commands.structure.music_box")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createRemoveCommand())
                .withSubcommand(new CommandAPICommand("edit")
                        .withSubcommand(createEditMessageCommand()));
    }

    // ----- PLACE COMMAND -----

    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withArguments(new TextArgument("sound"))
                .executesPlayer(MusicBoxCommand::placeMusicBox)
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executesPlayer(MusicBoxCommand::placeMusicBox);
    }

    private static void placeMusicBox(Player p, Object[] args) {
        // Get location depending on the arguments given
        Location location;
        if (args.length < 1) location = p.getLocation();
        else location = (Location) args[1];

        // Create the music box
        String sound = (String)args[0];
        WorldManager.registerMusicBox(location, sound);

        // Send command feedback
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("location", location)
                .addPlaceholder("sound", sound)
                .build();
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.music_box.place", placeholders);
    }

    // ----- EDIT COMMAND -----

    private static CommandAPICommand createEditMessageCommand() {
        return new CommandAPICommand("message")
                .withArguments(new IntegerArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getInstance().getMusicBoxIdStrings())))
                .withArguments(new GreedyStringArgument("message"))
                .executesPlayer(MusicBoxCommand::editMusicBoxMessage);
    }

    private static void editMusicBoxMessage(Player p, Object[] args) {
        int id = (Integer)args[0];
        String message = (String)args[1];
        MusicBox box = WorldManager.getInstance().getMusicBox(id);

        // Create placeholder
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", id)
                .addPlaceholder("message", message)
                .build();

        // If box does not exist, send error message
        if (box == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.music_box.not_found", placeholders);
            return;
        }

        // Update the message in the cache and the database
        box.setMessage(message);
        DataManager.getInstance().getDao(IMusicBoxDao.class).updateMusicBoxMessageAsync(id, message);

        // Send success message
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.music_box.edit.message", placeholders);
    }

    // ----- LIST COMMAND -----

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .executesPlayer(MusicBoxCommand::listMusicBoxes);
    }

    private static void listMusicBoxes(Player p, Object[] args) {
        LocaleManager localeManager = LocaleManager.getInstance();
        MusicBox[] boxes = WorldManager.getInstance().getMusicBoxes();

        StringPlaceholders placeholders1 = StringPlaceholders.builder()
                .addPlaceholder("header", "Music Boxes")
                .build();

        localeManager.sendMessage(CobaltMagick.getInstance(), p, "list-header", placeholders1);

        for (MusicBox box : boxes) {
            Location location = box.getLocation();
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("id", box.getId())
                    .addPlaceholder("x", location.getX())
                    .addPlaceholder("y", location.getY())
                    .addPlaceholder("z", location.getZ())
                    .addPlaceholder("world", location.getWorld().getName())
                    .addPlaceholder("song", box.getSound())
                    .build();
            localeManager.sendMessage("", p, "commands.magick.structure.music_box.info", placeholders);
        }
    }

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new IntegerArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getInstance().getMusicBoxIdStrings())))
                .executesPlayer(MusicBoxCommand::removeMusicBox);
    }

    private static void removeMusicBox(Player player, Object[] args) {
        // Try to remove the music box
        int id = (Integer) args[0];
        MusicBox box = WorldManager.getInstance().removeMusicBox(id);

        // Send message
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", id)
                .build();

        if (box != null) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.music_box.remove", placeholders);
        else LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.music_box.remove", placeholders);
    }

}
