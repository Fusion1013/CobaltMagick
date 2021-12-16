package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class CommandManager extends Manager {

    private static CommandManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static CommandManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CommandManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    /**
     * Creates a new <code>CommandManager</code> object.
     * @param cobaltMagick
     */
    public CommandManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
    }

    private static PluginCommand getCommand(String name, Plugin plugin){
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);

            command = c.newInstance(name, plugin);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
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

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }
}
