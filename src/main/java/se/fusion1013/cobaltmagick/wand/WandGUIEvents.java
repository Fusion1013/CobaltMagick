package se.fusion1013.cobaltmagick.wand;

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
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.gui.AbstractGUI;
import se.fusion1013.cobaltmagick.spell.ISpellInstance;
import se.fusion1013.cobaltmagick.spell.ISpellTemplate;
import se.fusion1013.cobaltmagick.spell.SpellInstance;
import se.fusion1013.cobaltmagick.spell.SpellManager;

import java.util.UUID;

import static se.fusion1013.cobaltmagick.spell.SpellManager.SPELL_ID_KEY;

public class WandGUIEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        UUID playerUUID = player.getUniqueId();

        UUID inventoryUUID = AbstractGUI.openInventories.get(playerUUID);

        ItemStack clickedStack = e.getCurrentItem();
        ItemStack heldStack = e.getWhoClicked().getItemOnCursor();
        boolean validClick;

        if (clickedStack != null) {
            ItemMeta meta = clickedStack.getItemMeta();
            if (meta != null) {
                validClick = meta.getPersistentDataContainer().has(SPELL_ID_KEY);
            } else {
                validClick = false;
            }
        } else {
            validClick = true;
        }

        if (inventoryUUID != null && !validClick) {
            e.setCancelled(true);
            AbstractGUI gui = AbstractGUI.getInventoriesByUUID().get(inventoryUUID);
            AbstractGUI.GUIAction action = gui.getActions().get(e.getSlot());

            if (action != null) {
                action.click(player);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        HumanEntity player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        AbstractGUI gui = AbstractGUI.inventoriesByUUID.get(AbstractGUI.openInventories.get(playerUUID));
        if (gui instanceof WandGUI) updateWandSpells((Player) player, (WandGUI) gui);

        AbstractGUI.openInventories.remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        AbstractGUI gui = AbstractGUI.inventoriesByUUID.get(AbstractGUI.openInventories.get(playerUUID));
        if (gui instanceof WandGUI) updateWandSpells(player, (WandGUI) gui);

        AbstractGUI.openInventories.remove(playerUUID);
    }

    private void updateWandSpells(Player player, WandGUI gui) {
        ItemStack wandItem = gui.wand;
        Inventory inventory = gui.getGuiInventory();
        ISpellInstance[] spells = new ISpellInstance[gui.getGuiInventory().getSize()];

        CobaltMagick.getInstance().getLogger().info("Updating " + inventory.getSize() + " wand spells");

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null) {
                ISpellTemplate spell = SpellManager.getSpellTemplate(stack);
                CobaltMagick.getInstance().getLogger().info("Spell " + spell + " in slot " + i);
                if (spell != null) {
                    spells[i] = new SpellInstance(spell, "deck", i);
                }
            }
        }

        // Set the spells
        WandUtil.setSpells(wandItem, spells, RuleLogger.create("WandGUI::updateWandSpells"));

        // Set the new lore of the wand
        ItemMeta wandMeta = wandItem.getItemMeta();
        if (!wandMeta.getPersistentDataContainer().has(WandUtil.WandKey))
            return; // Prevent other items from getting wand lore
        wandMeta.lore(WandUtil.getLore(wandItem));
        player.getInventory().getItemInMainHand().setItemMeta(wandMeta);
    }
}
