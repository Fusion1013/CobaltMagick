package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.plugin.Plugin;

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
}
