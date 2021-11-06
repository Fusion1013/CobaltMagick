package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.scenario.Scenario;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;

import java.util.ArrayList;
import java.util.List;

public class ScenarioManager extends Manager {

    private List<Scenario> scenarios;

    public ScenarioManager(Cobalt cobalt) {
        super(cobalt);
        scenarios = new ArrayList<>();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    /***
     * Removes the specified scenario
     * @param name name of the scenario to remove
     * @return the removed scenario
     */
    public Scenario removeScenario(String name){
        for (int i = 0; i < scenarios.size(); i++){
            if (scenarios.get(i).getName().equalsIgnoreCase(name)){
                return removeScenario(i);
            }
        }
        return null;
    }

    /***
     * Removes the specified scenario
     * @param id id of the scenario to remove.
     * @return the removed scenario
     */
    public Scenario removeScenario(int id){
        return scenarios.remove(id);
    }

    /***
     * Create a scenario with a name, if a scenario with that name does not already exist.
     * @param name name of the scenario
     * @return the created scenario, or null if similar named scenario already exists
     */
    public Scenario createScenario(String name, World world){
        for (Scenario s : scenarios){
            if (s.getName().equalsIgnoreCase(name)){
                return null;
            }
        }
        Scenario scenario = new Scenario(name, world);
        scenarios.add(scenario);
        return scenario;
    }

    /***
     * Create a scenario with a name and a location, if a scenario with that name does not already exist. The location will be used as a center for added events.
     * @param name name of the scenario
     * @param location location of the scenario
     * @return the created scenario, or null if similar named scenario already exists
     */
    public Scenario createScenario(String name, Location location){
        return createScenario(name, location);
    }

    /***
     * Prints a list of all scenarios
     * @param sender player to send the messages to
     */
    public void printAllScenarios(CommandSender sender){
        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);
        StringPlaceholders header = StringPlaceholders.builder("header", "Scenarios").build();
        localeManager.sendMessage(sender, "list-scenarios", header);

        for (int i = 0; i < scenarios.size(); i++){
            Scenario scenario = scenarios.get(i);
            StringPlaceholders stringPlaceholders = StringPlaceholders.builder("id", i)
                    .addPlaceholder("name", scenario.getName())
                    .addPlaceholder("x", scenario.getScenarioLocation().getX())
                    .addPlaceholder("y", scenario.getScenarioLocation().getY())
                    .addPlaceholder("z", scenario.getScenarioLocation().getZ())
                    .addPlaceholder("world", scenario.getScenarioLocation().getWorld().getName())
                    .build();
            localeManager.sendMessage(sender, "list-item-id-name-location", stringPlaceholders);
        }
    }
}
