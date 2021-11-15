package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.List;

public abstract class ParticleStyle implements IParticleStyle {

    Particle particle;
    String internalStyleName;

    public ParticleStyle() {}

    public ParticleStyle(ParticleStyle target){
        if (target != null){
            this.particle = target.particle;
            this.internalStyleName = target.internalStyleName;
        }
    }

    public ParticleStyle(String internalStyleName) {
        this.internalStyleName = internalStyleName;
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public String getInternalName() {
        return this.internalStyleName;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Particle getParticle() {
        return null;
    }

    @Override
    public List<PParticle> getParticles(Location location) {
        return null;
    }

    @Override
    public List<PParticle> getParticles(Location startLocation, Location endLocation) { return null; }

    public abstract ParticleStyle clone();
}
