package se.fusion1013.plugin.cobaltmagick.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.particle.PParticle;

import java.util.ArrayList;
import java.util.List;

public abstract class ParticleStyle implements IParticleStyle, Cloneable {

    Particle particle = Particle.END_ROD;
    Vector offset;
    int count;
    double speed;
    String internalStyleName;
    Object extra;

    public ParticleStyle(ParticleStyle target){
        if (target != null){
            this.particle = target.getParticle();
            this.internalStyleName = target.getInternalName();
            this.offset = target.getOffset();
            this.count = target.getCount();
            this.speed = target.getSpeed();
            this.extra = target.getExtra();
        }
    }

    public ParticleStyle(String internalStyleName) {
        this.internalStyleName = internalStyleName;
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public void setOffset(Vector offset) { this.offset = offset.clone(); }

    public void setCount(int count) { this.count = count; }

    public void setSpeed(double speed) { this.speed = speed; }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public Object getExtra() {
        return extra;
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
        return particle;
    }

    @Override
    public List<PParticle> getParticles(Location location) {
        return new ArrayList<>();
    }

    @Override
    public List<PParticle> getParticles(Location startLocation, Location endLocation) { return new ArrayList<>(); }

    public Vector getOffset() {
        if (offset != null) return offset.clone();
        return null;
    }

    public int getCount() {
        return count;
    }

    public double getSpeed() {
        return speed;
    }

    public abstract ParticleStyle clone();

    protected static abstract class ParticleStyleBuilder<T extends ParticleStyle, B extends ParticleStyleBuilder> {

        T obj;

        Particle particle = Particle.FLAME;
        Vector offset = new Vector(0, 0, 0);
        int count = 1;
        double speed = 0;
        Object extra;

        // TODO: Set other variables for individual particles (/particle <particle> <x,y,z> <xOff,yOff,zOff> <speed> <count> <extra>)
        public ParticleStyleBuilder(){
            obj = createObj();
        }

        public T build(){
            obj.setParticle(particle);
            obj.setOffset(offset);
            obj.setCount(count);
            obj.setSpeed(speed);
            obj.setExtra(extra);

            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        public B setExtra(Object extra) {
            this.extra = extra;
            return getThis();
        }

        public B setParticle(Particle particle){
            this.particle = particle;
            return getThis();
        }

        public B setOffset(Vector offset){
            this.offset = offset;
            return getThis();
        }

        public B setCount(int count){
            this.count = count;
            return getThis();
        }

        public B setSpeed(double speed){
            this.speed = speed;
            return getThis();
        }
    }
}
