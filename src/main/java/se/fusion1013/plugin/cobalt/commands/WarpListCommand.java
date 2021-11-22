package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;
import se.fusion1013.plugin.cobalt.util.Warp;

import java.util.List;

@CommandDeclaration(
        commandName = "list",
        permission = "cobalt.warp.list",
        usage = "/warp list",
        description = "Lists all warps",
        validSenders = SenderType.PLAYER,
        parentCommandName = "warp"
)
public class WarpListCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);
        Player player = (Player)sender;

        List<Warp> warps = Cobalt.getInstance().getRDatabase().getWarps();

        localeManager.sendMessage(sender, "commands.warp.list.header");
        for (Warp w : warps){
            StringPlaceholders placeholders = new StringPlaceholders().builder()
                    .addPlaceholder("name", w.getName())
                    .addPlaceholder("x", w.getShortX())
                    .addPlaceholder("y", w.getShortY())
                    .addPlaceholder("z", w.getShortZ())
                    .addPlaceholder("world", w.getLocation().getWorld().getName())
                    .build();
            localeManager.sendMessage(sender, "commands.warp.list.entry", placeholders);
        }

        return true;
    }
}
