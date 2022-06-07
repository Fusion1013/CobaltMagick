package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundArea;
import se.fusion1013.plugin.cobaltcore.world.spawner.CustomSpawner;
import se.fusion1013.plugin.cobaltcore.world.structure.Dilapidate;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.ConnectedStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.BiomeStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.HeightStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.OnlyInChunkCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.*;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.item.loot.WandLootEntry;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.BookConstants;
import se.fusion1013.plugin.cobaltmagick.world.structures.modules.MagickChestStructureModule;
import se.fusion1013.plugin.cobaltmagick.world.structures.modules.MusicBoxStructureModule;

public class MagickStructureManager extends Manager implements Listener, CommandExecutor {

    // ----- LOOT TABLES -----

    // -- TEST

    static CustomLootTable table = new CustomLootTable("chest",
            new LootPool(1,
                    new LootEntry(new ItemStack(Material.GOLD_BLOCK), 1, 10),
                    new LootEntry(new ItemStack(Material.DIAMOND_SWORD), 1, 1)
            ),
            new LootPool(10,
                    new LootEntry(new ItemStack(Material.BLACKSTONE), 1, 64),
                    new LootEntry(new ItemStack(Material.POLISHED_BLACKSTONE), 1, 32),
                    new LootEntry(new ItemStack(Material.GILDED_BLACKSTONE), 1, 16)
            )
    );

    // -- HIGH ALCHEMIST DUNGEON

