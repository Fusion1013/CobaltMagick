package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.List;

public interface IElementalVein extends IRegistryItem {

    String getEssenceItemName();

    List<Vector> getPositions();

    ParametricSpline2D.Result getClosestPoint(double x, double y);

    List<ParametricSpline2D.Point2D> getNPoints(int n);

    List<ParametricSpline2D.Point2D> getPoints();

    Particle getParticle();

    int getParticleCount();

    double getParticleOffset();

}
