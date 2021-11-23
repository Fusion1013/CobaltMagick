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
import se.fusion1013.plugin.cobalt.spells.Spell;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.lang.reflect.Method;
import java.util.ArrayList;

@CommandDeclaration(
        commandName = "cgive",
        permission = "cobalt.cgive",
        usage = "/cgive",
        description = "Use to give yourself custom Cobalt items",
        validSenders = SenderType.PLAYER
)
public class CGiveCommand extends MainCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        return true;
    }
}
