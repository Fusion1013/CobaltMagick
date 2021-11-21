package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.util.VectorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStyleCube extends ParticleStyle implements IParticleStyle {

    private int step = 0;
    private boolean skipNextStep = false; // Only spawn every 2 ticks

    private Map<String, Double> doubleValues = new HashMap<>();

    public ParticleStyleCube(){
        this(Particle.FLAME);
    }

    public ParticleStyleCube(ParticleStyleCube target){
        super(target);
        doubleValues.put("edge_length", target.getDouble("edge_length"));
        doubleValues.put("angular_velocity_x", target.getDouble("angular_velocity_x"));
        doubleValues.put("angular_velocity_y", target.getDouble("angular_velocity_y"));
        doubleValues.put("angular_velocity_z", target.getDouble("angular_velocity_z"));
        doubleValues.put("particles_per_edge", target.getDouble("particles_per_edge"));
    }

    public ParticleStyleCube(Particle particle) {
        super("cube");
        this.particle = particle;
        setDefaultSettings();
    }

    public void setParticle(Particle particle){
        this.particle = particle;
    }

    /**
     * Returns a list of particles that together create this shapes style.
     * @param location The location to center the style on
     * @return List of particles that create the shape
     */
    @Override
    public List<PParticle> getParticles(Location location) {
        List<PParticle> pparticles = new ArrayList<>();

        double angularVelocityX = doubleValues.get("angular_velocity_x");
        double angularVelocityY = doubleValues.get("angular_velocity_y");
        double angularVelocityZ = doubleValues.get("angular_velocity_z");
        double edgeLength = doubleValues.get("edge_length");
        double particlesPerEdge = doubleValues.get("particles_per_edge");

        if (this.skipNextStep)
            return pparticles;

        double xRotation = this.step * angularVelocityX;
        double yRotation = this.step * angularVelocityY;
        double zRotation = this.step * angularVelocityZ;
        double a = edgeLength / 2;
        double angleX, angleY;
        Vector v = new Vector();
        for (int i = 0; i < 4; i++) {
            angleY = i * Math.PI / 2;
            for (int j = 0; j < 2; j++) {
                angleX = j * Math.PI;
                for (int p = 0; p <= particlesPerEdge; p++) {
                    v.setX(a).setY(a);
                    v.setZ(edgeLength * p / particlesPerEdge - a);
                    VectorUtils.rotateAroundAxisX(v, angleX);
                    VectorUtils.rotateAroundAxisY(v, angleY);
                    VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
                    pparticles.add(new PParticle(location.clone().add(v)));
                }
            }
            for (int p = 0; p <= particlesPerEdge; p++) {
                v.setX(a).setZ(a);
                v.setY(edgeLength * p / particlesPerEdge - a);
                VectorUtils.rotateAroundAxisY(v, angleY);
                VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
                pparticles.add(new PParticle(location.clone().add(v)));
            }
        }

        step+=1;

        return pparticles;
    }

    protected void setDefaultSettings() {
        doubleValues.put("edgeLength", 10.0);
        doubleValues.put("angularVelocityX", 0.00314159265);
        doubleValues.put("angularVelocityY", 0.00369599135);
        doubleValues.put("angularVelocityZ", 0.00405366794);
        doubleValues.put("particlesPerEdge", 100.0);
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleCube(this);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Particle getParticle() {
        return this.particle;
    }

    // ----- VALUE SETTERS / GETTERS ------

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
}
