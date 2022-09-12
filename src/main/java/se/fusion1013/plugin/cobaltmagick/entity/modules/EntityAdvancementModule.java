package se.fusion1013.plugin.cobaltmagick.entity.modules;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.IDeathExecutable;
import se.fusion1013.plugin.cobaltcore.entity.modules.ISpawnExecutable;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.AdvancementGranter;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;

public class EntityAdvancementModule extends EntityMagickModule implements ISpawnExecutable, IDeathExecutable {

    // ----- VARIABLES -----

    AdvancementGranter granter;

    // ----- CONSTRUCTORS -----

    public EntityAdvancementModule(AdvancementGranter granter) {
        this.granter = granter;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {
        if (customEntity.getLocation() != null) granter.execute(customEntity.getLocation());
        else if (customEntity.getSpawnLocation() != null) granter.execute(customEntity.getSpawnLocation());
    }

    // ----- CLONE -----

    public EntityAdvancementModule(EntityAdvancementModule target) {
        this.granter = target.granter;
    }

    @Override
    public EntityAdvancementModule clone() {
        return new EntityAdvancementModule(this);
    }
}
