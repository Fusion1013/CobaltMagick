package se.fusion1013.plugin.cobaltmagick.world.structures.hidden;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.world.chunk.IChunkBound;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;
import se.fusion1013.plugin.cobaltmagick.wand.WandManager;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.UUID;

public class HiddenObject implements IActivatable, IChunkBound<HiddenObject> {

    // ----- VARIABLES -----

    public static final int MAX_REVEAL_DISTANCE = 10;

    private final UUID uuid;

    // Extra Options
    private boolean hasParticleGroup = false;
    private ParticleGroup particleGroup = null;

    private boolean spawnsItem = false;
    private String item = "";

    private boolean spawnsWand = false;
    private int wandLevel = 0;

    private boolean deleteOnActivation = false;

    private final Location location;
    private final RevealMethod revealMethod;
    private boolean revealed = false;

    // ----- CONSTRUCTORS -----

    public HiddenObject(Location location, RevealMethod revealMethod) {
        this.uuid = UUID.randomUUID();
        this.location = location;
        this.revealMethod = revealMethod;
    }

    public HiddenObject(Location location, RevealMethod revealMethod, UUID uuid, boolean revealed) {
        this.location = location;
        this.revealMethod = revealMethod;
        this.uuid = uuid;
        this.revealed = revealed;
    }

    // ----- BUILDER CONSTRUCTORS -----

    public void setDeleteOnActivation(boolean deleteOnActivation) {
        this.deleteOnActivation = deleteOnActivation;
    }

    public void setWandSpawn(Integer wandLevel) {
        if (wandLevel != null) {
            this.spawnsWand = true;
            this.wandLevel = wandLevel;
        } else {
            this.spawnsWand = false;
            this.wandLevel = 0;
        }
    }

    public void setItemSpawn(String item) {
        if (item != null) {
            this.spawnsItem = true;
            this.item = item;
        } else {
            this.spawnsItem = false;
            this.item = null;
        }
    }

    public void setParticleGroup(ParticleGroup group) {
        if (group != null) {
            this.hasParticleGroup = true;
            this.particleGroup = group;
        } else {
            this.hasParticleGroup = false;
            this.particleGroup = null;
        }
    }

    // ----- LOGIC -----

    public void tick() {
        if (revealed) {
            if (hasParticleGroup) particleGroup.display(location);

            if (spawnsItem) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
                    location.getWorld().dropItemNaturally(location, CustomItemManager.getItemStack(item));
                }, 0);
            }

            if (spawnsWand) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
                    int cost = 20 * wandLevel;
                    if (cost == 20) cost+=10;
                    Wand wand = WandManager.getInstance().createWand(cost, wandLevel, true);
                    Item spawnedItem = location.getWorld().dropItem(location.toCenterLocation(), wand.getWandItem());
                    spawnedItem.setVelocity(new Vector()); // Reset velocity of the dropped item
                }, 0);
            }

            if (deleteOnActivation) WorldManager.removeHiddenObject(uuid);
        }
    }

    @Override
    public void activate() {
        this.revealed = true;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public boolean isActive() {
        return revealed;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public HiddenObject getObject() {
        return this;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public RevealMethod getRevealMethod() {
        return revealMethod;
    }

    public String getParticleGroupName() {
        if (particleGroup != null) return particleGroup.getName();
        else return "";
    }

    public boolean hasParticleGroup() {
        return hasParticleGroup;
    }

    public boolean spawnsItem() {
        return spawnsItem;
    }

    public String getItem() {
        return item;
    }

    public boolean spawnsWand() {
        return spawnsWand;
    }

    public int getWandLevel() {
        return wandLevel;
    }

    public boolean deleteOnActivation() {
        return deleteOnActivation;
    }

    public boolean isRevealed() {
        return revealed;
    }
}
