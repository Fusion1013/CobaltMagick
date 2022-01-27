package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager extends Manager implements Runnable {

    // ----- VARIABLES -----

    private final List<ParticleGroup> particleGroups; // Contains all particle groups
    private BukkitTask particleTask;

    // ----- CONSTRUCTOR -----

    public ParticleManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);

        this.particleGroups = new ArrayList<>();
        this.particleTask = null;
    }

    // ----- PARTICLE TASK -----

    @Override
    public void run() {
        for (ParticleGroup pg : particleGroups){
            // pg.display();
        }
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.cobaltMagick, () -> {
            long ticks = 1;
            this.particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.cobaltMagick, this, 0, ticks);
        }, 5);
    }

    @Override
    public void disable() {
        if (this.particleTask != null){
            this.particleTask.cancel();
            this.particleTask = null;
        }

        this.particleGroups.clear();
    }

    // ----- GETTERS / SETTERS -----

    public void createParticleGroup() {

    }

    // ----- INSTANCE -----

    private static ParticleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ParticleManager</code>.
     *
     * @return The object of this class
     */
    public static ParticleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ParticleManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
