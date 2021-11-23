package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobalt.spells.Spell;

@CommandDeclaration(
        commandName = "spell",
        permission = "cobalt.cgive.spell",
        usage = "/cgive spell <spell>",
        description = "Gives the specified spell",
        validSenders = SenderType.PLAYER,
        minArgs = 1,
        maxArgs = 1,
        parentCommandName = "cgive"
)
public class CGiveSpellCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        Player player = (Player)sender;
        String spellName = args[0];
        ItemStack is = Spell.getSpellItem(spellName);
        player.getInventory().addItem(is);
        return true;
    }
}
