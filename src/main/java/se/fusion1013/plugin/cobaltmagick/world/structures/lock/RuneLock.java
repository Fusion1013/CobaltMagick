package se.fusion1013.plugin.cobaltmagick.world.structures.lock;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
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
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.*;

/**
 * A <code>RuneLock</code> requires a number of specific items to be inserted into it in order.
 * When the correct items have been inserted it will unlock an <code>Unlockable</code>, after which the <code>RuneLock</code> goes inert.
 * A <code>RuneLock</code> can only hold on to items for a specific amount of time before spitting them out again, resetting the lock.
 */
public class RuneLock extends AbstractLock implements Runnable {

    // ----- VARIABLES -----

    private List<String> itemsNeeded = new ArrayList<>();
    private List<ItemStack> insertedItems = new ArrayList<>();
    private boolean removeOnActivation = true;

    int insertionCooldown = 0;

    // ----- CONSTRUCTORS -----

    public RuneLock() {}

    public RuneLock(Location location, UUID[] activatables, int id, String... items) {
        super(location, activatables);
        itemsNeeded.addAll(Arrays.asList(items));
        placeLock();
    }

    public RuneLock(Location location, UUID[] activatables, int id, UUID uuid, String... items) {
        super(location, uuid, activatables);
        itemsNeeded.addAll(Arrays.asList(items));
    }

    // ----- ATTEMPT ACTIVATION -----

    @Override
    public void onTrigger(Object... args) {
        Location location = (Location) args[0];
        Player player = (Player) args[1];
        if (!location.toBlockLocation().equals(super.location.toBlockLocation())) return;
        onClick(player);
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = super.toJson();

        JsonArray itemsNeededJo = new JsonArray();
        for (String s : itemsNeeded) itemsNeededJo.add(s);
        jo.add("items_needed", itemsNeededJo);

        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        super.fromJson(jsonObject);

        JsonArray itemsNeededJo = jsonObject.getAsJsonArray("items_needed");
        for (int i = 0; i < itemsNeededJo.size(); i++) itemsNeeded.add(itemsNeededJo.get(i).getAsString());
    }

    // ----- COMMAND INTEGRATION METHODS -----

    @Override
    public Argument<?>[] getListCommandArguments() {
        return new Argument[] {
                new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(CustomItemManager.getItemNames()))
        };
    }

    // ----- PLACEMENT -----

    private void placeLock() {
        Bukkit.getScheduler().runTask(CobaltMagick.getInstance(), () -> location.getBlock().setType(Material.RESPAWN_ANCHOR)); // TODO: Replace with custom block
    }

    // ----- LOGIC -----

    @Override
    public void onLoad() {
        placeLock();
    }

    @Override
    public void onUnload() {
        super.onUnload();
        for (ItemStack stack : insertedItems) location.getWorld().dropItemNaturally(location.clone().add(0, 2, 0), stack);
        insertedItems.clear();
    }

    public boolean onClick(Player player) {
        // If the lock is currently on cooldown, reduce the cooldown and return
        if (insertionCooldown > 0) {
            insertionCooldown--;
            return false;
        }

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
            insertionCooldown = 1;
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
            super.unlock();

            // Clear the items inserted list
            insertedItems.clear();

            // Play unlock effect
            World world = location.getWorld();
            world.spawnParticle(Particle.SCULK_CHARGE_POP, location.toCenterLocation(), 30, .5, .5, .5, 0);
            world.playSound(location, "cobalt.pulse", 1, 1);

            // Remove rune lock from object storage
            if (removeOnActivation) ObjectManager.removeStorageObject(getUniqueIdentifier(), getObjectIdentifier(), location.getChunk());
        }

        return true;
    }

    private void unlockingFailed() {
        World world = location.getWorld();
        Location spawnItemLocation = location.toCenterLocation().add(new Vector(0, 1, 0));

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

    private final ParticleStyleCircle particleStyleCircleObsidian = new ParticleStyleCircle.ParticleStyleCircleBuilder("rune_circle_obsidian")
            .setParticle(Particle.DRIPPING_OBSIDIAN_TEAR).setCount(1).setOffset(new Vector(0, 0, 0)).setSpeed(0)
            .setRadius(2).setIterations(8)
            .setAngularVelocity(0, Math.toRadians(35), 0)
            .build();

    private final ParticleStyleCircle particleStyleCircleEndRod = new ParticleStyleCircle.ParticleStyleCircleBuilder("rune_circle_end_rod")
            .setParticle(Particle.END_ROD).setCount(1).setOffset(new Vector(0, 0, 0)).setSpeed(0)
            .setRadius(1.5).setIterations(8)
            .setAngularVelocity(0, Math.toRadians(-35), 0)
            .build();

    private final ParticleGroup idleGroup = new ParticleGroup.ParticleGroupBuilder("rune_lock_idle")
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

            Location armorStandLocation = location.toCenterLocation().add(new Vector(xDelta, .5, zDelta));
            currentArmorStand.teleport(armorStandLocation);
            currentArmorStand.setRotation((float) Math.toDegrees(angle) + 90, 0);

            location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStandLocation.clone().add(0, .7, 0), 1, .1, .3, .1, .5, new Particle.DustTransition(Color.YELLOW, Color.WHITE, 1));
        }

        idleGroup.display(location.toCenterLocation().add(new Vector(0, 1, 0)));
        tick++;
        if (insertionCooldown > 0) insertionCooldown--;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        info.add("Items Needed:");
        for (String item : itemsNeeded) info.add(" - " + item);
        return info;
    }

    @Override
    public void addItem(String key, Object value) {
        super.addItem(key, value);

        switch (key) {
            case "item" -> itemsNeeded.add((String) value);
        }
    }

    @Override
    public void removeItem(String key, Object value) {
        super.removeItem(key, value);

        switch (key) {
            case "item" -> {
                // TODO
            }
        }
    }

    @Override
    public String getObjectIdentifier() {
        return "rune_lock";
    }

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

    // ----- CLONE CONSTRUCTOR & METHOD -----

    @Override
    public RuneLock clone() {
        return new RuneLock(this);
    }

    public RuneLock(RuneLock target) {
        super(target);

        this.itemsNeeded = new ArrayList<>();
        this.insertedItems = new ArrayList<>();

        this.insertionCooldown = target.insertionCooldown;
        this.tick = target.tick;
        this.runeLockTask = target.runeLockTask;
        this.armorStands = new ArrayList<>();

        this.removeOnActivation = target.removeOnActivation;
    }

}
