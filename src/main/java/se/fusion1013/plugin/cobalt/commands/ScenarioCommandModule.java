package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.manager.ScenarioManager;

import java.util.List;

public class ScenarioCommandModule implements CommandModule {
    @Override
    public void onCommandExecute(CommandSender sender, String[] args) {

        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);

        if (args.length == 0){
            return;
        }

        ScenarioManager scenarioManager = Cobalt.getInstance().getManager(ScenarioManager.class);

        if (sender instanceof Player){
            Player p = (Player)sender;

            switch (args[0]){
                case "create": // /scenario create <name> [location]
                    if (args.length >= 2){
                        String name = args[1];

                        if (args.length >= 5){
                            int x = Integer.parseInt(args[2]);
                            int y = Integer.parseInt(args[3]);
                            int z = Integer.parseInt(args[4]);

                            Location location = new Location(p.getWorld(), x, y, z);

                            scenarioManager.createScenario(name, location);
                            return;
                        }
                        scenarioManager.createScenario(name, p.getWorld());
                    } else {
                        localeManager.sendMessage(sender, "command-usage-scenario-create");
                    }
                    break;
                case "delete": // /scenario delete
                    if (args.length >= 2){
                        try{
                            int id = Integer.parseInt(args[1]);
                            scenarioManager.removeScenario(id);
                            return;
                        } catch (NumberFormatException ex) {
                        }
                        String name = args[1];
                        scenarioManager.removeScenario(name);
                        return;
                    } else {
                        localeManager.sendMessage(sender, "command-usage-scenario-delete");
                    }
                    break;
                case "list": // /scenario list
                    scenarioManager.printAllScenarios(sender);
                    break;
                case "info":
                    if (args.length >= 2){
                        try{
                            int id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex){
                        }
                        String name = args[1];
                    }
                    break;
                case "move":
                    localeManager.sendMessage(sender, "command-not-implemented");
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
