package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.VersionUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.commands.advancement.AdvancementCommand;
import se.fusion1013.plugin.cobaltmagick.commands.edit.EditCommand;
import se.fusion1013.plugin.cobaltmagick.commands.structure.StructureCommand;

public class MagickCommand {

    // ----- REGISTER -----

    public static void register(){

        // Main magick command
        new CommandAPICommand("magick")
                .withSubcommand(EditCommand.register())
                .withSubcommand(StructureCommand.register())
                .withSubcommand(AdvancementCommand.register())
                .withSubcommand(createVersionCommand())
                .withSubcommand(createColorizeCommand())
                .withSubcommand(createUpdateCommand())
                .register();
    }

    // ----- CREATE COMMANDS -----

    private static CommandAPICommand createUpdateCommand() {
        return new CommandAPICommand("update")
                .withPermission("cobalt.magick.commands.update")
                .withArguments(new StringArgument("file name"))
                .withArguments(new GreedyStringArgument("url"))
                .executes(((sender, args) -> {
                    /*
                    try {
                        File file1 = new File("plugins", (String)args[0]); // TODO: Replace path getting
                        // FileUtils.copyURLToFile(new URL((String)args[1]), file1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     */
                }));
    }

    private static CommandAPICommand createHiddenMessageCommand() {
        return new CommandAPICommand("hidden_message")
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new LocationArgument("location"))
                        .withArguments(new IntegerArgument("rotation"))
                        .withArguments(new GreedyStringArgument("text"))
                        .executesPlayer(MagickCommand::addHiddenMessage));
    }

    private static void addHiddenMessage(Player p, Object[] args) {
        // WorldManager.addHiddenMessage(new HiddenParticles((String)args[2], (Location)args[0], -Math.toRadians((int)args[1]), HiddenParticles.TextEncryption.GALACTIC)); // Rotate the rotation to switch the rotation direction
    }

    private static CommandAPICommand createVersionCommand() {
        return new CommandAPICommand("version")
                .executesPlayer((sender, args) -> {
                    VersionUtil.printVersion(CobaltMagick.getInstance(), sender);
                });
    }

    private static CommandAPICommand createColorizeCommand(){
        return new CommandAPICommand("colorize")
                .withPermission("cobalt.magick.commands.colorize")
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    Bukkit.broadcastMessage(HexUtils.colorify((String)args[0]));
                });
    }
}
