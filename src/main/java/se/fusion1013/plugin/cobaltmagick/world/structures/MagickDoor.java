package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.*;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IMagickDoor;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.UUID;

public class MagickDoor implements IMagickDoor, Unlockable {

    // ----- VARIABLES -----

    Location cornerLocation; // The north-west corner of the door

    int width, height, depth; // x, y, z

    Material[][][] doorMaterials; // Stores the materials the door is made up of

    boolean isClosed = true;

    UUID uuid;

    // ----- CONSTRUCTORS -----

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

        if (!isClosed) open();
    }

    // ----- LOGIC -----

    /**
     * Stores the block materials that are in the door space in the doorMaterials array.
     */
    private void storeDoorMaterials() {
        // Get the door world
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
    }

    @Override
    public void unlock() {
        open();
    }

    @Override
    public void open() {
        if (!isClosed) return;
        isClosed = false;

        storeDoorMaterials();

        World world = cornerLocation.getWorld();
        if (world == null) return;

        int cx = cornerLocation.getBlockX();
        int cy = cornerLocation.getBlockY();
        int cz = cornerLocation.getBlockZ();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Location location = new Location(world, cx + x, cy + y, cz + z);
                    location.getBlock().setType(Material.AIR);
                    world.spawnParticle(Particle.CRIT, location.add(new Vector(.5, .5, .5)), 8, .2, .2, .2, 0);
                }
            }
        }

        world.playSound(cornerLocation.clone().add(new Vector(width / 2, height / 2, depth / 2)), "cobalt.perk_unseal", SoundCategory.BLOCKS, 1, 1);
    }

    @Override
    public void close() {
        isClosed = true;
        World world = cornerLocation.getWorld();
        if (world == null) return;

        int cx = cornerLocation.getBlockX();
        int cy = cornerLocation.getBlockY();
        int cz = cornerLocation.getBlockZ();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    new Location(world, cx + x, cy + y, cz + z).getBlock().setType(doorMaterials[x][y][z]);
                }
            }
        }
    }

    // ----- GETTERS / SETTERS -----


    @Override
    public boolean isLocked() {
        return isClosed;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public Location getCorner() {
        return cornerLocation;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }
}
