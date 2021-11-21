package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleSphere extends ParticleStyle implements IParticleStyle {
    private Map<String, Double> doubleValues = new HashMap<>();

    public ParticleStyleSphere(ParticleStyleSphere target){
        doubleValues.put("density", target.getDouble("density"));
        doubleValues.put("radius", target.getDouble("radius"));
    }

    public ParticleStyleSphere(){
        this(Particle.BARRIER);
    }

    public ParticleStyleSphere(Particle particle){
        super("sphere");
        this.particle = particle;
        setDefaults();
    }

    public void setDefaults(){
        doubleValues.put("density", 150.0);
        doubleValues.put("radius", 5.0);
    }

    @Override
    public List<PParticle> getParticles(Location location){
        List<PParticle> particles = new ArrayList<>();

        int density = (int)Math.round(doubleValues.get("density")); // TODO: Make less yank
        double radius = doubleValues.get("radius");

        for (int i = 0; i < density; i++){
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double dx = radius * Math.sin(phi) * Math.cos(theta);
            double dy = radius * Math.sin(phi) * Math.sin(theta);
            double dz = radius * Math.cos(phi);
            particles.add(new PParticle(location.clone().add(dx, dy, dz)));
        }

        return particles;
    }

    @Override
    public void setDouble(String key, double p) {
        doubleValues.put(key, p);
    }

    @Override
    public List<String> getDoubleKeys() {
        return new ArrayList<>(doubleValues.keySet());
    }

    @Override
    public double getDouble(String key) {
        return doubleValues.get(key);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Particle getParticle() {
        return this.particle;
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleSphere(this);
    }
}
