package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class PotionManager extends Manager<CobaltMagick> implements Listener {

    private static final FileLoadedRegistry<IPotionRecipe> POTION_RECIPES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "potion_recipes",
            PotionRecipe::new,
            PotionRecipe::new,
            (p, r) -> {
            }
    );

    public PotionManager(CobaltMagick plugin) {
        super(plugin);
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

    public String[] getRecipeNames() {
        return POTION_RECIPES.getNames();
    }

    public IPotionRecipe getRecipe(String key) {
        return POTION_RECIPES.get(key);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        POTION_RECIPES.reload();
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
