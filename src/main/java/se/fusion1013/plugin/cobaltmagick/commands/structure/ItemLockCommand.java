package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.UUID;

public class ItemLockCommand {

    public static CommandAPICommand createItemLockCommand() {
        return new CommandAPICommand("lock")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createListCommand())
                .withSubcommand(createRemoveCommand());
    }

    // ----- PLACE COMMAND -----

    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNames())))
                .withArguments(new GreedyStringArgument("activatable_id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getActivatableStrings()))) // TODO: Replace with unlockable
                .executes(ItemLockCommand::placeItemLock);
    }

    /**
     * Places a new lock in the world.
     *
     * @param sender the sender that is placing the lock.
     * @param args the lock arguments.
     */
    private static void placeItemLock(CommandSender sender, Object[] args) {
        Location location = (Location)args[0];
        CustomItem item = CustomItemManager.getCustomItem((String)args[1]);
        Unlockable unlockable = WorldManager.getDoor(UUID.fromString((String)args[2]));
        WorldManager.registerItemLock(location, item, unlockable);

        if (sender instanceof Player p) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("structure", "Lock")
                    .addPlaceholder("location", location)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.place", placeholders);
        }
    }

    // ----- LIST COMMAND -----

    private static CommandAPICommand createListCommand() {
        return new CommandAPICommand("list")
                .executesPlayer(ItemLockCommand::listLock);
    }

    /**
     * Sends a list of all locks currently in the world to the player.
     *
     * @param player the player to send the list to.
     * @param args command arguments.
     */
    private static void listLock(Player player, Object[] args) {
        ItemLock[] locks = WorldManager.getItemLocks();

        if (locks.length == 0) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.not_found");
        } else {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("header", "Locks")
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "list-header", placeholders);
        }

        for (ItemLock lock : locks) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("id", lock.getUuid())
                    .addPlaceholder("location", lock.getLocation())
                    .addPlaceholder("key", lock.getItem().getInternalName())
                    .build();
            LocaleManager.getInstance().sendMessage("", player, "commands.magick.structure.lock.info", placeholders);
        }
    }

    // ----- INFO COMMAND -----

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new GreedyStringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getItemLockKeys())))
                .executes(ItemLockCommand::removeLock);
    }

    /**
     * Removes a lock from the world.
     *
     * @param sender the sender that is removing the lock.
     * @param args the lock arguments.
     */
    private static void removeLock(CommandSender sender, Object[] args) {
        UUID uuid = UUID.fromString((String)args[0]);
        ItemLock lock = WorldManager.getItemLock(uuid);

        if (sender instanceof Player p) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("structure", "Lock")
                    .addPlaceholder("location", lock.getLocation())
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), p, "commands.magick.structure.remove", placeholders);
        }

        WorldManager.removeItemLock(uuid);
    }

}
