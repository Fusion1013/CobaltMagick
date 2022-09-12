package se.fusion1013.plugin.cobaltmagick.world.structures.lock;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * For a <code>MaterialLock</code> to unlock, all blocks within the specified area must be of the allowed types.
 */
public class MaterialLock extends AbstractLock {

    // ----- VARIABLES -----

    private List<Material> allowedMaterials = new ArrayList<>();
    private Vector dimensions = new Vector();

    // ----- CONSTRUCTORS -----

    /**
     * This constructor should only be used for the object manager. // TODO: Replace with static method call (??)
     */
    public MaterialLock() {}

    public MaterialLock(Location location, int width, int height, int depth, Material[] materials, UUID[] activatables) {
        super(location, activatables);

        dimensions = new Vector(width, height, depth);
        this.allowedMaterials = List.of(materials);
    }

    public MaterialLock(Location location, UUID uuid, int width, int height, int depth, Material[] materials, UUID[] activatables) {
        super(location, uuid, activatables);

        dimensions = new Vector(width, height, depth);
        this.allowedMaterials = List.of(materials);
    }

    // ----- ATTEMPT ACTIVATION -----

    @Override
    public void onLoad() {
        super.onLoad();
        unlock(); // Attempts to unlock when the material pool is loaded
    }

    @Override
    public boolean unlock() {
        if (isPoolFilled()) return super.unlock();
        else return !super.lock();
    }

    private boolean isPoolFilled() {
        for (int x = 0; x < dimensions.getX(); x++) {
            for (int y = 0; y < dimensions.getY(); y++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    Location currentLocation = location.clone().add(x, y, z);

                    // Check if it is an allowed material
                    boolean materialAllowed = false;
                    for (Material mat : allowedMaterials) {
                        if (currentLocation.getBlock().getType() == mat) {
                            materialAllowed = true;
                            break;
                        }
                    }

                    if (!materialAllowed) return false;
                }
            }
        }

        return true;
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = super.toJson();
        jo.add("dimensions", JsonUtil.toJson(dimensions));
        jo.add("allowed_materials", JsonUtil.toJson(allowedMaterials.toArray(new Material[0])));
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        super.fromJson(jsonObject);
        dimensions = JsonUtil.toVector(jsonObject.getAsJsonObject("dimensions"));
        allowedMaterials = List.of(JsonUtil.toMaterialArray(jsonObject.getAsJsonArray("allowed_materials")));
    }

    // ----- COMMAND INTEGRATION METHODS -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        super.fromCommandArguments(objects);
        if (this.dimensions == null) this.dimensions = new Vector();
        this.dimensions.setX((int) objects[1]);
        this.dimensions.setY((int) objects[2]);
        this.dimensions.setZ((int) objects[3]);
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        List<Argument<?>> args = new ArrayList<>(List.of(super.getCommandArguments()));
        args.add(new DoubleArgument("width"));
        args.add(new DoubleArgument("height"));
        args.add(new DoubleArgument("depth"));
        args.add(new StringArgument("material"));
        return args.toArray(new Argument<?>[0]);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getObjectIdentifier() {
        return "material_lock";
    }

    @Override
    public void setValue(String key, Object value) {
        super.setValue(key, value);

        if (dimensions == null) dimensions = new Vector();

        switch (key) {
            case "width" -> this.dimensions.setX((double) value);
            case "height" -> this.dimensions.setY((double) value);
            case "depth" -> this.dimensions.setZ((double) value);
            case "material" -> this.allowedMaterials.add(Material.valueOf((String) value));
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        info.add("Dimensions: " + dimensions);
        info.add("Materials: " + allowedMaterials);
        return info;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    @Override
    public MaterialLock clone() {
        return new MaterialLock(this);
    }

    public MaterialLock(MaterialLock target) {
        super(target);

        this.allowedMaterials = new ArrayList<>();
        this.dimensions = new Vector();
    }

}
