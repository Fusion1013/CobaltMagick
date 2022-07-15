package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.executors.CommandBlockCommandExecutor;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

/**
 * Command for killing all currently active spells
 */
public class KillSpellsCommand {

    // ----- REGISTER COMMAND -----

    /**
     * Registers the killspells commands
     */
    public static void register(){
        new CommandAPICommand("killspells")
                .withPermission("cobalt.command.killspells")
                .executesPlayer(KillSpellsCommand::killSpells)
                .register();

        new CommandAPICommand("killspells")
                .withPermission("cobalt.command.killspells")
                .withArguments(new DoubleArgument("max_distance"))
                .executesPlayer(KillSpellsCommand::killSpells)
                .executesCommandBlock((CommandBlockCommandExecutor) (sender, args) -> killAllSpells())
                .register();
    }

    // ----- LOGIC -----

    /**
     * Kills all currently active spells.
     *
     * @param player the executor that is killing the spells.
     * @param args the max distance to the spells. If null kills all spells.
     */
    private static void killSpells(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        int nKilled;
        if (args.length > 0) nKilled = SpellManager.getInstance().killAllSpells(player.getLocation(), (int)args[0]);
        else nKilled = SpellManager.getInstance().killAllSpells();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("killed_spells", nKilled)
                .build();

        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.killspells.killall.success", placeholders);
    }

    private static void killAllSpells() {
        SpellManager.getInstance().killAllSpells();
    }
}
