package se.fusion1013.plugin.cobalt;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobalt.commands.CGiveCommand;
import se.fusion1013.plugin.cobalt.commands.HelloWorldCommand;
import se.fusion1013.plugin.cobalt.commands.*;
import se.fusion1013.plugin.cobalt.database.Database;
import se.fusion1013.plugin.cobalt.database.SQLite;
import se.fusion1013.plugin.cobalt.gui.AbstractGUIListener;
import se.fusion1013.plugin.cobalt.manager.*;
import se.fusion1013.plugin.cobalt.wand.OpenWandEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Cobalt extends JavaPlugin implements CobaltPlugin {

    private static Cobalt INSTANCE;
    private static Database db;

    private final Map<Class<?>, Manager> managers;

    public Cobalt(){
        INSTANCE = this;
        this.managers = new LinkedHashMap<>();
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting up Cobalt...");
        registerCobaltPlugin();
    }

    @Override
    public void onDisable() {
        System.out.println("Cobalt Plugin Disabled");
    }

    public static Cobalt getInstance(){
        return INSTANCE;
    }

    /**
     * Gets the database
     *
     * @return the databse
     */
    public Database getRDatabase() { return this.db; }

    /**
     * Gets a manager instance
     *
     * @param managerClass The class of the manager instance to get
     * @param <T> The manager type
     * @return The manager instance or null if one does not exist
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerClass) {
        if (this.managers.containsKey(managerClass))
            return (T) this.managers.get(managerClass);

        try {
            T manager = managerClass.getConstructor(this.getClass()).newInstance(this);
            this.managers.put(managerClass, manager);
            manager.reload();
            return manager;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Registers all Cobalt commands
     */
    public void registerCommands(){
        CommandManager cm = getManager(CommandManager.class);

        cm.registerMainCommand(this, HelloWorldCommand.class);

        cm.registerMainCommand(this, CGiveCommand.class);
        cm.registerMainCommand(this, WarpCommand.class);
        cm.registerSubCommand(this, WarpCreateCommand.class);
        cm.registerSubCommand(this, WarpInfoCommand.class);
        cm.registerSubCommand(this, WarpListCommand.class);
        cm.registerSubCommand(this, WarpTpCommand.class);
        cm.registerSubCommand(this, WarpDeleteCommand.class);
    }

    /**
     * Reloads all Cobalt managers
     */
    public void reloadManagers(){
        this.managers.values().forEach(Manager::disable);

        this.managers.values().forEach(Manager::reload);

        this.getManager(CommandManager.class);
        this.getManager(ParticleManager.class);
        this.getManager(ParticleStyleManager.class);
        this.getManager(LaserManager.class);
    }

    /**
     * Loads and registers the given <code>CobaltPlugin</code>.
     *
     * @return True if the plugin was successfully registered.
     */
    public boolean registerCobaltPlugin(){
        getLogger().info("Registering commands for " + getName() + "...");
        registerCommands();

        // Register settings

        // Instantiate Database
        getLogger().info("Instantiating database for " + getName() + "...");
        this.db = new SQLite(this);
        this.db.load();

        // Register listeners
        getServer().getPluginManager().registerEvents(new AbstractGUIListener(), this);
        getServer().getPluginManager().registerEvents(new OpenWandEvent(), this);

        // Reloads all managers
        this.reloadManagers();

        getLogger().info("Successfully registered " + getName() + ".");
        return true;
    }
}
