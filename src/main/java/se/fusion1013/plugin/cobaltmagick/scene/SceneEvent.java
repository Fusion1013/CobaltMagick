package se.fusion1013.plugin.cobaltmagick.scene;

import org.bukkit.Location;

public class SceneEvent {

    private final double startTime;
    private final double endTime;

    private final ISceneOperator operator;

    public boolean executed = false;

    public SceneEvent(double startTime, double endTime, ISceneOperator operator) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.operator = operator;
    }

    public void attemptRun(Location location, double currentTime) {
        if ((startTime <= currentTime && currentTime <= endTime) || (!executed && currentTime > endTime)) {
            executed = true;
            operator.execute(location);
        }
    }

}
