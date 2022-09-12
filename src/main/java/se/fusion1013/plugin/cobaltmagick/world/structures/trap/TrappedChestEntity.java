package se.fusion1013.plugin.cobaltmagick.world.structures.trap;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;

public class TrappedChestEntity extends AbstractTrappedChest {

    // ----- VARIABLES -----

    private String summonEntity;

    private static final String TRIGGER_SOUND = "cobalt.poof";
    private static final ParticleGroup SPAWN_PARTICLES = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setExtra(new Particle.DustTransition(Color.RED, Color.ORANGE, 1))
                    .setDensity(20)
                    .setRadius(1.14)
                    .setCount(2)
                    .setOffset(new Vector(.1, .1, .1))
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.END_ROD)
                    .setDensity(20)
                    .setRadius(1.3)
                    .setCount(2)
                    .setOffset(new Vector(.1, .1, .1))
                    .setSpeed(.03)
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.FIREWORKS_SPARK)
                    .setDensity(20)
                    .setRadius(1.4)
                    .setCount(3)
                    .setOffset(new Vector(.1, .1, .1))
                    .setSpeed(.08)
                    .build())
            .build();

    // ----- CONSTRUCTORS -----

    public TrappedChestEntity(Location chestLocation, String summonEntity) {
        super(chestLocation);
        this.summonEntity = summonEntity;
    }

    // ----- TRIGGER -----

    @Override
    public void onTrigger(Object... args) {
        Location triggerLocation = (Location) args[0];
        if (!triggerLocation.toBlockLocation().equals(chestLocation.toBlockLocation())) return;

        super.onTrigger(args);

        // Summon the entity & remove the chest
        CustomEntityManager.forceSummonEntity(summonEntity, chestLocation);
        chestLocation.getBlock().setType(Material.AIR);

        // Play spawn effects
        SPAWN_PARTICLES.display(chestLocation);
        chestLocation.getWorld().playSound(chestLocation, TRIGGER_SOUND, 1, 1);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getObjectIdentifier() {
        return "trapped_chest_entity";
    }

    // ----- JSON STORAGE METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = super.toJson();
        jo.addProperty("entity", summonEntity);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        super.fromJson(jsonObject);
        summonEntity = jsonObject.get("entity").getAsString();
    }


    // ----- CLONE CONSTRUCTOR & METHOD -----

    public TrappedChestEntity(TrappedChestEntity target) {
        super(target);
        this.summonEntity = target.summonEntity;
    }

    @Override
    public TrappedChestEntity clone() {
        return new TrappedChestEntity(this);
    }
}
