package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;

@CommandDeclaration(
        commandName = "warp",
        aliases = "w",
        permission = "cobalt.warp",
        usage = "/warp",
        description = "Warp",
        validSenders = SenderType.PLAYER,
        maxArgs = 0,
        executeIfInvalidSubCommand = true
)
public class WarpCommand extends MainCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        return getSubCommands().get("list").validate(sender, args);
    }
}
