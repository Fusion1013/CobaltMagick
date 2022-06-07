package se.fusion1013.plugin.cobaltmagick.world.structures.system;

import java.util.UUID;

public interface Unlockable {
    void unlock();
    boolean isLocked();
    UUID getUuid();
}
