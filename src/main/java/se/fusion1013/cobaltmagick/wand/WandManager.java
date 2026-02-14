package se.fusion1013.cobaltmagick.wand;

import org.bukkit.Bukkit;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class WandManager extends Manager<CobaltMagick> {
    public WandManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(new WandEvents(), CobaltMagick.getInstance());
        Bukkit.getPluginManager().registerEvents(new WandGUIEvents(), CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }

    private static WandManager INSTANCE;

    public static WandManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WandManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
