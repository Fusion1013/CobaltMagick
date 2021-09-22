package se.fusion1013.plugin.nicobalt.commands;

import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.nicobalt.Nicobalt;

import java.util.List;

public interface CommandModule {
    /**
     * Called when this command gets executed
     *
     * @param sender The PPlayer who executed this command
     * @param args The arguments to this command
     */
    void onCommandExecute(CommandSender sender, String[] args);

    /**
     * Called when a player tries to tab complete this command
     *
     * @param sender The PPlayer who is tab completing this command
     * @param args Arguments typed so far
     * @return A list of possible argument values
     */
    List<String> onTabComplete(CommandSender sender, String[] args);

    /**
     * Gets the name of this command
     *
     * @return The name of this command
     */
    String getName();

    /**
     * Gets the locale description key of this command
     *
     * @return The locale description key of this command
     */
    String getDescriptionKey();

    /**
     * Gets any arguments this command has
     *
     * @return The arguments this command has
     */
    String getArguments();

    /**
     * True if this command requires the player to have any effects and styles
     *
     * @return If the player must have effects and styles to use this command
     */
    boolean requiresEffectsAndStyles();

    /**
     * @return true if this command can be executed from console, otherwise false
     */
    boolean canConsoleExecute();

    static void printUsage(CommandSender sender, CommandModule command){
        sender.sendMessage("/" + command.getName() + " " + command.getArguments());
    }
}
