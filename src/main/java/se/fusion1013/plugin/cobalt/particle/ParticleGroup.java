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

public class ParticleGroup implements IParticleGroup, Cloneable {

    private List<ParticleStyle> particleStyleList; // List of particles in the group

    public ParticleGroup(){}

    public ParticleGroup(ParticleGroup target){
        this.particleStyleList = cloneList(target.particleStyleList);
    }

    // TODO: Move to abstract particle style class
    public static List<ParticleStyle> cloneList(List<ParticleStyle> list) {
        List<ParticleStyle> clone = new ArrayList<ParticleStyle>(list.size());
        for (ParticleStyle item : list) clone.add(item.clone());
        return clone;
    }

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

    @Override
    public ParticleGroup clone() {
        return new ParticleGroup(this);
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
