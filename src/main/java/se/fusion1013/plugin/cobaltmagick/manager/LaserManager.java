package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.laser.SimpleLaser;

import java.util.ArrayList;
import java.util.List;

public class LaserManager extends Manager implements Runnable {

    private static LaserManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static LaserManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LaserManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    private final List<SimpleLaser> lasers;
    private BukkitTask laserTask;

    public LaserManager(CobaltCore cobaltCore) {
        super(cobaltCore);

        this.lasers = new ArrayList<>();
        this.laserTask = null;
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.cobaltCore, () -> {
            long ticks = 1;
            this.laserTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.cobaltCore, this, 0, ticks);
        }, 5);
    }

    @Override
    public void disable() {
        if (laserTask != null){
            this.laserTask.cancel();
            this.laserTask = null;
        }

        this.lasers.clear();
    }

    @Override
    public void run() {
    }
}
