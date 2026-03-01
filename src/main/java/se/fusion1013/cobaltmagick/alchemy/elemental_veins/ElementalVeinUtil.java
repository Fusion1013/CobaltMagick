package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import org.bukkit.Location;
import se.fusion1013.cobaltmagick.alchemy.IAlchemyState;

public class ElementalVeinUtil {

    private static final int MIN_DISTANCE = 5;
    private static final int MAX_DISTANCE = 20;

    public static void addVeinModifiersToState(Location location, IAlchemyState state, IElementalAffinity affinity) {
        String[] affinityNames = affinity.getElementalAffinities();
        for (String affinityName : affinityNames) {
            IElementalVein vein = ElementalVeinManager.getElementalVein(affinityName);
            if (vein == null) continue;

            ParametricSpline2D.Result result = vein.getClosestPoint(location.x(), location.z());
            double normalized = getModifier(result.distance());


        }
    }

    private static double getModifier(double distance) {
        double normalized = (distance - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE);
        return Math.max(0.0, Math.min(1.0, normalized));
    }

}
