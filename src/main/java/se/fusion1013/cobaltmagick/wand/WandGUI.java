package se.fusion1013.cobaltmagick.wand;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltmagick.gui.AbstractGUI;
import se.fusion1013.cobaltmagick.spell.ISpellInstance;

import java.util.List;

import static se.fusion1013.cobaltmagick.wand.WandUtil.CapacityKey;

public class WandGUI extends AbstractGUI {

    private static int realInvSize;
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
        List<ISpellInstance> spells = WandUtil.getSpells(wand);

        for (int i = 0; i < spells.size(); i++) {
            ISpellInstance spell = spells.get(i);
            if (spell != null) {
                ItemStack item = spell.getItemStack();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.lore(List.of(Component.text("Spell State: " + spell.state())));
                item.setItemMeta(itemMeta);
                setItem(i, item);
            }
        }
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
        if (!persistent.has(CapacityKey, PersistentDataType.INTEGER)) return 0;
        return persistent.get(CapacityKey, PersistentDataType.INTEGER);
    }

    private static int getInventorySize(double n) {
        int size = 0;
        while (size < n) size += 9;
        return size;
    }
}