    static CustomLootTable HIGH_ALCHEMIST_KITCHEN_LOOT = new CustomLootTable("chest",
            new LootPool(2,
                    new LootEntry(new ItemStack(Material.BREAD), 2, 27),
                    new LootEntry(new ItemStack(Material.COOKED_BEEF), 1, 15),
                    new LootEntry(new ItemStack(Material.GOLDEN_CARROT), 1, 8)
            ),
            new LootPool(5,
                     new LootEntry(new ItemStack(Material.ROTTEN_FLESH), 4, 19),
                     new LootEntry(new ItemStack(Material.BONE), 2, 7)
            ),
            new LootPool(1,
                     new LootEntry(new ItemStack(Material.WHEAT), 4, 8),
                     new LootEntry(new ItemStack(Material.HAY_BLOCK), 1, 29)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_DEAD_LOOT = new CustomLootTable("chest",
            new LootPool(9,
                    new LootEntry(new ItemStack(Material.ROTTEN_FLESH), 4, 19),
                    new LootEntry(new ItemStack(Material.GUNPOWDER), 4, 11),
                    new LootEntry(new ItemStack(Material.SPIDER_EYE), 1, 2),
                    new LootEntry(new ItemStack(Material.BONE), 2, 7)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_ENCHANTING_LOOT = new CustomLootTable("chest", // TODO: Add randomly enchanted book
            new LootPool(13,
                    new LootEntry(new ItemStack(Material.PAPER), 2, 27),
                    new LootEntry(new ItemStack(Material.BOOK), 1, 15),
                    new LootEntry(new ItemStack(Material.LAPIS_LAZULI), 8, 21),
                    new LootEntry(new ItemStack(Material.LAPIS_BLOCK), 1, 3)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_STORAGE_LOOT = new CustomLootTable("chest",
            new LootPool(3,
                    new LootEntry(new ItemStack(Material.IRON_INGOT), 2, 27),
                    new LootEntry(new ItemStack(Material.GOLD_INGOT), 1, 15),
                    new LootEntry(new ItemStack(Material.LAPIS_LAZULI), 8, 21),
                    new LootEntry(new ItemStack(Material.DIAMOND), 1, 3),
                    new LootEntry(new ItemStack(Material.RAW_IRON_BLOCK), 2, 10),
                    new LootEntry(new ItemStack(Material.RAW_COPPER_BLOCK), 4, 17),
                    new LootEntry(new ItemStack(Material.RAW_GOLD_BLOCK), 2, 4)
            ),
            new LootPool(10,
                    new LootEntry(new ItemStack(Material.COBBLESTONE), 12, 52),
                    new LootEntry(new ItemStack(Material.ANDESITE), 12, 52),
                    new LootEntry(new ItemStack(Material.COBBLED_DEEPSLATE), 12, 52),
                    new LootEntry(new ItemStack(Material.GRANITE), 12, 52),
                    new LootEntry(new ItemStack(Material.DIORITE), 12, 52)
            ),
            new LootPool(4,
                    new LootEntry(new ItemStack(Material.GRAVEL), 12, 52),
                    new LootEntry(new ItemStack(Material.OBSIDIAN), 1, 3),
                    new LootEntry(new ItemStack(Material.TUFF), 5, 39),
                    new LootEntry(new ItemStack(Material.CALCITE), 5, 39),
                    new LootEntry(new ItemStack(Material.ROOTED_DIRT), 2, 17)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_DRIPSTONE_LOOT = new CustomLootTable("chest",
            new LootPool(13,
                    new LootEntry(new ItemStack(Material.DRIPSTONE_BLOCK), 2, 9),
                    new LootEntry(new ItemStack(Material.POINTED_DRIPSTONE), 3, 17)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_MOSS_LOOT = new CustomLootTable("chest",
            new LootPool(13,
                    new LootEntry(new ItemStack(Material.MOSS_BLOCK), 2, 17),
                    new LootEntry(new ItemStack(Material.MOSS_CARPET), 1, 3),
                    new LootEntry(new ItemStack(Material.WHEAT_SEEDS), 1, 2),
                    new LootEntry(new ItemStack(Material.AZALEA), 2, 4),
                    new LootEntry(new ItemStack(Material.FLOWERING_AZALEA), 1, 2),
                    new LootEntry(new ItemStack(Material.CLAY_BALL), 1, 13)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_SPELL_LOOT = new CustomLootTable("chest",
            new LootPool(1,
                    new LootEntry(SpellManager.SPARK_BOLT.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.BUBBLE_SPARK.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.BOUNCING_BURST.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.FIREBOLT.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.BURST_OF_AIR.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.SPARK_BOLT_WITH_TRIGGER.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.SPHERE_OF_BUOYANCY.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.SPHERE_OF_STILLNESS.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.SPHERE_OF_THUNDER.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.ALL_SEEING_EYE.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.TUPLE_SPELL.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.TRIPLE_SPELL.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.BIFURCATED.getSpellItem(), 1, 1),
                    new LootEntry(SpellManager.TRIFURCATED.getSpellItem(), 1, 1)
            )
    );

    static CustomLootTable HIGH_ALCHEMIST_POTION_LOOT = new CustomLootTable("chest",
            new LootPool(13,
                    new LootEntry(new ItemStack(Material.BLAZE_POWDER), 2, 9),
                    new LootEntry(new ItemStack(Material.GOLDEN_CARROT), 2, 9),
                    new LootEntry(new ItemStack(Material.RABBIT_FOOT), 2, 7),
                    new LootEntry(new ItemStack(Material.RABBIT_HIDE), 5, 13),
                    new LootEntry(new ItemStack(Material.MAGMA_CREAM), 3, 17),
                    new LootEntry(new ItemStack(Material.GLISTERING_MELON_SLICE), 3, 17)
            )
    );

    // -- LIGHT CHEST

    static CustomLootTable light_chest = new CustomLootTable("chest",
            new LootPool(1,
                    new LootEntry(BookConstants.getLightBook(), 1, 1)
            )
    );

    static CustomLootTable light_chest_insides = new CustomLootTable("drop",
            new LootPool(1,
                    new LootEntry(SpellManager.OCTUPLE_SPELL.getSpellItem(), 1, 1)
            )
    );

    static CustomLootTable dark_chest = new CustomLootTable("chest",
            new LootPool(1,
                    new LootEntry(BookConstants.getLightBook(), 1, 1)
            )
    );

    static CustomLootTable dark_chest_insides = new CustomLootTable("drop",
            new LootPool(1,
                    new LootEntry(SpellManager.OCTUPLE_SPELL.getSpellItem(), 1, 1)
            )
    );

    // ----- HIGH ALCHEMIST STRUCTURES: 1xxxx -----

    // -- HIGH ALCHEMIST

    public static final IStructure HIGH_ALCHEMIST_BOSS_ROOM = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 10, "high_alchemist_boss_room", "structures/high_alchemist_boss_room.json")
            .setNaturalGeneration(false)
            .addStructureModule(new CustomSpawnerStructureModule(new CustomSpawner(null, "high_alchemist", 1, 15, 0), Material.RED_WOOL))
            .addStructureModule(new SoundAreaStructureModule(new SoundArea(null, 20, 173000, "cobalt.eternal_halls"), Material.YELLOW_WOOL))
            .addStructureModule(new SurfaceStructureStructureModule(CobaltMagick.getInstance(), "structures/high_alchemist/high_alchemist_surface_structure.json", new Vector(0, -2, 0)))
            .build());

    public static final IStructure DUNGEON_ROOM_5_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 11, "5_dungeon_room_1", "structures/high_alchemist/5_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_5_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 12, "5_dungeon_room_2", "structures/high_alchemist/5_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_5_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 13, "5_dungeon_room_3", "structures/high_alchemist/5_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_5_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 14, "5_dungeon_room_4", "structures/high_alchemist/5_dungeon_room_4.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 15, "6_dungeon_room_1", "structures/high_alchemist/6_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 16, "6_dungeon_room_2", "structures/high_alchemist/6_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 17, "6_dungeon_room_3", "structures/high_alchemist/6_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 18, "6_dungeon_room_4", "structures/high_alchemist/6_dungeon_room_4.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_5 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 19, "6_dungeon_room_5", "structures/high_alchemist/6_dungeon_room_5.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_6_6 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 110, "6_dungeon_room_6", "structures/high_alchemist/6_dungeon_room_6.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_7_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 111, "7_dungeon_room_1", "structures/high_alchemist/7_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_7_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 112, "7_dungeon_room_2", "structures/high_alchemist/7_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_7_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 113, "7_dungeon_room_3", "structures/high_alchemist/7_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_7_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 114, "7_dungeon_room_4", "structures/high_alchemist/7_dungeon_room_4.json")
            .setNaturalGeneration(false)
            .build());

    // -- KITCHEN

    public static final IStructure DUNGEON_ROOM_8_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 115, "8_dungeon_room_1", "structures/high_alchemist/8_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_KITCHEN_LOOT))
            .build());

    // -- ENCHANTING ROOM

    public static final IStructure DUNGEON_ROOM_8_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 116, "8_dungeon_room_2", "structures/high_alchemist/8_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_ENCHANTING_LOOT))
            .build());

    // -- STORAGE ROOM

    public static final IStructure DUNGEON_ROOM_8_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 117, "8_dungeon_room_3", "structures/high_alchemist/8_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_STORAGE_LOOT))
            .addStructureModule(new CriteriaStructureModule(
                    new LootStructureModule(HIGH_ALCHEMIST_MOSS_LOOT),
                    new BiomeStructureCriteria(Biome.LUSH_CAVES)
            ))
            .addStructureModule(new CriteriaStructureModule(
                    new LootStructureModule(HIGH_ALCHEMIST_DRIPSTONE_LOOT),
                    new BiomeStructureCriteria(Biome.DRIPSTONE_CAVES)
            ))
            .build());

    // -- ENCHANTING ROOM

    public static final IStructure DUNGEON_ROOM_8_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 118, "8_dungeon_room_4", "structures/high_alchemist/8_dungeon_room_4.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_ENCHANTING_LOOT))
            .build());

    public static final IStructure DUNGEON_ROOM_8_5 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 119, "8_dungeon_room_5", "structures/high_alchemist/8_dungeon_room_5.json")
            .setNaturalGeneration(false)
            .build());

    // -- SPELL / WAND LOOT ROOM

    public static final IStructure DUNGEON_ROOM_9_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 120, "9_dungeon_room_1", "structures/high_alchemist/9_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_SPELL_LOOT))
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_ENCHANTING_LOOT))
            .build());

