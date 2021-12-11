package se.fusion1013.plugin.cobalt;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobalt.commands.CGiveCommand;
import se.fusion1013.plugin.cobalt.commands.*;
import se.fusion1013.plugin.cobalt.database.Database;
import se.fusion1013.plugin.cobalt.database.SQLite;
import se.fusion1013.plugin.cobalt.gui.AbstractGUIListener;
import se.fusion1013.plugin.cobalt.manager.*;
import se.fusion1013.plugin.cobalt.wand.Wand;
import se.fusion1013.plugin.cobalt.wand.WandEvents;

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
        registerCobaltPlugin();
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving wands...");
        Wand.saveAllWandData();
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
        WarpCommand.register();
        GamemodeCommand.register();
        CGiveCommand.register();
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
        this.getManager(SpellManager.class);
    }

    /**
     * Loads and registers the given <code>CobaltPlugin</code>.
     *
     * @return True if the plugin was successfully registered.
     */
    public boolean registerCobaltPlugin(){
        // Instantiate Database
        getLogger().info("Instantiating Database...");
        db = new SQLite(this);
        db.load();

        // Register settings

        // Register Commands
        getLogger().info("Registering commands...");
        registerCommands();

        // Register listeners
        getLogger().info("Registering Listeners...");
        getServer().getPluginManager().registerEvents(new AbstractGUIListener(), this);
        getServer().getPluginManager().registerEvents(new WandEvents(), this);

        // Reloads all managers
        getLogger().info("Reloading Managers...");
        this.reloadManagers();

        // Load wand cache
        getLogger().info("Loading Wand Cache from Database...");
        Wand.loadCacheFromDatabase();

        getLogger().info("Successfully registered " + getName() + ".");
        return true;
    }
}
