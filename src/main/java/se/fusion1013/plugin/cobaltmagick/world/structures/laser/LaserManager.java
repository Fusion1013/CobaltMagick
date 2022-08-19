package se.fusion1013.plugin.cobaltmagick.world.structures.laser;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.world.chunk.ChunkBoundObjectManager;
import se.fusion1013.plugin.cobaltcore.world.chunk.IChunkBound;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.laser.ILaserDao;

import java.util.*;

public class LaserManager extends Manager implements Runnable {

    // ----- VARIABLES -----

    private final Map<UUID, SimpleLaser> LASER_EMITTERS = new HashMap<>();
    private BukkitTask laserTask;

    // ----- CONSTRUCTORS -----

    public LaserManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- CREATION -----

    public SimpleLaser createLaserEmitter(Location location) {
        SimpleLaser laser = new SimpleLaser(location);
        LASER_EMITTERS.put(laser.getUUID(), laser);
        core.getManager(CobaltCore.getInstance(), DataManager.class).getDao(ILaserDao.class).insertSimpleLaserAsync(laser);
        ChunkBoundObjectManager.addChunkLoadableObject(location.getChunk(), laser);
        return laser;
    }

    // ----- REMOVAL -----

    public SimpleLaser removeLaser(UUID uuid) {
        SimpleLaser laser = LASER_EMITTERS.remove(uuid);
        core.getManager(CobaltCore.getInstance(), DataManager.class).getDao(ILaserDao.class).removeLaserAsync(uuid);
        ChunkBoundObjectManager.removeChunkBound(SimpleLaser.class, uuid);
        return laser;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Load all lasers from the database
        List<SimpleLaser> lasers = core.getManager(CobaltCore.getInstance(), DataManager.class).getDao(ILaserDao.class).getSimpleLasers();
        for (SimpleLaser laser : lasers) {
            LASER_EMITTERS.put(laser.getUUID(), laser);
            ChunkBoundObjectManager.addChunkLoadableObject(laser.getStartLocation().getChunk(), laser);
        }

        // Register the laser task
        this.laserTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 1, 2);
    }

    @Override
    public void disable() {
        if (laserTask != null){
            this.laserTask.cancel();
        }
    }

    // ----- LASER TASK -----

    @Override
    public void run() {
        List<IChunkBound<?>> laserList = ChunkBoundObjectManager.getLoadedOfType(SimpleLaser.class);
        for (IChunkBound<?> laser : laserList) {
            if (laser instanceof SimpleLaser simpleLaser) simpleLaser.tick();
        }
    }

    // ----- GETTERS / SETTERS -----

    public String[] getLaserIdentifiers() {
        List<String> laserIdentifiers = new ArrayList<>();
        for (UUID uuid : LASER_EMITTERS.keySet()) {
            laserIdentifiers.add(uuid.toString());
        }
        return laserIdentifiers.toArray(new String[0]);
    }

    // ----- INSTANCE CONSTRUCTOR & METHOD -----

    private static LaserManager INSTANCE = null;
    /**
     * Returns the object representing this <code>LaserManager</code>.
     *
     * @return The object of this class
     */
    public static LaserManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LaserManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
