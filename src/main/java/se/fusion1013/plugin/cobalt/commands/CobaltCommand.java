package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a command that is handled by Cobalt's custom command system
 */
public interface CobaltCommand {

    void populate(Plugin plugin);

    Plugin getPlugin();

    void registerSubCommand(SubCommand subCommand);

    String getCommandName();

    String getDescription();

    int getMinArgs();

    int getMaxArgs();

    List<SenderType> getValidSenders();

    boolean shouldParseCommandFlags();

    boolean isExecuteIfInvalidSubCommand();

    HashMap<String, SubCommand> getSubCommands();

    boolean validate(CommandSender sender, String[] args);

    boolean execute(CommandSender sender, String label, String[] args);

    boolean execute(CommandSender sender, String[] args, CommandFlags flags);

    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
