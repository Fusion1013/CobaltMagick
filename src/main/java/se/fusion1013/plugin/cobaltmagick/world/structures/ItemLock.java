package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.*;

public class ItemLock {

    // ----- VARIABLES -----

    Location location;
    CustomItem item; // TODO: Should work for any item, not just CustomItems.
    // TODO: Could possibly be done by storing the name of the item, and creating a method in CustomItemManager that gets an itemstack from a name, that includes vanilla items and custom items
    UUID uuid;
    Unlockable unlockable;

    // ----- CONSTRUCTORS -----

    public ItemLock(Location location, CustomItem item, Unlockable unlockable) {
        this.location = location;
        this.item = item;
        this.uuid = UUID.randomUUID();
        this.unlockable = unlockable;
    }

    public ItemLock(UUID uuid, Location location, CustomItem item, Unlockable unlockable) {
        this.uuid = uuid;
        this.location = location;
        this.item = item;
        this.unlockable = unlockable;
    }

    // ----- LOGIC -----

    public void onClick(Player p) {
        ItemStack pItem = p.getInventory().getItemInMainHand();
        if (item.compareTo(pItem)) {
            if (unlockable.isLocked()) {
                unlockable.unlock();
                if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().getItemInMainHand().setAmount(pItem.getAmount()-1);
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public CustomItem getItem() {
        return item;
    }

    public Unlockable getUnlockable() {
        return unlockable;
    }

}
