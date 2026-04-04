package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.wand.WandStats;

public class WandCommand {

    public static void register() {
        new CommandAPICommand("wand")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "wand"))
                .withSubcommand(WandCommand.createInfoCommand())
                .register();
    }

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "wand.info"))
                .executesPlayer(WandCommand::displayWandInfo);
    }

    private static void displayWandInfo(Player player, CommandArguments args) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.isEmpty()) return;

        WandStats wandStats = WandStats.create(itemStack);
        if (wandStats == null) return;

        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.wand.info.header");
        sendWandStat(player, "Shuffle", wandStats.getShuffle());
        sendWandStat(player, "Spells Per Cast", wandStats.getSpellsPerCast());
        sendWandStat(player, "Cast Delay", wandStats.getCastDelay());
        sendWandStat(player, "Recharge Time", wandStats.getRechargeTime());
        sendWandStat(player, "Mana Max", wandStats.getManaMax());
        sendWandStat(player, "Mana Charge Speed", wandStats.getManaChargeSpeed());
        sendWandStat(player, "Capacity", wandStats.getCapacity());
        sendWandStat(player, "Spread", wandStats.getSpread());
    }

    private static void sendWandStat(Player player, String title, Object value) {
        LocaleManager.getInstance().sendMessage("", player, "commands.magick.wand.info.item", StringPlaceholders.builder()
                .addPlaceholder("title", title)
                .addPlaceholder("value", value)
                .build());
    }

}
