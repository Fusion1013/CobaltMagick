package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleLine extends ParticleStyle implements IParticleStyle {

    private Map<String, Double> doubleValues = new HashMap<>();

    public ParticleStyleLine(ParticleStyleLine target){
        doubleValues.put("density", target.getDouble("density"));
    }

    public ParticleStyleLine(){
        this(Particle.BARRIER);
    }

    public ParticleStyleLine(Particle particle) {
        super("line");
        setDefaultSettings();
    }

    @Override
    public List<PParticle> getParticles(Location startLocation, Location endLocation) { // TODO: Find some way to not do this with two location inputs
        List<PParticle> pParticles = new ArrayList<>();
        double density = doubleValues.get("density");
        double distance = startLocation.distance(endLocation);
        int steps = (int)density * (int)distance;
        Vector direction = startLocation.subtract(endLocation).getDirection().normalize();

        for (int i = 0; i < steps; i++){
            Location location = startLocation.add(direction.multiply((double)i / (double)density));
            pParticles.add(new PParticle(location));
        }

        return pParticles;
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

    protected void setDefaultSettings(){
        doubleValues.put("density", 10.0);
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleLine(this);
    }
}
