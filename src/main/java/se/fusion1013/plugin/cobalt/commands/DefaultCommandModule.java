package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.CommandManager;

import java.util.ArrayList;
import java.util.List;

public class DefaultCommandModule implements CommandModule {
    @Override
    public void onCommandExecute(CommandSender sender, String[] args) {
        // Default command does nothing
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> matches = new ArrayList<>();
        List<String> commandNames = Cobalt.getInstance().getManager(CommandManager.class).getCommandNames();

        if (args.length == 0){
            return commandNames;
        }

        StringUtil.copyPartialMatches(args[0], commandNames, matches);

        return matches;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescriptionKey() {
        return "command-description-default";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public boolean requiresEffectsAndStyles() {
        return false;
    }

    @Override
    public boolean canConsoleExecute() {
        return false;
    }
}
