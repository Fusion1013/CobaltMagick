package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class MaterialPool {

    // ----- VARIABLES -----

    Location corner; // The north-west corner
    Vector size; // The size of the container
    Material material;

    // ----- CONSTRUCTORS -----

    public MaterialPool(Location corner, Vector size, Material material) {
        this.corner = corner;
        this.size = size;
        this.material = material;
    }

    // ----- LOGIC -----

    public boolean isFilled() {
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (corner.clone().add(new Vector(x, y, z)).getBlock().getType() != material) return false;
                }
            }
        }

        return true;
    }
}
