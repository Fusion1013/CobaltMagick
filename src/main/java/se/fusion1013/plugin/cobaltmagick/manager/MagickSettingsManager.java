package se.fusion1013.plugin.cobaltmagick.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltcore.settings.StringSetting;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public class MagickSettingsManager extends Manager {

    // ----- REGISTERED SETTINGS -----

    public static final StringSetting WAND_HUD_APPEARANCE = SettingsManager.register(new StringSetting(CobaltMagick.getInstance(), "wand_hud_appearance",
            "Changes the appearance of the wand HUD.", "cobalt.magick.setting.wand_hud_appearance", "text", "text", new String[] {"text", "bars_only"} ));

    // ----- CONSTRUCTORS -----

    public MagickSettingsManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
