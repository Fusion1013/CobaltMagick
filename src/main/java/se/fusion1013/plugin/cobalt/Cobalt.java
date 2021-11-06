package se.fusion1013.plugin.cobalt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobalt.manager.*;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Cobalt extends JavaPlugin {

    private static Cobalt INSTANCE;

    private final Map<Class<?>, Manager> managers;

    public Cobalt(){
        INSTANCE = this;
        this.managers = new LinkedHashMap<>();
    }

    @Override
    public void onEnable() {
        System.out.println("Cobalt Plugin Enabled");

        this.reload();

        PluginManager pm = Bukkit.getPluginManager();
        // Register events
    }

    @Override
    public void onDisable() {
        System.out.println("Cobalt Plugin Disabled");
    }

    public static Cobalt getInstance(){
        return INSTANCE;
    }

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

    public void reload(){
        this.managers.values().forEach(Manager::disable);

        this.managers.values().forEach(Manager::reload);

        this.getManager(CommandManager.class);
        this.getManager(ParticleManager.class);
        this.getManager(ParticleStyleManager.class);
        this.getManager(LaserManager.class);
    }
}
