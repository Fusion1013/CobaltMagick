package se.fusion1013.plugin.cobaltmagick.scene;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    // ----- VARIABLES -----

    private final String sceneName;

    private double duration = 0; // Measured in milliseconds, not ticks
    private double timer;
    private long time;

    private List<SceneEvent> sceneEvents = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public Scene(String sceneName) {
        this.sceneName = sceneName;
    }

    // ----- START SCENE -----

    BukkitTask task;

    public void play(Location location) {

        timer = 0;
        time = System.currentTimeMillis();
        for (SceneEvent event : sceneEvents) event.executed = false;

        CobaltMagick.getInstance().getLogger().info("Executing scene '" + sceneName + "'");

        task = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {

            // Increment Timer
            timer += System.currentTimeMillis() - time;
            time = System.currentTimeMillis();

            // Do stuff
            for (SceneEvent event : sceneEvents) event.attemptRun(location, timer);

            if (timer >= duration) task.cancel();
        }, 0, 1);
    }

    // ----- BUILDER METHODS -----

    public Scene addEvent(SceneEvent event) {
        sceneEvents.add(event);
        if (duration < event.getEndTime()) duration = event.getEndTime();
        return this;
    }

}
