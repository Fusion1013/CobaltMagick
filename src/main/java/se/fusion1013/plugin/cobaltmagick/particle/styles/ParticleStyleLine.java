package se.fusion1013.plugin.cobaltmagick.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
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
        this.particle = particle;
        setDefaultSettings();
    }

    @Override
    public List<PParticle> getParticles(Location startLocation, Location endLocation) {
        List<PParticle> pParticles = new ArrayList<>();
        double distance = startLocation.distance(endLocation);
        int steps = density * (int)Math.round(distance);
        Vector direction = startLocation.clone().subtract(endLocation).getDirection().normalize();

        for (int i = 0; i < steps; i++){
            Location location = startLocation.clone().add(direction.clone().multiply((double)i / (double)density));
            pParticles.add(new PParticle(location, offset.getX(), offset.getY(), offset.getZ(), speed, count));
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

    public static class ParticleStyleLineBuilder extends ParticleStyleBuilder<ParticleStyleLine, ParticleStyleLineBuilder> {

        int density = 10;

        @Override
        public ParticleStyleLine build() {
            obj.setDensity(density);

            return super.build();
        }

        public ParticleStyleLineBuilder setDensity(int density) {
            this.density = density;
            return getThis();
        }

        @Override
        protected ParticleStyleLine createObj() {
            return new ParticleStyleLine(particle);
        }

        @Override
        protected ParticleStyleLineBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setDensity(int density) {
        this.density = density;
    }
}
