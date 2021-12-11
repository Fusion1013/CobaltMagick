package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.IParticleGroup;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager extends Manager implements Listener, Runnable {

    private static ParticleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static ParticleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ParticleManager(Cobalt.getInstance());
        }
        return INSTANCE;
    }

    private final List<ParticleGroup> particleGroups; // Contains all particle groups
    private BukkitTask particleTask;

    public ParticleManager(Cobalt cobalt) {
        super(cobalt);

        this.particleGroups = new ArrayList<>();
        this.particleTask = null;

        Bukkit.getPluginManager().registerEvents(this, this.cobalt);
    }

    // ---------- ParticleGroup Editing ----------

    /***
     * Creates a new ParticleGroup with a name and location. This group will have a default style and particle
     * @param name the name of the ParticleGroup
     * @param location the location of the ParticleGroup
     * @return id of the ParticleGroup
     */
    public int createParticleGroup(String name, Location location){
        particleGroups.add(new ParticleGroup());
        return particleGroups.size()-1; // The id of the new ParticleGroup is its position in the list
    }

    @Deprecated
    public void addParticleGroup(ParticleGroup group){
        particleGroups.add(group);
    }

    @Deprecated
    public List<ParticleGroup> getParticleGroups(){
        return particleGroups;
    }

    // ---------- // ----------

    @Override
    public void run() {
        for (ParticleGroup pg : particleGroups){
            // pg.display();
        }
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskLater(this.cobalt, () -> {
            long ticks = 1;
            this.particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.cobalt, this, 0, ticks);
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

    public ParticleGroup getParticleGroupByName(String name){
        for (ParticleGroup group : particleGroups){
            /*
            if (group.getName().equalsIgnoreCase(name)){
                return group;
            }

             */
        }
        return null;
    }
}
