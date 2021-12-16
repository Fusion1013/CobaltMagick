package se.fusion1013.plugin.cobaltmagick.manager;

import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.scenario.CustomEvent;
import se.fusion1013.plugin.cobaltmagick.scenario.CustomEventMotion;

import java.util.ArrayList;
import java.util.List;

public class CustomEventManager extends Manager {

    private static CustomEventManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static CustomEventManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CustomEventManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    private List<CustomEvent> customEventList;

    public CustomEventManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
    }

    @Override
    public void reload() {
        registerCustomEvents();
    }

    @Override
    public void disable() {
        customEventList.clear();
    }

    /**
     * Returns a list of all registered event names
     * @return list of all registered event names
     */
    public List<String> getEventNames(){
        List<String> customEventNames = new ArrayList<>();
        for (CustomEvent e : customEventList){
            customEventNames.add(e.getName());
        }
        return customEventNames;
    }

    /***
     * Returns a CustomEvent object based on the given name. If not found, return null
     * @param customEventName the name of the CustomEvent
     * @return A CustomEvent matching the customEventName
     */
    public CustomEvent getEventByName(String customEventName){
        for (CustomEvent e : customEventList){
            if (e.getName().equalsIgnoreCase(customEventName)){
                return e.copyOf();
            }
        }
        System.out.println("Event '" + customEventName + "' not found");
        return null;
    }

    private void registerCustomEvents(){
        customEventList = new ArrayList<>();

        customEventList.add(new CustomEventMotion());
    }
}
