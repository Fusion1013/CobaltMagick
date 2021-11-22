package se.fusion1013.plugin.cobalt.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;

@CommandDeclaration(
        commandName = "cgive",
        permission = "cobalt.cgive",
        usage = "/cgive",
        description = "Use to give yourself custom Cobalt items",
        validSenders = SenderType.PLAYER
)
public class CGiveCommand extends MainCommand {

    // TODO: This should work for multiple items, it should not just give a wand
    // (This is all temporary code used for testing, that's why it's shit)

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        Wand wand = new Wand(false, 2, 0.1, 0.4, 200, 20, 4, 5.0, new ArrayList<>(), 1);
        Cobalt.getInstance().getRDatabase().insertWand(wand);

        ItemStack is = new ItemStack(Material.STICK, 1);
        ItemMeta meta = is.getItemMeta();

        // Set Persistent Data
        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);

        // TODO: Move item generation to the wand class

        // Set Wand Lore & Name
        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Wand");
        meta.setLore(wand.getLore());

        is.setItemMeta(meta);

        Player player = (Player)sender;
        player.getInventory().addItem(is);
        return true;
    }
}
