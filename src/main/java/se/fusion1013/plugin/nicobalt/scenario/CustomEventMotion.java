package se.fusion1013.plugin.nicobalt.scenario;

import java.util.ArrayList;
import java.util.List;

public class CustomEventMotion extends DefaultCustomEvent implements CustomEvent {

    public CustomEventMotion() {
        super("motion");
    }

    @Override
    public List<String> getPossibleFlags() {
        List<String> possibleFlags = new ArrayList<>();
        possibleFlags.add("location");
        return possibleFlags;
    }
}
