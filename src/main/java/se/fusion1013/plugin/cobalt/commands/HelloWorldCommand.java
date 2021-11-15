package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandDeclaration(
        commandName = "helloWorld",
        aliases = {"hw"},
        permission = "cobalt.hellowworld",
        usage = "/helloWorld",
        description = "Hello World",
        validSenders = SenderType.PLAYER
)
public class HelloWorldCommand extends MainCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        sender.sendMessage("Hello World");
        return true;
    }
}
