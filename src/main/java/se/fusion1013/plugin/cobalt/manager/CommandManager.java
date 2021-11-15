package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.commands.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager extends Manager implements CommandExecutor, TabCompleter {

    private static CommandManager instance = null;
    private HashMap<String, CobaltCommand> cobaltCommands = new HashMap<>();

    /**
     * Creates a new <code>CommandManager</code> object.
     * @param cobalt
     */
    public CommandManager(Cobalt cobalt) {
        super(cobalt);
    }

    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static CommandManager getInstance(){
        if (instance == null){
            instance = new CommandManager(Cobalt.getInstance());
        }
        return instance;
    }

    /**
     * Creates and registers a <code>MainCommand</code>.
     * This will also inject it into Bukkit's central <code>CommandMap</code>.
     *
     * @param plugin The plugin that owns the given command.
     * @param commandClass The class of the command to register.
     */
    public <T extends MainCommand> void registerMainCommand(Plugin plugin, Class<T> commandClass){
        try {
            // Instantiate and populate the command
            MainCommand command = commandClass.getDeclaredConstructor().newInstance();
            command.populate(plugin);

            // Register the command
            cobaltCommands.put(command.getCommandName(), command);
            PluginCommand pluginCommand = getCommand(command.getCommandName(), plugin);
            pluginCommand.setAliases(command.getAliases());
            getCommandMap().register(plugin.getDescription().getName(), command);

            // Set executor & tab completer to this
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);

            plugin.getLogger().info("Registering Command: " + command.getCommandName());

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static PluginCommand getCommand(String name, Plugin plugin){
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        }

        return command;
    }

    private static CommandMap getCommandMap(){
        CommandMap commandMap = null;

        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager){
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);

                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException e){
            e.printStackTrace();
        } catch (SecurityException e){
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return commandMap;
    }

    /**
     * Creates and registers a <code>SubCommand</code>.
     *
     * @param plugin The plugin that owns the given command.
     * @param commandClass The class of the command to register.
     */
    public <T extends SubCommand> void registerSubCommand(Plugin plugin, Class<T> commandClass){
        try {
            // Instantiate and populate the command
            SubCommand command = commandClass.getDeclaredConstructor().newInstance();
            command.populate(plugin);

            // Register the command
            String parentCommandName = command.getParentCommandName();
            getCommandFromName(parentCommandName).registerSubCommand(command);
            String commandKey = parentCommandName + " " + command.getCommandName();
            cobaltCommands.put(commandKey, command);

            plugin.getLogger().info("Registering Sub Command: " + command.getCommandName());

        } catch (Exception e){
            plugin.getLogger().severe("Possibly attempted to register a SubCommand before its parent!");
            e.printStackTrace();
        }
    }

    /**
     * Gets the <code>CobaltCommand</code> with the given command name.
     *
     * @param commandName The name of the command to get.
     * @return The <code>CobaltCommand</code>, if found, or null;
     */
    public CobaltCommand getCommandFromName(String commandName){
        return cobaltCommands.get(commandName);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        return getCommandFromName(command.getName()).execute(sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null; // TODO: Find command and get tab completion
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    /*
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
                this.add(new ScenarioCommandModule());
            }
        };
    }

    @Override
    public void disable() {

    }
    */
}
