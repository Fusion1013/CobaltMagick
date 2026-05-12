package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.components.conditions.ICondition;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.logger.RuleLogger;
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
                .withSubcommand(createTemplateCommand())
                .register();
    }

    private static CommandAPICommand createTemplateCommand() {
        return new CommandAPICommand("template")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.template"))
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                        .withArguments(new StringArgument("template").replaceSuggestions(ArgumentSuggestions.strings(k -> PotionManager.getInstance().getRecipeNames())))
                        .withArguments(new BooleanArgument("cinematic"))
                        .executes(AlchemyCommand::placeTemplate));
    }

    private static void placeTemplate(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        String templateName = (String) args.get("template");
        boolean cinematic = (boolean) args.get("cinematic");

        IPotionRecipe recipe = PotionManager.getInstance().getRecipe(templateName);
        if (recipe == null) return;

        recipe.placeTemplate(location, cinematic);
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
                    RuleLogger ruleLogger = RuleLogger.create("Debug Cauldron Command");
                    Location location = (Location) args.get("block");
                    CauldronState state = CauldronManager.createCauldronState(location, ruleLogger, new ItemStack(Material.GLASS_BOTTLE));
                    if (state == null) return;

                    sender.sendMessage(state.toString());
                    ruleLogger.print(CobaltMagick.getInstance());
                });
    }

    private static CommandAPICommand createPotionCommand() {
        return new CommandAPICommand("potions")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.potions"))
                .withSubcommand(new CommandAPICommand("list")
                        .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "alchemy.potions.list"))
                        .executesPlayer(AlchemyCommand::listPotionRecipes))
                .withSubcommand(new CommandAPICommand("info")
                        .withArguments(new StringArgument("recipe").replaceSuggestions(ArgumentSuggestions.strings(k -> PotionManager.getInstance().getRecipeNames())))
                        .executesPlayer(AlchemyCommand::potionRecipeInfo))
                .withSubcommand(new CommandAPICommand("prepare")
                        .withArguments(new StringArgument("recipe").replaceSuggestions(ArgumentSuggestions.strings(k -> PotionManager.getInstance().getRecipeNames())))
                        .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                        .withArguments(new BooleanArgument("cinematic"))
                        .executesPlayer(AlchemyCommand::preparePotionRecipe));
    }

    private static void preparePotionRecipe(Player player, CommandArguments args) {
        Location location = (Location) args.get("location");
        String recipeName = (String) args.get("recipe");
        boolean cinematic = (boolean) args.get("cinematic");

        IPotionRecipe recipe = PotionManager.getInstance().getRecipe(recipeName);
        if (recipe == null) return;

        recipe.placeTemplate(location, cinematic);
        ItemStack metalItem = CustomItemManager.getItemStack(recipe.getMetalName());
        player.give(metalItem);

        ItemStack bindingItem = CustomItemManager.getItemStack(recipe.getBindingName());
        player.give(bindingItem);

        for (String item : recipe.getItemNames()) {
            ItemStack itemStack = CustomItemManager.getItemStack(item);
            player.give(itemStack);
        }

        player.give(new ItemStack(Material.GLASS_BOTTLE));

        location.getBlock().setBlockData(Material.WATER_CAULDRON.createBlockData());
    }

    private static void potionRecipeInfo(Player player, CommandArguments args) {
        String recipeName = (String) args.get("recipe");
        IPotionRecipe recipe = PotionManager.getInstance().getRecipe(recipeName);
        if (recipe == null) return;

        player.sendMessage("## " + recipe.getInternalName() + " Info ##");
        player.sendMessage("Metal: " + recipe.getMetalName());
        player.sendMessage("Binding: " + recipe.getBindingName());

        player.sendMessage("Items:");
        for (String item : recipe.getItemNames()) {
            player.sendMessage(" - " + item);
        }

        player.sendMessage("Conditions:");
        for (ICondition condition : recipe.getConditions()) {
            player.sendMessage(" - " + condition.getInternalName() + ": " + condition.getDescription());
        }
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
