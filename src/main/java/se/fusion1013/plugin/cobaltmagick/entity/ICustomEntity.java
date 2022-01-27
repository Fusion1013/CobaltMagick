package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public interface ICustomEntity {

    // ----- EVENTS -----

    void spawn(Location location);
    void tick();
    void onDeath();

    // ----- ENTITY INFORMATION -----

    LivingEntity getEntity();

    double getMaxHealth();
    double getCurrentHealth();

    // ----- CHECKS -----

    boolean isAlive();

    // ----- GETTERS / SETTERS -----

    void switchBossbarColor(BarColor color);
    String getInbuiltName();
    String getUniqueId();
    Location getSpawnLocation();

    // ----- CLONE -----

    ICustomEntity clone();
}
