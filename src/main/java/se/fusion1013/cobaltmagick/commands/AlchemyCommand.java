package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyBlockProperties;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;

import java.util.List;

public class AlchemyCommand {

    private static final AlchemyManager ALCHEMY = AlchemyManager.getInstance();

    public static void register() {
        new CommandAPICommand("alchemy")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy"))
                .withSubcommand(createBlockPropertiesCommand())
                .register();
    }

    private static CommandAPICommand createBlockPropertiesCommand() {
        return new CommandAPICommand("properties")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.property"))
                .withSubcommand(
                        new CommandAPICommand("list")
                                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.property.list"))
                                .executes(AlchemyCommand::listBlockProperties)
                );
    }

    private static void listBlockProperties(CommandSender commandSender, CommandArguments commandArguments) {
        List<AlchemyBlockProperties> properties = AlchemyManager.getProperties();
        if (commandSender instanceof Player player) {
            player.sendMessage("## PROPERTIES ##");
            for (AlchemyBlockProperties property : properties) {
                player.sendMessage(" - " + property.getInternalName() + ": " + property.getPropertyInfo());
            }
        }
    }

}
