package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.util.BlockUtil;
import se.fusion1013.plugin.cobaltmagick.util.constants.ItemConstants;

import java.util.Random;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

public class ShinyOrb {

    public static CustomItem create() {
        return register(new CustomItem.CustomItemBuilder("shiny_orb", Material.EMERALD, 1)
                .setCustomName(HexUtils.colorify("&r&6Shiny Orb"))
                .addLoreLine(HexUtils.colorify("&e&oIt hums slightly. What happens if you throw it, you wonder..."))
                .addItemActivator(ItemActivator.PLAYER_DROP_CUSTOM_ITEM, (item, event, slot) -> {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {

                        // Get the dropped item
                        PlayerDropItemEvent dropEvent = (PlayerDropItemEvent) event;
                        Item droppedItem = dropEvent.getItemDrop();

                        if (!droppedItem.isValid()) return; // If the item is no longer on the ground, return

                        Location location = droppedItem.getLocation();
                        Random r = new Random();

                        // TODO: Keep track of how many times the item has been thrown, and increase explosion risk
                        // Set persistent data container
                        ItemMeta meta = droppedItem.getItemStack().getItemMeta();
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        if (!container.has(ItemConstants.SHINY_ORB_USES)) container.set(ItemConstants.SHINY_ORB_USES, PersistentDataType.INTEGER, 0);
                        int currentUses = container.get(ItemConstants.SHINY_ORB_USES, PersistentDataType.INTEGER);
                        container.set(ItemConstants.SHINY_ORB_USES, PersistentDataType.INTEGER, currentUses+1);
                        droppedItem.getItemStack().setItemMeta(meta);

                        // Check if the orb should drop gold or explode (25% to explode)
                        if (r.nextDouble() < 0.25 + (.1*currentUses)) {
                            BlockUtil.createExplosion(location, location.getWorld(), 6, true, true, true);
                            return;
                        }

                        // Drop Gold
                        int limit = r.nextInt(1, 16);

                        for (int i = 0; i < limit; i++) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {

                                // Pick the item to spawn randomly
                                ItemStack itemSpawn;
                                double itemSpawnRandom = r.nextDouble();
                                if (itemSpawnRandom < 0.4 - (currentUses/20.0)) itemSpawn = new ItemStack(Material.GOLD_NUGGET);
                                else if (itemSpawnRandom < .98 - (currentUses/100.0)) itemSpawn = new ItemStack(Material.GOLD_INGOT);
                                else itemSpawn = ItemManager.GOLD_COIN.getItemStack();

                                // Spawn the item and play a sound
                                location.getWorld().dropItemNaturally(location.clone().add(new Vector(-.5, -.5, -.5)), itemSpawn);
                                location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.PLAYERS, 1, 1);
                            }, r.nextInt(0, 20));
                        }

                    }, 40); // Activate after 2 seconds
                })
                .setCustomModel(41)
                .build());
    }

}
