package se.fusion1013.cobaltmagick.alchemy.ritual;

import org.bukkit.event.Listener;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class RitualManager extends Manager<CobaltMagick> implements Listener {

    private static final FileLoadedRegistry<IRitualRecipe> RITUAL_RECIPES = new FileLoadedRegistry<IRitualRecipe>(
            CobaltMagick.getInstance(),
            "ritual_recipes",
            RitualRecipe::new,
            RitualRecipe::new,
            (p, r) -> {
            }
    );

    public RitualManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        RITUAL_RECIPES.reload();
    }

    @Override
    public void disable() {

    }

    public IRitualRecipe[] getRecipes() {
        return RITUAL_RECIPES.values().toArray(new IRitualRecipe[0]);
    }

    private static RitualManager INSTANCE;

    public static RitualManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RitualManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
