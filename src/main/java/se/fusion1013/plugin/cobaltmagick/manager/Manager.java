package se.fusion1013.plugin.cobaltmagick.manager;

import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public abstract class Manager {

    protected CobaltMagick cobaltMagick;

    public Manager(CobaltMagick cobaltMagick){
        this.cobaltMagick = cobaltMagick;
    }

    public abstract void reload();

    public abstract void disable();
}
