package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.ISpellTemplate;
import se.fusion1013.cobaltmagick.spell.SpellManager;

public class SpellCommand {

    public static void register() {
        new CommandAPICommand("spell")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "spell"))
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(SpellManager.getSpellNames())))
                .executesPlayer(SpellCommand::giveSpell)
                .register();
    }

    private static void giveSpell(Player player, CommandArguments commandArguments) {
        String spellId = (String) commandArguments.get("id");
        ISpellTemplate template = SpellManager.getSpellTemplate(spellId);
        ItemStack itemStack = template.getItemStack();
        player.getInventory().addItem(itemStack);
    }

}
