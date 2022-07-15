package se.fusion1013.plugin.cobaltmagick.entity.modules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;

public class EntityLiftedPassengerModule extends EntityMagickModule implements ITickExecutable {

    // ----- VARIABLES -----

    ICustomEntity rider;
    ArmorStand stand;

    // ----- CONSTRUCTORS -----

    public EntityLiftedPassengerModule(ICustomEntity rider) {
        this.rider = rider;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {

        Entity summonedEntity = customEntity.getSummonedEntity();

        if (summonedEntity.isValid()) {

            Location entityLocation = summonedEntity.getLocation();
            World world = entityLocation.getWorld();

            if (stand == null) {
                // Create armor stand
                stand = world.spawn(entityLocation, ArmorStand.class, armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.setInvisible(true);
                    armorStand.setSmall(true);
                });

                // Set armor stand as passenger
                summonedEntity.addPassenger(stand);

                // Summon rider
                CustomEntity riderEntity = rider.forceSpawn(entityLocation, null);

                // Set rider as passenger
                stand.addPassenger(riderEntity.getSummonedEntity());
            }
        } else if (stand.isValid()) {
            stand.remove();
        }
    }

    // ----- CLONE -----

    public EntityLiftedPassengerModule(EntityLiftedPassengerModule target) {
        this.rider = target.rider;
        this.stand = target.stand;
    }

    @Override
    public EntityLiftedPassengerModule clone() {
        return new EntityLiftedPassengerModule(this);
    }
}
