package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;
import se.fusion1013.plugin.cobalt.util.Warp;

import java.util.List;
import java.util.UUID;

@CommandDeclaration(
        commandName = "tp",
        aliases = "teleport",
        permission = "cobalt.warp.tp",
        usage = "/warp tp <warp>",
        description = "Tp to the given warp",
        validSenders = SenderType.PLAYER,
        minArgs = 1,
        maxArgs = 1,
        parentCommandName = "warp"
)
public class WarpTpCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);
        Player player = (Player)sender;
        UUID pID = player.getUniqueId();

        String name = args[0];
        List<Warp> warps = Cobalt.getInstance().getRDatabase().getWarpsByName(name);

        if (warps == null) return false;

        StringPlaceholders namePlaceholder = new StringPlaceholders().builder()
                .addPlaceholder("name", name).build();

        if (warps.size() == 0){
            localeManager.sendMessage(sender, "commands.warp.error.warp_not_found");
            return false;
        }

        Warp highestPriorityWarp = warps.get(0);

        localeManager.sendMessage(sender, "commands.warp.teleport.success", namePlaceholder);
        player.teleport(highestPriorityWarp.getLocation());

        return true;
    }
}
