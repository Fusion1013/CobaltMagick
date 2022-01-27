package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import se.fusion1013.plugin.cobaltmagick.state.ICobaltOperator;
import se.fusion1013.plugin.cobaltmagick.state.StateEngine;

import java.util.ArrayList;
import java.util.List;

public class StateEntity extends AbstractCustomEntity {

    // ----- CONSTRUCTORS -----

    public StateEntity(EntityType baseEntity, String inbuiltName) {
        super(baseEntity, inbuiltName);
    }

    // ----- STATE VARIABLES -----

    private List<StateEngine> stateEngines = new ArrayList<>();
    ICobaltOperator<StateEntity> executeOnSpawn = null;

    // ----- EVENTS -----

    @Override
    public void spawn(Location location) {
        super.spawn(location);
        if (executeOnSpawn != null) executeOnSpawn.performOperation(this, null);
    }

    @Override
    public void tick() {
        super.tick();

        // Performs one tick of all state engines
        for (StateEngine stateEngine : stateEngines) {
            stateEngine.tick(this);
        }
    }

    @Override
    public void onDeath() {
        super.onDeath();
    }

    // ----- STATE ENTITY BUILDER -----

    public static class StateEntityBuilder extends AbstractCustomEntityBuilder<StateEntity, StateEntityBuilder> {

        // ----- VARIABLES -----

        List<StateEngine> stateEngines = new ArrayList<>();

        // ----- CONSTRUCTOR -----

        public StateEntityBuilder(EntityType baseEntity, String inbuiltName) {
            super(baseEntity, inbuiltName);
        }

        // ----- BUILDER METHODS -----

        public StateEntityBuilder addStateEngine(StateEngine<StateEntity> engine) {
            stateEngines.add(engine);
            return getThis();
        }

        // ----- MANAGEMENT -----

        /**
         * Creates a new <code>StateEntity</code>
         * @return a new <code>StateEntity</code>
         */
        @Override
        public StateEntity build() {
            obj.stateEngines = stateEngines;

            return super.build();
        }

        @Override
        protected StateEntity createObj() {
            return new StateEntity(baseEntity, inbuiltName);
        }

        @Override
        protected StateEntityBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    public void addStateEngine(StateEngine<StateEntity> engine) {
        stateEngines.add(engine);
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    /**
     * Clone Constructor. Creates a copy of the target
     *
     * @param target the StateEntity to create a copy of
     */
    public StateEntity(StateEntity target) {
        super(target);

        this.stateEngines = new ArrayList<>();
        for (StateEngine<StateEntity> engine : target.stateEngines) {
            stateEngines.add(engine.clone());
        }
        this.executeOnSpawn = target.executeOnSpawn;
    }

    /**
     * Returnes a copy of the object.
     *
     * @return a copy of the object.
     */
    @Override
    public StateEntity clone() {
        return new StateEntity(this);
    }
}
