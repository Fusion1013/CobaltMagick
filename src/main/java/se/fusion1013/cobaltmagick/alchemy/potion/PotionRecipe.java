package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyBlockProperties;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;

import java.util.*;
import java.util.function.Function;

public class PotionRecipe implements IPotionRecipe {

    private static final Random random = new Random();
    private static final Material[] REPLACE_MATERIAL = new Material[]{
            Material.INFESTED_COBBLESTONE, Material.INFESTED_STONE, Material.INFESTED_CHISELED_STONE_BRICKS, Material.INFESTED_CRACKED_STONE_BRICKS, Material.INFESTED_STONE_BRICKS
    };

    private final String internalName;
    private final PotionEffectType effectType;
    private final String[] inputItems;
    private final String glyph;
    private final List<Function<Location, Boolean>> conditions;

    private final Map<Vector, Boolean> glyphOffsets = new HashMap<>();
    private final List<Vector> glyphVectors = new ArrayList<>();

    public PotionRecipe(String internalName, PotionEffectType effectType, String[] inputItems, String glyph, List<Function<Location, Boolean>> conditions) {
        this.internalName = internalName;
        this.effectType = effectType;
        this.inputItems = inputItems;
        this.glyph = glyph;
        this.conditions = conditions;

        createGlyphOffsets();
    }

    private void createGlyphOffsets() {
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                glyphOffsets.put(new Vector(x, -1, z), false);
            }
        }

        GlyphData glyphData = GlyphManager.getGlyphFromName(glyph);
        if (glyphData == null) return;

        for (Vector vector : glyphData.positions()) {
            Vector newVector = new Vector(vector.getX(), -1, vector.getY());
            glyphOffsets.put(newVector, true);
            glyphVectors.add(newVector);
        }
    }

    @Override
    public void addGlyphToCauldronState(Location cauldronLocation, CauldronState state) {
        for (Vector vector : glyphVectors) {
            Location blockLocation = cauldronLocation.clone().add(vector);
            Material actualMaterial = blockLocation.getBlock().getType();
            AlchemyBlockProperties properties = AlchemyManager.getProperties(actualMaterial);
            if (properties == null) continue;
            state.update(properties);
        }
    }

    public boolean validateGlyph(Location cauldronLocation) {
        // The glyph should be placed one block down from the cauldron
        // It should be able to be rotated in any direction

        World world = cauldronLocation.getWorld();
        GlyphData data = GlyphManager.getGlyphFromName(glyph);
        for (Vector vector : data.positions()) {
            // world.spawnParticle(Particle.END_ROD, cauldronLocation.clone().add(new Vector(vector.getX(), 0, vector.getY())).toCenterLocation(), 10, .1, .1, .1, 0);
        }

        for (Map.Entry<Vector, Boolean> entrySet : glyphOffsets.entrySet()) {
            Location blockLocation = cauldronLocation.clone().add(entrySet.getKey());
            Material actualMaterial = blockLocation.getBlock().getType();

            AlchemyBlockProperties properties = AlchemyManager.getProperties(actualMaterial);
            if (properties == null && entrySet.getValue()) return false;
        }
        return true;
    }

    public boolean validateConditions(Location cauldronLocation) {
        return conditions.stream().map(c -> c.apply(cauldronLocation)).anyMatch(c -> c == false);
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getFirstInputItem() {
        return inputItems[0];
    }

    @Override
    public String[] getInputItemOrder() {
        return inputItems;
    }

    @Override
    public PotionEffectType getPotionEffectType() {
        return effectType;
    }

    @Override
    public void decay(Location cauldronLocation, int decay) {
        World world = cauldronLocation.getWorld();
        int iterations = Math.min(decay, glyphVectors.size());

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
