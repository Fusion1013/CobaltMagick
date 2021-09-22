package se.fusion1013.plugin.nicobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.nicobalt.particle.PParticle;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleSphere extends DefaultParticleStyles implements ParticleStyle {
    private int density;
    private double radius;
    private Particle particle;

    public ParticleStyleSphere(Particle particle){
        super("sphere");
        this.particle = particle;
        setDefaults();
    }

    public void setDefaults(){
        density = 150;
        radius = 5;
    }

    @Override
    public List<PParticle> getParticles(Location location){
        List<PParticle> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double dx = this.radius * Math.sin(phi) * Math.cos(theta);
            double dy = this.radius * Math.sin(phi) * Math.sin(theta);
            double dz = this.radius * Math.cos(phi);
            particles.add(new PParticle(location.clone().add(dx, dy, dz)));
        }

        return particles;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Particle getParticle() {
        return this.particle;
    }
}
