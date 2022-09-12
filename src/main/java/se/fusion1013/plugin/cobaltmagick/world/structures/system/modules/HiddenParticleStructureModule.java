package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.HiddenParticle;

import java.util.UUID;

public class HiddenParticleStructureModule implements IStructureModule, IStorageObjectStructureModule {

    // ----- VARIABLES -----

    ParticleGroup particleGroup;
    Material replaceMaterial;

    // ----- CONSTRUCTORS -----

    public HiddenParticleStructureModule(ParticleGroup particleGroup, Material replaceMaterial) {
        this.particleGroup = particleGroup;
        this.replaceMaterial = replaceMaterial;

        // Register particle group
        ParticleGroupManager.createParticleGroup(particleGroup);
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder structureHolder) {
        executeWithSeed(location, structureHolder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long l) {
        executeStorage(location, holder);
    }

    @Override
    public IStorageObject executeStorage(Location location, StructureUtil.StructureHolder holder) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {
                        replaceLocation.getBlock().setType(Material.AIR);

                        HiddenParticle hiddenParticle = new HiddenParticle(replaceLocation, particleGroup.getName());
                        ObjectManager.insertStorageObject(hiddenParticle, replaceLocation.getChunk());

                        return hiddenParticle;
                    }
                }
            }
        }

        return null;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
