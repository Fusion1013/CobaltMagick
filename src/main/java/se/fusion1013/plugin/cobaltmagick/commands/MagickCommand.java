package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VersionUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.commands.structure.StructureCommand;
import se.fusion1013.plugin.cobaltmagick.manager.*;
import se.fusion1013.plugin.cobaltmagick.util.SchematicUtil;
import se.fusion1013.plugin.cobaltmagick.world.structures.HiddenMessage;
import se.fusion1013.plugin.cobaltmagick.world.structures.ItemLock;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.Unlockable;

import java.util.UUID;

public class MagickCommand {

    // ----- REGISTER -----

    public static void register(){

        // Main magick command
        new CommandAPICommand("magick")
                .withSubcommand(createVersionCommand())
                .withSubcommand(createColorizeCommand())
                .withSubcommand(createColorCommand())
                .withSubcommand(createEditCommand())
                .withSubcommand(createDreamCommand())
                .withSubcommand(StructureCommand.createStructureCommand())
                .withSubcommand(createUpdateCommand())
                .register();
    }

    // ----- CREATE COMMANDS -----

    private static CommandAPICommand createUpdateCommand() {
        return new CommandAPICommand("update")
                .withPermission("commands.magick.update")
                .withArguments(new StringArgument("file name"))
                .withArguments(new GreedyStringArgument("url"))
                .executes(((sender, args) -> {
                    /*
                    try {
                        File file1 = new File("plugins", (String)args[0]); // TODO: Replace path getting
                        // FileUtils.copyURLToFile(new URL((String)args[1]), file1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     */
                }));
    }

    private static CommandAPICommand createStructureCommand() {
        return new CommandAPICommand("structure")
                .withPermission("commands.magick.structure")
                .withSubcommand(createHiddenMessageCommand())
                .withSubcommand(createLockCommand());
    } // TODO: Replace sound suggestions

