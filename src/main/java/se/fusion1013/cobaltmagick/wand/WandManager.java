package se.fusion1013.cobaltmagick.wand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class WandManager extends Manager<CobaltMagick> {

    public WandManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(new WandEvents(), CobaltMagick.getInstance());
        Bukkit.getPluginManager().registerEvents(new WandGUIEvents(), CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), WandManager::displayWandStats, 0, 2);
    }

    private static void displayWandStats(BukkitTask object) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is.getType() == Material.AIR) return;

            WandState wandState = WandState.getWand(is);
            if (wandState == null) return;

            String rechargeTimeText = wandState.isOnRecharge() ? String.format("%.1f", wandState.getRechargeTime() / 1000f) + "s" : "0s";
            String castDelayTimeText = wandState.isOnCastDelay() ? String.format("%.1f", wandState.getCastDelayTime() / 1000f) + "s" : "0s";

            player.sendActionBar(HexUtils.colorify("&3" + wandState.getCurrentMana() + "&7 / &3" + wandState.getStats().getManaMax() + "&7 | | &7Cast Delay: &3" + castDelayTimeText + "&7 | | Recharge: &3" + rechargeTimeText));
        }
    }

    @Override
    public void disable() {

    }

    private static WandManager INSTANCE;

    public static WandManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WandManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
