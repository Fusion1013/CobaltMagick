package se.fusion1013.cobaltmagick.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class ContainerUtil {

    public static int getContentAmount(Location location) {
        Block block = location.getBlock();
        BlockState blockState = block.getState();

        if (!(blockState instanceof InventoryHolder container)) return 0;

        return Arrays.stream(container.getInventory().getContents())
                .filter(Objects::nonNull)
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static void setAllItemsToAmount(Location location, int amount) {
        Block block = location.getBlock();
        BlockState blockState = block.getState();

        if (!(blockState instanceof InventoryHolder container)) return;

        ItemStack[] items = container.getInventory().getContents();
        Arrays.stream(items).forEach(i -> i.setAmount(amount));
    }

}
