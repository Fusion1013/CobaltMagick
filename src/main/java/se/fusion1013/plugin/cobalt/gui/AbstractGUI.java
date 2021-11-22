package se.fusion1013.plugin.cobalt.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractGUI {

    private UUID uuid;
    private Inventory guiInventory;
    private Map<Integer, GUIAction> actions;

    public static Map<UUID, AbstractGUI> inventoriesByUUID = new HashMap<>();
    public static Map<UUID, UUID> openInventories = new HashMap<>();

    public AbstractGUI(int invSize, String invName) {
        uuid = UUID.randomUUID();
        guiInventory = Bukkit.createInventory(null, invSize, invName);
        actions = new HashMap<>();
        inventoriesByUUID.put(getUUID(), this);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void open(Player p) {
        p.openInventory(guiInventory);
        openInventories.put(p.getUniqueId(), getUUID());
    }

    public void setItem(int slot, ItemStack stack, GUIAction action){
        guiInventory.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void delete(){
        for (Player p : Bukkit.getOnlinePlayers()){
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUUID())){
                p.closeInventory();
            }
        }
        inventoriesByUUID.remove(getUUID());
    }

    public void setItem(int slot, ItemStack stack) {
        setItem(slot, stack, null);
    }

    public static Map<UUID, AbstractGUI> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories(){
        return openInventories;
    }

    public Map<Integer, GUIAction> getActions(){
        return actions;
    }

    public Inventory getGuiInventory() {
        return guiInventory;
    }

    public interface GUIAction {
        void click(Player player);
    }
}