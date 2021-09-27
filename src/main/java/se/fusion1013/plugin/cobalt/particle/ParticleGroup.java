package se.fusion1013.plugin.cobalt.particle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyleCube;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyleSphere;

import java.util.ArrayList;
import java.util.List;

public class ParticleGroup {

    private List<ParticleStyle> particleStyleList; // List of particles in the group
    private Location location; // The location of the emitter
    private String name; // The name of the emitter
    private int id; // The id of the emitter

    public ParticleGroup(Location location, String name, int id){
        particleStyleList = new ArrayList<>();
        this.location = location;
        this.name = name;
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        if (name != null) return name + " (" + particleStyleList.size() + ")";
        else return "Group (" + particleStyleList.size() + ")";
    }

    public boolean addParticle(String styleName, Particle particle, Vector offset){
        ParticleStyle style = null;
        switch (styleName){
            case "cube":
                style = new ParticleStyleCube(particle);
                break;
            case "sphere":
                style = new ParticleStyleSphere(particle);
                break;
        }
        if (style != null) {
            particleStyleList.add(style);
            return true;
        }
        return false;
    }

    public void display(){
        for (Player p : Bukkit.getOnlinePlayers()){
            for (ParticleStyle ps : particleStyleList){
                List<PParticle> particles = ps.getParticles(location);

                for (PParticle particle : particles){
                    p.spawnParticle(ps.getParticle(), particle.getLocation(), 1, particle.getxOff(), particle.getyOff(), particle.getzOff(), particle.getSpeed());
                }
            }
        }
    }

    public Location getLocation(){
        return this.location;
    }

    public List<ParticleStyle> getParticleStyleList(){
        return particleStyleList;
    }
}
