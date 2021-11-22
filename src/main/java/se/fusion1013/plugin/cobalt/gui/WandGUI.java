package se.fusion1013.plugin.cobalt.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandGUI extends AbstractGUI {

    static int realInvSize;

    public WandGUI(int invSize, String invName) {
        super(getInventorySize(invSize), invName);
        realInvSize = getInventorySize(invSize);

        setInaccessibleSlots(realInvSize-invSize);
    }

    private void setInaccessibleSlots(int count){
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("--");
        stack.setItemMeta(meta);

        for (int i = 1; i <= count; i++){
            setItem(realInvSize - i, stack);
        }
    }

    private static int getInventorySize(double n){
        // x = 9
        if (9 > n) return 9;

        n = n + 4.5;
        n = n - (n%9);
        return (int)n;
    }
}
