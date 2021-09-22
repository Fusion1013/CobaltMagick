package se.fusion1013.plugin.nicobalt.manager;

import se.fusion1013.plugin.nicobalt.Nicobalt;

public abstract class Manager {

    protected Nicobalt nicobalt;

    public Manager(Nicobalt nicobalt){
        this.nicobalt = nicobalt;
    }

    public abstract void reload();

    public abstract void disable();
}
