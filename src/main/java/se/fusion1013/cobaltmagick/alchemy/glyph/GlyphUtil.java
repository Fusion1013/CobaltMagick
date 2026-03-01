package se.fusion1013.cobaltmagick.alchemy.glyph;

import org.bukkit.*;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.*;

public class GlyphUtil {

    private static final Random random = new Random();
    private static final Material[] REPLACE_MATERIAL = new Material[]{
            Material.INFESTED_COBBLESTONE, Material.INFESTED_STONE, Material.INFESTED_CHISELED_STONE_BRICKS, Material.INFESTED_CRACKED_STONE_BRICKS, Material.INFESTED_STONE_BRICKS
    };

    public static void addGlyphToCauldronState(Location cauldronLocation, Set<Vector> glyphVectors, CauldronState state) {
        for (Vector vector : glyphVectors) {
            Location blockLocation = cauldronLocation.clone().add(vector);
            Material actualMaterial = blockLocation.getBlock().getType();
            AlchemyBlockProperties properties = AlchemyManager.getProperties(actualMaterial);
            if (properties == null) continue;
            state.update(properties);
        }
        CobaltMagick.getInstance().getLogger().info(state.toString());
    }

    public static void decay(Location cauldronLocation, Set<Vector> glyphVectors, int decay) {
        World world = cauldronLocation.getWorld();
        int iterations = Math.min(decay, glyphVectors.size());

        CobaltMagick.getInstance().getLogger().info("Decaying " + decay);

        List<Vector> shuffledPositions = new ArrayList<>(glyphVectors);
        Collections.shuffle(shuffledPositions);

        for (int i = 0; i < iterations; i++) {
            Vector vector = shuffledPositions.get(i);
            Location blockLocation = cauldronLocation.clone().add(vector);

            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
                world.setBlockData(blockLocation, REPLACE_MATERIAL[random.nextInt(REPLACE_MATERIAL.length)].createBlockData());
                world.spawnParticle(Particle.SMOKE, blockLocation.toCenterLocation().add(new Vector(0, .6, 0)), 10, .3, .1, .3, 0);
                world.playSound(blockLocation, Sound.BLOCK_LAVA_EXTINGUISH, .3f, 1);
            }, random.nextInt(20));
        }
    }

}
