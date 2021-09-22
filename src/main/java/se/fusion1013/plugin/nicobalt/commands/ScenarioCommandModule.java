package se.fusion1013.plugin.nicobalt.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class ScenarioCommandModule implements CommandModule {
    @Override
    public void onCommandExecute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescriptionKey() {
        return null;
    }

    @Override
    public String getArguments() {
        return null;
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
