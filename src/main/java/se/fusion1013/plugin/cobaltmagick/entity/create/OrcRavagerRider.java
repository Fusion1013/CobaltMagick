package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.entity.EntityType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltmagick.entity.EntityManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.EntityLiftedPassengerModule;

public class OrcRavagerRider {

    public static ICustomEntity register() {

        ICustomEntity orcRavagerRider = new CustomEntity.CustomEntityBuilder("orc_ravager_rider", EntityType.RAVAGER)
                // Put the rider on the ravager
                .addExecuteOnTickModule(new EntityLiftedPassengerModule(EntityManager.ORC_ARBALIST))
                .build();

        return CustomEntityManager.register(orcRavagerRider);

    }

}
