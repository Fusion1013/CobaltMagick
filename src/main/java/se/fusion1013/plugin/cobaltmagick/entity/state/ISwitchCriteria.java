package se.fusion1013.plugin.cobaltmagick.entity.state;

import se.fusion1013.plugin.cobaltmagick.entity.ICustomEntity;

/**
 * Defines a switch criteria for a state.
 */
public interface ISwitchCriteria {

    /**
     * Checks if all conditions have been achieved to switch to this state.
     *
     * @param entity the custom entity.
     * @return if all criteria have been achieved to warrant a switch to this state.
     */
    boolean switchCriteriaAchieved(ICustomEntity entity);
}
