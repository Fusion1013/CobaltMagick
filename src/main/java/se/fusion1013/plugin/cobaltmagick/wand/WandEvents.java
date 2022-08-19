package se.fusion1013.plugin.cobaltmagick.wand;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.gui.WandGUI;
import se.fusion1013.plugin.cobaltmagick.manager.MagickConfigManager;
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
    public void onPlayerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();

        uuidList.add(player.getUniqueId());
        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> uuidList.remove(player.getUniqueId()), 1);

        if (player.isSneaking()) return;

        ItemStack is = event.getItemDrop().getItemStack();
        if (is.getType() == Material.AIR) return;

        Wand wand = Wand.getWand(is);
        if (wand == null) return;

        openWandInventory(wand, player);
        event.setCancelled(true);
    }

    /**
     * Handles casting of a wand
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (player.getOpenInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getType() != InventoryType.CREATIVE) return;

        ItemStack is = player.getInventory().getItemInMainHand();
        if (is.getType() == Material.AIR || event.getAction() == Action.PHYSICAL) return;

        Wand wand = Wand.getWand(is);
        if (wand == null) return;
        if (uuidList.contains(player.getUniqueId())) {
            CobaltMagick.getInstance().getLogger().info("!!");
            uuidList.remove(player.getUniqueId());
            return;
        }

        castSpells(wand, player, event.getAction());
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
        if ((boolean) ConfigManager.getInstance().getFromConfig(CobaltMagick.getInstance(), "magick.yml", "disable-wand-editing")) return false;

        return true;
    }
}
