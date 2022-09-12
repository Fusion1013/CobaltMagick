package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.structures.lock.MaterialLock;

import java.util.Random;
import java.util.UUID;

public class MaterialLockStructureModule implements IStructureModule, IStorageObjectStructureModule {

    // ----- VARIABLES -----

    Material replaceMaterial;
    Material[] validMaterials;
    IStorageObjectStructureModule[] activates;

    // ----- CONSTRUCTORS -----

    public MaterialLockStructureModule(Material replaceMaterial, Material[] validMaterials, IStorageObjectStructureModule... activates) {
        this.replaceMaterial = replaceMaterial;
        this.validMaterials = validMaterials;
        this.activates = activates;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder structureHolder) {
        executeWithSeed(location, structureHolder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder structureHolder, long l) {
        executeStorage(location, structureHolder);
    }

    @Override
    public IStorageObject executeStorage(Location location, StructureUtil.StructureHolder holder) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {

                        // Find width of thing
                        Vector dimensions = findConnectedOfMaterial(replaceLocation, replaceMaterial, 500); // Note; If this somehow becomes a problem at some point, uhhh good luck finding the problem
                        MaterialLock lock = new MaterialLock(replaceLocation, dimensions.getBlockX(), dimensions.getBlockY(), dimensions.getBlockZ(), validMaterials, new UUID[0]);

                        // Set clue blocks
                        setClueBlocks(replaceLocation, dimensions.getBlockX(), dimensions.getBlockY(), dimensions.getBlockZ(), validMaterials);

                        // Add what it activates
                        for (IStorageObjectStructureModule actObj : activates) {
                            IStorageObject storageObject = actObj.executeStorage(location, holder);
                            lock.addActivatable(storageObject.getUniqueIdentifier());
                        }

                        // TODO: Set correct materials, with 'hint' blocks
                        // replaceLocation.getBlock().setType(Material.AIR);

                        // Insert lock
                        ObjectManager.insertStorageObject(lock, lock.getLocation().getChunk());
                        return lock;
                    }
                }
            }
        }
        return null;
    }

    private void setClueBlocks(Location corner, int width, int height, int depth, Material[] materials) {
        Random r = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Location currentLocation = corner.clone().add(x, y, z);
                    if (r.nextDouble() <= .25 && y == 0) {
                        Material mat = materials[r.nextInt(0, materials.length)];
                        currentLocation.getBlock().setType(mat);
                    }
                    else currentLocation.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private Vector findConnectedOfMaterial(Location corner, Material material, int maxSize) {
        Vector dim = new Vector();
        for (int x = 0; x < maxSize; x++) {
            if (corner.clone().add(x, 0, 0).getBlock().getType() != material) {
                dim.setX(x);
                break;
            }
        }
        for (int y = 0; y < maxSize; y++) {
            if (corner.clone().add(0, y, 0).getBlock().getType() != material) {
                dim.setY(y);
                break;
            }
        }
        for (int z = 0; z < maxSize; z++) {
            if (corner.clone().add(0, 0, z).getBlock().getType() != material) {
                dim.setZ(z);
                break;
            }
        }
        return dim;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
