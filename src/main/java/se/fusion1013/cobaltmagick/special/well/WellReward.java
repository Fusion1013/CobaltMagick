package se.fusion1013.cobaltmagick.special.well;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.ActionVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.Map;

public class WellReward implements IWellReward {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final IntVariable coinAmount = new IntVariable("coins");
    private final ActionVariable actions = new ActionVariable("actions");

    public WellReward(ConfigurationSection yaml) {
        internalName.load(yaml);
        coinAmount.load(yaml);
        actions.load(yaml);
    }

    public WellReward(JsonObject json) {
        throw new NotImplementedException();
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    @Override
    public int getCoinAmount() {
        return coinAmount.getValue();
    }

    @Override
    public void trigger(Map<String, Object> context) {
        actions.getValueList().forEach(a -> a.execute(context));
    }
}
