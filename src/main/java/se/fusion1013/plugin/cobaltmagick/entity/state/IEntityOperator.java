package se.fusion1013.plugin.cobaltmagick.entity.state;

import se.fusion1013.plugin.cobaltmagick.entity.ICustomEntity;

/**
 * Defines an operator performed on a custom entity every tick
 */
public interface IEntityOperator { // TODO: Rename to something more appropriate
    /**
     * Defines an operator performed on a custom entity every tick
     */
    void performOperation(ICustomEntity entity);
}
