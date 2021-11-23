package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;

@CommandDeclaration(
        commandName = "wand",
        permission = "cobalt.cgive.wand",
        usage = "/cgive wand",
        description = "Gives a new wand",
        validSenders = SenderType.PLAYER,
        parentCommandName = "cgive"
)
public class CGiveWandCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        Wand wand = new Wand(false, 2, 0.1, 0.4, 200, 20, 4, 5.0, new ArrayList<>(), 1);
        int id = Cobalt.getInstance().getRDatabase().insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand);
        ItemStack is = wand.getWandItem();
        ((Player)sender).getInventory().addItem(is);
        return true;
    }
}
