package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.potion.IPotionRecipe;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionManager;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.List;

public class AlchemyCommand {

    private static final AlchemyManager ALCHEMY = AlchemyManager.getInstance();
    private static final PotionManager POTION = PotionManager.getInstance();

    public static void register() {
        new CommandAPICommand("alchemy")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy"))
                .withSubcommand(createBlockPropertiesCommand())
                .withSubcommand(createPotionCommand())
                .withSubcommand(createDebugCommand())
                .register();
    }

    private static CommandAPICommand createDebugCommand() {
        return new CommandAPICommand("debug")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.debug"))
                .withSubcommand(createDebugCauldronCommand());
    }

    private static CommandAPICommand createDebugCauldronCommand() {
        return new CommandAPICommand("cauldron")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.debug.cauldron"))
                .withArguments(new LocationArgument("block", LocationType.BLOCK_POSITION))
                .executesPlayer((sender, args) -> {
                    Location location = (Location) args.get("block");
                    CauldronState state = CauldronManager.createCauldronState(location);
                    if (state == null) return;

                    sender.sendMessage(state.toString());
                });
    }

    private static CommandAPICommand createPotionCommand() {
        return new CommandAPICommand("potions")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.potions"))
                .withSubcommand(new CommandAPICommand("list")
                        .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.potions.list"))
                        .executesPlayer(AlchemyCommand::listPotionRecipes));
    }

    private static void listPotionRecipes(Player player, CommandArguments commandArguments) {
        IPotionRecipe[] recipes = POTION.getRecipes();
        player.sendMessage("## POTION RECIPES ##");
        for (IPotionRecipe recipe : recipes) {
            player.sendMessage(" - " + recipe.getPotionEffectType().getName() + ": " + recipe.getGlyph());
        }
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
