package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.locale.Locale;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;
import se.fusion1013.plugin.cobalt.util.Warp;

import java.util.List;
import java.util.UUID;

@CommandDeclaration(
        commandName = "info",
        permission = "cobalt.warp.info",
        usage = "/warp info <warp>",
        description = "Displays information about a warp",
        minArgs = 1,
        maxArgs = 1,
        validSenders = SenderType.PLAYER,
        parentCommandName = "warp"
)
public class WarpInfoCommand extends SubCommand {
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
        if (warps.size() == 1 || highestPriorityWarp.getOwner().equals(pID)){
            localeManager.sendMessage(sender, "commands.warp.info.header", namePlaceholder);

            Location loc = highestPriorityWarp.getLocation();

            // Send publicly available details
            StringPlaceholders pOwner = new StringPlaceholders().builder()
                    .addPlaceholder("owner", Bukkit.getPlayer(highestPriorityWarp.getOwner()).getDisplayName())
                    .build();
            localeManager.sendMessage(sender, "commands.warp.info.detail.owner", pOwner);
            StringPlaceholders pWorld = new StringPlaceholders().builder()
                    .addPlaceholder("world", loc.getWorld().getName())
                    .build();
            localeManager.sendMessage(sender, "commands.warp.info.detail.world", pWorld);
            StringPlaceholders pLocation = new StringPlaceholders().builder()
                    .addPlaceholder("x", highestPriorityWarp.getShortX())
                    .addPlaceholder("y", highestPriorityWarp.getShortY())
                    .addPlaceholder("z", highestPriorityWarp.getShortZ())
                    .build();
            localeManager.sendMessage(sender, "commands.warp.info.detail.location", pLocation);
            StringPlaceholders pDistance = new StringPlaceholders().builder()
                    .addPlaceholder("distance", (double)Math.round(player.getLocation().distance(loc)*100)/100)
                    .build();
            localeManager.sendMessage(sender, "commands.warp.info.detail.distance", pDistance);
            StringPlaceholders pPrivacy = new StringPlaceholders().builder()
                    .addPlaceholder("privacy", highestPriorityWarp.getPrivacyLevel().name())
                    .build();
            localeManager.sendMessage(sender, "commands.warp.info.detail.privacy", pPrivacy);
        }

        return true;
    }
}
