package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Particle;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.particle.styles.*;

import java.util.*;

public class ParticleStyleManager extends Manager {

    private static ParticleStyleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static ParticleStyleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ParticleStyleManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    public static final Map<String, ParticleStyle> INBUILT_PARTICLE_STYLES = new HashMap<>();

    public static final ParticleStyle CUBE = register(new ParticleStyleCube());
    public static final ParticleStyle ICOSPHERE = register(new ParticleStyleIcosphere());
    public static final ParticleStyle LINE = register(new ParticleStyleLine());
    public static final ParticleStyle SPHERE = register(new ParticleStyleSphere(Particle.FLAME));

    public ParticleStyleManager(CobaltMagick cobaltMagick){
        super(cobaltMagick);
    }

    private static <T extends ParticleStyle> T register(final T particleStyle){
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
        return style.clone();
    }
}
