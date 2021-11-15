package se.fusion1013.plugin.cobalt.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public interface IParticleGroup {

    void display();

    boolean addParticle(String style);
    boolean addParticle(String style, Particle particle);
    boolean addParticle(String style, Particle particle, Vector offset);

    Location getLocation();
    void setLocation(Location location);

    String getName();
    void setName(String name);
}
