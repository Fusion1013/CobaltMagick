package se.fusion1013.plugin.cobaltmagick.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;

public class PartingGift extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    private int fuseTicks;
    private boolean fire;

    // ----- CONSTRUCTORS -----

    public PartingGift(int fuseTicks, boolean fire) {
        super(0);

        this.fuseTicks = fuseTicks;
        this.fire = fire;
    }

    // ----- EXECUTE -----

    @Override
    public boolean attemptAbility(CustomEntity entity, ISpawnParameters spawnParameters) {
        return false;
}

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {}

    @Override
    public void onEntityDeath(CustomEntity entity, ISpawnParameters spawnParameters, Location location, Entity dyingEntity) {
        location.getWorld().spawn(location, TNTPrimed.class, tntPrimed -> {
            tntPrimed.setFuseTicks(fuseTicks);
            tntPrimed.setIsIncendiary(fire);
        });
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getAbilityName() {
        return "Parting Gift";
    }

    @Override
    public String getAbilityDescription() {
        return "Explodes on death";
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public PartingGift(PartingGift target) {
        super(target);

        this.fuseTicks = target.fuseTicks;
        this.fire = target.fire;
    }

    @Override
    public PartingGift clone() {
        return new PartingGift(this);
    }
}
