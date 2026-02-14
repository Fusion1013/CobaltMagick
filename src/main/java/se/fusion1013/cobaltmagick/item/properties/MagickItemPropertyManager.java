package se.fusion1013.cobaltmagick.item.properties;

import se.fusion1013.cobaltCore.item.properties.ItemPropertyManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class MagickItemPropertyManager extends Manager<CobaltMagick> {

    public MagickItemPropertyManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        ItemPropertyManager.register("item_wand", ItemWandProperty::new);
    }

    @Override
    public void disable() {

    }

    private static MagickItemPropertyManager INSTANCE;

    public static MagickItemPropertyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MagickItemPropertyManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
