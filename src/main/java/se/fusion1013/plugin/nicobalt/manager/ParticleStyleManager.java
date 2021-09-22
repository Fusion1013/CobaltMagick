package se.fusion1013.plugin.nicobalt.manager;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.nicobalt.Nicobalt;
import se.fusion1013.plugin.nicobalt.event.ParticleStyleRegistrationEvent;
import se.fusion1013.plugin.nicobalt.particle.styles.DefaultParticleStyles;
import se.fusion1013.plugin.nicobalt.particle.styles.ParticleStyle;
import se.fusion1013.plugin.nicobalt.particle.styles.ParticleStyleCube;

import java.util.*;

public class ParticleStyleManager extends Manager {

    private final Map<String, ParticleStyle> stylesByName;
    private final Map<String, ParticleStyle> stylesByInternalName;
    private final List<ParticleStyle> eventStyles;

    public ParticleStyleManager(Nicobalt nicobalt){
        super(nicobalt);

        this.stylesByName = new LinkedHashMap<>();
        this.stylesByInternalName = new LinkedHashMap<>();
        this.eventStyles = new ArrayList<>();
    }

    @Override
    public void reload() {
        this.stylesByName.clear();
        this.stylesByInternalName.clear();

        Bukkit.getScheduler().runTask(this.nicobalt, () ->{
            ParticleStyleRegistrationEvent event = new ParticleStyleRegistrationEvent();
            Bukkit.getPluginManager().callEvent(event);

            Collection<ParticleStyle> eventStyles = event.getRegisteredEventStyles().values();
            List<ParticleStyle> styles = new ArrayList<>(event.getRegisteredStyles().values());
            styles.addAll(eventStyles);
            styles.sort(Comparator.comparing(ParticleStyle::getName));

            for (ParticleStyle style : styles){
                try {
                    if (style == null) throw new IllegalArgumentException("Tried to register a null style");

                    if (style.getInternalName() == null || style.getInternalName().trim().isEmpty())
                        throw new IllegalArgumentException("Tried to register a style with a null or empty name: '" + style.getInternalName() + "'");
                    if (this.stylesByName.containsValue(style))
                        throw new IllegalArgumentException("Tried to register the same style twice: '" + style.getInternalName() + "'");
                    if (this.stylesByInternalName.containsKey(style.getInternalName().toLowerCase()))
                        throw new IllegalArgumentException("Tried to register two styles with the same internal name spelling: '" + style.getInternalName() + "'");

                    this.stylesByName.put(style.getName().toLowerCase(), style);
                    this.stylesByInternalName.put(style.getInternalName().toLowerCase(), style);

                    if (eventStyles.contains(style)){
                        this.eventStyles.contains(style);
                    }
                } catch (IllegalArgumentException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void disable() {

    }

    public ParticleStyle getStyleByName(String name){
        ParticleStyle style = this.stylesByName.get(name.toLowerCase());
        if (style != null && !style.isEnabled()){
            style = null;
        }
        return style;
    }

    public ParticleStyle getStyleByInternalName(String internalName){
        ParticleStyle style = this.stylesByInternalName.get(internalName.toLowerCase());
        if (style != null && !style.isEnabled()){
            style = null;
        }
        return style;
    }
}
