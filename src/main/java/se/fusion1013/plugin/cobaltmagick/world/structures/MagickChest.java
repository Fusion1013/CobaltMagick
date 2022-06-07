package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleFinnishGlyph;

public class MagickChest {

    // ----- VARIABLES -----

    Location location;
    int width;
    CustomItem key;
    ItemStack[] content;

    ParticleGroup particles = new ParticleGroup.ParticleGroupBuilder("light_chest")
            .addStyle(new ParticleStyleFinnishGlyph.ParticleStyleFinnishGlyphBuilder("light_chest_glyph")
                    .setLetter('l')
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setOffset(new Vector(.01, .01, .01))
                    .setCount(1)
                    .setSpeed(.1)
                    .setExtra(new Particle.DustTransition(Color.RED, Color.BLACK, 1))
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder("light_sphere")
                    .setParticle(Particle.END_ROD)
                    .setOffset(new Vector(.1, .1, .1))
                    .setCount(3)
                    .setSpeed(.1)
                    .setRadius(4)
                    .setDensity(120)
                    .build())
            .build();

    // ----- CONSTRUCTORS -----

    public MagickChest(Location location, int width, CustomItem key, ItemStack... content) {
        this.location = location;
        this.width = width;
        this.key = key;
        this.content = content;
    }

    // ----- OPEN CHEST -----

    public boolean attemptOpen(Item attemptedKey) {

        if (!key.compareTo(attemptedKey.getItemStack())) return false;

        attemptedKey.remove();

        // Remove Chest Blocks
        for (int x = -width; x <= width; x++) {
            for (int y = -width; y <= width; y++) {
                for (int z = -width; z <= width; z++) {
                    location.clone().add(new Vector(x, y, z)).getBlock().setType(Material.AIR);
                }
            }
        }

        // Spawn Items
        World world = location.getWorld();
        for (ItemStack item : content) world.dropItemNaturally(location, item);

        particles.display(location);
        location.getWorld().playSound(location, "cobalt.perk_unlock", SoundCategory.MASTER, 1, 1);

        return true;
    }

}
