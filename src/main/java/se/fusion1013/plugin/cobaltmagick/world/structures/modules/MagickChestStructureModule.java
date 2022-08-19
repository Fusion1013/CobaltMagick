package se.fusion1013.plugin.cobaltmagick.world.structures.modules;

import org.bukkit.*;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleFinnishGlyph;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModule;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.StructureModuleType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class MagickChestStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    int width;
    CustomItem item;
    CustomLootTable lootTable;
    Material replaceMaterial;
    String title;
    String subtitle;

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

    public MagickChestStructureModule(Material replaceMaterial, int width, CustomItem item, CustomLootTable lootTable) {
        this.replaceMaterial = replaceMaterial;
        this.width = width;
        this.item = item;
        this.lootTable = lootTable;
    }

    public MagickChestStructureModule(Material replaceMaterial, int width, CustomItem item, CustomLootTable lootTable, String title, String subtitle) {
        this.replaceMaterial = replaceMaterial;
        this.width = width;
        this.item = item;
        this.lootTable = lootTable;
        this.title = title;
        this.subtitle = subtitle;
    }

    // ----- EXECUTE -----

    @Override
    public <T extends Event> void onEvent(T event, Location location, StructureUtil.StructureHolder holder) {
        if (event instanceof PlayerDropItemEvent dropEvent) {

            // Check if the item was dropped within the structure radius
            /*
            Location corner2 = location.clone().add(new Vector(holder.width, holder.height, holder.depth));
            if (!(location.getX() <= dropEvent.getItemDrop().getLocation().getX() && corner2.getX() >= dropEvent.getItemDrop().getLocation().getX() &&
                    location.getY() <= dropEvent.getItemDrop().getLocation().getY() && corner2.getY() >= dropEvent.getItemDrop().getLocation().getY() &&
                    location.getZ() <= dropEvent.getItemDrop().getLocation().getZ() && corner2.getZ() >= dropEvent.getItemDrop().getLocation().getZ())) {
                return;
            }
             */

            if (dropEvent.getItemDrop().getWorld() != location.getWorld()) return;

            /*
            Location structureCenter = location.clone().add(new Vector(holder.width/2, holder.height/2, holder.depth/2));
            if (dropEvent.getItemDrop().getLocation().distanceSquared(structureCenter) > (width+5)*(width+5)) return;
             */

            // Check if the correct item was dropped
            ItemStack dropped = dropEvent.getItemDrop().getItemStack();
            if (!item.compareTo(dropped)) return;

            // Find the center of the magick chest
            for (int x = 0; x < holder.width; x++) {
                for (int y = 0; y < holder.height; y++) {
                    for (int z = 0; z < holder.depth; z++) {
                        Location replaceLocation = location.clone().add(new Vector(x, y, z));
                        if (replaceLocation.getBlock().getType() == replaceMaterial) {

                            // Check if the drop event was in range
                            if (dropEvent.getItemDrop().getLocation().distanceSquared(replaceLocation) > (width+5)*(width+5)) break;

                            dropEvent.getItemDrop().remove();

                            // Remove Chest Blocks
                            for (int xChest = -width; xChest <= width; xChest++) {
                                for (int yChest = -width; yChest <= width; yChest++) {
                                    for (int zChest = -width; zChest <= width; zChest++) {
                                        replaceLocation.clone().add(new Vector(xChest, yChest, zChest)).getBlock().setType(Material.AIR);
                                    }
                                }
                            }

                            // Spawn Items
                            World world = replaceLocation.getWorld();
                            List<ItemStack> stacks = lootTable.getLoot(10000);
                            for (ItemStack item : stacks) world.dropItemNaturally(replaceLocation, item);

                            particles.display(replaceLocation);
                            replaceLocation.getWorld().playSound(replaceLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1, 1);

                            // Title
                            if (title != null) {
                                dropEvent.getPlayer().sendTitle(ChatColor.GOLD + title, ChatColor.YELLOW + subtitle, 20, 70, 20);
                            }

                            // Cast Water Spell
                            SpellManager.SPHERE_OF_WATER.clone().castSpell(new Wand(false, 1, 1, 1, 1000, 1000, 1, 0, new ArrayList<>(), 10), null, new Vector(), replaceLocation);

                            // TODO: Remove structure from database

                        }
                    }
                }
            }
        }
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        // executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {

        /*
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {
                        replaceLocation.getBlock().setType(Material.AIR);

                        // Place new Music Box
                        MagickStructureManager.getInstance().registerMagickChest(replaceLocation, width, item, stacks);
                    }
                }
            }
        }
         */
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
