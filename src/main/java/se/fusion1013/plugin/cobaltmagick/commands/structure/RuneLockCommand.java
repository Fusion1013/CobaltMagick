package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.RuneLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.List;
import java.util.UUID;

public class RuneLockCommand {

    public static CommandAPICommand createRuneLockCommand() {
        return new CommandAPICommand("rune_lock")
                .withPermission("cobalt.magick.commands.structure.rune_lock")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createInfoCommand())
                .withSubcommand(addItemCommand())
                .withSubcommand(createRemoveCommand());
    }

    // ----- PLACE COMMAND -----

    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNames())))
                .withArguments(new GreedyStringArgument("activatable_id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getActivatableStrings())))
                .executes(RuneLockCommand::placeRuneLock);
    }

    private static void placeRuneLock(CommandSender sender, Object[] args) {
        Location location = (Location)args[0];
        String item = (String) args[1];
        IActivatable activatable = WorldManager.getActivatable(UUID.fromString((String)args[2]));
        WorldManager.registerRuneLock(location, activatable, item);

        if (sender instanceof Player p) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("structure", "Rune Lock")
                    .addPlaceholder("location", location)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.place", placeholders);
        }
    }

    // ----- ADD ITEM COMMAND -----

    private static CommandAPICommand addItemCommand() {
        return new CommandAPICommand("add_item")
                .withArguments(new IntegerArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getRuneLockIds())))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNames())))
                .executesPlayer(RuneLockCommand::addItem);
    }

    private static void addItem(Player player, Object[] args) {
        int id = (Integer) args[0];
        String item = (String) args[1];
        boolean added = WorldManager.addRuneLockItem(id, item);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", id)
                .addPlaceholder("item", item)
                .build();

        if (!added) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.not_found", placeholders);
        else LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.add_item", placeholders);
    }

    // ----- LIST COMMAND -----

    // ----- INFO COMMAND -----

    private static CommandAPICommand createInfoCommand() {
        return new CommandAPICommand("info")
                .withArguments(new IntegerArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getRuneLockIds())))
                .executesPlayer(RuneLockCommand::infoRuneLock);
    }

    private static void infoRuneLock(Player player, Object[] args) {
        int id = (Integer) args[0];
        RuneLock runeLock = WorldManager.getRuneLock(id);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", id)
                .build();

        if (runeLock == null) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.not_found", placeholders);
        else {
            placeholders.addPlaceholder("location", runeLock.getLocation());
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.info", placeholders);


            // List all items that are needed for the lock
            placeholders.addPlaceholder("header", "Items Needed");
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.list_header", placeholders);

            List<String> itemsNeeded = runeLock.getItemsNeeded();
            for (int i = 0; i < itemsNeeded.size(); i++) {
                String item = itemsNeeded.get(i);
                StringPlaceholders placeholders1 = StringPlaceholders.builder()
                        .addPlaceholder("item", item)
                        .addPlaceholder("id", i)
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.item", placeholders1);
            }

            // List all items that have already been inserted into the lock
            placeholders.addPlaceholder("header", "Items Inserted");
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.list_header", placeholders);

            List<ItemStack> itemsInserted = runeLock.getInsertedItems();
            for (int i = 0; i < itemsInserted.size(); i++) {
                ItemStack item = itemsInserted.get(i);
                StringPlaceholders placeholders1 = StringPlaceholders.builder()
                        .addPlaceholder("item", CustomItemManager.getItemName(item))
                        .addPlaceholder("id", i)
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.item", placeholders1);
            }
        }
    }

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.magick.commands.structure.rune_lock.remove")
                .withArguments(new IntegerArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getRuneLockIds())))
                .executes(RuneLockCommand::removeRuneLock);
    }

    private static void removeRuneLock(CommandSender sender, Object[] args) {
        int id = (Integer) args[0];
        RuneLock runeLock = WorldManager.getRuneLock(id);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("id", id)
                .build();

        boolean removed = false;

        if (runeLock != null) {
            WorldManager.removeRuneLock(id);
            removed = true;

        }

        if (sender instanceof Player player) {
            if (removed) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.remove", placeholders);
            else LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.rune_lock.not_found", placeholders);
        }
    }

}
