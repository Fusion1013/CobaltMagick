package se.fusion1013.plugin.cobalt.flags;

import java.util.ArrayList;
import java.util.List;

public class Flags {

    private static final List<String> INBUILT_FLAGS_LIST = new ArrayList<>();

    public static final LocationFlag LOCATION = register(new LocationFlag("location"));

    private static <T extends Flag<?>> T register(final T flag){
        INBUILT_FLAGS_LIST.add(flag.getName());
        return flag;
    }
}
