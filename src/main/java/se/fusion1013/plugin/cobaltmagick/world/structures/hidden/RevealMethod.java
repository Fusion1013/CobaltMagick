package se.fusion1013.plugin.cobaltmagick.world.structures.hidden;

import java.util.ArrayList;
import java.util.List;

public enum RevealMethod {
    ALL_SEEING_EYE,
    PROXIMITY;

    public static String[] getRevealMethodNames() {
        RevealMethod[] methods = RevealMethod.values();
        List<String> revealMethodStrings = new ArrayList<>();
        for (RevealMethod method : methods) revealMethodStrings.add(method.toString());
        return revealMethodStrings.toArray(new String[0]);
    }
}