    private static CommandAPICommand createLockCommand() {
        return new CommandAPICommand("lock")
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new LocationArgument("location"))
                        .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNames())))
                        .withArguments(new GreedyStringArgument("door_id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getDoorKeys())))
                        .executes((MagickCommand::placeLock)))
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(new GreedyStringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getItemLockKeys())))
                        .executes(MagickCommand::removeLock))
                .withSubcommand(new CommandAPICommand("list")
                        .executesPlayer(MagickCommand::listLock));
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

    /**
     * Places a new lock in the world.
     *
     * @param sender the sender that is placing the lock.
     * @param args the lock arguments.
     */
    private static void placeLock(CommandSender sender, Object[] args) {
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

    private static CommandAPICommand createHiddenMessageCommand() {
        return new CommandAPICommand("hidden_message")
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new LocationArgument("location"))
                        .withArguments(new IntegerArgument("rotation"))
                        .withArguments(new GreedyStringArgument("text"))
                        .executesPlayer(MagickCommand::addHiddenMessage));
    }

    private static void addHiddenMessage(Player p, Object[] args) {
        WorldManager.addHiddenMessage(new HiddenMessage((String)args[2], (Location)args[0], -Math.toRadians((int)args[1]), HiddenMessage.TextEncryption.GALACTIC)); // Rotate the rotation to switch the rotation direction
    }

    private static CommandAPICommand createVersionCommand() {
        return new CommandAPICommand("version")
                .executesPlayer((sender, args) -> {
                    VersionUtil.printVersion(CobaltMagick.getInstance(), sender);
                });
    }

    private static CommandAPICommand createColorizeCommand(){
        return new CommandAPICommand("colorize")
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    Bukkit.broadcastMessage(HexUtils.colorify((String)args[0]));
                });
    }

    private static CommandAPICommand createColorCommand(){
        return new CommandAPICommand("colors")
                .executesPlayer((sender, args) -> {
                    LocaleManager localeManager = LocaleManager.getInstance();

                    localeManager.sendMessage(CobaltMagick.getInstance(), sender, "commands.magick.colors.header");
                    localeManager.sendMessage(CobaltMagick.getInstance(), sender, "commands.magick.colors.color_codes_description");
                    localeManager.sendMessage(CobaltMagick.getInstance(), sender, "commands.magick.colors.color_codes");
                });
    }

    /**
     * Command for editing things
     *
     * @return <code>CommandAPICommand</code> command
     */
    private static CommandAPICommand createEditCommand(){
        return new CommandAPICommand("edit")
                .withPermission("cobalt.magick.commands.magick.edit")
                .withSubcommand(new CommandAPICommand("item")
                        .withPermission("cobalt.magick.commands.magick.edit.item")
                        .withSubcommand(new CommandAPICommand("custom-model-data")
                                .withArguments(new IntegerArgument("model data", 0))
                                .executesPlayer(MagickCommand::editItemCustomModelData))
                        .withSubcommand(new CommandAPICommand("custom-name")
                                .withArguments(new GreedyStringArgument("name"))
                                .withHelp(HexUtils.colorify("Adds a custom name to an item"), HexUtils.colorify("Adds a custom name to an item. Can be used with color codes. Use &3/magick colors &ffor more info on color codes"))
                                .executesPlayer(MagickCommand::editItemCustomName))
                        .withSubcommand(new CommandAPICommand("book-author")
                                .withArguments(new StringArgument("author"))
                                .executesPlayer(MagickCommand::editBookAuthor))
                        .withSubcommand(new CommandAPICommand("book-generation")
                                .withArguments(new StringArgument("generation").replaceSuggestions(ArgumentSuggestions.strings(info -> bookGenerationTypes())))
                                .executesPlayer(MagickCommand::editBookGeneration))
                        .withSubcommand(new CommandAPICommand("book-text")
                                .withArguments(new IntegerArgument("page-index"))
                                .withArguments(new GreedyStringArgument("text"))
                                .executesPlayer(MagickCommand::editBookText))
                        .withSubcommand(new CommandAPICommand("toggle-writable-book")
                                .executesPlayer(MagickCommand::convertBook)));
    }

    /**
     * Converts a book between a Book and Quill and a written book, depending on what the player is currently holding
     *
     * @param p player
     * @param args arguments
     */
    private static void convertBook(Player p, Object[] args) {
        ItemStack stack = p.getInventory().getItemInMainHand();
        if (stack.getType() == Material.WRITABLE_BOOK) stack.setType(Material.WRITTEN_BOOK);
        else if (stack.getType() == Material.WRITTEN_BOOK) stack.setType(Material.WRITABLE_BOOK);
    }

    /**
     * Edits a book in the players hand
     *
     * @param p player that is holding the item
     * @param author new author of the book
     * @param generation new generation of the book
     * @param text new text of the book. Uses HexUtils to color the text
     * @param page the page to insert the text into
     */
    private static void editBook(Player p, String author, String generation, String text, int page) {
        ItemStack stack = p.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) return;
        if (meta instanceof BookMeta bookMeta) {
            if (author != null) bookMeta.setAuthor(author);
            if (generation != null) bookMeta.setGeneration(BookMeta.Generation.valueOf(generation.toUpperCase()));
            if (text != null) {
                int currentPageCount = bookMeta.getPageCount();
                if (currentPageCount < page) {
                    for (int i = currentPageCount; i < page; i++) {
                        bookMeta.addPage("");
                    }
                }

                if (page > 0) bookMeta.setPage(page, HexUtils.colorify(text));
            }

            stack.setItemMeta(bookMeta);
        }
    }

    private static void editBookText(Player p, Object[] args) {
        editBook(p, null, null, (String)args[1], (Integer)args[0]);
    }

    private static void editBookGeneration(Player p, Object[] args) {
        editBook(p, null, (String)args[0], null, 0);
    }

    private static String[] bookGenerationTypes() {
        BookMeta.Generation[] generations = BookMeta.Generation.values();
        String[] genStrings = new String[generations.length];
        for (int i = 0; i < generations.length; i++) genStrings[i] = generations[i].toString().toLowerCase();
        return genStrings;
    }

    /**
     * Sets the author of a book
     *
     * @param p player that is holding the book
     * @param args author of the book
     */
    private static void editBookAuthor(Player p, Object[] args) {
        editBook(p, (String)args[0], null, null, 0);
    }

    private static void editItemCustomName(Player p, Object[] args){
        ItemStack stack = p.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName(HexUtils.colorify((String)args[0]));
        stack.setItemMeta(meta);
    }

    private static void editItemCustomModelData(Player p, Object[] args){
        ItemStack stack = p.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) return;
        meta.setCustomModelData((Integer)args[0]);
        stack.setItemMeta(meta);
    }

    private static CommandAPICommand createDreamCommand(){
        return new CommandAPICommand("dream")
                .withPermission("cobalt.magick.commands.magick.dream")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new BooleanArgument("dreaming"))
                .executesPlayer(MagickCommand::setDreamingPlayer)
                .executesCommandBlock(MagickCommand::setDreamingPlayer);
    }

    private static void setDreamingPlayer(CommandSender sender, Object[] args){
        Player dreamPlayer = (Player)args[0];
        boolean isDreaming = (Boolean)args[1];

        if (isDreaming) DreamManager.getInstance().addDreamingPlayer(dreamPlayer);
        else DreamManager.getInstance().removeDreamingPlayer(dreamPlayer);

        CobaltMagick.getInstance().getLogger().info("Set Dreaming state for player " + dreamPlayer.getName() + " to " + isDreaming);
    }

    private static void pasteSchem(Player p, Object[] args){
        SchematicUtil.pasteSchematic((String)args[0], p.getLocation());
    }
}
