package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.PParticle;
import se.fusion1013.plugin.cobalt.util.VectorUtils;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleCube extends DefaultParticleStyles implements ParticleStyle {

    private int step = 0;
    private boolean skipNextStep = false; // Only spawn every 2 ticks

    private double edgeLength;
    private double angularVelocityX;
    private double angularVelocityY;
    private double angularVelocityZ;
    private int particlesPerEdge;
    private Particle particle;

    public ParticleStyleCube(){
        this(Particle.BARRIER);
    }

    public ParticleStyleCube(Particle particle) {
        super("cube");
        this.particle = particle;
        setDefaultSettings();
    }

    public void setParticle(Particle particle){
        this.particle = particle;
    }

    @Override
    public List<PParticle> getParticles(Location location) {
        List<PParticle> pparticles = new ArrayList<>();

        if (this.skipNextStep)
            return pparticles;

        double xRotation = this.step * this.angularVelocityX;
        double yRotation = this.step * this.angularVelocityY;
        double zRotation = this.step * this.angularVelocityZ;
        double a = this.edgeLength / 2;
        double angleX, angleY;
        Vector v = new Vector();
        for (int i = 0; i < 4; i++) {
            angleY = i * Math.PI / 2;
            for (int j = 0; j < 2; j++) {
                angleX = j * Math.PI;
                for (int p = 0; p <= this.particlesPerEdge; p++) {
                    v.setX(a).setY(a);
                    v.setZ(this.edgeLength * p / this.particlesPerEdge - a);
                    VectorUtils.rotateAroundAxisX(v, angleX);
                    VectorUtils.rotateAroundAxisY(v, angleY);
                    VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
                    pparticles.add(new PParticle(location.clone().add(v)));
                }
            }
            for (int p = 0; p <= this.particlesPerEdge; p++) {
                v.setX(a).setZ(a);
                v.setY(this.edgeLength * p / this.particlesPerEdge - a);
                VectorUtils.rotateAroundAxisY(v, angleY);
                VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
                pparticles.add(new PParticle(location.clone().add(v)));
            }
        }

        step+=1;

        return pparticles;
    }

    protected void setDefaultSettings() {
        edgeLength = 10.0;
        angularVelocityX = 0.00314159265;
        angularVelocityY = 0.00369599135;
        angularVelocityZ = 0.00405366794;
        particlesPerEdge = 100;
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
