package se.fusion1013.plugin.cobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobalt.particle.PParticle;

import java.util.List;

public interface ParticleStyle {

    String getInternalName();

    default String getName(){
        return this.getInternalName();
    }

    boolean isEnabled();

    Particle getParticle();

    List<PParticle> getParticles(Location location);

    static ParticleStyle fromName(String styleName){
        return Cobalt.getInstance().getManager(ParticleStyleManager.class).getStyleByName(styleName);
    }
}
