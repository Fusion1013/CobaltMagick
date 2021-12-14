package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.util.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleSphere extends ParticleStyle implements IParticleStyle {
    private int density;
    private double radius;
    private boolean inSphere;

    public ParticleStyleSphere(ParticleStyleSphere target){
        super(target);
        this.density = target.density;
        this.radius = target.radius;
    }

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
        if (inSphere) return particlesInSphere(location);
        else return particlesOnSphere(location);
    }

    private List<PParticle> particlesOnSphere(Location center){
        List<PParticle> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new PParticle(center.clone().add(GeometryUtil.getPointOnSphere(radius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles;
    }

    private List<PParticle> particlesInSphere(Location center){
        List<PParticle> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new PParticle(center.clone().add(GeometryUtil.getPointInSphere(radius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles;
    }

    public void setRadius(double radius) { this.radius = radius; }

    public void setDensity(int density) { this.density = density; }

    public void setInSphere(boolean onSphere) { this.inSphere = onSphere; }

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

    public static class ParticleStyleSphereBuilder extends ParticleStyleBuilder<ParticleStyleSphere, ParticleStyleSphere.ParticleStyleSphereBuilder> {

        double radius = 1;
        int density = 1; // TODO: Change default value
        boolean inSphere;

        @Override
        public ParticleStyleSphere build(){
            obj.setRadius(radius);
            obj.setDensity(density);
            obj.setInSphere(inSphere);
            return super.build();
        }

        protected ParticleStyleSphere createObj() { return new ParticleStyleSphere(particle); }

        protected ParticleStyleSphere.ParticleStyleSphereBuilder getThis() { return this; }

        public ParticleStyleSphereBuilder setRadius(double radius){
            this.radius = radius;
            return getThis();
        }

        public ParticleStyleSphereBuilder setDensity(int density){
            this.density = density;
            return getThis();
        }

        public ParticleStyleSphereBuilder setInSphere(){
            this.inSphere = true;
            return getThis();
        }
    }
}
