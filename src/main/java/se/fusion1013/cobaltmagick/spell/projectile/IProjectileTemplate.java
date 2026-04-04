package se.fusion1013.cobaltmagick.spell.projectile;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

public interface IProjectileTemplate extends IRegistryItem {

    void display(Location location);

    SpellProjectile create(LivingEntity caster, ShotState context);

}
