package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum SenderType {
    PLAYER,
    CONSOLE,
    BLOCK,
    UNKNOWN;

    /**
     * Gets the <code>SenderType</code> of the given <code>CommandSender</code>.
     *
     * @param sender The <code>CommandSender</code> to check.
     * @return The <code>SenderType</code> of the <code>CommandSender</code>
     */
    public static SenderType getSenderType(CommandSender sender){
        SenderType senderType = UNKNOWN;

        if (sender instanceof Player) senderType = PLAYER;
        else if (sender instanceof ConsoleCommandSender) senderType = CONSOLE;
        else if (sender instanceof BlockCommandSender) senderType = BLOCK;

        return senderType;
    }
}
