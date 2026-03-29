package se.fusion1013.cobaltmagick.special.well;

import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.Map;

public interface IWellReward extends IRegistryItem {

    int getCoinAmount();

    void trigger(Map<String, Object> context);

}
