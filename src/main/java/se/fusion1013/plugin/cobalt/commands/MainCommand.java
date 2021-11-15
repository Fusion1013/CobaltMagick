package se.fusion1013.plugin.cobalt.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import se.fusion1013.plugin.cobalt.locale.Message;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a <code>MainCommand</code>, which executes code whenever
 * its primary name or aliases is entered as a command in chat.
 */
public abstract class MainCommand extends Command implements CobaltCommand {

    private Plugin plugin;
    private int minArgs;
    private int maxArgs;
    private List<SenderType> validSenders;
    private boolean parseCommandFlags;
    private boolean executeIfInvalidSubCommand;
    private HashMap<String, SubCommand> subCommands;

    /**
     * Creates a new <code>MainCommand</code> but does not populate any fields.
     * Call <code>populate()</code> after in order for this command to be fully functional.
     */
    public MainCommand() {
        super("");
        this.subCommands = new HashMap<>();
    }

    /**
     * Populates the command with information form the <code>CommandDeclaration</code>.
     * @param plugin The plugin that owns this command.
     */
    public void populate(Plugin plugin){
        this.plugin = plugin;

        CommandDeclaration commandDeclaration = getClass().getAnnotation(CommandDeclaration.class);
        super.setName(commandDeclaration.commandName());
        super.setLabel(commandDeclaration.commandName());
        super.setAliases(Arrays.asList(commandDeclaration.aliases()));
        super.setPermission(commandDeclaration.permission());
        super.setUsage(commandDeclaration.usage());
        super.setDescription(commandDeclaration.description());
        this.minArgs = commandDeclaration.minArgs();
        this.maxArgs = commandDeclaration.maxArgs();
        this.validSenders = Arrays.asList(commandDeclaration.validSenders());
        this.parseCommandFlags = commandDeclaration.parseCommandFlags();
        this.executeIfInvalidSubCommand = commandDeclaration.executeIfInvalidSubCommand();
    }

    public Plugin getPlugin() { return plugin; }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getCommandName(), subCommand);

        for (String alias : subCommand.getAliases()){
            subCommands.put(alias, subCommand);
        }
    }

    public String getCommandName() { return super.getName(); }

    public String getDescription() { return description; }

    public int getMinArgs() { return minArgs; }

    public int getMaxArgs() { return maxArgs; }

    public List<SenderType> getValidSenders() { return validSenders; }

    public boolean shouldParseCommandFlags() { return parseCommandFlags; }

    public boolean isExecuteIfInvalidSubCommand() { return executeIfInvalidSubCommand; }

    public HashMap<String, SubCommand> getSubCommands() { return subCommands; }

    public boolean validate(CommandSender sender, String[] args) {
        // SenderType check
        if (!validSenders.contains(SenderType.getSenderType(sender))){
            LocaleManager.getInstance().sendMessage(sender, Message.COMMAND_WRONG_SENDER_TYPE.getCode());
            return false;
        }

        // Permission Check
        if (!sender.hasPermission(getPermission())){
            LocaleManager.getInstance().sendMessage(sender, Message.COMMAND_NO_PERMISSION.getCode());
            return false;
        }

        // TODO: Parse and strip flags
        CommandFlags flags = null;
        String[] newArgs = args;

        if (parseCommandFlags){
            flags = new CommandFlags(args);
            newArgs = CommandFlags.stripFlags(args);
        }

        // If this command is the parent of other commands then execute the code of those
        if (!subCommands.isEmpty()){
            // Check if a valid SubCommand was entered
            if (args.length > 0 && subCommands.containsKey(args[0])){
                return subCommands.get(args[0]).validate(sender, (String[]) ArrayUtils.remove(args, 0));
            } else if (executeIfInvalidSubCommand){
                // Check the number of arguments
                if (newArgs.length < minArgs || newArgs.length > maxArgs){
                    LocaleManager.getInstance().sendMessage(sender, Message.COMMAND_INCORRECT_SYNTAX.getCode());
                    return false;
                }

                // If the SubCommand was invalid but the MainCommand should still execute then do it
                return execute(sender, newArgs, flags);
            }

            // Otherwise give the sender an incorrect syntax error
            LocaleManager.getInstance().sendMessage(sender, Message.COMMAND_INCORRECT_SYNTAX.getCode());
            return false;
        }

        // Argument count check
        if (newArgs.length < minArgs || newArgs.length > maxArgs){
            LocaleManager.getInstance().sendMessage(sender, Message.COMMAND_INCORRECT_SYNTAX.getCode());
            return false;
        }

        return execute(sender, newArgs, flags);
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        return validate(sender, args);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        // TODO
        return new ArrayList<String>();
    }
}
