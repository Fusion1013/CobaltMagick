package se.fusion1013.cobaltmagick.spell.projectile;

import org.bukkit.Location;

public interface ISpellProjectile {

    void tick();

    Location getLocation();

    Location getPreviousLocation();

    boolean isDead();

    void display();

}
