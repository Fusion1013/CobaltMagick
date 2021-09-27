package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.commands.CommandModule;
import se.fusion1013.plugin.cobalt.commands.DefaultCommandModule;
import se.fusion1013.plugin.cobalt.commands.EmitterCommandModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends Manager implements CommandExecutor, TabCompleter {

    /**
     * A list of all commands
     */
    private List<CommandModule> commands;

    public CommandManager(Cobalt cobalt) {
        super(cobalt);

        PluginCommand emitter = this.cobalt.getCommand("emitter");
        PluginCommand scenario = this.cobalt.getCommand("scenario");

        emitter.setTabCompleter(this);
        emitter.setExecutor(this);

        scenario.setTabCompleter(this);
        scenario.setExecutor(this);
    }

    public CommandModule findMatchingCommand(String commandName){
        for (CommandModule commandModule : this.commands){
            if (commandModule.getName().equalsIgnoreCase(commandName)){
                return commandModule;
            }
        }
        return null;
    }

    public List<CommandModule> getCommands(){
        return this.commands;
    }

    public List<String> getCommandNames(){
        List<String> commandNames = new ArrayList<>();
        for (CommandModule cmd : this.commands){
            commandNames.add(cmd.getName());
        }
        return commandNames;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandName = command.getName();
        CommandModule commandModule = this.findMatchingCommand(commandName);

        if (commandModule == null){
            sender.sendMessage("Unknown command");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this.cobalt, () -> {
            String[] cmdArgs = args.length > 0 ? Arrays.copyOfRange(args, 0, args.length) : new String[0];

            // Execute the command
            commandModule.onCommandExecute(sender, cmdArgs);
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        CommandModule commandModule = this.findMatchingCommand(command.getName());
        if (commandModule != null){
            String[] cmdArgs = Arrays.copyOfRange(args, 0, args.length);
            return commandModule.onTabComplete(sender, cmdArgs);
        }

        return new ArrayList<>();
    }

    @Override
    public void reload() {
        this.commands = new ArrayList<CommandModule>(){
            {
                this.add(new DefaultCommandModule());
                this.add(new EmitterCommandModule());
            }
        };
    }

    @Override
    public void disable() {

    }
}
