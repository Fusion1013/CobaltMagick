package se.fusion1013.plugin.cobaltmagick.world.structures.hidden;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;
import se.fusion1013.plugin.cobaltmagick.wand.WandManager;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HiddenObject implements IActivatableStorageObject, Runnable {

    // ----- VARIABLES -----

    public static final int MAX_REVEAL_DISTANCE = 10;

    private UUID uuid;

    // Extra Options
    private boolean hasParticleGroup = false;
    private String particleGroup = "";

    private boolean spawnsItem = false;
    private String item = "";

    private boolean spawnsWand = false;
    private int wandLevel = 0;

    private boolean deleteOnActivation = false;

    private Location location;
    private RevealMethod revealMethod;
    private boolean revealed = false;

    private BukkitTask task;

    // ----- CONSTRUCTORS -----

    public HiddenObject() {}

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
            this.particleGroup = group.getName();
        } else {
            this.hasParticleGroup = false;
            this.particleGroup = null;
        }
    }

    // ----- ACTIVATING / DISABLING -----

    @Override
    public void onTrigger(Object... args) {
        Location triggerLocation = (Location) args[0];
        RevealMethod triggerMethod = (RevealMethod) args[1];

        if (location.getWorld() != triggerLocation.getWorld()) return;

        if (location.distanceSquared(triggerLocation) <= MAX_REVEAL_DISTANCE * MAX_REVEAL_DISTANCE && triggerMethod == revealMethod) {
            activate();
        }
    }

    @Override
    public void activate(Object... objects) {
        revealed = true;
    }

    @Override
    public void deactivate(Object... objects) {
        revealed = false;
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 2);
    }

    @Override
    public void onUnload() {
        if (this.task != null) this.task.cancel();
    }

    // ----- LOGIC -----

    @Override
    public void run() {
        if (revealed) {
            ParticleGroup particleGroup = ParticleGroupManager.getParticleGroup(this.particleGroup);

            if (hasParticleGroup) particleGroup.display(location);

            if (spawnsItem) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
                    Item spawnedItem = location.getWorld().dropItem(location, CustomItemManager.getItemStack(item));
                    spawnedItem.setGravity(false);
                    spawnedItem.setVelocity(new Vector());
                    spawnedItem.setGlowing(true);
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

            if (deleteOnActivation) ObjectManager.removeStorageObject(uuid, getObjectIdentifier(), location.getChunk());
        }
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());

        jo.addProperty("has_particle_group", hasParticleGroup);
        jo.addProperty("particle_group", particleGroup);

        jo.addProperty("spawns_item", spawnsItem);
        if (spawnsItem) jo.addProperty("item", item);

        jo.addProperty("spawns_wand", spawnsWand);
        jo.addProperty("wand_level", wandLevel);

        jo.addProperty("delete_on_activation", deleteOnActivation);

        jo.add("location", JsonUtil.toJson(location));
        if (revealMethod != null) jo.addProperty("reveal_method", revealMethod.toString());
        jo.addProperty("revealed", revealed);

        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());

        this.hasParticleGroup = jsonObject.get("has_particle_group").getAsBoolean();
        this.particleGroup = jsonObject.get("particle_group").getAsString();

        this.spawnsItem = jsonObject.get("spawns_item").getAsBoolean();
        if (spawnsItem) this.item = jsonObject.get("item").getAsString();

        this.spawnsWand = jsonObject.get("spawns_wand").getAsBoolean();
        this.wandLevel = jsonObject.get("wand_level").getAsInt();

        this.deleteOnActivation = jsonObject.get("delete_on_activation").getAsBoolean();

        this.location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));
        this.revealMethod = RevealMethod.valueOf(jsonObject.get("reveal_method").getAsString());
        this.revealed = jsonObject.get("revealed").getAsBoolean();
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public boolean isActive() {
        return revealed;
    }

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectIdentifier() {
        return "hidden_object";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void fromCommandArguments(Object[] objects) {
        // TODO
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new StringArgument("particle_group").replaceSuggestions(ArgumentSuggestions.strings(ParticleGroupManager.getParticleGroupNames())),
                new StringArgument("item_spawn").replaceSuggestions(ArgumentSuggestions.strings(CustomItemManager.getItemNames())),
                new IntegerArgument("wand_level"),
                new BooleanArgument("delete_on_activation"),
                new StringArgument("reveal_method").replaceSuggestions(ArgumentSuggestions.strings(RevealMethod.getRevealMethodNames()))
        };
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "particle_group" -> {
                this.particleGroup = (String) value;
                this.hasParticleGroup = true;
            }
            case "item_spawn" -> {
                this.item = (String) value;
                this.spawnsItem = true;
            }
            case "wand_level" -> {
                this.wandLevel = (int) value;
                this.spawnsWand = true;
            }
            case "delete_on_activation" -> this.deleteOnActivation = (boolean) value;
            case "reveal_method" -> this.revealMethod = RevealMethod.valueOf((String) value);
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        if (hasParticleGroup) info.add("Particle Group: " + particleGroup);
        if (spawnsItem) info.add("Item: " + item);
        if (spawnsWand) info.add("Wand Level: " + wandLevel);
        info.add("Delete on Activation: " + deleteOnActivation);
        info.add("Location: " + location.toVector());
        info.add("Reveal Method: " + revealMethod.toString());
        info.add("Revealed: " + revealed);
        return info;
    }

    public RevealMethod getRevealMethod() {
        return revealMethod;
    }

    public String getParticleGroupName() {
        if (particleGroup != null) return particleGroup;
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

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public HiddenObject(HiddenObject target) {
        this.uuid = target.uuid;
        this.hasParticleGroup = target.hasParticleGroup;
        this.particleGroup = target.particleGroup;
        this.spawnsItem = target.spawnsItem();
        this.item = target.item;
        this.spawnsWand = target.spawnsWand();
        this.wandLevel = target.wandLevel;
        this.deleteOnActivation = target.deleteOnActivation;
        this.location = target.location;
        this.revealMethod = target.revealMethod;
        this.revealed = target.revealed;
        this.task = target.task;
    }

    @Override
    public HiddenObject clone() {
        return new HiddenObject(this);
    }
}
