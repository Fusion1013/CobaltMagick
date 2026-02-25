package se.fusion1013.cobaltmagick.components.actions;

import se.fusion1013.cobaltCore.components.actions.ActionManager;
import se.fusion1013.cobaltCore.components.actions.IAction;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.function.Supplier;

public class MagickActionManager extends Manager<CobaltMagick> {

    private static final Supplier<IAction> ELEMENTAL_VEIN_INFO_ACTION_FACTORY = ActionManager.register("elemental_vein_info", ElementalVeinInfoAction::new);

    public MagickActionManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
