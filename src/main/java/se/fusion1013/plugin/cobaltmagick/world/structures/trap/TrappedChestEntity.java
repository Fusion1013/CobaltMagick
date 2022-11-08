package se.fusion1013.plugin.cobaltmagick.world.structures.trap;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.*;

public class TrappedChestEntity extends AbstractTrappedChest {

    // ----- VARIABLES -----

    private String summonEntity;
    private List<String> chestContent = new ArrayList<>();

    private UUID summonedEntityUUID = null;

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

    // ----- LOADING / UNLOADING -----

    @Override
    public void onUnload() {
        super.onUnload();
        if (summonedEntityUUID != null) ObjectManager.removeStorageObject(uuid, getObjectIdentifier(), chestLocation.getChunk());
    }

    // ----- TRIGGER -----

    @Override
    public void onTrigger(Object... args) {
        switch ((String) args[0]) {
            case "chest_open" -> chestOpen(false, args);
            case "chest_break" -> chestOpen(true, args);
            case "entity_death" -> chestDeath(args);
        }
    }

    private void chestDeath(Object... args) {
        // Check if the entity that died was the chest entity
        ICustomEntity dead = (ICustomEntity) args[1];
        if (summonedEntityUUID == null) return;
        if (!summonedEntityUUID.equals(dead.getEntityUuid())) return;

        Location deathLocation = (Location) args[2];
        deathLocation.getBlock().setType(Material.CHEST);

        // Insert items into chest
        if (deathLocation.getBlock().getState() instanceof Container container) {
            CustomLootTable.insertItemsFromNames(new Random(), container, chestContent);
        }

        // Remove the object
        summonedEntityUUID = null;
        ObjectManager.removeStorageObject(uuid, getObjectIdentifier(), chestLocation.getChunk());
    }

    private void chestOpen(boolean broken, Object... args) {
        Location triggerLocation = (Location) args[1];
        if (!triggerLocation.toBlockLocation().equals(chestLocation.toBlockLocation())) return;

        // Summon the entity & remove the chest
        CustomEntity summonedEntity = CustomEntityManager.forceSummonEntity(summonEntity, chestLocation.toCenterLocation());
        summonedEntityUUID = summonedEntity.getEntityUuid();
        chestLocation.getBlock().setType(Material.AIR);

        // Decrease health of summoned entity to 4/6 if chest was broken
        if (broken) {
            if (summonedEntity.getSummonedEntity() instanceof LivingEntity living) {
                living.setHealth(living.getHealth() * (4/6.0));
            }
        }

        // Play spawn effects
        SPAWN_PARTICLES.display(chestLocation);
        chestLocation.getWorld().playSound(chestLocation, TRIGGER_SOUND, 1, 1);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getObjectIdentifier() {
        return "trapped_chest_entity";
    }

    @Override
    public void addItem(String key, Object value) {
        super.addItem(key, value);

        switch (key) {
            case "item" -> chestContent.add((String) value); // TODO: Use loot table instead
        }
    }

    @Override
    public void setValue(String key, Object value) {
        super.setValue(key, value);

        switch (key) {
            case "entity" -> summonEntity = (String) value;
        }
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public Argument<?>[] getListCommandArguments() {
        List<Argument> arguments = new ArrayList<>(List.of(super.getListCommandArguments()));
        arguments.add(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(CustomItemManager.getItemNames())));
        return arguments.toArray(new Argument[0]);
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        List<Argument> arguments = new ArrayList<>(List.of(super.getCommandArguments()));
        arguments.add(new StringArgument("entity").replaceSuggestions(ArgumentSuggestions.strings(CustomEntityManager.getInternalEntityNames())));
        return arguments.toArray(new Argument[0]);
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
        this.chestContent = target.chestContent;
        if (target.summonedEntityUUID != null) this.summonedEntityUUID = target.summonedEntityUUID;
    }

    @Override
    public TrappedChestEntity clone() {
        return new TrappedChestEntity(this);
    }
}
