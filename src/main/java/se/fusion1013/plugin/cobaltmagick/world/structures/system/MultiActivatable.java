package se.fusion1013.plugin.cobaltmagick.world.structures.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IActivatorStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;

import java.util.*;

public class MultiActivatable implements IActivatableStorageObject, IActivatorStorageObject {

    // ----- VARIABLES -----

    private Location location;
    private int activationsRequired = 0;
    private List<UUID> activatableUUIDs = new ArrayList<>();
    private final Map<UUID, Boolean> uniqueActivations = new HashMap<>();

    private UUID uuid;
    private boolean isActive = false;

    // ----- CONSTRUCTORS -----

    public MultiActivatable() {}

    public MultiActivatable(Location location, int activationsRequired, UUID... activatableUUIDs) {
        this.location = location;
        this.activationsRequired = activationsRequired;
        this.activatableUUIDs.addAll(List.of(activatableUUIDs));
        this.uuid = UUID.randomUUID();
    }

    // ----- ACTIVATING -----

    @Override
    public void deactivate(Object... args) {
        if (args.length >= 1) {
            uniqueActivations.remove((UUID) args[0]);
            update();
        }
    }

    @Override
    public void activate(Object... args) {
        if (args.length >= 1) {
            uniqueActivations.put((UUID) args[0], true);
            update();
        }
    }

    private void update() {
        for (UUID activatableUUID : activatableUUIDs) {
            IActivatableStorageObject inst = ObjectManager.getLoadedActivatableObject(activatableUUID);
            if (inst != null) {
                if (uniqueActivations.size() >= activationsRequired) {
                    inst.activate();
                    inst.activate(uuid);
                    isActive = true;
                } else {
                    inst.deactivate();
                    inst.deactivate(uuid);
                    isActive = false;
                }
            }
        }
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(location));
        jo.addProperty("activations_required", activationsRequired);
        jo.addProperty("is_active", isActive);

        JsonArray jsonActivatableUUIDs = new JsonArray();
        for (UUID activatable : activatableUUIDs) {
            jsonActivatableUUIDs.add(activatable.toString());
        }
        jo.add("activates", jsonActivatableUUIDs);

        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        this.location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));
        this.activationsRequired = jsonObject.get("activations_required").getAsInt();
        this.isActive = jsonObject.get("is_active").getAsBoolean();

        JsonArray jsonActivatableUUIDs = jsonObject.getAsJsonArray("activates");
        for (int i = 0; i < jsonActivatableUUIDs.size(); i++) {
            this.activatableUUIDs.add(UUID.fromString(jsonActivatableUUIDs.get(i).getAsString()));
        }
    }

    // ----- COMMAND INTEGRATION METHODS -----

    @Override
    public void fromCommandArguments(Object[] objects) {
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new IntegerArgument("activations_required")
        };
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void addActivatable(UUID uuid) {
        activatableUUIDs.add(uuid);
    }

    @Override
    public void removeActivatable(UUID uuid) {
        activatableUUIDs.remove(uuid);
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "activations_required" -> this.activationsRequired = (int) value;
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
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
        return "multi_activatable";
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
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        info.add("Location: " + location.toVector());

        info.add("Activates:");
        for (UUID uuid : activatableUUIDs) info.add(" - [" + uuid.toString() + "]");

        return info;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MultiActivatable(MultiActivatable target) {
        this.location = target.location;
        this.uuid = target.uuid;
        this.activatableUUIDs = new ArrayList<>();
        this.activationsRequired = target.activationsRequired;
        this.isActive = target.isActive;
    }

    @Override
    public MultiActivatable clone() {
        return new MultiActivatable(this);
    }
}
