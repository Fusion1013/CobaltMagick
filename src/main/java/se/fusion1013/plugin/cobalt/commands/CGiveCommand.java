package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;

@CommandDeclaration(
        commandName = "cgive",
        permission = "cobalt.cgive",
        usage = "/cgive",
        description = "Use to give yourself custom Cobalt items",
        validSenders = SenderType.PLAYER
)
public class CGiveCommand extends MainCommand {

    // TODO: This should work for multiple items, it should not just give a wand

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        ItemStack is = new ItemStack(Material.STICK, 1);
        ItemMeta meta = is.getItemMeta();

        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");

        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);
        is.setItemMeta(meta);

        Player player = (Player)sender;
        player.getInventory().addItem(is);
        return true;
    }
}
