package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.protection.CustomWorldGuardFlags;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class WandManager extends Manager implements Runnable {

    private static WandManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WandManager</code>.
     *
     * @return The object of this class
     */
    public static WandManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WandManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    public WandManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            List<Wand> wandList = findWandsInPlayerInventory(p);
            boolean rechargeAllowed = CustomWorldGuardFlags.isManaRechargeAllowed(p, p.getLocation());

            for (Wand wand : wandList) {
                wand.setRegionAllowsManaRecharge(rechargeAllowed);
            }
        }
    }

    private List<Wand> findWandsInPlayerInventory(Player p) {
        Inventory inventory = p.getInventory();
        List<Wand> wandList = new ArrayList<>();

        for (ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                Wand wand = Wand.getWand(stack);
                if (wand != null) wandList.add(wand);
            }
        }

        return wandList;
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);
    }

    @Override
    public void disable() {

    }
}
