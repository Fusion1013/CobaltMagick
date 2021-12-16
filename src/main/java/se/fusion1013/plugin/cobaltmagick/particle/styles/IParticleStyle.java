package se.fusion1013.plugin.cobaltmagick.particle.styles;

import org.bukkit.Location;
import org.bukkit.Particle;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltmagick.particle.PParticle;

import java.util.List;

public interface IParticleStyle {

    void setParticle(Particle particle);

    String getInternalName();

    default String getName(){
        return this.getInternalName();
    }

    boolean isEnabled();

    Particle getParticle();

    List<PParticle> getParticles(Location location);

    List<PParticle> getParticles(Location startLocation, Location endLocation);

    Object getExtra();

    static IParticleStyle fromName(String styleName){
        return CobaltMagick.getInstance().getManager(ParticleStyleManager.class).getStyleByName(styleName);
    }
}
