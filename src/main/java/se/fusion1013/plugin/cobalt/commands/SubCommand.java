package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand extends MainCommand implements CobaltCommand {

    private String parentCommandName;

    public SubCommand(){
        super();
    }

    public String getParentCommandName() { return parentCommandName; }

    @Override
    public void populate(Plugin plugin) {
        super.populate(plugin);

        CommandDeclaration commandDeclaration = getClass().getAnnotation(CommandDeclaration.class);
        this.parentCommandName = commandDeclaration.parentCommandName();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
