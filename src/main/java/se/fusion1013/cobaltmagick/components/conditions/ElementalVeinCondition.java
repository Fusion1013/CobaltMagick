package se.fusion1013.cobaltmagick.components.conditions;

import org.bukkit.Location;
import se.fusion1013.cobaltCore.components.conditions.AbstractCondition;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;

import java.util.List;
import java.util.Map;

public class ElementalVeinCondition extends AbstractCondition {

    private final StringVariable veinName = new StringVariable("vein");
    private final DoubleVariable distance = new DoubleVariable("distance");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(veinName);
    }

    @Override
    public boolean evaluate(Map<String, Object> map) {
        IElementalVein vein = ElementalVeinManager.getElementalVein(veinName.getValue());
        if (vein == null) return false;

        Location location = getTargetLocation(map);
        if (location == null) return false;

        ParametricSpline2D.Result result = vein.getClosestPoint(location.x(), location.z());
        return result.distance() <= distance.getValue();
    }

    @Override
    public String getInternalName() {
        return "elemental_vein";
    }
}
