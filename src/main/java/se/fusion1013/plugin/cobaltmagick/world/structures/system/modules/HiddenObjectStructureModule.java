package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;

import java.util.UUID;

public class HiddenObjectStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    Material replaceMaterial;

    RevealMethod revealMethod;
    private boolean hasParticleGroup = false;
    private ParticleGroup particleGroup = null;

    private boolean spawnsItem = false;
    private String item = "";

    private boolean spawnsWand = false;
    private int wandLevel = 0;

    private boolean deleteOnActivation = false;

    // ----- CONSTRUCTORS -----

    public HiddenObjectStructureModule(Material replaceMaterial, RevealMethod revealMethod) {
        this.replaceMaterial = replaceMaterial;
        this.revealMethod = revealMethod;
    }

    // ----- BUILDER METHODS -----

    public HiddenObjectStructureModule setDeleteOnActivation(boolean deleteOnActivation) {
        this.deleteOnActivation = deleteOnActivation;
        return this;
    }

    public HiddenObjectStructureModule setWandSpawn(Integer wandLevel) {
        if (wandLevel != null) {
            this.spawnsWand = true;
            this.wandLevel = wandLevel;
        } else {
            this.spawnsWand = false;
            this.wandLevel = 0;
        }

        return this;
    }

    public HiddenObjectStructureModule setItemSpawn(String item) {
        if (item != null) {
            this.spawnsItem = true;
            this.item = item;
        } else {
            this.spawnsItem = false;
            this.item = null;
        }

        return this;
    }

    public HiddenObjectStructureModule setParticleGroup(ParticleGroup group) {
        if (group != null) {
            this.hasParticleGroup = true;
            this.particleGroup = group;
        } else {
            this.hasParticleGroup = false;
            this.particleGroup = null;
        }

        return this;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        // Find the replacement location
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {

                        // Place new hidden object
                        /*
                        UUID uuid = WorldManager.createHiddenObject(replaceLocation.clone().add(.5, .5, .5), revealMethod);

                        if (hasParticleGroup) WorldManager.setHiddenObjectParticleGroup(uuid, particleGroup);
                        if (spawnsItem) WorldManager.setHiddenObjectItemSpawn(uuid, item);
                        if (spawnsWand) WorldManager.setHiddenObjectWandSpawn(uuid, wandLevel);
                        if (deleteOnActivation) WorldManager.setHiddenObjectDeleteOnActivation(uuid, true);

                        replaceLocation.getBlock().setType(Material.AIR);
                         */

                    }
                }
            }
        }
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder structureHolder, long l) {
        execute(location, structureHolder);
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
