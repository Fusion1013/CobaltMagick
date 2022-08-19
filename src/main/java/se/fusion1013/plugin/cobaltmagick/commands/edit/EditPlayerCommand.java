package se.fusion1013.plugin.cobaltmagick.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;

public class EditPlayerCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("player")
                .withPermission("cobalt.magick.commands.cedit.player")
                .withSubcommand(createDreamCommand());
    }

    // ----- ENTER DREAM COMMAND -----

    private static CommandAPICommand createDreamCommand() {
        return new CommandAPICommand("dream")
                .withPermission("cobalt.magick.commands.cedit.player.dream")
                .withArguments(new PlayerArgument("player"))
                .executes(EditPlayerCommand::executeDream);
    }

    private static void executeDream(CommandSender sender, Object[] args) {
        Player player = (Player) args[0];

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            ICustomItem customItem = CustomItemManager.getCustomItem(item);

            if (item == null) continue;

            boolean drop = true;

            if (customItem != null) {
                if (customItem.getTags() != null) {
                    for (String tag : customItem.getTags()) {
                        if (tag.equalsIgnoreCase("dream_item")) {
                            drop = false;
                            break;
                        }
                    }
                }
            }

            if (drop) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

}
