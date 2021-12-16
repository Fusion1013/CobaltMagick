package se.fusion1013.plugin.cobaltmagick.scenario;

import java.util.List;

public interface CustomEvent {

    String getInternalName();

    default String getName(){
        return this.getInternalName();
    }

    boolean isEnabled();

    List<String> getPossibleFlags();

    CustomEvent copyOf();

}
