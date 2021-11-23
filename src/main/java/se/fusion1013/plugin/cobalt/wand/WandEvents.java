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

public class WandEvents implements Listener {

    /**
     * Handles opening the spell inventory / casting of a wand
     * @param event
     */
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        ItemStack is = p.getInventory().getItemInMainHand();
        if (is == null || is.getType() == Material.AIR) return;

        Wand wand = Wand.getWand(is);
        if (wand == null) return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            castSpells(wand, p);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            openWandInventory(wand, p);
        }
    }

    private void castSpells(Wand wand, Player p) {
        wand.castSpells(p);
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
