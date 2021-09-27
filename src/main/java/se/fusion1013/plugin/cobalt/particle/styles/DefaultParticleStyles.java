package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.List;

public class DefaultParticleStyles implements ParticleStyle {

    private String internalStyleName;

    public DefaultParticleStyles(String internalStyleName){
        this.internalStyleName = internalStyleName;
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
}
