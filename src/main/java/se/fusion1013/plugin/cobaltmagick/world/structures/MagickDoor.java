package se.fusion1013.plugin.cobaltmagick.world.structures;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;
import se.fusion1013.plugin.cobaltcore.world.block.entity.BlockEntityCollection;
import se.fusion1013.plugin.cobaltcore.world.block.entity.BlockEntityManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IMagickDoor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MagickDoor implements IActivatableStorageObject {

    // ----- VARIABLES -----

    UUID uuid;
    Location cornerLocation; // The north-west corner of the door
    int width = 1, height = 1, depth = 1; // x, y, z
    Material[][][] doorMaterials; // Stores the materials the door is made up of
    boolean isClosed = true;

    // ----- CONSTRUCTORS -----

    public MagickDoor() {
        this.doorMaterials = new Material[width][height][depth];
        storeDoorMaterials();
    }

    public MagickDoor(Location cornerLocation, int width, int height, int depth) {
        this.cornerLocation = cornerLocation;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.uuid = UUID.randomUUID();

        doorMaterials = new Material[width][height][depth];
        storeDoorMaterials();
    }

    public MagickDoor(UUID uuid, Location cornerLocation, int width, int height, int depth, boolean isClosed) {
        this.uuid = uuid;
        this.cornerLocation = cornerLocation;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.isClosed = isClosed;

        doorMaterials = new Material[width][height][depth];
        storeDoorMaterials();

        if (!this.isClosed) {
            setDoorBlocks(true);
        }
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        if (!isClosed) setDoorBlocks(true);
    }

    // ----- ACTIVATING / DISABLING -----

    @Override
    public void activate(Object... objects) {
        open();
    }

    @Override
    public void deactivate(Object... objects) {
        close();
    }

    // ----- LOGIC -----

    /**
     * Stores the block materials that are in the door space in the doorMaterials array.
     */
    private void storeDoorMaterials() {
        // Get the door world
        if (cornerLocation == null) return;
        World world = cornerLocation.getWorld();
        if (world == null) return;

        int cx = cornerLocation.getBlockX();
        int cy = cornerLocation.getBlockY();
        int cz = cornerLocation.getBlockZ();

        // Loop through door blocks and store materials
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    doorMaterials[x][y][z] = new Location(world, cx + x, cy + y, cz + z).getBlock().getType();
                }
            }
        }

        // TODO: Update this object in the database
    }

    public void open() {
        if (!isClosed) return;
        isClosed = false;

        storeDoorMaterials();

        World world = cornerLocation.getWorld();

        // setDoorBlocks(true);
        createMovingDoor(cornerLocation.clone(), cornerLocation.clone().add(new Vector(0, height+1, 0)), true);

        world.playSound(cornerLocation.clone().add(new Vector(width / 2, height / 2, depth / 2)), "cobalt.perk_unseal", SoundCategory.BLOCKS, 1, 1);


    }

    public void close() {
        isClosed = true;

        // setDoorBlocks(false);
        createMovingDoor(cornerLocation.clone().add(new Vector(0, height+1, 0)), cornerLocation.clone(), false);
    }

    public void setDoorBlocks(boolean remove) {
        World world = cornerLocation.getWorld();
        if (world == null) return;

        int cx = cornerLocation.getBlockX();
        int cy = cornerLocation.getBlockY();
        int cz = cornerLocation.getBlockZ();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if (remove) new Location(world, cx + x, cy + y, cz + z).getBlock().setType(Material.AIR);
                    else new Location(world, cx + x, cy + y, cz + z).getBlock().setType(doorMaterials[x][y][z]);
                }
            }
        }
    }

    BukkitTask doorTask;
    private double p = 0;

    private void createMovingDoor(Location from, Location to, boolean remove) {
        BlockEntityCollection collection = BlockEntityManager.getInstance().createBlockEntityCollection(from, doorMaterials);

        if (remove) setDoorBlocks(true);

        this.doorTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (p >= 1) {
                BlockEntityManager.getInstance().removeBlockEntityCollection(collection.getUuid());
                if (!remove) setDoorBlocks(false);
                p = 0;
                doorTask.cancel();
            }

            Vector newLocation = VectorUtil.lerp(from.toVector(), to.toVector(), p);
            Location currentLocation = new Location(from.getWorld(), newLocation.getX(), newLocation.getY(), newLocation.getZ());
            collection.moveTo(currentLocation);
            p += (1.0 / (height * 8));
        }, 0, 1);
    }

    // ----- JSON INTEGRATION -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(cornerLocation));
        jo.addProperty("width", width);
        jo.addProperty("height", height);
        jo.addProperty("depth", depth);
        if (doorMaterials != null) jo.add("materials", JsonUtil.toJson(doorMaterials));
        jo.addProperty("is_closed", isClosed);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        this.cornerLocation = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));
        this.width = jsonObject.get("width").getAsInt();
        this.height = jsonObject.get("height").getAsInt();
        this.depth = jsonObject.get("depth").getAsInt();
        this.doorMaterials = JsonUtil.toMaterialArray3D(jsonObject.get("materials").getAsJsonArray());
        this.isClosed = jsonObject.get("is_closed").getAsBoolean();
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {

    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new IntegerArgument("width"),
                new IntegerArgument("height"),
                new IntegerArgument("depth")
        };
    }


    // ----- GETTERS / SETTERS -----

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "width" -> this.width = (int) value;
            case "height" -> this.height = (int) value;
            case "depth" -> this.depth = (int) value;
        }

        this.doorMaterials = new Material[width][height][depth];
        storeDoorMaterials();
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid);
        info.add("Location: " + cornerLocation.toVector());
        info.add("Width: " + width);
        info.add("Height: " + height);
        info.add("Depth: " + depth);
        info.add("Is Closed: " + isClosed);
        return info;
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
        return "magick_door";
    }

    @Override
    public Location getLocation() {
        return cornerLocation;
    }

    @Override
    public void setLocation(Location location) {
        this.cornerLocation = location;
    }

    @Override
    public boolean isActive() {
        return !isClosed;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MagickDoor(MagickDoor target) {
        this.uuid = target.uuid;
        this.cornerLocation = target.cornerLocation;
        this.width = target.width;
        this.height = target.height;
        this.depth = target.depth;
        this.doorMaterials = target.doorMaterials;
        this.isClosed = target.isClosed;
        this.p = target.p;
        this.doorTask = target.doorTask;
    }

    @Override
    public MagickDoor clone() {
        return new MagickDoor(this);
    }
}
