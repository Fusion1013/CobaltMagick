package se.fusion1013.cobaltmagick.alchemy.glyph;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.*;

public class GlyphValidatorUtil {

    private static final Map<String, Set<Vector>> glyphOffsets = new HashMap<>();

    public static boolean hasValidGlyph(Location location, String glyphName) {
        Set<Vector> validGlyphOffsets = getValidGlyphOffsets(location, glyphName);
        return validGlyphOffsets != null && !validGlyphOffsets.isEmpty();
    }

    private static boolean validateGlyph(Location location, Set<Vector> offsets) {
        boolean isValid = true;
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                Vector offset = new Vector(x, -1, z);
                boolean shouldBeGlyphBlock = offsets.contains(offset);
                Location blockLocation = location.clone().add(offset);
                Material actualMaterial = blockLocation.getBlock().getType();

                AlchemyBlockProperties properties = AlchemyManager.getProperties(actualMaterial);
                if (properties == null && shouldBeGlyphBlock) isValid = false;
                if (properties != null && !shouldBeGlyphBlock) isValid = false;
            }
        }
        return isValid;
    }

    public static Set<Vector> getValidGlyphOffsets(Location location, String glyphName) {
        Set<Vector> offsets = getGlyphOffsets(glyphName);
        if (offsets.isEmpty()) return null;

        List<Set<Vector>> rotatedOffsets = generateRotations(offsets);
        for (Set<Vector> rotation : rotatedOffsets) {
            boolean isValid = validateGlyph(location, rotation);
            if (isValid) return rotation;
        }
        return null;
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

    private static List<Set<Vector>> generateRotations(Set<Vector> input) {
        Set<Vector> rot0 = new HashSet<>();
        Set<Vector> rot90 = new HashSet<>();
        Set<Vector> rot180 = new HashSet<>();
        Set<Vector> rot270 = new HashSet<>();

        for (Vector v : input) {
            double x = v.getX();
            double y = v.getY();
            double z = v.getZ();

            rot0.add(new Vector(x, y, z));
            rot90.add(new Vector((int) -z, y, x));
            rot180.add(new Vector((int) -x, y, (int) -z));
            rot270.add(new Vector(z, y, (int) -x));
        }

        List<Set<Vector>> rotations = new ArrayList<>(4);
        rotations.add(rot0);
        rotations.add(rot90);
        rotations.add(rot180);
        rotations.add(rot270);

        return rotations;
    }

}
