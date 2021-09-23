package se.fusion1013.plugin.nicobalt.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.nicobalt.Cobalt;
import se.fusion1013.plugin.nicobalt.manager.ParticleStyleManager;
import se.fusion1013.plugin.nicobalt.particle.PParticle;

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
