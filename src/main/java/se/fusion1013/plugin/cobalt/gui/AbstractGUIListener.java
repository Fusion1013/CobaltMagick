package se.fusion1013.plugin.cobalt.gui;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.spells.ISpell;
import se.fusion1013.plugin.cobalt.spells.Spell;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractGUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)){
            return;
        }

        Player player = (Player) e.getWhoClicked();
        UUID playerUUID = player.getUniqueId();

        UUID inventoryUUID = AbstractGUI.openInventories.get(playerUUID);

        // TODO: You should not be able to stack spells inside a wand inventory
        ItemStack clickedStack = e.getCurrentItem();
        boolean validClick;
        if (clickedStack != null) {
            ItemMeta meta = clickedStack.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "spell");
            validClick = meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER);
        } else {
            validClick = true;
        }

        if (inventoryUUID != null && !validClick){
            e.setCancelled(true);
            AbstractGUI gui = AbstractGUI.getInventoriesByUUID().get(inventoryUUID);
            AbstractGUI.GUIAction action = gui.getActions().get(e.getSlot());

            if (action != null){
                action.click(player);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        HumanEntity player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        AbstractGUI gui = AbstractGUI.inventoriesByUUID.get(AbstractGUI.openInventories.get(playerUUID));
        if (gui instanceof WandGUI) updateWandSpells((WandGUI)gui);

        AbstractGUI.openInventories.remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        AbstractGUI gui = AbstractGUI.inventoriesByUUID.get(AbstractGUI.openInventories.get(playerUUID));
        if (gui instanceof WandGUI) updateWandSpells((WandGUI)gui);

        AbstractGUI.openInventories.remove(playerUUID);
    }

    private void updateWandSpells(WandGUI gui) {
        Wand wand = gui.wand;
        Inventory inventory = gui.getGuiInventory();
        List<ISpell> spells = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null) {
                ISpell spell = Spell.getSpell(stack);
                if (spell != null) spells.add(spell);
            }
        }

        wand.setSpells(spells);

        Cobalt.getInstance().getRDatabase().updateWandSpells(wand);
    }
}