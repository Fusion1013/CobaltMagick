package se.fusion1013.plugin.nicobalt.scenario;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private List<CustomEvent> events;

    public Scenario(){
        events = new ArrayList<>();
    }

    public void addEvent(CustomEvent event){
        events.add(event);
    }
}
