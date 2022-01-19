package se.fusion1013.plugin.cobaltmagick.entity.state;

import se.fusion1013.plugin.cobaltmagick.entity.ICustomEntity;

/**
 * Defines a state for a CustomEntity
 */
public class EntityState {

    IEntityOperator operation; // The operation that is going to be performed every tick
    ISwitchCriteria switchCriteria; // Checks if all criteria have been achieved for a switch to this state to be made
    int switchPriority; // The priority level for the switch, lower number >> higher priority

    EntityState[] connectedStates; // The connected states

    /**
     * Creates a new Entity State.
     *
     * @param operation the operation this state will execute every tick
     */
    public EntityState(IEntityOperator operation, ISwitchCriteria switchCriteria, int switchPriority, EntityState... connectedStates) {
        this.operation = operation;
        this.switchCriteria = switchCriteria;
        this.switchPriority = switchPriority;
        this.connectedStates = connectedStates;
    }

    /**
     * Performs one iteration of the state.
     *
     * @param customEntity the custom entity
     */
    public void performStateTick(ICustomEntity customEntity) {
        operation.performOperation(customEntity);
    }

    /**
     * Returns the new state after this one. May return the same state if no connected states have achieved all their criteria.
     *
     * @param customEntity the custom entity
     * @return new state
     */
    public EntityState getNewState(ICustomEntity customEntity) {
        EntityState newState = this;

        for (EntityState state : connectedStates) {
            if (state.switchCriteria.switchCriteriaAchieved(customEntity) && state.switchPriority < newState.switchPriority) newState = state;
        }

        return newState;
    }
}
