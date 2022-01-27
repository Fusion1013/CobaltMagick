package se.fusion1013.plugin.cobaltmagick.wand;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.gui.WandGUI;
import se.fusion1013.plugin.cobaltmagick.manager.ConfigManager;
import se.fusion1013.plugin.cobaltmagick.manager.WorldGuardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WandEvents implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Wand wand = Wand.getWand(item);
        if (wand != null) wand.forceRecharge();
    }

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
        }.runTaskTimer(CobaltMagick.getInstance(), 0, 1);
    }

    List<UUID> uuidList = new ArrayList<>();

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if (event.getSlot() == -999) {
            uuidList.add(event.getWhoClicked().getUniqueId());
            CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> uuidList.remove(event.getWhoClicked().getUniqueId()), 1);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        Player p = event.getPlayer();

        ItemStack is = event.getItemDrop().getItemStack();
        if (is.getType() == Material.AIR) return;

        Wand wand = Wand.getWand(is);
        if (wand == null || uuidList.contains(p.getUniqueId())) return;

        Block b;
        if (p.getGameMode() == GameMode.CREATIVE) {
            b = p.getTargetBlockExact(5);
        } else {
            b = p.getTargetBlockExact(4);
        }

        if (b == null) uuidList.add(p.getUniqueId());
        openWandInventory(wand, p);
        event.setCancelled(true);
    }

    /**
     * Handles casting of a wand
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        ItemStack is = p.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR || event.getAction() == Action.PHYSICAL) return;

        Wand wand = Wand.getWand(is);
        if (wand == null) return;
        if (uuidList.contains(p.getUniqueId())) {
            uuidList.remove(p.getUniqueId());
            return;
        }

        castSpells(wand, p, event.getAction());
    }

    private void castSpells(Wand wand, Player p, Action action) {

        if (!allowCast(p)) {
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1); // TODO: Replace with something else (Soundmanager ???)
            return;
        }

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
        }.runTaskTimer(CobaltMagick.getInstance(), 0, 1);
    }

    private boolean allowCast(Player p){
        if (!WorldGuardManager.getInstance().isCastingAllowed(p, p.getLocation())){
            return false;
        }
        return true;
    }

    /**
     * Opens a new wand inventory for the given player with the given wand capacity and spells
     *
     * @param wand wand to open the inventory of
     * @param p player to open the inventory for
     */
    private void openWandInventory(Wand wand, Player p) {
        if (!allowWandEdit(p)) {
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1); // TODO: Replace with something else (Soundmanager ???)
            return;
        }

        WandGUI gui = new WandGUI(wand, "Wand");
        gui.open(p);
    }

    private boolean allowWandEdit(Player p){
        if (!WorldGuardManager.getInstance().isWandEditingAllowed(p, p.getLocation())) return false;
        if (ConfigManager.getInstance().getCustomConfig().getBoolean("disable-wand-editing")) return false;

        return true;
    }
}
