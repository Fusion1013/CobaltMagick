package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import com.google.gson.JsonObject;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.*;

import java.util.List;

public class ElementalVein implements IElementalVein {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final VectorListVariable positions = new VectorListVariable("positions");
    private final IntVariable splinePoints = new IntVariable("spline_points", 1000);
    private final ParticleVariable particle = new ParticleVariable("particle", Particle.END_ROD);
    private final IntVariable particleCount = new IntVariable("particle_count", 1);
    private final DoubleVariable particleOffset = new DoubleVariable("particle_offset", 0);

    private final ParametricSpline2D spline;
    private final List<ParametricSpline2D.Point2D> points;

    public ElementalVein(ConfigurationSection yaml) {
        internalName.load(yaml);
        positions.load(yaml);
        splinePoints.load(yaml);
        particle.load(yaml);
        particleCount.load(yaml);
        particleOffset.load(yaml);

        spline = new ParametricSpline2D(ParametricSpline2D.getPoints(positions.getValue()));
        points = spline.sampleEquidistant(splinePoints.getValue());
    }

    public ElementalVein(JsonObject json) {
//        internalName.load(json);
//        positions.load(json);
//        splinePoints.load(json);
//        particle.load(json);
//        particleCount.load(json);
//        particleOffset.load(json);

        spline = new ParametricSpline2D(ParametricSpline2D.getPoints(positions.getValue()));
        points = spline.sampleEquidistant(splinePoints.getValue());
    }

    @Override
    public List<Vector> getPositions() {
        return positions.getValue();
    }

    @Override
    public ParametricSpline2D.Result getClosestPoint(double x, double y) {
        return spline.closestPoint(x, y);
    }

    @Override
    public List<ParametricSpline2D.Point2D> getNPoints(int n) {
        return spline.sampleEquidistant(n);
    }

    @Override
    public List<ParametricSpline2D.Point2D> getPoints() {
        return points;
    }

    @Override
    public Particle getParticle() {
        return particle.getParticle();
    }

    @Override
    public int getParticleCount() {
        return particleCount.getValue();
    }

    @Override
    public double getParticleOffset() {
        return particleOffset.getValue();
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
