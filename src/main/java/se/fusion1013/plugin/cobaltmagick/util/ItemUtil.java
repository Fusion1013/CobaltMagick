package se.fusion1013.plugin.cobaltmagick.util;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * Contains various methods for handling items.
 */
public class ItemUtil {

    /**
     * Gives a number of chests filled with the provided items to the player.
     * The number of chests given depends on the number of items.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the chest with.
     * @param name the name of the chest. Will ignore if set to null.
     */
    public static void giveChest(Player p, ItemStack[] items, String name) {
        ItemStack chestItem = new ItemStack(Material.CHEST, 1);
        BlockStateMeta bsm = (BlockStateMeta)chestItem.getItemMeta();
        if (bsm == null) return; // This should never happen

        ItemStack[] truncatedItems = new ItemStack[27];
        for (int i = 0; i < items.length; i++) {
            if (i % 27 == 0) {
                // If the list of items is full, give a box to the player and reset the list.
                giveBox(p, truncatedItems, chestItem, (Chest)bsm.getBlockState(), name);

                chestItem = new ItemStack(Material.CHEST, 1);
                bsm = (BlockStateMeta)chestItem.getItemMeta();
                if (bsm == null) return; // This should never happen
                truncatedItems = new ItemStack[27];
            }
            truncatedItems[i % 27] = items[i];
        }

        giveBox(p, truncatedItems, chestItem, (Chest)bsm.getBlockState(), name);
    }

    /**
     * Gives a number of shulker boxes filled with the provided items to the player.
     * The number of shulker boxes given depends on the number of items.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the box with.
     * @param name the name of the shulker box. Will ignore if set to null.
     */
    public static void giveShulkerBox(Player p, ItemStack[] items, String name) {
        giveShulkerBox(p, items, Material.SHULKER_BOX, name);
    }

    /**
     * Gives a number of shulker boxes filled with the provided items to the player.
     *
     * @param p the player to give the box to.
     * @param items the items to populate the box with.
     * @param shulkerMaterial the material of the shulker box.
     * @param name the name of the shulker box. Will ignore if set to null.
     */
    public static void giveShulkerBox(Player p, ItemStack[] items, Material shulkerMaterial, String name) {

        ItemStack shulkerItem = new ItemStack(shulkerMaterial, 1);
        BlockStateMeta bsm = (BlockStateMeta)shulkerItem.getItemMeta();
        if (bsm == null) return; // This should never happen

        ItemStack[] truncatedItems = new ItemStack[27];
        for (int i = 0; i < items.length; i++) {
            if (i % 28 == 27) {
                // If the list of items is full, give a box to the player and reset the list.
                giveBox(p, truncatedItems, shulkerItem, (ShulkerBox)bsm.getBlockState(), name);

                shulkerItem = new ItemStack(shulkerMaterial, 1);
                bsm = (BlockStateMeta)shulkerItem.getItemMeta();
                if (bsm == null) return; // This should never happen
                truncatedItems = new ItemStack[27];
            }
            truncatedItems[i % 27] = items[i];
        }

        giveBox(p, truncatedItems, shulkerItem, (ShulkerBox)bsm.getBlockState(), name);
    }

    /**
     * Gives a box filled with the provided items to the player.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the box with.
     * @param box the box <code>ItemStack</code> to give to the player.
     * @param container the container to populate with the items.
     * @param name the name of the box. Will ignore if set to null.
     * @param <T> the type of the container.
     * @return the filled container.
     */
    private static <T extends Container> T giveBox(Player p, ItemStack[] items, ItemStack box, T container, String name) {
        BlockStateMeta blockStateMeta = (BlockStateMeta)box.getItemMeta();
        if (blockStateMeta == null) return null;

        Inventory inventory = container.getInventory();
        insertItems(inventory, items);

        if (name != null) blockStateMeta.setDisplayName(name);
        blockStateMeta.setBlockState(container);
        box.setItemMeta(blockStateMeta);

        p.getInventory().addItem(box);

        return container;
    }

    /**
     * Inserts an array of items into an inventory.
     *
     * @param inventory the inventory to insert the items into.
     * @param items the items to insert into the inventory.
     * @return the inventory.
     */
    public static Inventory insertItems(Inventory inventory, ItemStack[] items) {
        for (ItemStack stack : items) {
            if (stack != null) inventory.addItem(stack);
        }
        return inventory;
    }
}
