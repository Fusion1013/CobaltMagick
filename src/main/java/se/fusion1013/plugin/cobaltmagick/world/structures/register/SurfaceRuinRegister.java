package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.BiomeStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.HeightStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.MaxHeightVariationStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.ExecuteRandomStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.LootStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.ReplaceBlocksStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.ConnectedStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

import static se.fusion1013.plugin.cobaltmagick.world.structures.MagickStructureManager.GENERIC_DEAD_LOOT;

public class SurfaceRuinRegister {

    // ----- LOOT TABLES -----

    static CustomLootTable SURFACE_RUIN_LOOT_ROOM_LOOT = new CustomLootTable("chest",
            new LootPool(7,
                    new LootEntry(ItemManager.SHINY_ORB.getItemStack(), 1, 1),
                    new LootEntry(new ItemStack(Material.GOLD_INGOT), 1, 27),
                    new LootEntry(new ItemStack(Material.GOLD_NUGGET), 1, 51),
                    new LootEntry(new ItemStack(Material.GOLD_BLOCK), 1, 9)
            )
    );

    static CustomLootTable SURFACE_RUIN_POTION_LOOT = new CustomLootTable("chest", // TODO: Add random potion
            new LootPool(5,
                    new LootEntry(new ItemStack(Material.BLAZE_POWDER), 2, 15),
                    new LootEntry(new ItemStack(Material.BLAZE_ROD), 1, 7),
                    new LootEntry(new ItemStack(Material.SPIDER_EYE), 1, 2),
                    new LootEntry(new ItemStack(Material.FERMENTED_SPIDER_EYE), 1, 1),
                    new LootEntry(new ItemStack(Material.NETHER_WART), 4, 18),
                    new LootEntry(new ItemStack(Material.GOLDEN_CARROT), 3, 17)
            )
    );

    static CustomLootTable SURFACE_RUIN_BOOKSHELF_LOOT = new CustomLootTable("chest", // TODO: Add randomly enchanted book
            new LootPool(5,
                    new LootEntry(new ItemStack(Material.PAPER), 2, 15),
                    new LootEntry(new ItemStack(Material.BOOK), 1, 7)
            )
    );

    // Bookshelf Room
    public static final IStructure SURFACE_RUIN_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 40, "surface_ruin_1", "structures/surface_ruin/surface_ruin_1.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 11, 11))
            .addStructureModule(new LootStructureModule(SURFACE_RUIN_BOOKSHELF_LOOT))
            .build());

    public static final IStructure SURFACE_RUIN_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 41, "surface_ruin_2", "structures/surface_ruin/surface_ruin_2.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 16, 13))
            .build());

    // Potion Room
    public static final IStructure SURFACE_RUIN_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 42, "surface_ruin_3", "structures/surface_ruin/surface_ruin_3.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 15, 9))
            .addStructureModule(new LootStructureModule(SURFACE_RUIN_POTION_LOOT))
            .build());

    public static final IStructure SURFACE_RUIN_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 43, "surface_ruin_4", "structures/surface_ruin/surface_ruin_4.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 12, 12))
            .build());

    public static final IStructure SURFACE_RUIN_5 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 44, "surface_ruin_5", "structures/surface_ruin/surface_ruin_5.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 13, 9))
            .build());

    public static final IStructure SURFACE_RUIN_6 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 45, "surface_ruin_6", "structures/surface_ruin/surface_ruin_6.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 13, 9))
            .build());

    // Dead Loot Room
    public static final IStructure SURFACE_RUIN_7 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 46, "surface_ruin_7", "structures/surface_ruin/surface_ruin_7.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 16, 12))
            .addStructureModule(new LootStructureModule(GENERIC_DEAD_LOOT))
            .build());

    // Starting structure
    public static final IStructure SURFACE_RUIN_8 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 47, "surface_ruin_8", "structures/surface_ruin/surface_ruin_8.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -9, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 24, 17))
            .addStructureModule(new LootStructureModule(SURFACE_RUIN_LOOT_ROOM_LOOT))
            .build());

    public static final IStructure SURFACE_RUIN_9 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 48, "surface_ruin_9", "structures/surface_ruin/surface_ruin_9.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 29, 29))
            .build());

    public static final IStructure SURFACE_RUIN_10 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 49, "surface_ruin_10", "structures/surface_ruin/surface_ruin_10.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 10, 9))
            .build());

    public static final IStructure SURFACE_RUIN_11 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 410, "surface_ruin_11", "structures/surface_ruin/surface_ruin_11.json")
            .setNaturalGeneration(false).setOnGround(true).setOffset(new Vector(0, -2, 0))
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 11, 12))
            .build());

    public static final IStructure SURFACE_RUIN = StructureManager.register(new ConnectedStructure.ConnectedStructureBuilder(CobaltMagick.getInstance(), 46, "surface_ruin", 3,
            SURFACE_RUIN_1,SURFACE_RUIN_2,SURFACE_RUIN_3,SURFACE_RUIN_4,SURFACE_RUIN_5,SURFACE_RUIN_6,SURFACE_RUIN_7,SURFACE_RUIN_9,SURFACE_RUIN_10,SURFACE_RUIN_11)
            .setInitialStructure(SURFACE_RUIN_8)
            // Random Block Generation
            .addStructureModule(new ExecuteRandomStructureModule(StructureModuleType.POST,
                    new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder()
                            .addMaterial(Material.IRON_BLOCK, Material.STONE_BRICKS, Material.ANDESITE, Material.STONE, Material.POLISHED_ANDESITE, Material.GRASS_BLOCK, Material.MOSSY_COBBLESTONE)
                            .addMaterial(Material.GOLD_BLOCK, Material.DEEPSLATE_BRICKS, Material.POLISHED_DEEPSLATE, Material.DEEPSLATE_TILES)
                            .build()
            ))
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder()
                    .addMaterial(Material.DIRT, Material.GRASS_BLOCK)
                    .build())
            // Generation Criteria
            .setNaturalGeneration(true)
            .setGenerationThreshold(.4)
            .setMinDistance(1200)
            .setOnGround(true)
            .addGenerationCriteria(new MaxHeightVariationStructureModule(4, 24, 17))
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.PLAINS, Biome.SAVANNA, Biome.TAIGA, Biome.SPARSE_JUNGLE, Biome.MEADOW))
            .addGenerationCriteria(location -> location.distanceSquared(new Location(location.getWorld(), 0, 64, 0)) > 500*500)
            .build());

}
