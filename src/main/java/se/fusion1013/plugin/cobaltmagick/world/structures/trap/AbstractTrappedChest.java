package se.fusion1013.plugin.cobaltmagick.world.structures.trap;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractTrappedChest implements IStorageObject {

    // ----- VARIABLES -----

    protected UUID uuid;
    protected Location chestLocation;

    // ----- CONSTRUCTORS -----

    public AbstractTrappedChest(Location chestLocation) {
        this.chestLocation = chestLocation;
        this.uuid = UUID.randomUUID();
    }

    // ----- ON LOAD -----

    @Override
    public void onLoad() {
        IStorageObject.super.onLoad();
        if (chestLocation != null) BlockPlacementManager.addBlock(Material.TRAPPED_CHEST, chestLocation); // TODO: Chest direction
    }

    // ----- ON TRIGGER -----

    @Override
    public void onTrigger(Object... args) {
        IStorageObject.super.onTrigger(args);
        ObjectManager.removeStorageObject(uuid, getObjectIdentifier(), chestLocation.getChunk());
    }


    // ----- JSON STORAGE METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("chest_location", JsonUtil.toJson(chestLocation));
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        chestLocation = JsonUtil.toLocation(jsonObject.getAsJsonObject("chest_location"));
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public AbstractTrappedChest(AbstractTrappedChest target) {
        this.uuid = target.uuid;
        this.chestLocation = target.chestLocation;
    }

    @Override
    public AbstractTrappedChest clone() {
        return null;
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        this.chestLocation = (Location) objects[0];
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new LocationArgument("chest_location")
        };
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "chest_location" -> this.chestLocation = (Location) value;
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        info.add("Chest Location: " + chestLocation.toVector());
        return info;
    }

    @Override
    public Location getLocation() {
        return chestLocation;
    }

    @Override
    public void setLocation(Location location) {
        this.chestLocation = location;
    }

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

}
