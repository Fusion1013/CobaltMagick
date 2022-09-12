package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.EnvironmentStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.HeightStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.OnlyInChunkCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.LootStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.ReplaceBlocksStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.AdvancementGranter;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.BookConstants;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.modules.MagickChestStructureModule;

public class CreateChestStructures {

    public static void create() {}

    static CustomLootTable light_chest = new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.CHEST, CustomLootTable.LootTarget.BARREL, CustomLootTable.LootTarget.SHULKER_BOX},
            new LootPool(1,
                    new LootEntry(BookConstants.getCunningContraptionBook(), 1, 1)
            )
    );

    static CustomLootTable light_chest_insides = new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.DROP},
            new LootPool(1,
                    new LootEntry(ItemManager.EVIL_EYE.getItemStack(), 1, 1),
                    new LootEntry(SpellManager.OMEGA.getSpellItem(), 1, 1)
            )
    );

    static CustomLootTable dark_chest_insides = new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.DROP},
            new LootPool(1,
                    new LootEntry(ItemManager.DEATH_BOUND_AMULET_DEACTIVATED.getItemStack(), 1, 1),
                    new LootEntry(SpellManager.RANDOM_SPELL.getSpellItem(), 1, 1)
            )
    );

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
            ).addAdvancement(new AdvancementGranter("progression", "light_chest_open", 20)))
            .setGenerationThreshold(-1)
            .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
            .build(), new Vector(0, 288, 0));

    public static final IStructure DARK_CHEST = StructureManager.registerAlwaysGenerate(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), 110, "dark_chest", "structures/dark_chest.json")
            .setNaturalGeneration(true)
            .setMinDistance(0)
            .addGenerationCriteria(new OnlyInChunkCriteria(0, 0))
            .addGenerationCriteria(new HeightStructureCriteria(-64, -64))
            .addStructureModule(new ReplaceBlocksStructureModule.ReplaceBlocksStructureModuleBuilder().addMaterial(Material.RED_WOOL, Material.STRUCTURE_BLOCK).build())
            .addStructureModule(new MagickChestStructureModule(Material.STRUCTURE_BLOCK, 2, ItemManager.CRYSTAL_KEY_DARK_ACTIVE,
                    dark_chest_insides,
                    "The glass key speaks!", "The chest listens"
            ).addAdvancement(new AdvancementGranter("progression", "dark_chest_open", 20)).setSpawnWaterSphere(false))
            .setGenerationThreshold(-1)
            .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
            .build(), new Vector(0, -64, 0));

}
