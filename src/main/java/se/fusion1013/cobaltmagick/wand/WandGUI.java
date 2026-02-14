package se.fusion1013.cobaltmagick.wand;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltmagick.gui.AbstractGUI;
import se.fusion1013.cobaltmagick.item.properties.ItemWandProperty;

public class WandGUI extends AbstractGUI {

    static int realInvSize;
    public ItemStack wand; // TODO: Add getter

    public WandGUI(ItemStack wandItem, String invName) {
        super(getInventorySize(wandItem), invName);
        int invSize = getCapacity(wandItem);
        realInvSize = getInventorySize(invSize);
        this.wand = wandItem;

        setInaccessibleSlots(realInvSize - invSize);
        addSpells();
    }

    private void addSpells() {
//        List<ISpell> spells = wand.getSpells();
//
//        for (int i = 0; i < spells.size(); i++) {
//            ISpell spell = spells.get(i);
//            if (spell != null) {
//                ItemStack item = spell.getSpellItem();
//                setItem(i, item);
//            }
//        }
    }

    private void setInaccessibleSlots(int count) {
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("--");
        stack.setItemMeta(meta);

        for (int i = 1; i <= count; i++) {
            setItem(realInvSize - i, stack);
        }
    }

    private static int getInventorySize(ItemStack wandItem) {
        return getInventorySize(getCapacity(wandItem));
    }

    private static int getCapacity(ItemStack wandItem) {
        PersistentDataContainerView persistent = wandItem.getPersistentDataContainer();
        if (!persistent.has(ItemWandProperty.CapacityKey, PersistentDataType.INTEGER)) return 0;
        return persistent.get(ItemWandProperty.CapacityKey, PersistentDataType.INTEGER);
    }

    private static int getInventorySize(double n) {
        int size = 0;
        while (size < n) size += 9;
        return size;
    }
}