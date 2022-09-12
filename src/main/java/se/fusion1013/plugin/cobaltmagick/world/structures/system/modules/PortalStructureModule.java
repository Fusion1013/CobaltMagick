package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.AbstractMagickPortal;

public class PortalStructureModule extends StructureModule {

    // ----- VARIABLES -----

    AbstractMagickPortal portalTemplate;
    Vector exitLocation;
    Material replaceMaterial;

    // ----- CONSTRUCTORS -----

    public PortalStructureModule(AbstractMagickPortal portalTemplate, Vector exitLocation, Material replaceMaterial) {
        this.portalTemplate = portalTemplate;
        this.exitLocation = exitLocation;
        this.replaceMaterial = replaceMaterial;
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder structureHolder) {
        executeWithSeed(location, structureHolder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long l) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {
                        replaceLocation.getBlock().setType(Material.AIR);

                        AbstractMagickPortal portalInstance = portalTemplate.clone();
                        portalInstance.setPortalLocation(replaceLocation.toCenterLocation());
                        portalInstance.setExitLocation(new Location(replaceLocation.getWorld(), exitLocation.getX(), exitLocation.getY(), exitLocation.getZ()));

                        ObjectManager.insertStorageObject(portalInstance, location.getChunk());
                    }
                }
            }
        }
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
