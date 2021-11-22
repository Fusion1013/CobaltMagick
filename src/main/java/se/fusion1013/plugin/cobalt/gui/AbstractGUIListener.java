package se.fusion1013.plugin.cobalt.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        if (inventoryUUID != null){
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

        AbstractGUI.openInventories.remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        AbstractGUI.openInventories.remove(playerUUID);
    }
}