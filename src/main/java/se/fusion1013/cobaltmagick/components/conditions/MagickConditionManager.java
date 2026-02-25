package se.fusion1013.cobaltmagick.components.conditions;

import se.fusion1013.cobaltCore.components.conditions.ConditionManager;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.function.Supplier;

public class MagickConditionManager extends Manager<CobaltMagick> {

    private static final Supplier<ICondition> ELEMENTAL_VEIN = ConditionManager.CONDITIONS.register(ElementalVeinCondition::new);

    public MagickConditionManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
