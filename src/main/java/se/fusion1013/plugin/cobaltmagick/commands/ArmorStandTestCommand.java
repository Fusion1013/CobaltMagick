package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandTestCommand {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 4;
    private static final int DEPTH = 7;

    private static final int GAP = 6;

    // South-West
    private static final int[][] AMETHYST_OFFSETS = new int[][] { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 }, { 0, 0, 4 }, { 0, 0, 5 }, { 0, 0, 6 }, { 0, 0, 7 }, { 0, 1, 7 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, { 1, 0, 3 }, { 1, 0, 4 }, { 1, 0, 5 }, { 1, 0, 6 }, { 1, 0, 7 }, { 1, 1, 1 }, { 1, 1, 3 }, { 1, 1, 4 }, { 1, 1, 6 }, { 1, 1, 7 }, { 1, 2, 1 }, { 1, 2, 3 }, { 1, 2, 4 }, { 1, 2, 6 }, { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, { 2, 0, 3 }, { 2, 0, 4 }, { 2, 0, 5 }, { 2, 0, 6 }, { 2, 0, 7 }, { 2, 1, 2 }, { 2, 1, 3 }, { 2, 1, 4 }, { 2, 1, 5 }, { 2, 1, 6 }, { 2, 1, 7 }, { 2, 2, 7 }, { 3, 0, 0 }, { 3, 0, 1 }, { 3, 0, 2 }, { 3, 1, 1 }, { 3, 1, 2 }, { 3, 1, 3 }, { 3, 1, 4 }, { 3, 1, 5 }, { 3, 1, 6 }, { 3, 1, 7 }, { 3, 2, 1 }, { 3, 2, 7 }, { 4, 0, 0 }, { 4, 0, 1 }, { 4, 0, 2 }, { 4, 1, 1 }, { 4, 1, 2 }, { 4, 1, 3 }, { 4, 1, 4 }, { 4, 1, 5 }, { 4, 1, 6 }, { 4, 1, 7 }, { 4, 2, 1 }, { 4, 2, 4 }, { 4, 2, 5 }, { 4, 2, 6 }, { 4, 2, 7 }, { 4, 3, 7 }, { 5, 0, 0 }, { 5, 0, 1 }, { 5, 0, 2 }, { 5, 1, 2 }, { 5, 1, 3 }, { 5, 1, 4 }, { 5, 2, 4 }, { 5, 2, 5 }, { 5, 2, 6 }, { 5, 2, 7 }, { 5, 3, 5 }, { 5, 3, 6 }, { 5, 3, 7 }, { 5, 4, 6 }, { 5, 5, 5 }, { 5, 5, 6 }, { 5, 6, 6 }, { 5, 7, 5 }, { 5, 7, 6 }, { 6, 0, 0 }, { 6, 0, 1 }, { 6, 0, 2 }, { 6, 1, 1 }, { 6, 1, 2 }, { 6, 1, 3 }, { 6, 1, 4 }, { 6, 2, 1 }, { 6, 2, 4 }, { 6, 2, 5 }, { 6, 3, 5 }, { 6, 3, 6 }, { 6, 3, 7 }, { 6, 4, 5 }, { 6, 4, 6 }, { 6, 5, 5 }, { 6, 5, 6 }, { 6, 6, 5 }, { 6, 6, 6 }, { 6, 7, 5 }, { 6, 7, 6 }, { 7, 0, 0 }, { 7, 0, 1 }, { 7, 0, 2 }, { 7, 1, 0 }, { 7, 1, 1 }, { 7, 1, 2 }, { 7, 1, 3 }, { 7, 1, 4 }, { 7, 2, 2 }, { 7, 2, 3 }, { 7, 2, 4 }, { 7, 2, 5 }, { 7, 3, 4 }, { 7, 3, 5 }, { 7, 3, 6 }, { 7, 3, 7 } }; // North-West
    // North-East
    private static final int[][] RAW_IRON_OFFSETS = new int[][] { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 1, 0 }, { 0, 1, 1 }, { 0, 1, 2 }, { 0, 1, 3 }, { 0, 1, 4 }, { 0, 2, 2 }, { 0, 2, 3 }, { 0, 2, 4 }, { 0, 2, 5 }, { 0, 3, 4 }, { 0, 3, 5 }, { 0, 3, 6 }, { 0, 3, 7 }, { 0, 4, 6 }, { 0, 5, 5 }, { 0, 5, 6 }, { 0, 6, 6 }, { 0, 7, 5 }, { 0, 7, 6 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, { 1, 1, 1 }, { 1, 1, 2 }, { 1, 1, 3 }, { 1, 1, 4 }, { 1, 2, 1 }, { 1, 2, 4 }, { 1, 2, 5 }, { 1, 3, 5 }, { 1, 3, 6 }, { 1, 3, 7 }, { 1, 4, 5 }, { 1, 4, 6 }, { 1, 5, 5 }, { 1, 5, 6 }, { 1, 6, 5 }, { 1, 6, 6 }, { 1, 7, 5 }, { 1, 7, 6 }, { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, { 2, 1, 2 }, { 2, 1, 3 }, { 2, 1, 4 }, { 2, 2, 4 }, { 2, 2, 5 }, { 2, 2, 6 }, { 2, 2, 7 }, { 2, 3, 5 }, { 2, 3, 6 }, { 2, 3, 7 }, { 3, 0, 0 }, { 3, 0, 1 }, { 3, 0, 2 }, { 3, 1, 1 }, { 3, 1, 2 }, { 3, 1, 3 }, { 3, 1, 4 }, { 3, 1, 5 }, { 3, 1, 6 }, { 3, 1, 7 }, { 3, 2, 1 }, { 3, 2, 4 }, { 3, 2, 5 }, { 3, 2, 6 }, { 3, 2, 7 }, { 4, 0, 0 }, { 4, 0, 1 }, { 4, 0, 2 }, { 4, 0, 3 }, { 4, 0, 4 }, { 4, 0, 5 }, { 4, 0, 6 }, { 4, 0, 7 }, { 4, 1, 2 }, { 4, 1, 3 }, { 4, 1, 4 }, { 4, 1, 5 }, { 4, 1, 6 }, { 4, 1, 7 }, { 4, 2, 7 }, { 5, 0, 0 }, { 5, 0, 1 }, { 5, 0, 2 }, { 5, 0, 3 }, { 5, 0, 4 }, { 5, 0, 5 }, { 5, 0, 6 }, { 5, 0, 7 }, { 5, 1, 1 }, { 5, 1, 3 }, { 5, 1, 4 }, { 5, 1, 6 }, { 5, 1, 7 }, { 5, 2, 1 }, { 5, 2, 3 }, { 5, 2, 4 }, { 5, 2, 6 }, { 6, 0, 0 }, { 6, 0, 1 }, { 6, 0, 2 }, { 6, 0, 3 }, { 6, 0, 4 }, { 6, 0, 5 }, { 6, 0, 6 }, { 6, 0, 7 }, { 6, 1, 1 }, { 6, 1, 3 }, { 6, 1, 4 }, { 6, 1, 6 }, { 6, 1, 7 }, { 6, 2, 1 }, { 6, 2, 3 }, { 6, 2, 6 }, { 7, 0, 0 }, { 7, 0, 1 }, { 7, 0, 2 }, { 7, 0, 3 }, { 7, 0, 4 }, { 7, 0, 5 }, { 7, 0, 6 }, { 7, 0, 7 } }; // North-East
    // South-East
    private static final int[][] SCUTE_OFFSETS = new int[][] { { 0, 0, 4 }, { 0, 0, 5 }, { 0, 0, 6 }, { 0, 0, 7 }, { 0, 1, 3 }, { 0, 1, 4 }, { 0, 1, 5 }, { 0, 1, 6 }, { 0, 2, 2 }, { 0, 2, 3 }, { 0, 2, 4 }, { 0, 3, 0 }, { 0, 3, 1 }, { 0, 3, 2 }, { 0, 4, 1 }, { 0, 5, 0 }, { 0, 5, 1 }, { 0, 6, 1 }, { 0, 7, 0 }, { 0, 7, 1 }, { 1, 0, 4 }, { 1, 0, 5 }, { 1, 0, 6 }, { 1, 0, 7 }, { 1, 1, 3 }, { 1, 1, 4 }, { 1, 1, 5 }, { 1, 1, 6 }, { 1, 2, 2 }, { 1, 2, 3 }, { 1, 2, 5 }, { 1, 2, 6 }, { 1, 3, 0 }, { 1, 3, 1 }, { 1, 3, 2 }, { 1, 4, 0 }, { 1, 4, 1 }, { 1, 5, 0 }, { 1, 5, 1 }, { 1, 6, 0 }, { 1, 6, 1 }, { 1, 7, 0 }, { 1, 7, 1 }, { 2, 0, 4 }, { 2, 0, 5 }, { 2, 0, 6 }, { 2, 0, 7 }, { 2, 1, 3 }, { 2, 1, 4 }, { 2, 2, 0 }, { 2, 2, 1 }, { 2, 2, 2 }, { 2, 2, 3 }, { 2, 3, 0 }, { 2, 3, 1 }, { 2, 3, 2 }, { 3, 0, 4 }, { 3, 0, 5 }, { 3, 0, 6 }, { 3, 0, 7 }, { 3, 1, 0 }, { 3, 1, 1 }, { 3, 1, 2 }, { 3, 1, 3 }, { 3, 1, 4 }, { 3, 1, 5 }, { 3, 1, 6 }, { 3, 2, 0 }, { 3, 2, 1 }, { 3, 2, 2 }, { 3, 2, 3 }, { 3, 2, 5 }, { 3, 2, 6 }, { 4, 0, 0 }, { 4, 0, 1 }, { 4, 0, 2 }, { 4, 0, 3 }, { 4, 0, 4 }, { 4, 0, 5 }, { 4, 0, 6 }, { 4, 0, 7 }, { 4, 1, 0 }, { 4, 1, 1 }, { 4, 1, 2 }, { 4, 1, 3 }, { 4, 1, 4 }, { 4, 2, 0 }, { 5, 0, 0 }, { 5, 0, 1 }, { 5, 0, 2 }, { 5, 0, 3 }, { 5, 0, 4 }, { 5, 0, 5 }, { 5, 0, 6 }, { 5, 0, 7 }, { 5, 1, 0 }, { 5, 1, 1 }, { 5, 1, 3 }, { 5, 1, 5 }, { 5, 1, 6 }, { 5, 2, 1 }, { 5, 2, 3 }, { 5, 2, 5 }, { 5, 2, 6 }, { 6, 0, 0 }, { 6, 0, 1 }, { 6, 0, 2 }, { 6, 0, 3 }, { 6, 0, 4 }, { 6, 0, 5 }, { 6, 0, 6 }, { 6, 0, 7 }, { 6, 1, 0 }, { 6, 1, 1 }, { 6, 1, 3 }, { 6, 1, 5 }, { 6, 1, 6 }, { 6, 2, 1 }, { 6, 2, 3 }, { 6, 2, 5 }, { 7, 0, 0 }, { 7, 0, 1 }, { 7, 0, 2 }, { 7, 0, 3 }, { 7, 0, 4 }, { 7, 0, 5 }, { 7, 0, 6 }, { 7, 0, 7 } }; // South-East
    // South-West
    private static final int[][] STRING_OFFSETS = new int[][] { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 }, { 0, 0, 4 }, { 0, 0, 5 }, { 0, 0, 6 }, { 0, 0, 7 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, { 1, 0, 3 }, { 1, 0, 4 }, { 1, 0, 5 }, { 1, 0, 6 }, { 1, 0, 7 }, { 1, 1, 0 }, { 1, 1, 1 }, { 1, 1, 3 }, { 1, 1, 5 }, { 1, 1, 6 }, { 1, 2, 1 }, { 1, 2, 3 }, { 1, 2, 5 }, { 1, 2, 6 }, { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, { 2, 0, 3 }, { 2, 0, 4 }, { 2, 0, 5 }, { 2, 0, 6 }, { 2, 0, 7 }, { 2, 1, 0 }, { 2, 1, 1 }, { 2, 1, 2 }, { 2, 1, 3 }, { 2, 1, 4 }, { 2, 2, 0 }, { 3, 0, 4 }, { 3, 0, 5 }, { 3, 0, 6 }, { 3, 0, 7 }, { 3, 1, 0 }, { 3, 1, 1 }, { 3, 1, 2 }, { 3, 1, 3 }, { 3, 1, 4 }, { 3, 1, 5 }, { 3, 1, 6 }, { 3, 2, 0 }, { 3, 2, 5 }, { 3, 2, 6 }, { 4, 0, 4 }, { 4, 0, 5 }, { 4, 0, 6 }, { 4, 0, 7 }, { 4, 1, 0 }, { 4, 1, 1 }, { 4, 1, 2 }, { 4, 1, 3 }, { 4, 1, 4 }, { 4, 1, 5 }, { 4, 1, 6 }, { 4, 2, 0 }, { 4, 2, 1 }, { 4, 2, 2 }, { 4, 2, 3 }, { 4, 2, 5 }, { 4, 3, 0 }, { 5, 0, 4 }, { 5, 0, 5 }, { 5, 0, 6 }, { 5, 0, 7 }, { 5, 1, 3 }, { 5, 1, 4 }, { 5, 2, 0 }, { 5, 2, 1 }, { 5, 2, 2 }, { 5, 2, 3 }, { 5, 3, 0 }, { 5, 3, 1 }, { 5, 3, 2 }, { 5, 4, 1 }, { 5, 5, 0 }, { 5, 5, 1 }, { 5, 6, 1 }, { 5, 7, 0 }, { 5, 7, 1 }, { 6, 0, 4 }, { 6, 0, 5 }, { 6, 0, 6 }, { 6, 0, 7 }, { 6, 1, 3 }, { 6, 1, 4 }, { 6, 1, 5 }, { 6, 1, 6 }, { 6, 2, 2 }, { 6, 2, 3 }, { 6, 2, 5 }, { 6, 2, 6 }, { 6, 3, 0 }, { 6, 3, 1 }, { 6, 3, 2 }, { 6, 4, 0 }, { 6, 4, 1 }, { 6, 5, 0 }, { 6, 5, 1 }, { 6, 6, 0 }, { 6, 6, 1 }, { 6, 7, 0 }, { 6, 7, 1 }, { 7, 0, 4 }, { 7, 0, 5 }, { 7, 0, 6 }, { 7, 0, 7 }, { 7, 1, 3 }, { 7, 1, 4 }, { 7, 1, 5 }, { 7, 1, 6 }, { 7, 2, 2 }, { 7, 2, 3 }, { 7, 2, 4 }, { 7, 3, 0 }, { 7, 3, 1 }, { 7, 3, 2 } }; // South-West

    public static void register() {
        new CommandAPICommand("create_cauldron")
                .withPermission("cobalt.test")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes((sender, args) -> {
                    createCauldron(sender, args);
                })
                .register();
    }

    private static void createCauldron(CommandSender sender, Object[] args) {

        Location location = (Location) args[0];
        createCauldron(location);
    }

    public static List<Entity> createCauldron(Location location) {
        List<Entity> entities = new ArrayList<>();

        entities.addAll(createCorner(location, Material.AMETHYST_SHARD, 48, 0, 48, AMETHYST_OFFSETS)); // South-West
        entities.addAll(createCorner(location, Material.RAW_IRON, 0, 0, 48, RAW_IRON_OFFSETS)); // North-East
        entities.addAll(createCorner(location, Material.SCUTE, 0, 0, 0, SCUTE_OFFSETS)); // South-East
        entities.addAll(createCorner(location, Material.STRING, 48, 0, 0, STRING_OFFSETS)); // South-West

        return entities;
    }

    private static List<Entity> createCorner(Location location, Material material, int xOffset, int yOffset, int zOffset, int[][] offsets) {
        List<Entity> entities = new ArrayList<>();

        for (int[] offset : offsets) {
            int x = offset[0];
            int y = offset[1];
            int z = offset[2];

            String modelData = "10" + x + y + z;

            entities.add(location.getWorld().spawn(location.clone().add(xOffset + -x * GAP - 4, yOffset + y * GAP - 18, zOffset + -z * GAP - 4), ArmorStand.class, armorStand -> {

                armorStand.setGravity(false);
                armorStand.setMarker(true);
                armorStand.setInvisible(true);

                // Set item on head
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(Integer.parseInt(modelData));
                item.setItemMeta(meta);

                armorStand.getEquipment().setItem(EquipmentSlot.HEAD, item);
            }));
        }

        return entities;
    }

}
