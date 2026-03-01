package se.fusion1013.cobaltmagick.alchemy.glyph;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlyphValidatorUtil {

    private static final Map<String, Set<Vector>> glyphOffsets = new HashMap<>();

    public static boolean hasValidGlyph(Location location, String glyphName) {
        Set<Vector> offsets = getGlyphOffsets(glyphName);
        if (offsets.isEmpty()) return false;
        return validateGlyph(location, offsets);
    }

    private static boolean validateGlyph(Location location, Set<Vector> offsets) {
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                Vector offset = new Vector(x, -1, z);
                boolean shouldBeGlyphBlock = offsets.contains(offset);
                Location blockLocation = location.clone().add(offset);
                Material actualMaterial = blockLocation.getBlock().getType();

                AlchemyBlockProperties properties = AlchemyManager.getProperties(actualMaterial);
                if (properties == null && shouldBeGlyphBlock) return false;
                if (properties != null && !shouldBeGlyphBlock) return false;
            }
        }
        return true;
    }

    public static Set<Vector> getGlyphOffsets(String glyph) {
        return glyphOffsets.containsKey(glyph) ? glyphOffsets.get(glyph) : createGlyphOffsets(glyph);
    }

    private static Set<Vector> createGlyphOffsets(String glyph) {
        Set<Vector> offsets = new HashSet<>();

        GlyphData glyphData = GlyphManager.getGlyphFromName(glyph);
        if (glyphData == null) return new HashSet<>();

        for (Vector vector : glyphData.positions()) {
            Vector newVector = new Vector(vector.getX(), -1, vector.getY());
            offsets.add(newVector);
        }

        glyphOffsets.put(glyph, offsets);
        return offsets;
    }

}
