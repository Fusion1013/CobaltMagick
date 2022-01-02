package se.fusion1013.plugin.cobaltmagick.commands;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.ConfigManager;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.util.FileUtil;
import se.fusion1013.plugin.cobaltmagick.util.SchematicUtil;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MagickCommand {
    public static void register(){

        CommandAPICommand configCommand = createConfigCommand();

        CommandAPICommand loadSchemCommand = new CommandAPICommand("load")
                .withPermission("cobalt.magick.commands.magick.load")
                .withArguments(new StringArgument("schematic"))
                .executesPlayer(MagickCommand::pasteSchem);

        // Main magick command
        new CommandAPICommand("magick")
                .withPermission("cobalt.magick.commands.magick")
                .withSubcommand(configCommand)
                .withSubcommand(loadSchemCommand)
                .register();
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
