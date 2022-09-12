package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IActivatorStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.MultiActivatable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiActivatableStructureModule implements IStructureModule, IStorageObjectStructureModule {

    // ----- VARIABLES -----

    IStorageObjectStructureModule[] activates;
    IStorageObjectStructureModule[] activatedBy;
    int activationsRequired;

    // ----- CONSTRUCTORS -----

    public MultiActivatableStructureModule(int activationsRequired, IStorageObjectStructureModule[] activates, IStorageObjectStructureModule[] activatedBy) {
        this.activates = activates;
        this.activationsRequired = activationsRequired;
        this.activatedBy = activatedBy;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder structureHolder) {
        executeStorage(location, structureHolder);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder structureHolder, long l) {
        executeStorage(location, structureHolder);
    }

    @Override
    public IStorageObject executeStorage(Location location, StructureUtil.StructureHolder holder) {

        // Create the required Activatable's
        List<UUID> actUUIDs = new ArrayList<>();
        List<IActivatableStorageObject> actInstances = new ArrayList<>();
        for (IStorageObjectStructureModule act : activates) {
            IStorageObject newObject = act.executeStorage(location, holder);
            if (newObject == null) continue;

            if (newObject instanceof IActivatableStorageObject activatableStorageObject) {
                actUUIDs.add(newObject.getUniqueIdentifier());
                actInstances.add(activatableStorageObject);
            }
        }
        if (actInstances.size() <= 0) return null;

        // Put it in the location of one of the activatables, to make sure loading works correctly
        MultiActivatable multiActivatable = new MultiActivatable(actInstances.get(0).getLocation(), activationsRequired, actUUIDs.toArray(new UUID[0]));
        ObjectManager.insertStorageObject(multiActivatable, multiActivatable.getLocation().getChunk());

        // Create what this is activated by
        for (IStorageObjectStructureModule activatedByModule : activatedBy) {
            IActivatorStorageObject activatorStorageObject = (IActivatorStorageObject) activatedByModule.executeStorage(location, holder);
            activatorStorageObject.addActivatable(multiActivatable.getUniqueIdentifier());
            ObjectManager.updateStorageObject(activatorStorageObject);
        }

        return multiActivatable;
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
