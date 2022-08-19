package se.fusion1013.plugin.cobaltmagick.item.create;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;
import se.fusion1013.plugin.cobaltmagick.util.constants.ItemConstants;

import java.util.ArrayList;
import java.util.List;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

public class CreateExperienceOrb {

    public static CustomItem createExperienceOrb() {
        return register(new CustomItem.CustomItemBuilder("experience_orb", Material.EMERALD, 1)
                .setCustomName(HexUtils.colorify("&bExperience Orb"))
                .addLoreLine(HexUtils.colorify("&e0xp"))
                .addItemActivatorSync(ItemActivator.PLAYER_RIGHT_CLICK, ((iCustomItem, event, equipmentSlot) -> {

                    PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
                    Player player = interactEvent.getPlayer();
                    ItemStack item = player.getInventory().getItem(equipmentSlot);
                    ItemMeta itemMeta = item.getItemMeta();

                    int newValue = 0;

                    if (!player.isSneaking()) {

                        // Get experience from the orb
                        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                        int totalXpInOrb = container.getOrDefault(ItemConstants.EXPERIENCE_ORB_STORAGE, PersistentDataType.INTEGER, 0);

                        // Calculate xp needed to level up to the next level
                        int playerLevel = player.getLevel();
                        int xpToLevel = PlayerUtil.getExpToLevelUp(playerLevel);

                        int xpToUse = Math.min(xpToLevel, totalXpInOrb);

                        // Add xp to player
                        container.set(ItemConstants.EXPERIENCE_ORB_STORAGE, PersistentDataType.INTEGER, totalXpInOrb-xpToUse);
                        PlayerUtil.changePlayerExp(player, xpToUse);
                        newValue = totalXpInOrb-xpToUse;

                    } else {

                        // Put experience into the orb

                        // If player has no xp, return
                        int playerLevel = player.getLevel();
                        if (playerLevel <= 0) return;

                        // Calculate amount to put into the orb
                        int currentLevelXp = PlayerUtil.getExpAtLevel(playerLevel) - PlayerUtil.getExpAtLevel(playerLevel-1);

                        // Set new value
                        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                        newValue = container.getOrDefault(ItemConstants.EXPERIENCE_ORB_STORAGE, PersistentDataType.INTEGER, 0) + currentLevelXp;
                        container.set(ItemConstants.EXPERIENCE_ORB_STORAGE, PersistentDataType.INTEGER, newValue);
                        PlayerUtil.changePlayerExp(player, -currentLevelXp);
                    }

                    // Update lore
                    List<Component> newLore = new ArrayList<>();
                    newLore.add(Component.text(HexUtils.colorify("&e" + newValue + "xp")));
                    itemMeta.lore(newLore);

                    item.setItemMeta(itemMeta);

                }))
                .setCustomModel(1006).setItemCategory(MagickItemCategory.TOOL)
                .build());
    }

}
