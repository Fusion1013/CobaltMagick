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
    private Location location; // The location of the emitter
    private String name; // The name of the emitter

    //---------- CONSTRUCTORS ----------

    public ParticleGroup(String name, Location location){
        particleStyleList = new ArrayList<>();
        this.name = name;
        this.location = location;
    }

    // ---------- PARTICLE LOGIC ----------

    public boolean addParticle(String styleName){
        return addParticle(styleName, Particle.FLAME, new Vector());
    }

    public boolean addParticle(String styleName, Particle particle){
        return addParticle(styleName, particle, new Vector());
    }

    // TODO: Implement offset
    public boolean addParticle(String styleName, Particle particle, Vector offset){
        ParticleStyleManager particleStyleManager = Cobalt.getInstance().getManager(ParticleStyleManager.class);

        ParticleStyle style = particleStyleManager.getStyleByName(styleName);

        if (style != null) {
            style.setParticle(particle);
            particleStyleList.add(style);
            return true;
        }
        return false;
    }

    public boolean addParticle(ParticleStyle style){
        return particleStyleList.add(style);
    }

    public void display(){
        for (Player p : Bukkit.getOnlinePlayers()){
            for (IParticleStyle ps : particleStyleList){
                List<PParticle> particles = ps.getParticles(location);

                for (PParticle particle : particles){
                    p.spawnParticle(ps.getParticle(), particle.getLocation(), 1, particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    // ---------- GETTERS / SETTERS ----------

    public String getName(){
        if (name != null) return name + " (" + particleStyleList.size() + ")";
        else return "ParticleGroup(" + particleStyleList.size() + ")";
    }

    public void setName(String name){
        this.name = name;
    }

    public Location getLocation(){
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getStyleDescriptions(){
        List<String> descriptions = new ArrayList<>();
        for (ParticleStyle style : particleStyleList){
            descriptions.add(style.getInternalName());
        }
        return descriptions;
    }
}
