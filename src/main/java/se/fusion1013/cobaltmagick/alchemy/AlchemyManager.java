package se.fusion1013.cobaltmagick.alchemy;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlchemyManager extends Manager<CobaltMagick> implements Listener {

    private static final Map<String, AlchemyBlockProperties> BLOCK_ALCHEMY_PROPERTIES = new HashMap<>();
    private static final Map<Material, AlchemyBlockProperties> BLOCK_ALCHEMY_PROPERTIES_MATERIAL = new HashMap<>();

    public AlchemyManager(CobaltMagick plugin) {
        super(plugin);
    }

    public static void loadAlchemyBlockPropertyFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "alchemy_block_properties/", new IProviderStorage<INameProvider>() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getProperties(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getProperties(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return AlchemyBlockPropertyLoader.loadComponent(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return AlchemyBlockPropertyLoader.loadComponent(json);
            }
        }, overwrite);
    }

    public static void reloadProperties() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadAlchemyBlockPropertyFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        reloadProperties();
        CommandManager.registerReloadMethod("alchemy_block_properties", AlchemyManager::reloadProperties, AlchemyManager::getPropertyNames);
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
        return BLOCK_ALCHEMY_PROPERTIES_MATERIAL.get(material);
    }

    public static List<AlchemyBlockProperties> getProperties() {
        return BLOCK_ALCHEMY_PROPERTIES.values().stream().toList();
    }

    private static AlchemyBlockProperties register(AlchemyBlockProperties properties) {
        BLOCK_ALCHEMY_PROPERTIES.put(properties.getInternalName(), properties);
        Arrays.stream(properties.getMaterials()).forEach(p -> {
            BLOCK_ALCHEMY_PROPERTIES_MATERIAL.put(p, properties);
        });
        return properties;
    }

    private static AlchemyBlockProperties register(String id, Material[] materials, int variance, int potency, int duration, int wild, int decay) {
        AlchemyBlockProperties properties = new AlchemyBlockProperties(id, materials, variance, potency, duration, wild, decay);
        return register(properties);
    }

    private static AlchemyBlockProperties register(INameProvider properties) {
        return register((AlchemyBlockProperties) properties);
    }

    private static AlchemyManager INSTANCE;

    public static AlchemyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlchemyManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
