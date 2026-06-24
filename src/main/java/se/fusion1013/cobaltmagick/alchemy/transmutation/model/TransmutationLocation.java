package se.fusion1013.cobaltmagick.alchemy.transmutation.model;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.shape.ShapeUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.Element;
import se.fusion1013.cobaltmagick.alchemy.cauldron.effect.CauldronEffectUtil;
import se.fusion1013.cobaltmagick.alchemy.transmutation.repository.ITransmutationRepository;
import se.fusion1013.cobaltmagick.alchemy.transmutation.util.TransmutationUtil;

import java.util.*;

public class TransmutationLocation {

    private static final Random random = new Random();
    private static final NamespacedKey PROCESSED_ITEM_KEY = new NamespacedKey(CobaltMagick.getInstance(), "transmuted_item");
    private static final int LIFESPAN_TICKS = 20 * 60 * 4;

    private final Location location;
    private final int maxCharge;
    private final boolean showStatusRedstoneBlock;

    private TransmutationState state = TransmutationState.WAITING_FOR_CATALYST;
    private final Map<Element, Integer> charges = new HashMap<>();
    private int currentTick = 0;
    private int totalCharge = 0;

    public TransmutationLocation(Location location, boolean showStatusRedstoneBlock) {
        this.location = location;
        maxCharge = 64 * 16;
        this.showStatusRedstoneBlock = showStatusRedstoneBlock;
    }

    public TransmutationLocation(Location location, int maxCharge, boolean showStatusRedstoneBlock) {
        this.location = location;
        this.maxCharge = maxCharge;
        this.showStatusRedstoneBlock = showStatusRedstoneBlock;

        location.getWorld().setBlockData(location.clone().add(0, -10, 0), Material.REDSTONE_BLOCK.createBlockData());
    }

    public void displayTick() {
        switch (state) {
            case WAITING_FOR_CATALYST -> tickWaitingDisplay();
            case TRANSMUTING -> tickTransmutingDisplay();
        }
    }

    private void tickWaitingDisplay() {
        World world = location.getWorld();
        world.spawnParticle(Particle.END_ROD, location.clone(), 4, .2, .2, .2, 0);
    }

    private void tickTransmutingDisplay() {
        World world = location.getWorld();
        List<Vector> animatedCircle = ShapeUtils.getAnimatedCircle(4, 3, 1, 0.001, 0.3);
        animatedCircle.forEach(point -> {
            world.spawnParticle(Particle.COPPER_FIRE_FLAME, location.clone().add(point), 1, 0, 0, 0, 0);
        });
        TransmutationUtil.playTransmutationSounds(location);

        TransmutationUtil.displayElementOrbit(location, charges.keySet().stream().toList(), currentTick, 4, 1.5, 0.08, 0.12, 0.78, 2);
        TransmutationUtil.playTransmutationSounds(location, currentTick, 2, 1, 3);
        currentTick++;
    }

    public void tick() {
        switch (state) {
            case WAITING_FOR_CATALYST -> tickWaiting();
            case TRANSMUTING -> {
                tickWaiting();
                tickTransmuting();
            }
            case DONE -> {
            }
        }

        if (currentTick > LIFESPAN_TICKS) {
            state = TransmutationState.DONE;
        }

        if (state == TransmutationState.DONE) {
            location.getWorld().setBlockData(location.clone().add(0, -10, 0), Material.AIR.createBlockData());
        }
    }

    private void tickWaiting() {
        World world = location.getWorld();
        Collection<Item> transmutationCatalyst = world.getNearbyEntitiesByType(Item.class, location, 4, item ->
                item.getItemStack().getPersistentDataContainer().has(new NamespacedKey(CobaltCore.getInstance(), "transmutation_catalyst")) && item.isOnGround()
        );

        if (transmutationCatalyst.isEmpty()) return;

        consumeItems(transmutationCatalyst);
        state = TransmutationState.TRANSMUTING;
    }

    private void tickTransmuting() {
        World world = location.getWorld();
        Collection<Item> items = world.getNearbyEntitiesByType(Item.class, location, 4, item ->
                !item.getPersistentDataContainer().has(PROCESSED_ITEM_KEY) && item.isOnGround()
        );

        for (Item item : items) {
            if (item == null) continue;

            String internalItemName = CustomItemManager.getInternalItemName(item.getItemStack());

            if (internalItemName == null) {
                markItemAsProcessed(item);
                continue;
            }

            List<Transmutation> transmutations = DataManager.getInstance().getDao(ITransmutationRepository.class).getTransmutationsFromInput(internalItemName);
            for (Transmutation transmutation : transmutations) {
                if (!charges.containsKey(transmutation.getElement())) continue;

                markItemAsProcessed(item);
                Integer charge = charges.get(transmutation.getElement());
                int consumeAmount = Math.min(item.getItemStack().getAmount() * transmutation.getCost(), charge / transmutation.getCost());

                transmute(item, transmutation, item.getItemStack().getAmount());

                if (charge - (consumeAmount * transmutation.getCost()) <= 0) {
                    charges.remove(transmutation.getElement());
                } else {
                    charges.put(transmutation.getElement(), charge - (consumeAmount * transmutation.getCost()));
                }

                if (charges.isEmpty() && totalCharge >= maxCharge) {
                    state = TransmutationState.DONE;
                }

                return;
            }
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

        world.spawnParticle(Particle.SMOKE, spawnLocation, 10, 0.2, 0.2, 0.2, 0);

        ItemStack result = CustomItemManager.getItemStack(transmutation.getOutputItem());
        result.setAmount(amount);
        CauldronEffectUtil.animateCauldron(spawnLocation, (center) -> {
            // Final burst effect
            world.spawnParticle(Particle.FLASH, center.clone().add(0, 2.5, 0), 1, Color.WHITE);
            world.spawnParticle(Particle.END_ROD, center.clone().add(0, 2.5, 0), 10, .1, .1, .1, 0);
            world.playSound(center, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1.2f);

            Item dropped = world.spawn(center.clone().add(0, 2, 0), Item.class, spawnedItem -> {
                spawnedItem.setItemStack(result.clone());
            });
        }, CobaltMagick.getInstance(), false);
    }

    private void markItemAsProcessed(Item item) {
        item.getPersistentDataContainer().set(PROCESSED_ITEM_KEY, PersistentDataType.BOOLEAN, true);
    }

    private void consumeItems(Collection<Item> items) {
        if (totalCharge >= maxCharge) return;

        for (Item item : items) {
            String internalItemName = CustomItemManager.getInternalItemName(item.getItemStack());

            Element element = Element.getFromString(internalItemName); // Terrible
            if (element == null) continue;

            int current = charges.getOrDefault(element, 0);

            int addedCharge = item.getItemStack().getAmount() * 64;
            totalCharge += addedCharge;
            int newCharge = current + addedCharge;

            charges.put(element, newCharge);
            World world = item.getWorld();
            world.spawnParticle(Particle.POOF, item.getLocation(), 3, .1, .1, .1, 0);
            item.remove();

            world.playSound(
                    location,
                    "thegreatwork:sfx.ominous_woosh_2",
                    1,
                    1 + random.nextFloat() * 0.12f
            );

            if (totalCharge > maxCharge) return;
        }
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
