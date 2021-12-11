package se.fusion1013.plugin.cobalt.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobalt.spells.ISpell;
import se.fusion1013.plugin.cobalt.spells.Spell;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.List;

public class WandGUI extends AbstractGUI {

    static int realInvSize;
    public Wand wand; // TODO: Add getter

    public WandGUI(Wand wand, String invName) {
        super(getInventorySize(wand.getCapacity()), invName);
        int invSize = wand.getCapacity();
        realInvSize = getInventorySize(invSize);
        this.wand = wand;

        setInaccessibleSlots(realInvSize-invSize);
        addSpells();
    }

    private void addSpells(){
        List<ISpell> spells = wand.getSpells();

        for (int i = 0; i < spells.size(); i++) {
            ISpell spell = spells.get(i);
            if (spell != null){
                ItemStack item = spell.getSpellItem();
                setItem(i, item);
            }
        }
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
