package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LaserManager;

import java.util.List;

public class LaserCommandModule implements CommandModule {
    @Override
    public void onCommandExecute(CommandSender sender, String[] args) {
        if (args.length == 0){
            //TODO: Add usage feedback
        }

        LaserManager laserManager = Cobalt.getInstance().getManager(LaserManager.class);

        if (sender instanceof Player){
            Player p = (Player)sender;

            switch (args[0]){
                case "create": // /laser create
                    break;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public String getName() {
        return "laser";
    }

    @Override
    public String getDescriptionKey() {
        return "Creates a new laser";
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
