package se.fusion1013.plugin.cobaltmagick.entity.modules;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityStateModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.IDeathExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;

public class EntityStandPassengerModule extends EntityMagickModule implements ITickExecutable {

    // ----- VARIABLES -----

    ArmorStand armorStand;
    Vector offset;

    // Items
    ItemStack headItem;

    // ----- CONSTRUCTOR -----

    public EntityStandPassengerModule(ItemStack headItem, Vector offset) {
        this.headItem = headItem;
        this.offset = offset;
    }

    // ----- SETTERS -----

    public EntityStandPassengerModule setItem(ItemStack item) {
        this.headItem = item;
        return this;
    }

    // TODO

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        Location location = customEntity.getSummonedEntity().getLocation();

        if (armorStand == null) {
            // Spawn armor stand
            armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(offset), EntityType.ARMOR_STAND);
            armorStand.setInvisible(true); // TODO: make option
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setGravity(false);

            EntityEquipment equipment = armorStand.getEquipment();
            equipment.setHelmet(headItem);

            armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
        }

        if (!customEntity.isAlive()) {
            armorStand.remove();
        } else {
            // Teleport armor stand
            armorStand.teleport(location.clone().add(offset));
        }
    }

    // ----- CLONE -----

    public EntityStandPassengerModule(EntityStandPassengerModule target) {
        if (target.armorStand != null) this.armorStand = target.armorStand;
        this.headItem = target.headItem;
        this.offset = target.offset;
    }

    @Override
    public EntityStandPassengerModule clone() {
        return new EntityStandPassengerModule(this);
    }
}
