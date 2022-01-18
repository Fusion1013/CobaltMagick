package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Location;

public interface ICustomEntity {
    void spawn(Location location);
    void tick();
    String getInbuiltName();
    boolean isAlive();
    String getUniqueId();
    void kill();
    ICustomEntity clone();
}
