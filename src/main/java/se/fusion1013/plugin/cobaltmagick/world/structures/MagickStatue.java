package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.wand.AbstractWand;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class MagickStatue implements Runnable {
    Location location; // The statue location
    World world;
    double radius;

    int manaRecharge;
    int delayBetweenCharges;

    BukkitTask statueTask;
    boolean enabled;

    public MagickStatue(Location location, double radius, int manaRecharge, int delayBetweenCharges){
        this.location = location;
        this.world = location.getWorld();
        this.radius = radius;
        this.manaRecharge = manaRecharge;
        this.delayBetweenCharges = delayBetweenCharges;

        this.enabled = true;
    }

    private void init(){
        // Start statue tick
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            this.statueTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this, 0, delayBetweenCharges);
        }, 0);
    }

    @Override
    public void run() {
        // Find nearby players and increase the mana of their wand.
        if (!location.isWorldLoaded()) return;

        List<Entity> entities = new ArrayList<>(world.getNearbyEntities(location, radius, radius, radius, e -> e instanceof Player));
        for (Entity entity : entities){
            Player p = (Player)entity;
            ItemStack stack = p.getInventory().getItemInMainHand();
            Wand wand = AbstractWand.getWand(stack);
            if (wand != null) wand.increaseMana(manaRecharge);
        }
    }
}
