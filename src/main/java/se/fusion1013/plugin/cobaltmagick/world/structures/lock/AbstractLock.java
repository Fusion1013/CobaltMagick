package se.fusion1013.plugin.cobaltmagick.world.structures.lock;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IActivatorStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractLock implements IActivatorStorageObject, Cloneable {

    // ----- VARIABLES -----

    private UUID uuid = null;
    protected Location location = null;
    private List<UUID> activatables = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public AbstractLock() {}

    public AbstractLock(Location location, UUID... activatables) {
        this.uuid = UUID.randomUUID();
        this.location = location.toCenterLocation();
        this.activatables.addAll(List.of(activatables));
    }

    public AbstractLock(Location location, UUID uuid,  UUID... activatables) {
        this.location = location.toCenterLocation();
        this.uuid = uuid;
        this.activatables.addAll(List.of(activatables));
    }

    // ----- UNLOCKING / LOCKING -----

    @Override
    public void onTrigger(Object... args) {
        unlock(); // Attempts to unlock the lock
    }

    /**
     * Unlock all <code>IActivatable</code>'s connected to the <code>AbstractLock</code>.
     */
    protected boolean unlock() {
        for (UUID activatableUUID : activatables) {
            IActivatableStorageObject inst = ObjectManager.getLoadedActivatableObject(activatableUUID);
            if (inst == null) continue;

            if (!inst.isActive()) {
                inst.activate(uuid);
            }
        }
        return true;
    }

    /**
     * Lock all <code>IActivatable</code>'s connected to the <code>AbstractLock</code>.
     */
    protected boolean lock() {
        for (UUID activatableUUID : activatables) {
            IActivatableStorageObject inst = ObjectManager.getLoadedActivatableObject(activatableUUID);
            if (inst == null) continue;

            if (inst.isActive()) {
                inst.deactivate(uuid);
            }
        }
        return true;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public AbstractLock(AbstractLock target) {
        this.uuid = target.uuid;
        this.location = target.location;
        this.activatables = new ArrayList<>();
    }

    @Override
    public AbstractLock clone() {
        return null;
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        this.location = ((Location) objects[0]).toCenterLocation();
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new LocationArgument("location", LocationType.BLOCK_POSITION)
        };
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void addActivatable(UUID uuid) {
        activatables.add(uuid);
    }

    @Override
    public void removeActivatable(UUID uuid) {
        activatables.remove(uuid);
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "location" -> this.location = ((Location) value).toCenterLocation();
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        info.add("Location: " + location.toVector());

        info.add("Activates:");
        for (UUID uuid : activatables) info.add(" - [" + uuid.toString() + "]");

        return info;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location.toCenterLocation();
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
        return "lock"; // Override in parent methods
    }

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(location));
        JsonArray jsonActivatableUUIDs = new JsonArray();
        for (UUID activatable : activatables) {
            if (activatable != null) jsonActivatableUUIDs.add(activatable.toString());
        }
        jo.add("activates", jsonActivatableUUIDs);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location")).toCenterLocation();

        JsonArray jsonActivatableUUIDs = jsonObject.getAsJsonArray("activates");
        this.activatables = new ArrayList<>();
        for (int i = 0; i < jsonActivatableUUIDs.size(); i++) {
            this.activatables.add(UUID.fromString(jsonActivatableUUIDs.get(i).getAsString()));
        }
    }

}
