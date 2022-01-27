package se.fusion1013.plugin.cobaltmagick.state;

/**
 * A Timed State is a state that can not be exited before a set amount of time has passed.
 *
 * @param <T> the type of the state
 */
public class TimedState<T> extends CobaltState<T> implements Cloneable {

    // ----- VARIABLES -----

    int timer;
    int currentTick;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new Entity State.
     *
     * @param switchCriteria the criteria that must be met to switch to this state.
     * @param switchPriority the priority this switch holds over other switches.
     * @param timer the time in ticks that must pass before this state can be exited.
     */
    public TimedState(ISwitchCriteria<T> switchCriteria, int switchPriority, int timer) {
        super(switchCriteria, switchPriority);
        this.timer = timer;
    }

    // ----- STATE EVENTS -----

    @Override
    public void performStateTick(T item, CobaltState<T> state) {
        super.performStateTick(item, state);
        currentTick++;
    }

    @Override
    public void performStateExit(T item, CobaltState<T> state) {
        super.performStateExit(item, state);
        currentTick = 0;
    }

    // ----- STATE TRAVERSAL -----

    @Override
    public CobaltState<T> getNewState(T item) {
        if (currentTick >= timer) return super.getNewState(item);
        else return this;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public TimedState(TimedState<T> target) {
        super(target);

        this.currentTick = target.currentTick;
        this.timer = target.timer;
    }

    @Override
    public CobaltState<T> clone() {
        super.clone();
        return new TimedState<T>(this);
    }
}
