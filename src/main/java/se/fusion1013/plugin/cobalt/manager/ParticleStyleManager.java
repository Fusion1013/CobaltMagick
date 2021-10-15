package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.styles.*;

import java.util.*;

public class ParticleStyleManager extends Manager {

    public static final Map<String, ParticleStyle> INBUILT_PARTICLE_STYLES = new HashMap<>();

    public static final ParticleStyle CUBE = register(new ParticleStyleCube());
    public static final ParticleStyle ICOSPHERE = register(new ParticleStyleIcosphere());
    public static final ParticleStyle LINE = register(new ParticleStyleLine());
    public static final ParticleStyle SPHERE = register(new ParticleStyleSphere());

    public ParticleStyleManager(Cobalt cobalt){
        super(cobalt);
    }

    private static <T extends DefaultParticleStyles> T register(final T particleStyle){
        INBUILT_PARTICLE_STYLES.put(particleStyle.getName(), particleStyle);
        return particleStyle;
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {
    }

    public List<String> getStyleNames(){
        return new ArrayList<String>(INBUILT_PARTICLE_STYLES.keySet());
    }

    public ParticleStyle getStyleByName(String name){
        ParticleStyle style = this.INBUILT_PARTICLE_STYLES.get(name.toLowerCase());
        if (style != null && !style.isEnabled()){
            style = null;
        }
        return style;
    }
}
