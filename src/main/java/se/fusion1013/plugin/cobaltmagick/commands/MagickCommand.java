package se.fusion1013.plugin.cobaltmagick.commands;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.*;
import se.fusion1013.plugin.cobaltmagick.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.util.SchematicUtil;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.world.structures.MusicBox;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
                .withSubcommand(createConfigCommand())
                .withSubcommand(createSummonCommand())
                .withSubcommand(createSummonRelativeCommand())
                .withSubcommand(createStructureCommand())
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
                    try {
                        File file1 = new File("plugins", (String)args[0]); // TODO: Replace path getting
                        FileUtils.copyURLToFile(new URL((String)args[1]), file1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
    }

    private static CommandAPICommand createStructureCommand() {
        return new CommandAPICommand("structure")
                .withPermission("commands.magick.structure")
                .withSubcommand(new CommandAPICommand("music_box")
                        .executesPlayer(MagickCommand::placeMusicBox)
                        .withArguments(new GreedyStringArgument("sound")));
    } // TODO: Replace sound suggestions

    private static void placeMusicBox(Player p, Object[] args) {
        Location location = p.getLocation();
        String sound = (String)args[0];
        WorldManager.registerMusicBox(location, sound);
    }

    private static CommandAPICommand createSummonCommand() {
        String[] entityKeys = EntityManager.getInstance().getCustomEntityNames();

        return new CommandAPICommand("summon")
                .withPermission("commands.magick.summon")
                .withArguments(new StringArgument("entity").replaceSuggestions(info -> entityKeys))
                .executesPlayer(((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], sender.getLocation());
                }))
                .executesCommandBlock(((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], sender.getBlock().getLocation());
                }))
                .executesEntity(((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], sender.getLocation());
                }));
    }

    private static CommandAPICommand createSummonRelativeCommand() {
        String[] entityKeys = EntityManager.getInstance().getCustomEntityNames();

        return new CommandAPICommand("summon")
                .withPermission("commands.magick.summon")
                .withArguments(new StringArgument("entity").replaceSuggestions(info -> entityKeys))
                .withArguments(new LocationArgument("location"))
                .executesPlayer((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], (Location)args[1]);
                })
                .executesCommandBlock(((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], (Location)args[1]);
                }))
                .executesEntity(((sender, args) -> {
                    EntityManager.getInstance().spawnCustomEntity((String)args[0], (Location)args[1]);
                }));
    }

    private static CommandAPICommand createVersionCommand() {
        return new CommandAPICommand("version")
                .executesPlayer((sender, args) -> {
                    printVersion(sender);
                });
    }

    private static void printVersion(CommandSender sender) {
        PluginDescriptionFile desc = CobaltMagick.getInstance().getDescription();
        LocaleManager localeManager = LocaleManager.getInstance();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("plugin_name", desc.getName())
                .addPlaceholder("version", desc.getVersion())
                .addPlaceholder("github_issues_link", "https://github.com/Fusion1013/CobaltMagick/issues")
                .build();

        localeManager.sendMessage(sender, "commands.magick.version.version", placeholders);
        localeManager.sendMessage(sender, "commands.magick.version.author");
        localeManager.sendMessage(sender, "commands.magick.version.github_issues", placeholders);
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

                    localeManager.sendMessage(sender, "commands.magick.colors.header");
                    localeManager.sendMessage(sender, "commands.magick.colors.color_codes_description");
                    localeManager.sendMessage(sender, "commands.magick.colors.color_codes");
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
                                .withArguments(new StringArgument("generation").replaceSuggestions(info -> bookGenerationTypes()))
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

                bookMeta.setPage(page, HexUtils.colorify(text));
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

    private static CommandAPICommand createConfigCommand(){

        List<String> keys = new ArrayList<>(ConfigManager.getInstance().getCustomConfig().getKeys(false));
        String[] configKeys = keys.toArray(new String[0]);

        CommandAPICommand getCommand = new CommandAPICommand("get")
                .withPermission("cobalt.magick.commands.magick.config.get")
                .withArguments(new StringArgument("key").replaceSuggestions(info -> configKeys))
                .executesPlayer(MagickCommand::getConfigValue);

        CommandAPICommand editCommand = new CommandAPICommand("edit")
                .withPermission("cobalt.magick.commands.magick.config.edit")
                .withArguments(new StringArgument("key").replaceSuggestions(info -> configKeys))
                .withArguments(new StringArgument("value"))
                .executesPlayer(MagickCommand::editKey);

        return new CommandAPICommand("config")
                .withPermission("cobalt.magick.commands.magick.config")
                .withSubcommand(editCommand)
                .withSubcommand(getCommand);
    }

    private static void getConfigValue(Player p, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        String key = (String)args[0];
        String value = ConfigManager.getInstance().getFromConfig(key);

        StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("key", key).addPlaceholder("value", value).build();
        localeManager.sendMessage(p, "commands.magick.config.get", placeholders);
    }

    private static void editKey(Player p, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        String key = (String)args[0];
        String value = (String)args[1];

        ConfigManager.getInstance().writeToConfig(key, value);

        StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("key", key).addPlaceholder("value", value).build();
        localeManager.sendMessage(p, "commands.magick.config.edit", placeholders);
    }
}
