package se.fusion1013.plugin.cobalt.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyle;

import java.util.ArrayList;
import java.util.List;

public class ParticleGroupBuilder {

    String name;
    Location location;
    List<ParticleStyle> styles = new ArrayList<>();

    public ParticleGroupBuilder(String name, Location location){
        this.name = name;
        this.location = location;
    }

    public ParticleGroupBuilder addStyle(ParticleStyle style, Particle particle){
        styles.add(style);
        return this;
    }

    public ParticleGroup build(){
        ParticleGroup group = new ParticleGroup(name, location);

        // Add all particles
        for (ParticleStyle style : styles){
            group.addParticle(style);
        }

        return group;
    }

    public static class ParticleStyleBuilder{

        ParticleStyle style;

        public ParticleStyleBuilder(String style){
            this.style = ParticleStyleManager.getStyleByName(style);
        }

        public ParticleStyleBuilder setParticle(Particle particle){
            style.setParticle(particle);
            return this;
        }

        public ParticleStyleBuilder setDoubleParameter(String key, double p){
            style.setDouble(key, p);
            return this;
        }

        public ParticleStyle build(){
            return style;
        }
    }
}
