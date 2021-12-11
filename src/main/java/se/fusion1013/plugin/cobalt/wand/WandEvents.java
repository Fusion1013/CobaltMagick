package se.fusion1013.plugin.cobalt.wand;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.gui.AbstractGUI;
import se.fusion1013.plugin.cobalt.gui.WandGUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WandEvents implements Listener {

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event){
        Item t = event.getEntity();
        Wand wand = Wand.getWand(t.getItemStack());
        if (wand == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!t.isValid()) cancel();
                for (Player p : Bukkit.getOnlinePlayers()){
                    p.spawnParticle(Particle.DUST_COLOR_TRANSITION, t.getLocation().clone().add(new Vector(0, .5, 0)), 1, .1, .3, .1, .5, new Particle.DustTransition(Color.YELLOW, Color.WHITE, 1));
                }
            }
        }.runTaskTimer(Cobalt.getInstance(), 0, 1);
    }

    List<UUID> uuidList = new ArrayList<>();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if (event.getSlot() == -999) {
            uuidList.add(event.getWhoClicked().getUniqueId());
            Cobalt.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Cobalt.getInstance(), new Runnable() {
                public void run() {
                    uuidList.remove(event.getWhoClicked().getUniqueId());
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        Player p = event.getPlayer();

        ItemStack is = event.getItemDrop().getItemStack();
        if (is.getType() == Material.AIR) return;

        Wand wand = Wand.getWand(is);
        if (wand == null || uuidList.contains(p.getUniqueId())) return;

        uuidList.add(p.getUniqueId());
        openWandInventory(wand, p);
        event.setCancelled(true);

        Cobalt.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Cobalt.getInstance(), new Runnable() {
            public void run() {
                uuidList.remove(event.getPlayer().getUniqueId());
            }
        }, 1);
    }

    /**
     * Handles casting of a wand
     * @param event
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        ItemStack is = p.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR) return;

        Wand wand = Wand.getWand(is);
        if (wand == null) return;
        if (uuidList.contains(p.getUniqueId())) return;

        castSpells(wand, p, event.getAction());
    }

    private void castSpells(Wand wand, Player p, Action action) {

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
            wand.castSpells(p);
            return;
        }

        new BukkitRunnable(){
            int timer = 4;

            @Override
            public void run() {
                timer--;
                wand.castSpells(p);
                if (timer == 0) cancel();
            }
        }.runTaskTimer(Cobalt.getInstance(), 0, 1);
    }

    /**
     * Opens a new wand inventory for the given player with the given wand capacity and spells
     *
     * @param wand wand to open the inventory of
     * @param p player to open the inventory for
     */
    private void openWandInventory(Wand wand, Player p) {
        WandGUI gui = new WandGUI(wand, "Wand");
        gui.open(p);
    }
}
