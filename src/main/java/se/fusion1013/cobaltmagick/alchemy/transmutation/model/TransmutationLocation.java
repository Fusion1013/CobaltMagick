package se.fusion1013.cobaltmagick.alchemy.transmutation.model;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.transmutation.repository.ITransmutationRepository;
import se.fusion1013.cobaltmagick.alchemy.transmutation.util.TransmutationUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransmutationLocation {

    private static final NamespacedKey PROCESSED_ITEM_KEY = new NamespacedKey(CobaltMagick.getInstance(), "transmuted_item");

    private final Location location;

    private TransmutationState state = TransmutationState.WAITING_FOR_CATALYST;
    private final Map<String, Integer> charges = new HashMap<>();

    public TransmutationLocation(Location location) {
        this.location = location;
    }

    public void displayTick() {
        switch (state) {
            case WAITING_FOR_CATALYST -> tickWaitingDisplay();
            case TRANSMUTING -> tickTransmutingDisplay();
        }
    }

    private void tickWaitingDisplay() {
        World world = location.getWorld();
        world.spawnParticle(Particle.END_ROD, location.clone().add(0, 1, 0), 4, .2, .2, .2, 0);
    }

    private void tickTransmutingDisplay() {
        World world = location.getWorld();
        List<Vector> animatedCircle = ShapeUtils.getAnimatedCircle(4, 3, 1, 0.001, 0.3);
        animatedCircle.forEach(point -> {
            world.spawnParticle(Particle.COPPER_FIRE_FLAME, location.clone().add(point.getX(), point.getY() + 0.5, point.getZ()), 1, 0, 0, 0, 0);
        });
        TransmutationUtil.playTransmutationSounds(location);
    }

    public void tick() {
        switch (state) {
            case WAITING_FOR_CATALYST -> tickWaiting();
            case TRANSMUTING -> tickTransmuting();
            case DONE -> {
            }
        }
    }

    private void tickWaiting() {
        World world = location.getWorld();
        Collection<Item> transmutationCatalyst = world.getNearbyEntitiesByType(Item.class, location, 4, item ->
                item.getItemStack().getPersistentDataContainer().has(new NamespacedKey(CobaltCore.getInstance(), "transmutation_catalyst"))
        );

        if (transmutationCatalyst.isEmpty()) return;

        consumeItems(transmutationCatalyst);
        state = TransmutationState.TRANSMUTING;
    }

    private void tickTransmuting() {
        World world = location.getWorld();
        Collection<Item> items = world.getNearbyEntitiesByType(Item.class, location, 4, item ->
                !item.getPersistentDataContainer().has(PROCESSED_ITEM_KEY)
        );

        Item item = items.stream().findFirst().orElse(null);

        if (item == null) return;

        String internalItemName = CustomItemManager.getInternalItemName(item.getItemStack());

        if (internalItemName == null) {
            markItemAsProcessed(item);
            return;
        }

        List<Transmutation> transmutations = DataManager.getInstance().getDao(ITransmutationRepository.class).getTransmutationsFromInput(internalItemName);
        for (Transmutation transmutation : transmutations) {
            if (!charges.containsKey(transmutation.getCatalyst())) continue;

            markItemAsProcessed(item);
            Integer charge = charges.get(transmutation.getCatalyst());
            int consumeAmount = Math.min(item.getItemStack().getAmount() * transmutation.getCost(), charge / transmutation.getCost());

            transmute(item, transmutation, consumeAmount);

            if (charge - (consumeAmount * transmutation.getCost()) <= 0) {
                charges.remove(transmutation.getCatalyst());
            } else {
                charges.put(transmutation.getCatalyst(), charge - (consumeAmount * transmutation.getCost()));
            }
        }

        if (charges.isEmpty()) {
            state = TransmutationState.DONE;
        }
    }

    private void transmute(Item item, Transmutation transmutation, int amount) {
        World world = item.getWorld();
        Location spawnLocation = item.getLocation();

        if (item.getItemStack().getAmount() > amount) {
            item.getItemStack().setAmount(item.getItemStack().getAmount() - amount);
        } else {
            item.remove();
        }

        ItemStack result = CustomItemManager.getItemStack(transmutation.getOutputItem());
        for (int i = 0; i < amount; i++) {
            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
                ItemStack spawnItem = result.clone();
                spawnItem.setAmount(1);

                world.dropItem(spawnLocation, spawnItem, spawnedItem -> {
                    spawnedItem.getPersistentDataContainer().set(PROCESSED_ITEM_KEY, PersistentDataType.BOOLEAN, true);
                });
                world.spawnParticle(Particle.POOF, spawnLocation, 1, 0.1, 0.1, 0.1, 0);
            }, i);
        }
    }

    private void markItemAsProcessed(Item item) {
        item.getPersistentDataContainer().set(PROCESSED_ITEM_KEY, PersistentDataType.BOOLEAN, true);
    }

    private void consumeItems(Collection<Item> items) {
        items.forEach(item -> {
            String internalItemName = CustomItemManager.getInternalItemName(item.getItemStack());
            int current = charges.getOrDefault(internalItemName, 0);
            charges.put(internalItemName, current + item.getItemStack().getAmount() * 64);
        });

        items.forEach(item -> {
            item.getWorld().spawnParticle(Particle.POOF, item.getLocation(), 3, .1, .1, .1, 0);
        });
        items.forEach(Entity::remove);
    }

    public Location getLocation() {
        return location;
    }

    public void setState(TransmutationState state) {
        this.state = state;
    }

    public TransmutationState getState() {
        return state;
    }
}
