package se.fusion1013.plugin.cobalt.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobalt.particle.styles.IParticleStyle;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyle;

import java.util.ArrayList;
import java.util.List;

public class ParticleGroup implements IParticleGroup {

    private List<ParticleStyle> particleStyleList; // List of particles in the group

    public void display(Location location){
        for (Player p : Bukkit.getOnlinePlayers()){
            for (IParticleStyle ps : particleStyleList){
                List<PParticle> particles = ps.getParticles(location);

                for (PParticle particle : particles){
                    p.spawnParticle(ps.getParticle(), particle.getLocation(), particle.getCount(), particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    // ---------- GETTERS / SETTERS ----------

    public void setParticleStyles(List<ParticleStyle> styles) { this.particleStyleList = styles; }

    /**
     * Builds a new particle group
     */
    public static class ParticleGroupBuilder {

        ParticleGroup obj;
        List<ParticleStyle> styles = new ArrayList<>();

        public ParticleGroupBuilder(){
            obj = new ParticleGroup();
        }

        public ParticleGroup build(){
            obj.setParticleStyles(styles);

            return obj;
        }

        public ParticleGroupBuilder addStyle(ParticleStyle style){
            styles.add(style);
            return this;
        }
    }
}
