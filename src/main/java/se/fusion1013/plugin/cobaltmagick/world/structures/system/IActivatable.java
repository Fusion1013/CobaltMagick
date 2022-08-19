package se.fusion1013.plugin.cobaltmagick.world.structures.system;

import java.util.UUID;

public interface IActivatable {

    /**
     * True if the <code>IActivatable</code> is active.
     *
     * @return whether it is active or not.
     */
    boolean isActive();

    /**
     * Activates the <code>IActivatable</code>.
     */
    void activate();

    /**
     * Get the <code>UUID</code> of the <code>IActivatable</code>.
     *
     * @return the <code>UUID</code> of the <code>IActivatable</code>.
     */
    UUID getUuid();

}
