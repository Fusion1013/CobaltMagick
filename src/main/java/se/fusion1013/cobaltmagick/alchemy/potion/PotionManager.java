package se.fusion1013.cobaltmagick.alchemy.potion;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.CobaltRegistry;
import se.fusion1013.cobaltCore.manager.registry.RegistryProviderStorage;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class PotionManager extends Manager<CobaltMagick> implements Listener {

    private static final CobaltRegistry<IPotionRecipe> POTION_RECIPES = new CobaltRegistry<>();

    public PotionManager(CobaltMagick plugin) {
        super(plugin);
    }

    public static void loadPotionRecipes(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "potion_recipes/", new RegistryProviderStorage<>(POTION_RECIPES), new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return PotionRecipe.create(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return null;
            }
        }, overwrite);
    }

    public static void reloadPotionRecipes() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadPotionRecipes(plugin, true);
        }
    }

    public IPotionRecipe[] getRecipesMatchingGlyph(GlyphData glyph) {
        return POTION_RECIPES.values()
                .stream()
                .filter(r -> r.getGlyph().equalsIgnoreCase(glyph.category() + "." + glyph.name()))
                .toArray(IPotionRecipe[]::new);
    }

    public IPotionRecipe[] getRecipes() {
        return POTION_RECIPES.values().toArray(new IPotionRecipe[0]);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        reloadPotionRecipes();
        CommandManager.registerReloadMethod("potion_recipes", PotionManager::reloadPotionRecipes, POTION_RECIPES::getNames);
    }

    @Override
    public void disable() {

    }

    private static PotionManager INSTANCE;

    public static PotionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PotionManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
