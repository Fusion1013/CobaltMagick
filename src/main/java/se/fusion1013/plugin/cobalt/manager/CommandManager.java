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

public class CommandManager extends Manager {

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
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
