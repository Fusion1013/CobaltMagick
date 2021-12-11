package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.ArrayList;
import java.util.List;

public class ParticleStylePoint extends ParticleStyle implements IParticleStyle, Cloneable {

    public ParticleStylePoint(ParticleStylePoint target){
        super(target);
    }

    public ParticleStylePoint(Particle particle){
        super("point");
        this.particle = particle;
    }

    @Override
    public List<PParticle> getParticles(Location location) {
        List<PParticle> particles = new ArrayList<>();
        particles.add(new PParticle(location.clone(), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        return particles;
    }

    @Override
    public ParticleStylePoint clone() {
        return new ParticleStylePoint(this);
    }

    public static class ParticleStylePointBuilder extends ParticleStyleBuilder<ParticleStylePoint, ParticleStylePointBuilder> {

        protected ParticleStylePoint createObj() { return new ParticleStylePoint(particle); }

        protected ParticleStylePointBuilder getThis() { return this; }

        public ParticleStylePoint build(){
            return super.build();
        }
    }
}
