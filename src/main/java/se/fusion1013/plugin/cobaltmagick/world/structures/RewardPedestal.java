package se.fusion1013.plugin.cobaltmagick.world.structures;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IActivatorStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.ObjectManager;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class RewardPedestal implements IActivatorStorageObject {

    // ----- VARIABLES -----

    // -- Internals
    private UUID uuid;
    private Location location;

    // -- Pedestals
    private List<String> itemOffers = new ArrayList<>();
    private List<Location> pedestalLocations = new ArrayList<>();

    // -- Activatable
    private List<UUID> activatables = new ArrayList<>();

    // -- Instance variables
    private List<LivingEntity> triggerSheep = new ArrayList<>();
    private List<ArmorStand> pedestalModels = new ArrayList<>();
    private List<Item> displayItems = new ArrayList<>();

    // -- Particles
    private static final ParticleGroup SMALL_PORTAL_PARTICLE = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.SPELL_WITCH)
                    .setCount(1)
                    .setSpeed(0)
                    .setDensity(1)
                    .setRadius(.5)
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setExtra(new Particle.DustTransition(Color.PURPLE, Color.MAROON, 1))
                    .setCount(3)
                    .setSpeed(.8)
                    .setDensity(4)
                    .setRadius(1)
                    .build())
            .build();

    // ----- CONSTRUCTORS -----

    public RewardPedestal() {}

    // ----- TRIGGER -----

    @Override
    public void onTrigger(Object... args) {
        switch ((String) args[0]) {
            case "player_move" -> playerMove((PlayerMoveEvent) args[1]);
            case "entity_hit" -> entityHit((EntityDamageByEntityEvent) args[1]);
        }
    }

    private void playerMove(PlayerMoveEvent event) {
        if (pedestalLocations.isEmpty()) return;
        if (itemOffers.isEmpty()) return;

        Collection<Player> players = location.getNearbyPlayers(10);
        if (players.size() <= 0) {
            // Remove display items
            displayItems.forEach(Entity::remove);
            displayItems.clear();
        } else {
            if (displayItems.isEmpty()) {
                // Spawn display item
                for (int i = 0; i < pedestalLocations.size(); i++) {

                    if (itemOffers.size() <= i) return;

                    Location location = pedestalLocations.get(i);
                    String itemOffer = itemOffers.get(i);

                    Item displayItem = location.getWorld().spawn(location.toCenterLocation().add(0, 1, 0), Item.class, item -> {
                        item.setItemStack(CustomItemManager.getItemStack(itemOffer));
                        item.setCanPlayerPickup(false);
                        item.setCanMobPickup(false);
                        item.setGravity(false);
                        item.setVelocity(new Vector());
                        item.setGlowing(true);
                    });
                    displayItems.add(displayItem);
                }
            }
        }
    }

    private void entityHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        for (int i = 0; i < triggerSheep.size(); i++) {
            LivingEntity sheep = triggerSheep.get(i);

            if (sheep.getUniqueId().equals(entity.getUniqueId())) {

                Item selectedItem = displayItems.get(i);
                displayItems.remove(selectedItem);

                selectedItem.setCanPlayerPickup(true);
                selectedItem.setGravity(true);
                selectedItem.setVelocity(new Vector(0, .3, 0));

                // Particles
                SMALL_PORTAL_PARTICLE.display(selectedItem.getLocation());
                selectedItem.getWorld().playSound(selectedItem.getLocation(), Sound.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 1, 1);

                // Remove all display items
                displayItems.forEach(Entity::remove);
                displayItems.clear();

                // Remove all trigger entities
                triggerSheep.forEach(Entity::remove);
                triggerSheep.clear();

                pedestalModels.clear();

                // Activate activatables
                for (UUID uuid : activatables) {
                    IActivatableStorageObject object = ObjectManager.getLoadedActivatableObject(uuid);
                    if (object != null) object.activate(this);
                }

                // Remove from storage
                ObjectManager.removeStorageObject(uuid, getObjectIdentifier(), location.getChunk());
                return;
            }
        }
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            for (int i = 0; i < pedestalLocations.size(); i++) {

                if (itemOffers.size() <= i) return;

                Location location = pedestalLocations.get(i);

                // Spawn pedestal model
                ArmorStand stand = location.getWorld().spawn(location.clone().add(.5, 0, .5), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setMarker(true);
                    armorStand.setGravity(false);
                    armorStand.setInvulnerable(true);

                    // Create pillar item
                    ItemStack pillar = new ItemStack(Material.CLOCK);
                    ItemMeta meta = pillar.getItemMeta();
                    meta.setCustomModelData(10006);
                    pillar.setItemMeta(meta);
                    armorStand.setItem(EquipmentSlot.HEAD, pillar);
                });
                pedestalModels.add(stand);

                // Spawn trigger sheep
                Collection<LivingEntity> nearby = location.getNearbyLivingEntities(.5);
                nearby.forEach(living -> {
                    if (living instanceof Sheep) living.remove(); // Kill old sheep if they are still left
                });
                Sheep sheep = location.getWorld().spawn(location.toCenterLocation().add(0, 1, 0), Sheep.class, sheepEntity -> {
                    sheepEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000*20, 1, true, false));
                    sheepEntity.setSheared(true);
                    sheepEntity.setSilent(true);
                    sheepEntity.setAI(false);
                    sheepEntity.setGravity(false);
                });
                triggerSheep.add(sheep);

            }
        }, 0);
    }

    @Override
    public void onUnload() {
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            // Remove all armor stands
            pedestalModels.forEach(Entity::remove);
            pedestalModels.clear();

            // Remove all display items
            displayItems.forEach(Entity::remove);
            displayItems.clear();

            // Remove all trigger entities
            triggerSheep.forEach(Entity::remove); // TODO: This does not seem to work
            triggerSheep.clear();
        }, 0);
    }

    // TODO:
    /*
    On Load:
    - Spawn pedestal block models
    - Spawn hovering items above pedestals
    - Spawn trigger sheep, with 1 health, no sound

    On Unload:
    - Remove pedestal block models
    - Remove hovering items above pedestals
    - Remove trigger sheep
     */

    // ----- TRIGGER -----

    // TODO
    /*
    Trigger when an entity dies. Pass event as argument to trigger. Do the following:
    - Check if the entity that was killed is one of the trigger sheep.
    - If the trigger sheep was not killed by a player, cancel the event
    - If the trigger sheep was killed by a player;
        - Spawn chosen item entity
        - Remove display items
        - Remove trigger entities
        - Run pedestal remove animation (Sink into ground??)
     */

    // ----- ACTIVATABLE -----

    @Override
    public void addActivatable(UUID uuid) {
        activatables.add(uuid);
    }

    @Override
    public void removeActivatable(UUID uuid) {
        activatables.remove(uuid);
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {

    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
        };
    }

    @Override
    public Argument<?>[] getListCommandArguments() {
        return new Argument[] {
                new LocationArgument("pedestal_location", LocationType.BLOCK_POSITION),
                new StringArgument("pedestal_item").replaceSuggestions(ArgumentSuggestions.strings(CustomItemManager.getItemNames()))
        };
    }

    // ----- JSON INTEGRATION -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(location));

        JsonArray pLocs = new JsonArray();
        for (Location location : pedestalLocations) pLocs.add(JsonUtil.toJson(location));
        jo.add("pedestal_locations", pLocs);

        JsonArray iOffs = new JsonArray();
        for (String item : itemOffers) iOffs.add(item);
        jo.add("item_offers", iOffs);

        JsonArray activs = new JsonArray();
        for (UUID uuid : activatables) activs.add(uuid.toString());
        jo.add("activatables", activs);

        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        this.location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));

        JsonArray pLocs = jsonObject.getAsJsonArray("pedestal_locations");
        for (int i = 0; i < pLocs.size(); i++) pedestalLocations.add(JsonUtil.toLocation(pLocs.get(i).getAsJsonObject()));

        JsonArray iOffs = jsonObject.getAsJsonArray("item_offers");
        for (int i = 0; i < iOffs.size(); i++) itemOffers.add(iOffs.get(i).getAsString());

        JsonArray activs = jsonObject.getAsJsonArray("activatables");
        for (int i = 0; i < activs.size(); i++) activatables.add(UUID.fromString(activs.get(i).getAsString()));
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void addItem(String key, Object value) {
        switch (key) {
            case "pedestal_location" -> pedestalLocations.add((Location) value);
            case "pedestal_item" -> itemOffers.add((String) value);
        }

        // Reload the object
        onUnload();
        onLoad();
    }

    @Override
    public void setValue(String s, Object o) {

    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();

        info.add("UUID: " + uuid.toString());
        info.add("Location: " + location.toVector());

        info.add("Reward Items:");
        for (String s : itemOffers) info.add(" - " + s);

        info.add("Pedestal Locations:");
        for (Location location : pedestalLocations) info.add(" - " + location.toVector());

        info.add("Activates:");
        for (UUID uuid : activatables) info.add(" - " + uuid.toString());

        return info;
    }

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectIdentifier() {
        return "reward_pedestal";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    // ----- CLONE METHOD & CONSTRUCTOR -----

    public RewardPedestal(RewardPedestal target) {
        this.uuid = target.uuid;
        this.location = target.location;

        this.itemOffers = target.itemOffers;
        this.pedestalLocations = target.pedestalLocations;

        this.activatables = target.activatables;

        this.triggerSheep = target.triggerSheep;
        this.displayItems = target.displayItems;
        this.pedestalModels = target.pedestalModels;
    }

    @Override
    public RewardPedestal clone() {
        return new RewardPedestal();
    }

}
