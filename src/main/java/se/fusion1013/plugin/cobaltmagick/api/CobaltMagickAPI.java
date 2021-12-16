package se.fusion1013.plugin.cobaltmagick.api;

import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public class CobaltMagickAPI {
    private static CobaltMagickAPI INSTANCE;

    private final CobaltMagick cobaltMagick;

    private CobaltMagickAPI(){
        this.cobaltMagick = CobaltMagick.getInstance();
    }

    public static CobaltMagickAPI getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CobaltMagickAPI();
        }
        return INSTANCE;
    }
}
