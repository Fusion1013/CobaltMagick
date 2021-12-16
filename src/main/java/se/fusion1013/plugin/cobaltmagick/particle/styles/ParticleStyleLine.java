package se.fusion1013.plugin.cobaltmagick.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.particle.PParticle;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleLine extends ParticleStyle implements IParticleStyle {

    private int density; // in particles/block

    public ParticleStyleLine(ParticleStyleLine target){
        super(target);
        this.density = target.density;
    }

    public ParticleStyleLine(){
        this(Particle.FLAME);
    }

    public ParticleStyleLine(Particle particle) {
        super("line");
        setDefaultSettings();
    }

    @Override
    public List<PParticle> getParticles(Location startLocation, Location endLocation) {
        List<PParticle> pParticles = new ArrayList<>();
        double distance = startLocation.distance(endLocation);
        int steps = density * (int)distance;
        Vector direction = startLocation.subtract(endLocation).getDirection().normalize();

        for (int i = 0; i < steps; i++){
            Location location = startLocation.add(direction.multiply((double)i / (double)density));
            pParticles.add(new PParticle(location));
        }

        return pParticles;
    }

    protected void setDefaultSettings(){
        density = 10;
    }

    @Override
    public ParticleStyle clone() {
        return new ParticleStyleLine(this);
    }
}
