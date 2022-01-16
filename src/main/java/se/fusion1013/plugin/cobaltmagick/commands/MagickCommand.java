package se.fusion1013.plugin.cobaltmagick.commands;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.ConfigManager;
import se.fusion1013.plugin.cobaltmagick.manager.DreamManager;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.util.SchematicUtil;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class MagickCommand {
    public static void register(){

        CommandAPICommand versionCommand = createVersionCommand();

        CommandAPICommand colorizeMessage = createColorizeCommand();

        CommandAPICommand colorCommand = createColorCommand();

        CommandAPICommand editCommand = createEditCommand();

        CommandAPICommand dreamCommand = createDreamCommand();

        CommandAPICommand configCommand = createConfigCommand();

        CommandAPICommand loadSchemCommand = new CommandAPICommand("load")
                .withPermission("cobalt.magick.commands.magick.load")
                .withArguments(new StringArgument("schematic"))
                .executesPlayer(MagickCommand::pasteSchem);

        // Main magick command
        new CommandAPICommand("magick")
                .withSubcommand(versionCommand)
                .withSubcommand(colorizeMessage)
                .withSubcommand(colorCommand)
                .withSubcommand(editCommand)
                .withSubcommand(dreamCommand)
                .withSubcommand(configCommand)
                .withSubcommand(loadSchemCommand)
                .register();
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
                                .executesPlayer(MagickCommand::editItemCustomName)));
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
