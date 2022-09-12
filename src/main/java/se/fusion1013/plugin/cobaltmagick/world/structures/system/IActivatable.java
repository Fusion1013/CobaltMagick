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
     *
     * @param uuid the <code>UUID</code> of the object that called this method.
     */
    default void activate(UUID uuid) {}

    /**
     * Activates the <code>IActivatable</code>.
     */
    void activate();

    /**
     * Deactivates the <code>IActivatable</code>.
     *
     * @param uuid the <code>UUID</code> of the object that called this method.
     */
    default void deactivate(UUID uuid) {}

    /**
     * Deactivates the <code>IActivatable</code>.
     */
    default void deactivate() {}

    /**
     * Get the <code>UUID</code> of the <code>IActivatable</code>.
     *
     * @return the <code>UUID</code> of the <code>IActivatable</code>.
     */
    UUID getUuid();

}
