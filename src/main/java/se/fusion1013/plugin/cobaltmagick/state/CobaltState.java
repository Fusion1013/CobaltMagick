package se.fusion1013.plugin.cobaltmagick.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a CobaltState.
 * T defines the operator
 * B defines what the operator operates on
 */
public class CobaltState<T> implements Cloneable {

    // ----- STATE VARIABLES -----

    ICobaltOperator<T> enterOperation = null; // The operation that is going to be performed when this state is entered
    ICobaltOperator<T> tickOperation = null; // The operation that is going to be performed every tick
    ICobaltOperator<T> exitOperation = null; // The operation that is going to be performed when this state is exited

    ISwitchCriteria<T> switchCriteria; // Checks if all criteria have been achieved for a switch to this state to be made
    int switchPriority; // The priority level for the switch, lower number >> higher priority

    CobaltState<T>[] connectedStates = new CobaltState[0]; // The connected states

    Map<String, Object> stateObjects = new HashMap<>(); // This map holds objects that need to be persistent

    // ----- CONSTRUCTOR -----

    /**
     * Creates a new Entity State.
     *
     * @param switchCriteria the criteria that must be met to switch to this state.
     * @param switchPriority the priority this switch holds over other switches.
     */
    public CobaltState(ISwitchCriteria<T> switchCriteria, int switchPriority) {
        this.switchCriteria = switchCriteria;
        this.switchPriority = switchPriority;
    }

    // ----- STATE EVENTS -----

    /**
     * Performs the entry operation for this state.
     *
     * @param item the item to perform the operation on.
     * @param state the state that is performing the operation
     */
    public void performStateEnter(T item, CobaltState<T> state) {
        if (enterOperation != null) enterOperation.performOperation(item, state);
    }

    /**
     * Performs one iteration of the state.
     *
     * @param item the item to perform the operation on.
     * @param state the state that is performing the operation.
     */
    public void performStateTick(T item, CobaltState<T> state) {
        if (tickOperation != null) tickOperation.performOperation(item, state);
    }

    /**
     * Performs the exit operation for this state.
     *
     * @param item the item to perform the operation on.
     * @param state the state that is performing the operation.
     */
    public void performStateExit(T item, CobaltState<T> state) {
        if (exitOperation != null) exitOperation.performOperation(item, state);
    }

    // ----- STATE TRAVERSAL -----

    /**
     * Returns the new state after this one. May return the same state if no connected states have achieved all their criteria.
     *
     * @param item the item
     * @return new state
     */
    public CobaltState<T> getNewState(T item) {
        CobaltState<T> newState = this;

        for (CobaltState<T> state : connectedStates) {
            if (state.switchCriteria.switchCriteriaAchieved(item, this) && newState == this) newState = state.clone();
            if (state.switchCriteria.switchCriteriaAchieved(item, this) && state.switchPriority < newState.switchPriority) newState = state.clone();
        }

        // If the current state was switched, perform the exit operation for the current state, and the enter operation for the new state
        if (newState != this) {
            performStateExit(item, this);
            newState.performStateEnter(item, newState);
        }

        return newState;
    }

    // ----- GETTERS / SETTERS -----

    public CobaltState<T> setEnterOperation(ICobaltOperator<T> operation) {
        this.enterOperation = operation;
        return this;
    }

    public CobaltState<T> setTickOperation(ICobaltOperator<T> operation) {
        this.tickOperation = operation;
        return this;
    }

    public CobaltState<T> setExitOperation(ICobaltOperator<T> operation) {
        this.exitOperation = operation;
        return this;
    }

    @SafeVarargs
    public final CobaltState<T> setConnectedStates(CobaltState<T>... states) {
        this.connectedStates = states;
        return this;
    }

    public void addPersistentObject(String key, Object object) {
        stateObjects.put(key, object);
    }

    public Object getPersistentObject(String key) {
        return stateObjects.get(key);
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public CobaltState(CobaltState<T> target) {
        this.enterOperation = target.enterOperation;
        this.tickOperation = target.tickOperation;
        this.exitOperation = target.tickOperation;

        this.switchCriteria = target.switchCriteria;
        this.switchPriority = target.switchPriority;

        this.connectedStates = target.connectedStates;

        this.stateObjects = new HashMap<>(); // Do not copy this, as it should not persist.
    }

    @Override
    public CobaltState<T> clone() {
        try {
            super.clone();
            return new CobaltState<T>(this);
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
