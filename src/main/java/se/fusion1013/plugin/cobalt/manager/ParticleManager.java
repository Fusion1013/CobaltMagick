package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager extends Manager implements Listener, Runnable {

    private final List<ParticleGroup> particleGroups;
    private BukkitTask particleTask;

    public ParticleManager(Cobalt cobalt) {
        super(cobalt);

        this.particleGroups = new ArrayList<>();
        this.particleTask = null;

        Bukkit.getPluginManager().registerEvents(this, this.cobalt);
    }

    public void addParticleGroup(ParticleGroup group){
        particleGroups.add(group);
    }

    public List<ParticleGroup> getParticleGroups(){
        return particleGroups;
    }

    @Override
    public void run() {
        for (ParticleGroup pg : particleGroups){
            pg.display();
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
            if (group.getName().equalsIgnoreCase(name)){
                return group;
            }
        }
        return null;
    }
}
