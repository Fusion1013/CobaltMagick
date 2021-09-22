package se.fusion1013.plugin.nicobalt.api;

import se.fusion1013.plugin.nicobalt.Nicobalt;

public class NicobaltAPI {
    private static NicobaltAPI INSTANCE;

    private final Nicobalt nicobalt;

    private NicobaltAPI(){
        this.nicobalt = Nicobalt.getInstance();
    }

    public static NicobaltAPI getInstance(){
        if (INSTANCE == null){
            INSTANCE = new NicobaltAPI();
        }
        return INSTANCE;
    }
}
