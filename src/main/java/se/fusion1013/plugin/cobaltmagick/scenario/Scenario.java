package se.fusion1013.plugin.cobaltmagick.scenario;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.CustomEventManager;

import java.util.List;

public class Scenario {
    private List<CustomEvent> events;

    private String name;
    private Location scenarioLocation;

    public Scenario(String name, Location scenarioLocation){
        this.name = name;
        this.scenarioLocation = scenarioLocation;
    }

    public Scenario(String name, World world){
        this(name, new Location(world, 0, 0, 0));
    }

    //TODO: Event system
    public void addEvent(String eventName){
        addEvent(CobaltMagick.getInstance().getManager(CustomEventManager.class).getEventByName(eventName));
    }

    private void addEvent(CustomEvent event){
        events.add(event);
    }

    public void printScenarioInfo(CommandSender sender){

    }

    /***
     * Returns the name of the scenario
     * @return scenario name
     */
    public String getName(){
        return this.name;
    }

    /***
     * Returns the location of the scenario
     * @return scenario location
     */
    public Location getScenarioLocation(){
        return this.scenarioLocation;
    }

    /***
     * Sets the scenario locaiton
     * @param scenarioLocation scenario location
     */
    public Scenario setScenarioLocation(Location scenarioLocation){
        this.scenarioLocation = scenarioLocation;
        return this;
    }
}
