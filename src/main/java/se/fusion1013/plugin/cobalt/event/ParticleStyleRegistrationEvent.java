package se.fusion1013.plugin.cobalt.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParticleStyleRegistrationEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Map<String, ParticleStyle> registeredStyles;
    private Map<String, ParticleStyle> registeredEventStyles;

    public ParticleStyleRegistrationEvent(){
        super(!Bukkit.isPrimaryThread());
        this.registeredStyles = new HashMap<>();
        this.registeredEventStyles = new HashMap<>();
    }

    public Map<String, ParticleStyle> getRegisteredStyles(){
        return Collections.unmodifiableMap(this.registeredStyles);
    }

    public Map<String, ParticleStyle> getRegisteredEventStyles(){
        return Collections.unmodifiableMap(this.registeredEventStyles);
    }

    public boolean registerStyle(ParticleStyle style){
        if (this.registeredEventStyles.containsKey(style.getInternalName())){
            this.registeredEventStyles.remove(style.getInternalName());
            this.registeredStyles.put(style.getInternalName(), style);
            return false;
        }

        return this.registeredStyles.put(style.getInternalName(), style) == null;
    }

    public boolean registerEventStyle(ParticleStyle style){
        if (this.registeredStyles.containsKey(style.getInternalName())){
            this.registeredStyles.remove(style.getInternalName());
            this.registeredEventStyles.put(style.getInternalName(), style);
            return false;
        }

        return this.registeredEventStyles.put(style.getInternalName(), style) == null;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}
