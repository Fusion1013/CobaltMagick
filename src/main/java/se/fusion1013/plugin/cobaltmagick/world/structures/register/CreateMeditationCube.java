package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Material;
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
import se.fusion1013.plugin.cobaltmagick.item.loot.SpellLootEntry;
import se.fusion1013.plugin.cobaltmagick.item.loot.WandLootEntry;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.modules.PortalStructureModule;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MeditationPortal;

public class CreateMeditationCube {

    private static final CustomLootTable MEDITATION_CUBE_LOOT = new CustomLootTable(
            new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.SHULKER_BOX},
            new LootPool(3,
                    new SpellLootEntry(4),
                    new SpellLootEntry(3),
                    new SpellLootEntry(2),
                    new SpellLootEntry(1)
            ), new LootPool(1,
                    new SpellLootEntry(10)
            )
    );

    private static final CustomLootTable MEDITATION_CUBE_TEMP_EXTRA_LOOT = new CustomLootTable(
            new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.BARREL},
            new LootPool(1, new LootEntry(SpellManager.OCARINA_E.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.OCARINA_C.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.OCARINA_B.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.OCARINA_G_SHARP.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.OCARINA_F.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.KANTELE_G.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.KANTELE_D_SHARP.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.KANTELE_G.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.KANTELE_E.getSpellItem(), 1, 1)),
            new LootPool(1, new LootEntry(SpellManager.KANTELE_A.getSpellItem(), 1, 1)),
            new LootPool(1, new WandLootEntry(5, true))
    );

    public static IStructure createMeditationCubeInsides(int id, Vector exitPortalLocation) {
        return new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id, "meditation_cube_inside", "structures/meditation_cube/meditation_cube_inside.json")
                .setNaturalGeneration(true)
                .setGenerationThreshold(-1)
                .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
                .addStructureModule(new PortalStructureModule(new MagickPortal(null, null), exitPortalLocation, Material.RED_WOOL))
                .addStructureModule(new LootStructureModule(MEDITATION_CUBE_LOOT))
                .addStructureModule(new LootStructureModule(MEDITATION_CUBE_TEMP_EXTRA_LOOT))
                .build();
    }

    public static IStructure createMeditationCubeOutsides(int id, Vector exitPortalLocation) {
        return new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id, "meditation_cube_outside", "structures/meditation_cube/meditation_cube_outside.json")
                .setNaturalGeneration(true)
                .setGenerationThreshold(-1)
                .setOffset(new Vector(0, -7, 0))
                .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
                .addStructureModule(new PortalStructureModule(new MeditationPortal(null, null), exitPortalLocation, Material.GREEN_WOOL))
                .build();
    }

}
