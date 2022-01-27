package se.fusion1013.plugin.cobaltmagick.state;

/**
 * Holds a number of state that it switches between.
 */
public class StateEngine<T> implements Cloneable {

    // ----- VARIABLES -----

    CobaltState<T> currentState; // The current state the engine is in.

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new state engine with a start state.
     *
     * @param startState the starting state.
     */
    public StateEngine(CobaltState<T> startState) {
        currentState = startState;
    }


    /**
     * Performs one tick of the State Engine
     *
     * @param item the item that the state engine is operating on
     */
    public void tick(T item) {

        // ----- ATTEMPT STATE SWITCH -----

        currentState = currentState.getNewState(item);

        // ----- PERFORM CURRENT STATE OPERATIONS -----

        currentState.performStateTick(item, currentState);
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public StateEngine(StateEngine<T> target) {
        this.currentState = target.currentState.clone(); // TODO: CLONE
    }

    @Override
    public StateEngine<T> clone() {
        try {
            super.clone();
            return new StateEngine<T>(this);
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
