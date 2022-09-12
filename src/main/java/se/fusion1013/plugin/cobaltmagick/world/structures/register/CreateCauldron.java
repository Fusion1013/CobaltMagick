package se.fusion1013.plugin.cobaltmagick.world.structures.register;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleFinnishGlyph;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleText;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.EnvironmentStructureCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.SimpleStructure;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.portal.MagickPortal;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.modules.*;

public class CreateCauldron {

    static ParticleGroup TEMP_GROUP = new ParticleGroup.ParticleGroupBuilder("cauldron_text_1")
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.END_ROD)
                    .setRadius(5)
                    .setDensity(40)
                    .setCount(10)
                    .setOffset(new Vector(.3, .3, .3))
                    .build())
            .build();

    private static final ParticleGroup NIGREDO_TEXT = new ParticleGroup.ParticleGroupBuilder("nigredo_text")
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("nigredo1")
                    .setText("First, thou must call upon death indiscriminate,")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 180, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("nigredo2")
                    .setText("for it must be and shall always be.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 180, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("nigredo3")
                    .setText("Bound by incantation. Call upon the shadow within")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 180, 0))
                    .build())
            .setIntegrity(.2)
            .build();

    private static final ParticleGroup ALBEDO_TEXT = new ParticleGroup.ParticleGroupBuilder("albedo_text")
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("albedo1")
                    .setText("Take, he tells us, Our Matter, combine with First Matter.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 270, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("albedo2")
                    .setText("Which is darkness, set in its vessel.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 270, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("albedo3")
                    .setText("Over a gentle fire until liquefaction takes place.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 270, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("albedo4")
                    .setText("Bringing light and clarity to the prima materia")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 270, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("albedo5")
                    .setText("Cleansed from impurities")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 270, 0))
                    .build())
            .setIntegrity(.2)
            .build();

    private static final ParticleGroup CITRINITAS_TEXT = new ParticleGroup.ParticleGroupBuilder("citrinitas_text")
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("citrinitas1")
                    .setText("Purified, it may be, yet darkness still persists")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 0, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("citrinitas2")
                    .setText("Thus comes the dawn of Solar Light")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 0, 0))
                    .build())
            .setIntegrity(.2)
            .build();

    private static final ParticleGroup RUBEDO_TEXT = new ParticleGroup.ParticleGroupBuilder("rubedo_text")
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("rubedo1")
                    .setText("A promise made, a promise to keep.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 90, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("rubedo2")
                    .setText("Bound by incantation.")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 90, 0))
                    .build())
            .addStyle(new ParticleStyleText.ParticleStyleTextBuilder("rubedo3")
                    .setText("The reddening shall mark the end of the Great Work")
                    .setGlyphStyle(new ParticleStyleFinnishGlyph("glyph1"))
                    .setParticle(Particle.END_ROD)
                    .setRotation(new Vector(0, 90, 0))
                    .build())
            .setIntegrity(.2)
            .build();

    public static IStructure createCauldron(int id, Vector entryPortalLocation) {
        NIGREDO_TEXT.setStyleOffset("nigredo1", new Vector(0, 4, 0));
        NIGREDO_TEXT.setStyleOffset("nigredo3", new Vector(0, -4, 0));

        ALBEDO_TEXT.setStyleOffset("albedo1", new Vector(0, 8, 0));
        ALBEDO_TEXT.setStyleOffset("albedo2", new Vector(0, 4, 0));
        ALBEDO_TEXT.setStyleOffset("albedo3", new Vector(0, 0, 0));
        ALBEDO_TEXT.setStyleOffset("albedo4", new Vector(0, -4, 0));
        ALBEDO_TEXT.setStyleOffset("albedo5", new Vector(0, -8, 0));

        CITRINITAS_TEXT.setStyleOffset("citrinitas1", new Vector(0, 4, 0));

        RUBEDO_TEXT.setStyleOffset("rubedo1", new Vector(0, 4, 0));
        RUBEDO_TEXT.setStyleOffset("rubedo3", new Vector(0, -4, 0));

        return new SimpleStructure.SimpleStructureBuilder(CobaltMagick.getInstance(), id, "cauldron", "structures/cauldron/cauldron.json")
                .setNaturalGeneration(true)
                .setGenerationThreshold(-1)
                .addGenerationCriteria(new EnvironmentStructureCriteria(World.Environment.NORMAL))
                .addStructureModule(new PortalStructureModule(new MagickPortal(null, null), entryPortalLocation, Material.PURPLE_WOOL))
                .addStructureModule( // Nigredo Secret Text
                        new MultiActivatableStructureModule(2,
                                new IStorageObjectStructureModule[] {
                                        new HiddenParticleStructureModule(NIGREDO_TEXT, Material.WHITE_CONCRETE
                                    )},
                                new IStorageObjectStructureModule[] {
                                        new MaterialLockStructureModule(Material.WHITE_STAINED_GLASS, new Material[] {
                                                Material.IRON_BLOCK,
                                                Material.RAW_IRON_BLOCK, Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE
                                        }),
                                        new MaterialLockStructureModule(Material.ORANGE_STAINED_GLASS, new Material[] {
                                                Material.COPPER_BLOCK, Material.EXPOSED_COPPER, Material.WEATHERED_COPPER, Material.OXIDIZED_COPPER,
                                                Material.CUT_COPPER, Material.EXPOSED_CUT_COPPER, Material.WEATHERED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER,
                                                Material.RAW_COPPER_BLOCK, Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE
                                        })
                                }
                        )
                )
                .addStructureModule( // Albedo Secret Text
                        new MultiActivatableStructureModule(2,
                                new IStorageObjectStructureModule[] {
                                        new HiddenParticleStructureModule(ALBEDO_TEXT, Material.ORANGE_CONCRETE
                                        )},
                                new IStorageObjectStructureModule[] {
                                        new MaterialLockStructureModule(Material.MAGENTA_STAINED_GLASS, new Material[] {
                                                Material.GOLD_BLOCK, Material.RAW_GOLD_BLOCK
                                        }),
                                        new MaterialLockStructureModule(Material.LIGHT_BLUE_STAINED_GLASS, new Material[] {
                                                Material.LAVA
                                        })
                                }
                        )
                )
                .addStructureModule( // Citrinitas Secret Text
                        new MultiActivatableStructureModule(2,
                                new IStorageObjectStructureModule[] {
                                        new HiddenParticleStructureModule(CITRINITAS_TEXT, Material.MAGENTA_CONCRETE
                                        )},
                                new IStorageObjectStructureModule[] {
                                        new MaterialLockStructureModule(Material.YELLOW_STAINED_GLASS, new Material[] {
                                                Material.SNOW_BLOCK
                                        }),
                                        new MaterialLockStructureModule(Material.LIME_STAINED_GLASS, new Material[] {
                                                Material.WATER
                                        })
                                }
                        )
                )
                .addStructureModule( // Rubedo Secret Text
                        new MultiActivatableStructureModule(2,
                                new IStorageObjectStructureModule[] {
                                        new HiddenParticleStructureModule(RUBEDO_TEXT, Material.LIGHT_BLUE_CONCRETE
                                        )},
                                new IStorageObjectStructureModule[] {
                                        new MaterialLockStructureModule(Material.PINK_STAINED_GLASS, new Material[] {
                                                Material.CRYING_OBSIDIAN
                                        }),
                                        new MaterialLockStructureModule(Material.GRAY_STAINED_GLASS, new Material[] {
                                                Material.MAGMA_BLOCK
                                        })
                                }
                        )
                )
                .build();
    }
}
