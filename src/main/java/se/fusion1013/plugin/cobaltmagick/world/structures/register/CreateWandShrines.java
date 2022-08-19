package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.BiomeStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.HeightStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.LootStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.BookConstants;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;
import se.fusion1013.plugin.cobaltmagick.world.structures.modules.HiddenObjectStructureModule;

import java.util.ArrayList;
import java.util.List;

public class CreateWandShrines {

    static CustomLootTable SHRINE_2_LOOT = new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.CHEST, CustomLootTable.LootTarget.BARREL, CustomLootTable.LootTarget.SHULKER_BOX},
            new LootPool(1,
                    new LootEntry(BookConstants.getDarkBook(), 1, 1),
                    new LootEntry(ItemManager.AQUAMARINE.getItemStack(), 1, 3),
                    new LootEntry(ItemManager.AQUAMARINE.getItemStack(), 1, 3),
                    new LootEntry(ItemManager.MANA_POWDER.getItemStack(), 1, 2)
            ),
            new LootPool(7,
                    new LootEntry(ItemManager.AQUAMARINE.getItemStack(), 1, 3),
                    new LootEntry(ItemManager.AQUAMARINE.getItemStack(), 1, 3),
                    new LootEntry(ItemManager.MANA_POWDER.getItemStack(), 1, 2)
            )
    );

    public static List<IStructure> create(int id) {

        List<IStructure> shrineStructures = new ArrayList<>();

        // Level 1 Shrine Structure
        shrineStructures.add(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id, "wand_shrine_level_1", "structures/wand_shrines/wand_shrine_1.json")
                .addStructureModule(new HiddenObjectStructureModule(Material.BARRIER, RevealMethod.PROXIMITY)
                        .setWandSpawn(1)
                        .setDeleteOnActivation(true))
                .setNaturalGeneration(true)
                .setGenerationThreshold(.55)
                .addGenerationCriteria(new HeightStructureCriteria(30, -60))
                .build());

        // Level 2 Shrine Structure
        shrineStructures.add(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id+1, "wand_shrine_level_2", "structures/wand_shrines/wand_shrine_2.json")
                .addStructureModule(new HiddenObjectStructureModule(Material.BARRIER, RevealMethod.PROXIMITY)
                        .setWandSpawn(2)
                        .setDeleteOnActivation(true))
                .addStructureModule(new LootStructureModule(SHRINE_2_LOOT))
                .setNaturalGeneration(true)
                .setGenerationThreshold(.6)
                .addGenerationCriteria(new HeightStructureCriteria(-10, -60))
                .build());

        // Level 3 Shrine Structure
        shrineStructures.add(new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id+2, "wand_shrine_level_3", "structures/wand_shrines/wand_shrine_1.json")
                .addStructureModule(new HiddenObjectStructureModule(Material.BARRIER, RevealMethod.PROXIMITY)
                        .setWandSpawn(3)
                        .setDeleteOnActivation(true))
                .setNaturalGeneration(true)
                .setGenerationThreshold(.5)
                .addGenerationCriteria(new HeightStructureCriteria(30, -60))
                .addGenerationCriteria(new BiomeStructureCriteria(Biome.DEEP_DARK))
                .build());

        return shrineStructures;
    }

}
