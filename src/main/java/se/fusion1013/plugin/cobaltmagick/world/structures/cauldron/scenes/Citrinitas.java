package se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.scene.Scene;
import se.fusion1013.plugin.cobaltmagick.scene.SceneEvent;

public class Citrinitas {

    // ----- VARIABLES -----

    private static Player amuletOwner;
    private static final Scene CITRINITAS_SCENE = new Scene("citrinitas");
    private static final boolean IS_SCENES_REGISTERED = registerSceneEvents();

    private static final Vector CAULDRON_OFFSET = new Vector(0.5, 3, 0.5);

    // ----- INIT -----

    public static void start(Location location, Player amuletOwner) {
        Citrinitas.amuletOwner = amuletOwner;
        initParticles();
        CITRINITAS_SCENE.play(location);
    }

    // ----- REGISTER SCENE -----

    private static boolean registerSceneEvents() {

        return true;
    }

    // ----- SCENE EVENTS -----

    // ----- PARTICLES -----

    private static void initParticles() {

    }
}
