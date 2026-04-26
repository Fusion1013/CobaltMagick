package se.fusion1013.cobaltmagick.alchemy.transmutation;

import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class TransmutationManager extends Manager<CobaltMagick> {

    private static final FileLoadedRegistry<ITransmutationRecipe> TRANSMUTATION_RECIPES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "transmutation_recipes",
            TransmutationRecipe::new,
            TransmutationRecipe::new,
            (p, r) -> {
            }
    );

    public TransmutationManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        TRANSMUTATION_RECIPES.reload();
    }

    @Override
    public void disable() {

    }

    private static TransmutationManager INSTANCE;

    public static TransmutationManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransmutationManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