    public static final IStructure DUNGEON_ROOM_9_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 121, "9_dungeon_room_2", "structures/high_alchemist/9_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_9_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 122, "9_dungeon_room_3", "structures/high_alchemist/9_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure DUNGEON_ROOM_10_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 123, "10_dungeon_room_1", "structures/high_alchemist/10_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .build());

    // -- POTION ROOM

    public static final IStructure DUNGEON_ROOM_11_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 124, "11_dungeon_room_1", "structures/high_alchemist/11_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_POTION_LOOT))
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_STORAGE_LOOT))
            .build());

    // -- PRISON ROOM 1

    public static final IStructure DUNGEON_ROOM_11_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 125, "11_dungeon_room_2", "structures/high_alchemist/11_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_DEAD_LOOT))
            .build());

    // -- PRISON ROOM 2

    public static final IStructure DUNGEON_ROOM_11_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 126, "11_dungeon_room_3", "structures/high_alchemist/11_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_DEAD_LOOT))
            .build());

    // -- PRISON ROOM 3

    public static final IStructure DUNGEON_ROOM_11_4 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 127, "11_dungeon_room_4", "structures/high_alchemist/11_dungeon_room_4.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_DEAD_LOOT))
            .build());

    public static final IStructure DUNGEON_ROOM_15_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 128, "15_dungeon_room_1", "structures/high_alchemist/15_dungeon_room_1.json")
            .setNaturalGeneration(false)
            .build());

    // -- WAND ROOM

    public static final IStructure DUNGEON_ROOM_15_2 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 129, "15_dungeon_room_2", "structures/high_alchemist/15_dungeon_room_2.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(new CustomLootTable("chest", new LootPool(1, new WandLootEntry(0, false)))))
            .build());

    public static final IStructure DUNGEON_ROOM_15_3 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 130, "15_dungeon_room_3", "structures/high_alchemist/15_dungeon_room_3.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_DEAD_LOOT))
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_STORAGE_LOOT))
            .build());

    // -- MUSIC BOXES

    public static final IStructure MUSIC_BOX_DESERT = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 15, "music_box_desert", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.ancient_getaway", Material.RED_WOOL))
            .setGenerationThreshold(.5)
            .setMinDistance(1000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.DESERT))
            .build());

    public static final IStructure MUSIC_BOX_PLAINS = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 16, "music_box_plains", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.hydroangea", Material.RED_WOOL))
            .setGenerationThreshold(.5)
            .setMinDistance(1000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.PLAINS, Biome.SUNFLOWER_PLAINS))
            .build());

    public static final IStructure MUSIC_BOX_OCEAN = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 17, "music_box_ocean", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.shooting_for_the_stars", Material.RED_WOOL))
            .setGenerationThreshold(.5)
            .setMinDistance(2000)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_LUKEWARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN))
            .build());

    public static final IStructure MUSIC_BOX_ISLAND = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 18, "music_box_island", "structures/music_box.json")
            .setNaturalGeneration(true)
            .addStructureModule(new MusicBoxStructureModule("cobalt.outpost", Material.RED_WOOL))
            .setGenerationThreshold(.5)
            .setMinDistance(500)
            .setOnGround(true)
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.MUSHROOM_FIELDS))
            .build());

    // -- LIGHT & DARK CHEST

    public static final IStructure LIGHT_CHEST = StructureManager.registerAlwaysGenerate(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 19, "light_chest", "structures/light_chest.json")
            .setNaturalGeneration(true)
            .setMinDistance(0)
            .addGenerationCriteria(new OnlyInChunkCriteria(0, 0))
            .addGenerationCriteria(new HeightStructureCriteria(288, 288))
            .addStructureModule(new LootStructureModule(light_chest))
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder().addMaterial(Material.RED_WOOL, Material.STRUCTURE_BLOCK).build())
            .addStructureModule(new MagickChestStructureModule(Material.STRUCTURE_BLOCK, 2, ItemManager.CRYSTAL_KEY_LIGHT_ACTIVE,
                    light_chest_insides,
                    "The chest opens!", "But the key might have other stories to tell..."
            ))
            .setGenerationThreshold(-1)
            .build(), new Vector(0, 288, 0));

    public static final IStructure DARK_CHEST = StructureManager.registerAlwaysGenerate(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 110, "dark_chest", "structures/dark_chest.json")
            .setNaturalGeneration(true)
            .setMinDistance(0)
            .addGenerationCriteria(new OnlyInChunkCriteria(0, 0))
            .addGenerationCriteria(new HeightStructureCriteria(-64, -64))
            .addStructureModule(new LootStructureModule(dark_chest))
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder().addMaterial(Material.RED_WOOL, Material.STRUCTURE_BLOCK).build())
            .addStructureModule(new MagickChestStructureModule(Material.STRUCTURE_BLOCK, 2, ItemManager.CRYSTAL_KEY_DARK_ACTIVE,
                    dark_chest_insides,
                    "The glass key speaks!", "The chest listens"
            ))
            .setGenerationThreshold(-1)
            .build(), new Vector(0, -64, 0));

    // ----- CONNECTED STRUCTURES: 2xxxx -----

    public static final IStructure HIGH_ALCHEMIST_DUNGEON = StructureManager.register(new ConnectedStructure.ConnectedStructureBuilder(CobaltMagick.getInstance(), 131, "high_alchemist_dungeon", 8,
            // Structures
            DUNGEON_ROOM_5_1,DUNGEON_ROOM_5_2,DUNGEON_ROOM_5_3,DUNGEON_ROOM_5_4,
            DUNGEON_ROOM_6_1,DUNGEON_ROOM_6_2,DUNGEON_ROOM_6_3,DUNGEON_ROOM_6_4,DUNGEON_ROOM_6_5,DUNGEON_ROOM_6_6,
            DUNGEON_ROOM_7_1,DUNGEON_ROOM_7_2,DUNGEON_ROOM_7_3,DUNGEON_ROOM_7_4,
            DUNGEON_ROOM_8_1,DUNGEON_ROOM_8_2,DUNGEON_ROOM_8_3,DUNGEON_ROOM_8_4,DUNGEON_ROOM_8_5,
            DUNGEON_ROOM_9_1,DUNGEON_ROOM_9_2,DUNGEON_ROOM_9_3,
            DUNGEON_ROOM_10_1,
            DUNGEON_ROOM_11_1,DUNGEON_ROOM_11_2,DUNGEON_ROOM_11_3,DUNGEON_ROOM_11_4,
            DUNGEON_ROOM_15_1,DUNGEON_ROOM_15_2,DUNGEON_ROOM_15_3)
            // Generation Settings
            .setInitialStructure(HIGH_ALCHEMIST_BOSS_ROOM)
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder()
                    .addMaterial(Material.IRON_BLOCK, Material.BLACKSTONE, Material.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_BRICKS)
                    .addMaterial(Material.GOLD_BLOCK, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE)
                    .addMaterial(Material.EMERALD_BLOCK, Material.POLISHED_BASALT)
                    .addMaterial(Material.OAK_STAIRS, Material.DEEPSLATE_BRICK_STAIRS, Material.DEEPSLATE_TILE_STAIRS)
                    .addMaterial(Material.OAK_SLAB, Material.DEEPSLATE_BRICK_SLAB, Material.DEEPSLATE_TILE_SLAB)
                    .build())
            .addStructureModule(new CustomSpawnerStructureModule(
                    new CustomSpawner(null, "apprentice", 2, 16.0, 2, 20*10), Material.DIAMOND_BLOCK
            ))
            .addStructureModule(new DilapidateStructureModule.DilapidateStructureModuleBuilder(
                    new Dilapidate(.25).setOnlyRemove(
                            new Material[] {Material.BLACKSTONE, Material.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_BRICKS, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE}
                    )
            )
            .build())
            // Generation Conditions
            .setGenerationThreshold(.55)
            .setMinDistance(1000)
            .addGenerationCriteria(new BiomeStructureCriteria(
                    Biome.DRIPSTONE_CAVES, Biome.LUSH_CAVES
            ))
            .addGenerationCriteria(new HeightStructureCriteria(0, -50))
            .setNaturalGeneration(true)
            .addGenerationCriteria(location -> location.distanceSquared(new Location(location.getWorld(), 0, 64, 0)) > 300*300) // Don't generate within 300 blocks of (0,0)
            .build());

    // ----- SMALL DUNGEON SURFACE: 3xxx -----

    public static final IStructure SMALL_DUNGEON_SURFACE_START = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 31, "small_dungeon_surface_start", "structures/small_dungeon_surface/small_dungeon_surface.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure SMALL_DUNGEON_SURFACE_SHAFT = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 32, "small_dungeon_surface_shaft", "structures/small_dungeon_surface/small_dungeon_shaft.json")
            .setNaturalGeneration(false)
            .build());

    public static final IStructure SMALL_DUNGEON_SURFACE_STORAGE_1 = StructureManager.register(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 33, "small_dungeon_surface_storage", "structures/small_dungeon_surface/small_dungeon_storage_1.json")
            .setNaturalGeneration(false)
            .addStructureModule(new LootStructureModule(HIGH_ALCHEMIST_STORAGE_LOOT)) // TODO: Add unique loot
            .build());

    public static final IStructure SMALL_SURFACE_DUNGEON = StructureManager.register(new ConnectedStructure.ConnectedStructureBuilder(CobaltMagick.getInstance(), 34, "small_surface_dungeon", 5,
            // Structures
            SMALL_DUNGEON_SURFACE_SHAFT, SMALL_DUNGEON_SURFACE_STORAGE_1)
            .setInitialStructure(SMALL_DUNGEON_SURFACE_START)
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder()
                    .addMaterial(Material.IRON_BLOCK, Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.POLISHED_DEEPSLATE)
                    .addMaterial(Material.GOLD_BLOCK, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE)
                    .build())
            // Generation Criteria
            .setNaturalGeneration(true)
            .setGenerationThreshold(.4)
            .setMinDistance(1200)
            .setOnGround(true)
            .setOffset(new Vector(0, -5, 0))
            .addGenerationCriteria(new HeightStructureCriteria(319, 100))
            .addGenerationCriteria(new BiomeStructureCriteria(Biome.SNOWY_PLAINS))
            .addGenerationCriteria(location -> location.distanceSquared(new Location(location.getWorld(), 0, 64, 0)) > 500*500) // Don't generate within 500 blocks of (0,0)
            .build());

    // ----- CONSTRUCTORS -----

    public MagickStructureManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("magick_structure", this);
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        // Load magick chests
        // TODO: Incorporate this into the general structure layout

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static MagickStructureManager INSTANCE = null;
    /**
     * Returns the object representing this <code>MagickStructureManager</code>.
     *
     * @return The object of this class
     */
    public static MagickStructureManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new MagickStructureManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
