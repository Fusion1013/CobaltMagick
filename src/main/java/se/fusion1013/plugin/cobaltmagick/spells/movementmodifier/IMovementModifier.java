package se.fusion1013.plugin.cobaltmagick.spells.movementmodifier;

import org.bukkit.util.Vector;

/**
 * Represents a modifier that modifies the movement of a spell
 */
public interface IMovementModifier {
    /**
     * Modifies the current velocity of the spell
     *
     * @param currentVelocity the current velocity of the spell
     * @return new vector
     */
    Vector modifyVelocityVector(Vector currentVelocity);
}
