package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class WandEntity extends AbstractCustomEntity implements Cloneable {

    ArmorStand armorStandEntity;
    Wand wand;

    public WandEntity(Wand wand) {
        super(EntityType.BAT, "wand_entity");
        this.wand = wand;
    }

    public WandEntity(WandEntity target) {
        super(target);
        this.armorStandEntity = target.armorStandEntity;
        this.wand = new Wand(target.wand);
    }

    @Override
    public void tick() {
        super.tick();

        // Set bat to invisible
        if (summonedEntity instanceof LivingEntity living) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 0, true, false));
        }

        // Teleport item to entity
        armorStandEntity.teleport(summonedEntity.getLocation().subtract(new Vector(0, 1, 0)));

        // Find target
        Location target = null;
        Location castLocation = armorStandEntity.getLocation().clone().add(new Vector(0, 2.2, 0));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (castLocation.distanceSquared(p.getLocation()) < castLocation.distanceSquared(p.getLocation()) || target == null) {
                target = p.getLocation();
            }
        }

        if (target != null) {
            Vector delta = new Vector(target.getX() - castLocation.getX(), target.getY() - castLocation.getY() + 1, target.getZ() - castLocation.getZ()).normalize();
            // Cast spell
            wand.castSpells(armorStandEntity, delta, castLocation);
        }

        // Wand Particles
        for (Player p : Bukkit.getOnlinePlayers()){
            p.spawnParticle(Particle.DUST_COLOR_TRANSITION, castLocation, 1, .1, .3, .1, .5, new Particle.DustTransition(Color.YELLOW, Color.WHITE, 1));
        }
    }

    @Override
    public void spawn(Location location) {
        super.spawn(location);

        if (location.getWorld() == null) return;

        wand.setRechargeCooldown(2); // Sets the initial cooldown of casting to 2 seconds

        summonedEntity.setSilent(true);

        // Create the wand item and set attributes
        armorStandEntity = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStandEntity.setInvisible(true);
        armorStandEntity.setInvulnerable(true);

        armorStandEntity.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING); // TODO: Do this for all slots

        EntityEquipment equipment = armorStandEntity.getEquipment();
        if (equipment != null) equipment.setHelmet(wand.getWandItem());
        armorStandEntity.setGravity(false);
    }

    @Override
    public void kill() {
        super.kill();
        if (armorStandEntity != null) armorStandEntity.remove();
    }

    @Override
    public WandEntity clone() {
        return new WandEntity(this);
    }
}
