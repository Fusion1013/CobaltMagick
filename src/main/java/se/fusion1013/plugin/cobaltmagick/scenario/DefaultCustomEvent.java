package se.fusion1013.plugin.cobaltmagick.scenario;

import org.jetbrains.annotations.Nullable;
import se.fusion1013.plugin.cobaltmagick.flags.Flag;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class DefaultCustomEvent implements CustomEvent {

    private String internalEventName;
    private ConcurrentMap<Flag<?>, Object> flags = new ConcurrentHashMap<>();
    private boolean dirty = true;

    public DefaultCustomEvent(String internalEventName){
        this.internalEventName = internalEventName;
    }

    @Override
    public String getInternalName() {
        return this.internalEventName;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public List<String> getPossibleFlags() {
        return null;
    }

    public <T extends Flag<V>, V> void setFlag(T flag, @Nullable V val){
        checkNotNull(flag);
        setDirty(true);

        if (val == null){
            flags.remove(flag);
        } else {
            flags.put(flag, val);
        }
    }

    public void setDirty(boolean dirty){
        this.dirty = dirty;
    }
}
