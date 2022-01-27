package se.fusion1013.plugin.cobaltmagick.state;

/**
 * Defines a switch criteria for a state.
 */
public interface ISwitchCriteria<T> {

    /**
     * Checks if all conditions have been achieved to switch to this state.
     *
     * @param item the item to check.
     * @return if all criteria have been achieved to warrant a switch to this state.
     */
    boolean switchCriteriaAchieved(T item, CobaltState state);
}
