package se.fusion1013.plugin.cobaltmagick.entity.modules;

import org.bukkit.entity.LivingEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;

public class EntityKillTimerModule extends EntityModule implements ITickExecutable {

    int tick;

    public EntityKillTimerModule(int timer) {
        this.tick = timer;
    }

    public EntityKillTimerModule(EntityKillTimerModule target) {
        this.tick = target.tick;
    }

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        if (tick <= 0) {
            if (customEntity.getSummonedEntity() instanceof LivingEntity living) living.setHealth(0);
        } else {
            tick--;
        }
    }

    @Override
    public EntityKillTimerModule clone() {
        return new EntityKillTimerModule(this);
    }
}
