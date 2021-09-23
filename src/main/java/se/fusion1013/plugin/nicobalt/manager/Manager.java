package se.fusion1013.plugin.nicobalt.manager;

import se.fusion1013.plugin.nicobalt.Cobalt;

public abstract class Manager {

    protected Cobalt cobalt;

    public Manager(Cobalt cobalt){
        this.cobalt = cobalt;
    }

    public abstract void reload();

    public abstract void disable();
}
