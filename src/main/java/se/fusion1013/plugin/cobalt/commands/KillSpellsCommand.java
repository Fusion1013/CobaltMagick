package se.fusion1013.plugin.cobalt.commands;

import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.manager.SpellManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;

public class KillSpellsCommand {
    public static void register(){
        CommandAPICommand killSpellsCommand = new CommandAPICommand("killspells")
                .withPermission("cobalt.command.killspells")
                .executesPlayer(KillSpellsCommand::killAllSpells);
        killSpellsCommand.register();
    }

    private static void killAllSpells(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();
        int nKilled = SpellManager.getInstance().killAllSpells();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("killed_spells", nKilled)
                .build();

        localeManager.sendMessage(player, "commands.killspells.killall.success", placeholders);
    }
}
