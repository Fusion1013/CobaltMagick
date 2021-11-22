package se.fusion1013.plugin.cobalt.wand;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.gui.AbstractGUI;
import se.fusion1013.plugin.cobalt.gui.WandGUI;

public class OpenWandEvent implements Listener {
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        ItemStack is = p.getInventory().getItemInMainHand();
        if (is == null || is.getType() == Material.AIR) return;

        ItemMeta meta = is.getItemMeta();

        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");

        if (meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER)){
            int wandId = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
            Wand wand = Cobalt.getInstance().getRDatabase().getWandByID(wandId);

            WandGUI gui = new WandGUI(wand.getCapacity(), "Wand");
            gui.open(p);

        }
    }
}
