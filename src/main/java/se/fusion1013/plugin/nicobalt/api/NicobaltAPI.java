package se.fusion1013.plugin.nicobalt.api;

import se.fusion1013.plugin.nicobalt.Cobalt;

public class NicobaltAPI {
    private static NicobaltAPI INSTANCE;

    private final Cobalt cobalt;

    private NicobaltAPI(){
        this.cobalt = Cobalt.getInstance();
    }

    public static NicobaltAPI getInstance(){
        if (INSTANCE == null){
            INSTANCE = new NicobaltAPI();
        }
        return INSTANCE;
    }
}
