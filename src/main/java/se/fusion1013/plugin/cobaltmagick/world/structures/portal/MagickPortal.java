package se.fusion1013.plugin.cobaltmagick.world.structures.portal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;


public class MagickPortal extends AbstractMagickPortal implements Runnable {

    // ----- VARIABLES -----

    private BukkitTask task;

    // ----- CONSTRUCTORS -----

    public MagickPortal(Location portalLocation, Location exitLocation) {
        super(portalLocation, exitLocation);
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        super.onLoad();
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 20, 2);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.task != null) this.task.cancel();
    }

    @Override
    public void run() {
        tickPortal();
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public MagickPortal(MagickPortal target) {
        super(target);

        this.task = target.task;
    }

    @Override
    public MagickPortal clone() {
        return new MagickPortal(this);
    }
}
