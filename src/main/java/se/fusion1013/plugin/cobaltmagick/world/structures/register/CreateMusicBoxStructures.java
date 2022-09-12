package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.BiomeStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.HeightStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.modules.MusicBoxStructureModule;

public class CreateMusicBoxStructures {

    public static void create() {}

    public static final IStructure MUSIC_BOX_DESERT = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 15, "music_box_desert", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.ancient_getaway", Material.RED_WOOL))
            .setGenerationThreshold(.61)
            .setMinDistance(1000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.DESERT))
            .build());

    public static final IStructure MUSIC_BOX_PLAINS = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 16, "music_box_plains", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.hydroangea", Material.RED_WOOL))
            .setGenerationThreshold(.61)
            .setMinDistance(1000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.PLAINS, Biome.SUNFLOWER_PLAINS))
            .build());

    public static final IStructure MUSIC_BOX_OCEAN = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 17, "music_box_ocean", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.shooting_for_the_stars", Material.RED_WOOL))
            .setGenerationThreshold(.61)
            .setMinDistance(2000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN))
            .build());

    public static final IStructure MUSIC_BOX_ISLAND = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 18, "music_box_island", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.outpost", Material.RED_WOOL))
            .setGenerationThreshold(.4)
            .setMinDistance(500)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.MUSHROOM_FIELDS))
            .build());

}
