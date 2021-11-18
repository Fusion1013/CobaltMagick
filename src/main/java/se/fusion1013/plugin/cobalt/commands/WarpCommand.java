package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandDeclaration(
        commandName = "warp",
        aliases = "w",
        permission = "cobalt.warp",
        usage = "/warp",
        description = "Warp",
        validSenders = SenderType.PLAYER
)
public class WarpCommand extends MainCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        return true;
    }
}
