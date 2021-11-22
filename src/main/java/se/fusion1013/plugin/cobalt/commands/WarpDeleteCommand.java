package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;

import java.util.UUID;

@CommandDeclaration(
        commandName = "delete",
        aliases = "del",
        permission = "cobalt.warp.delete",
        usage = "/warp delete <warp>",
        description = "Delete the given warp",
        validSenders = SenderType.PLAYER,
        minArgs = 1,
        maxArgs = 1,
        parentCommandName = "warp"
)
public class WarpDeleteCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);

        String name = args[0];
        int deletedWarps = Cobalt.getInstance().getRDatabase().deleteWarp(name);
        StringPlaceholders namePlaceholder = new StringPlaceholders().builder()
                .addPlaceholder("name", name)
                .addPlaceholder("count", deletedWarps)
                .build();

        if (deletedWarps > 0){
            localeManager.sendMessage(sender, "commands.warp.delete.deleted_warps", namePlaceholder);
            return true;
        } else {
            localeManager.sendMessage(sender, "commands.warp.error.warp_not_found", namePlaceholder);
            return false;
        }
    }
}
