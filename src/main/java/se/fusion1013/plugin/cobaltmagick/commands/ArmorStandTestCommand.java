package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorStandTestCommand {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 4;
    private static final int DEPTH = 7;

    private static final int GAP = 6;

    private static final int[][] AMETHYST_OFFSETS = new int[][] { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 }, { 0, 0, 3 }, { 0, 0, 4 }, { 0, 0, 5 }, { 0, 0, 6 }, { 0, 0, 7 }, { 0, 1, 7 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 2 }, { 1, 0, 3 }, { 1, 0, 4 }, { 1, 0, 5 }, { 1, 0, 6 }, { 1, 0, 7 }, { 1, 1, 1 }, { 1, 1, 3 }, { 1, 1, 4 }, { 1, 1, 6 }, { 1, 1, 7 }, { 1, 2, 1 }, { 1, 2, 3 }, { 1, 2, 4 }, { 1, 2, 6 }, { 2, 0, 0 }, { 2, 0, 1 }, { 2, 0, 2 }, { 2, 0, 3 }, { 2, 0, 4 }, { 2, 0, 5 }, { 2, 0, 6 }, { 2, 0, 7 }, { 2, 1, 2 }, { 2, 1, 3 }, { 2, 1, 4 }, { 2, 1, 5 }, { 2, 1, 6 }, { 2, 1, 7 }, { 2, 2, 7 }, { 3, 0, 0 }, { 3, 0, 1 }, { 3, 0, 2 }, { 3, 1, 1 }, { 3, 1, 2 }, { 3, 1, 3 }, { 3, 1, 4 }, { 3, 1, 5 }, { 3, 1, 6 }, { 3, 1, 7 }, { 3, 2, 1 }, { 3, 2, 7 }, { 4, 0, 0 }, { 4, 0, 1 }, { 4, 0, 2 }, { 4, 1, 1 }, { 4, 1, 2 }, { 4, 1, 3 }, { 4, 1, 4 }, { 4, 1, 5 }, { 4, 1, 6 }, { 4, 1, 7 }, { 4, 2, 1 }, { 4, 2, 4 }, { 4, 2, 5 }, { 4, 2, 6 }, { 4, 2, 7 }, { 4, 3, 7 }, { 5, 0, 0 }, { 5, 0, 1 }, { 5, 0, 2 }, { 5, 1, 2 }, { 5, 1, 3 }, { 5, 1, 4 }, { 5, 2, 4 }, { 5, 2, 5 }, { 5, 2, 6 }, { 5, 2, 7 }, { 5, 3, 5 }, { 5, 3, 6 }, { 5, 3, 7 }, { 5, 4, 6 }, { 5, 5, 5 }, { 5, 5, 6 }, { 5, 6, 6 }, { 5, 7, 5 }, { 5, 7, 6 }, { 6, 0, 0 }, { 6, 0, 1 }, { 6, 0, 2 }, { 6, 1, 1 }, { 6, 1, 2 }, { 6, 1, 3 }, { 6, 1, 4 }, { 6, 2, 1 }, { 6, 2, 4 }, { 6, 2, 5 }, { 6, 3, 5 }, { 6, 3, 6 }, { 6, 3, 7 }, { 6, 4, 5 }, { 6, 4, 6 }, { 6, 5, 5 }, { 6, 5, 6 }, { 6, 6, 5 }, { 6, 6, 6 }, { 6, 7, 5 }, { 6, 7, 6 }, { 7, 0, 0 }, { 7, 0, 1 }, { 7, 0, 2 }, { 7, 1, 0 }, { 7, 1, 1 }, { 7, 1, 2 }, { 7, 1, 3 }, { 7, 1, 4 }, { 7, 2, 2 }, { 7, 2, 3 }, { 7, 2, 4 }, { 7, 2, 5 }, { 7, 3, 4 }, { 7, 3, 5 }, { 7, 3, 6 }, { 7, 3, 7 } };

    public static void register() {
        new CommandAPICommand("create_cauldron")
                .withPermission("cobalt.test")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .executes(ArmorStandTestCommand::createCauldron)
                .register();
    }

    private static void createCauldron(CommandSender sender, Object[] args) {

        Location location = (Location) args[0];
        World world = location.getWorld();

        for (int[] offset : AMETHYST_OFFSETS) {
            int x = offset[0];
            int y = offset[1];
            int z = offset[2];

            String modelData = "10" + x + y + z;

            world.spawn(location.clone().add(-x * GAP, y * GAP, -z * GAP), ArmorStand.class, armorStand -> {

                armorStand.setGravity(false);
                armorStand.setMarker(true);
                armorStand.setInvisible(true);

                // Set item on head
                ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
                ItemMeta meta = item.getItemMeta();
                meta.setCustomModelData(Integer.parseInt(modelData));
                item.setItemMeta(meta);

                armorStand.getEquipment().setItem(EquipmentSlot.HEAD, item);
            });
        }
    }

}
