package se.fusion1013.cobaltmagick.wand;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WandEvents implements Listener {

    private static final List<UUID> uuidList = new ArrayList<>();

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getOpenInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getType() != InventoryType.CREATIVE)
            return;

        ItemStack is = player.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR || event.getAction() == Action.PHYSICAL) {
        }

        // TODO
//        Wand wand = Wand.getWand(is);
//        if (wand == null) return;
//        if (uuidList.contains(player.getUniqueId())) {
//            CobaltMagick.getInstance().getLogger().info("!!");
//            uuidList.remove(player.getUniqueId());
//            return;
//        }
//
//        castSpells(wand, player, event.getAction());
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        // Called when player clicks outside inventory
        if (event.getSlot() == -999) {
            uuidList.add(event.getWhoClicked().getUniqueId());
            CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> uuidList.remove(event.getWhoClicked().getUniqueId()), 1);
        }
    }

    @EventHandler
    public void onInventoryEvent(InventoryClickEvent event) {
        uuidList.add(event.getWhoClicked().getUniqueId());
        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> uuidList.remove(event.getWhoClicked().getUniqueId()), 1);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        uuidList.add(player.getUniqueId());
        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> uuidList.remove(player.getUniqueId()), 1);

        if (player.isSneaking()) return;

        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getType() == Material.AIR) return;

        if (true) return;

        openWandInventory(itemStack, player);
        event.setCancelled(true);
    }

    private void openWandInventory(ItemStack wandItem, Player p) {
        if (false) { // TODO
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1); // TODO: Replace with something else (Soundmanager ???)
            return;
        }

        WandGUI gui = new WandGUI(wandItem, "Wand");
        gui.open(p);
    }


}
