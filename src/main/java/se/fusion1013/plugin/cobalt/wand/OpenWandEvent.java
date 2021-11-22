package se.fusion1013.plugin.cobalt.wand;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;

public class OpenWandEvent implements Listener {
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        ItemStack is = p.getInventory().getItemInMainHand();
        ItemMeta meta = is.getItemMeta();

        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");

        if (meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER)){
            Cobalt.getInstance().getLogger().info("Open Wand GUI Event");
        } else {
            Cobalt.getInstance().getLogger().info("Not a wand!");
        }
    }
}
