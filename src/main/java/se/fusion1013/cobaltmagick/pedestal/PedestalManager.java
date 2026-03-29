package se.fusion1013.cobaltmagick.pedestal;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.PlayerUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;
import java.util.Random;

public class PedestalManager extends Manager<CobaltMagick> implements Listener {

    private static final Random random = new Random();
    public static final NamespacedKey NO_PICK_UP = new NamespacedKey(CobaltMagick.getInstance(), "item_pillar_no_pick_up");
    public static final NamespacedKey ITEM_PEDESTAL = new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal");

    public void create(Location location, String styleName) {
        PedestalStyle style = EnumUtils.findEnumInsensitiveCase(PedestalStyle.class, styleName);
        if (style == null) return;
        create(location, style);
    }

    public void create(Location location, PedestalStyle style) {
        World world = location.getWorld();
        location.getBlock().setBlockData(Material.BARRIER.createBlockData());

        ItemStack itemStack = new ItemStack(Material.CLOCK);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setItemModel(new NamespacedKey("thegreatwork", style.getModel()));
        itemStack.setItemMeta(meta);

        world.spawn(location.toCenterLocation(), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(itemStack);
            PersistentDataContainer ps = itemDisplay.getPersistentDataContainer();
            ps.set(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal"), PersistentDataType.INTEGER, 1);
        });

        world.playSound(location, Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1, 1);
        world.spawnParticle(Particle.BLOCK, location.toCenterLocation(), 10, .6, .6, .6, 0, Material.DEEPSLATE.createBlockData());
    }

    public ItemStack query(Location location) {
        Location itemLocation = location.clone().add(0, 1, 0);
        World world = location.getWorld();
        Collection<Item> itemEntities = world.getNearbyEntitiesByType(Item.class, itemLocation.toCenterLocation(), 0.5, item -> {
            PersistentDataContainer persistentDataContainer = item.getPersistentDataContainer();
            return persistentDataContainer.has(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal_item"));
        });
        if (itemEntities.isEmpty()) return null;
        Item firstItemEntity = itemEntities.iterator().next();
        return firstItemEntity.getItemStack();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        remove(block);
    }

    public static void remove(Block block) {
        if (block.getType() != Material.BARRIER) return;

        Location location = block.getLocation();
        World world = location.getWorld();
        block.setBlockData(Material.AIR.createBlockData());

        Collection<ItemDisplay> displayEntities = world.getNearbyEntitiesByType(ItemDisplay.class, location.toCenterLocation(), 0.5, itemDisplay -> {
            PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
            return persistentDataContainer.has(ITEM_PEDESTAL);
        });
        displayEntities.forEach(Entity::remove);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (clickedBlock == null) return;
        if (!isItemPedestal(clickedBlock.getLocation())) return;
        if (event.getHand().getGroup() != EquipmentSlotGroup.MAINHAND) return;

        event.setCancelled(true);

        Location location = clickedBlock.getLocation();
        ItemStack containedItem = query(location);
        Player player = event.getPlayer();
        player.swingHand(event.getHand());

        if (containedItem == null) {
            insertItem(location, event.getItem());
            PlayerUtil.reduceHeldItemStack(player, 1);
        } else if (!containedItem.getPersistentDataContainer().has(NO_PICK_UP)) {
            removeItem(location);
            insertItem(location, event.getItem());
            PlayerUtil.reduceHeldItemStack(player, 1);
            if (player.getGameMode() != GameMode.CREATIVE) player.give(containedItem);
        }
    }

    private void removeItem(Location location) {
        Location itemLocation = location.clone().add(0, 1, 0).toCenterLocation();
        World world = itemLocation.getWorld();

        Collection<Item> itemEntities = world.getNearbyEntitiesByType(Item.class, itemLocation.toCenterLocation(), 0.5, item -> {
            PersistentDataContainer persistentDataContainer = item.getPersistentDataContainer();
            return persistentDataContainer.has(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal_item"));
        });
        itemEntities.forEach(Entity::remove);
    }

    private void insertItem(Location location, @Nullable ItemStack item) {
        Location itemLocation = location.clone().add(0, 1, 0).toCenterLocation();
        World world = itemLocation.getWorld();

        if (item == null) {
            world.playSound(location, Sound.BLOCK_DECORATED_POT_INSERT_FAIL, random.nextFloat(0.9f, 1.1f), random.nextFloat(0.9f, 1.1f));
            return;
        }


        world.spawn(itemLocation, Item.class, itemEntity -> {
            ItemStack insertItem = item.clone();
            insertItem.setAmount(1);
            itemEntity.setItemStack(insertItem);
            itemEntity.setGlowing(true);
            itemEntity.setGravity(false);
            itemEntity.setCanMobPickup(false);
            itemEntity.setCanPlayerPickup(false);
            itemEntity.setVelocity(new Vector(0, 0, 0));
            itemEntity.setWillAge(false);

            itemEntity.getPersistentDataContainer().set(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal_item"), PersistentDataType.INTEGER, 1);
        });

        world.playSound(location, Sound.BLOCK_DECORATED_POT_INSERT, random.nextFloat(0.9f, 1.1f), random.nextFloat(0.9f, 1.1f));
    }

    public PedestalStyle getPedestalStyle(Location location) {
        if (!isItemPedestal(location)) return null;
        World world = location.getWorld();

        Collection<ItemDisplay> displayEntities = world.getNearbyEntitiesByType(ItemDisplay.class, location.toCenterLocation(), 0.5, itemDisplay -> {
            PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
            return persistentDataContainer.has(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal"));
        });
        if (displayEntities.isEmpty()) return null;

        ItemDisplay display = displayEntities.iterator().next();
        ItemStack itemStack = display.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getItemModel() == null) return null;

        String style = meta.getItemModel().value();
        return PedestalStyle.fromModel(style);
    }

    public boolean isItemPedestal(Location location) {
        Block block = location.getBlock();
        if (block.getType() != Material.BARRIER) return false;
        World world = location.getWorld();

        Collection<ItemDisplay> displayEntities = world.getNearbyEntitiesByType(ItemDisplay.class, location.toCenterLocation(), 0.5, itemDisplay -> {
            PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
            return persistentDataContainer.has(new NamespacedKey(CobaltMagick.getInstance(), "item_pedestal"));
        });
        return !displayEntities.isEmpty();
    }

    public PedestalManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }

    private static PedestalManager INSTANCE;

    public static PedestalManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PedestalManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
