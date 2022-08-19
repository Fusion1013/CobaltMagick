package se.fusion1013.plugin.cobaltmagick.world.structures.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;

public class MusicBoxStructureModule extends StructureModule {

    // ----- VARIABLES -----

    String sound;
    Material replaceMaterial;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>MusicBoxStructureModule</code>. Will replace all blocks of the given type with a music box playing the specified sound.
     *
     * @param sound the sound to play.
     * @param material the material to replace.
     */
    public MusicBoxStructureModule(String sound, Material material) {
        this.sound = sound;
        this.replaceMaterial = material;
    }

    // ----- EXECUTING -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {
                        replaceLocation.getBlock().setType(Material.AIR);

                        // Place new Music Box
                        WorldManager.registerMusicBox(replaceLocation, sound);
                    }
                }
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
