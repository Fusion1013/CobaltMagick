package se.fusion1013.plugin.cobalt.scenario;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private List<CustomEvent> events;

    public Scenario(){
        events = new ArrayList<>();
    }

    public void addEvent(String localEventName){

    }

    private void addEvent(CustomEvent event){
        events.add(event);
    }
}
