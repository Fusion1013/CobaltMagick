package se.fusion1013.plugin.cobaltmagick.world.structures.system.modules;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

public interface IStorageObjectStructureModule {

    IStorageObject executeStorage(Location location, StructureUtil.StructureHolder holder);

}
