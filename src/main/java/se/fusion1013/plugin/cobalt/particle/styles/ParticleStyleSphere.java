package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.util.GeometryUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleSphere extends ParticleStyle implements IParticleStyle {
    private int density;

    private double currentRadius;
    private double startRadius;
    private double targetRadius;
    private int expandTime;

    private boolean inSphere;

    public ParticleStyleSphere(ParticleStyleSphere target){
        super(target);
        this.density = target.density;

        this.currentRadius = target.currentRadius;
        this.startRadius = target.startRadius;
        this.expandTime = target.expandTime;
        this.targetRadius = target.targetRadius;

        this.inSphere = target.inSphere;
    }

    public ParticleStyleSphere(Particle particle){
        super("sphere");
        this.particle = particle;
        setDefaults();
    }

    public void setDefaults(){
        density = 150;
        currentRadius = 5;
    }

    @Override
    public List<PParticle> getParticles(Location location){
        if (currentRadius < targetRadius){
            currentRadius += Math.min((targetRadius - startRadius) / (double)expandTime, targetRadius);
        }
        if (inSphere) return particlesInSphere(location);
        else return particlesOnSphere(location);
    }

    private List<PParticle> particlesOnSphere(Location center){
        List<PParticle> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new PParticle(center.clone().add(GeometryUtil.getPointOnSphere(currentRadius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles;
    }

    private List<PParticle> particlesInSphere(Location center){
        List<PParticle> particles = new ArrayList<>();

        for (int i = 0; i < this.density; i++){
            particles.add(new PParticle(center.clone().add(GeometryUtil.getPointInSphere(currentRadius)), offset.getX(), offset.getY(), offset.getZ(), speed, count));
        }

        return particles;
    }

    public void setRadius(double radius) { this.targetRadius = radius; }

    public void setStartRadius(double startRadius) {
        this.startRadius = startRadius;
        this.currentRadius = startRadius;
    }

    public void setExpandTime(int expandTime) { this.expandTime = expandTime; }

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

        double targetRadius = 1;
        double startRadius = 0;
        boolean animateRadius = false;
        int expandTime = 1;

        int density = 1; // TODO: Change default value
        boolean inSphere;

        @Override
        public ParticleStyleSphere build(){
            obj.setRadius(targetRadius);

            if (animateRadius) obj.setStartRadius(startRadius);
            else obj.setStartRadius(targetRadius);
            obj.setExpandTime(expandTime);

            obj.setDensity(density);
            obj.setInSphere(inSphere);
            return super.build();
        }

        protected ParticleStyleSphere createObj() { return new ParticleStyleSphere(particle); }

        protected ParticleStyleSphere.ParticleStyleSphereBuilder getThis() { return this; }

        public ParticleStyleSphereBuilder animateRadius(double startRadius, int expandTime){
            this.startRadius = startRadius;
            this.expandTime = expandTime;
            this.animateRadius = true;
            return getThis();
        }

        public ParticleStyleSphereBuilder setRadius(double targetRadius){
            this.targetRadius = targetRadius;
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
