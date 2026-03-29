package se.fusion1013.cobaltmagick.alchemy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.Arrays;
import java.util.List;

public class AlchemyManager extends Manager<CobaltMagick> implements Listener {

    private static final FileLoadedRegistry<AlchemyBlockProperties> BLOCK_ALCHEMY_PROPERTIES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "alchemy_block_properties",
            AlchemyBlockProperties::new,
            AlchemyBlockProperties::new,
            (p, ab) -> {
            }
    );

    public AlchemyManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        BLOCK_ALCHEMY_PROPERTIES.reload();
    }

    @Override
    public void disable() {

    }

    public static String[] getPropertyNames() {
        return getProperties().stream().map(AlchemyBlockProperties::getInternalName).toArray(String[]::new);
    }

    public static AlchemyBlockProperties getProperties(String id) {
        return BLOCK_ALCHEMY_PROPERTIES.get(id);
    }

    public static AlchemyBlockProperties getProperties(Material material) {
        return BLOCK_ALCHEMY_PROPERTIES.values().stream().filter(p -> Arrays.stream(p.getMaterials()).anyMatch(m -> m == material)).findFirst().orElse(null);
    }

    public static List<AlchemyBlockProperties> getProperties() {
        return BLOCK_ALCHEMY_PROPERTIES.values().stream().toList();
    }

    private static AlchemyManager INSTANCE;

    public static AlchemyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlchemyManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
