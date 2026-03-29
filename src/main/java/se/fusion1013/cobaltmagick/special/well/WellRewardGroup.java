package se.fusion1013.cobaltmagick.special.well;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.List;

public class WellRewardGroup implements IWellRewardGroup {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final StringVariable wellRewards = new StringVariable("rewards");

    public WellRewardGroup(ConfigurationSection yaml) {
        internalName.load(yaml);
        wellRewards.load(yaml);
    }

    public WellRewardGroup(JsonObject json) {
        throw new NotImplementedException();
    }

    @Override
    public List<String> getWellRewards() {
        return wellRewards.getValueList();
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
