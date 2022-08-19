package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;
import se.fusion1013.plugin.cobaltcore.world.block.entity.BlockEntityCollection;
import se.fusion1013.plugin.cobaltcore.world.block.entity.BlockEntityManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IMagickDoor;

import java.util.UUID;

public class MagickDoor implements IMagickDoor, IActivatable {

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

        if (!this.isClosed) {
            setDoorBlocks(true);
        }
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
    public void activate() {
        open();
    }

    @Override
    public void open() {
        if (!isClosed) return;
        isClosed = false;

        storeDoorMaterials();

        World world = cornerLocation.getWorld();

        // setDoorBlocks(true);
        createMovingDoor(cornerLocation.clone(), cornerLocation.clone().add(new Vector(0, height+1, 0)), true);

        world.playSound(cornerLocation.clone().add(new Vector(width / 2, height / 2, depth / 2)), "cobalt.perk_unseal", SoundCategory.BLOCKS, 1, 1);


    }

    @Override
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

    // ----- GETTERS / SETTERS -----

    @Override
    public boolean isActive() {
        return !isClosed;
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
