package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.EnvironmentStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.LootStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.wand.AbstractWand;
import se.fusion1013.plugin.cobaltmagick.wand.WandManager;

import java.util.ArrayList;

public class CreateTree {

    public static IStructure createTree(int id) {
        return new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id, "tree", "structures/tree/big_tree.json")
                .setNaturalGeneration(true)
                .setGenerationThreshold(-1)
                .setOffset(new Vector(0, -10, 0))
                .addStructureModule(new LootStructureModule(
                        new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.SHULKER_BOX},
                                new LootPool(1, new LootEntry(FLUTE.getWandItem(), 1, 1))
                        )
                ))
                .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
                .build();
    }

    private static final AbstractWand FLUTE = WandManager.getInstance().createWand(false, 1, 0.03, 0.02, 2, 30, 20, 0, new ArrayList<>(), 10)
            .addSpells(SpellManager.OCARINA_E, SpellManager.OCARINA_C, SpellManager.OCARINA_B, SpellManager.OCARINA_G_SHARP, SpellManager.OCARINA_F, SpellManager.KANTELE_G, SpellManager.KANTELE_D_SHARP, SpellManager.KANTELE_G, SpellManager.KANTELE_E, SpellManager.KANTELE_A)
            .overrideModelData(10000);
}
