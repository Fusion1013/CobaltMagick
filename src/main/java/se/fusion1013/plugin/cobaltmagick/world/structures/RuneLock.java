package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleCircle;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.*;

/**
 * A <code>RuneLock</code> requires a number of specific items to be inserted into it in order.
 * When the correct items have been inserted it will unlock an <code>Unlockable</code>, after which the <code>RuneLock</code> goes inert.
 * A <code>RuneLock</code> can only hold on to items for a specific amount of time before spitting them out again, resetting the lock.
 */
public class RuneLock implements Runnable {

    // ----- VARIABLES -----

    Location location;
    List<String> itemsNeeded = new ArrayList<>();
    List<ItemStack> insertedItems = new ArrayList<>();
    int id;
    Unlockable unlockable;

    int insertionCooldown = 0;

    // ----- CONSTRUCTORS -----

    public RuneLock(Location location, Unlockable unlockable, int id, String... items) {
        this.location = location;
        this.unlockable = unlockable;
        this.id = id;
        itemsNeeded.addAll(Arrays.asList(items));

        placeLock();
    }

    // ----- PLACEMENT -----

    private void placeLock() {
        location.getBlock().setType(Material.RESPAWN_ANCHOR); // TODO: Replace with custom block
    }

    // ----- LOGIC -----

    public boolean onClick(Player player) {
        // If unlockable is already unlocked, do not attempt to unlock it again
        if (!unlockable.isLocked()) return false;

        // If the lock is currently on cooldown, reduce the cooldown and return
        if (insertionCooldown > 0) return false;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemsNeeded.size() == insertedItems.size()) return false; // This should never happen

        // Insert the new item, regardless of what it is (Unless it is nothing)
        ItemStack heldItem = player.getInventory().getItemInMainHand().clone();

        if (heldItem.getType() == Material.AIR || heldItem.getType() == Material.CAVE_AIR) return false;

        heldItem.setAmount(1); // Only insert one
        insertedItems.add(heldItem);
        insertionCooldown = 10;

        if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(itemInHand.getAmount()-1); // Reduce held item amount, if player is not in creative

        // TODO: Play insertion effects

        // Initiate rune lock task, and add an armor stand with the item
        if (runeLockTask == null) runeLockTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this, 0, 1);
        armorStands.add(location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setSmall(true);
            armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, heldItem);
        }));

        // Check if all the correct items have been inserted
        if (itemsNeeded.size() == insertedItems.size()) {

            // Stop the rune lock task
            runeLockTask.cancel();
            runeLockTask = null;
            tick = 0;
            armorStands.forEach(Entity::remove);
            armorStands.clear();

            for (int i = 0; i < itemsNeeded.size(); i++) {
                ItemStack needed = CustomItemManager.getItemStack(itemsNeeded.get(i));
                needed.setAmount(1);
                ItemStack given = insertedItems.get(i);
                given.setAmount(1);

                // If the current item is not the correct one, spit out all the items
                if (!needed.equals(given)) {
                    unlockingFailed();
                    return false;
                }
            }

            // All the items are the correct ones, unlock the unlockable
            unlockable.unlock();

            // Clear the items inserted list
            insertedItems.clear();

            // Play unlock effect
            World world = location.getWorld();
            world.spawnParticle(Particle.SCULK_CHARGE_POP, location.clone().add(new Vector(.5, .5, .5)), 30, .5, .5, .5, 0);
            world.playSound(location, "cobalt.pulse", 1, 1);
        }

        return true;
    }

    private void unlockingFailed() {
        World world = location.getWorld();
        Location spawnItemLocation = location.clone().add(new Vector(.5, 1, .5));

        // Drop all the inserted items
        for (ItemStack inserted : insertedItems) {
            world.dropItemNaturally(spawnItemLocation, inserted);
        }

        // Play sound effect
        world.playSound(spawnItemLocation, "cobalt.poof", 1, 1);

        // Play particle effect
        world.spawnParticle(Particle.CRIT, spawnItemLocation, 10, .2, .2, .2, 0);

        // Clear the item list
        insertedItems.clear();
    }

    // ----- RUNE LOCK TASK -----

    BukkitTask runeLockTask;

    ParticleStyleCircle particleStyleCircleObsidian = new ParticleStyleCircle.ParticleStyleCircleBuilder("rune_circle_obsidian")
            .setParticle(Particle.DRIPPING_OBSIDIAN_TEAR).setCount(1).setOffset(new Vector(0, 0, 0)).setSpeed(0)
            .setRadius(2).setIterations(8)
            .setAngularVelocity(0, Math.toRadians(10), 0)
            .build();

    ParticleStyleCircle particleStyleCircleEndRod = new ParticleStyleCircle.ParticleStyleCircleBuilder("rune_circle_end_rod")
            .setParticle(Particle.END_ROD).setCount(1).setOffset(new Vector(0, 0, 0)).setSpeed(0)
            .setRadius(1.5).setIterations(8)
            .setAngularVelocity(0, Math.toRadians(-10), 0)
            .build();

    ParticleGroup idleGroup = new ParticleGroup.ParticleGroupBuilder("rune_lock_idle")
            .addStyle(particleStyleCircleObsidian)
            .addStyle(particleStyleCircleEndRod)
            .build();

    int tick = 0;

    List<ArmorStand> armorStands = new ArrayList<>();

    @Override
    public void run() {
        for (int i = 0; i < armorStands.size(); i++) {
            ArmorStand currentArmorStand = armorStands.get(i);

            double radius = 1.7 + Math.sin((Math.PI / 40) * tick);

            double angle = (((Math.PI * 2) / armorStands.size()) * i) + ((Math.PI / 40) * tick);
            double xDelta = Math.cos(angle) * radius;
            double zDelta = Math.sin(angle) * radius;

            Location armorStandLocation = location.clone().add(new Vector(xDelta + .5, .5, zDelta + .5));
            currentArmorStand.teleport(armorStandLocation);
            currentArmorStand.setRotation((float) Math.toDegrees(angle) + 90, 0);

            location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStandLocation.clone().add(0, .7, 0), 1, .1, .3, .1, .5, new Particle.DustTransition(Color.YELLOW, Color.WHITE, 1));
        }

        idleGroup.display(location.clone().add(new Vector(.5, 1, .5)));
        tick++;
        if (insertionCooldown > 0) insertionCooldown--;
    }

    // ----- GETTERS / SETTERS -----

    public void addItem(String item) {
        itemsNeeded.add(item);
    }

    /**
     * Removes the first item in the queue.
     */
    public void removeItem() {
        if (!itemsNeeded.isEmpty()) itemsNeeded.remove(itemsNeeded.size()-1);
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getItemsNeeded() {
        return itemsNeeded;
    }

    public List<ItemStack> getInsertedItems() {
        return insertedItems;
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }
}
