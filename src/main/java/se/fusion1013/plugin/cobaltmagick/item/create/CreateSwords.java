package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;

public class CreateSwords {

    public static void create() {}

    public static ICustomItem HEAVY_NETHERITE_MACHETE = CustomItemManager.register(new CobaltItem.Builder("heavy_netherite_machete")
            .material(Material.NETHERITE_SWORD)
            .itemName(HexUtils.colorify("&8Heavy Netherite Machete"))
            .itemActivatorSync(ItemActivator.PLAYER_HIT_ENTITY, (iCustomItem, event, equipmentSlot) -> {
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

                if (equipmentSlot != EquipmentSlot.HAND) return;

                Entity hitEntity = entityDamageByEntityEvent.getEntity();
                if (hitEntity instanceof LivingEntity living) {
                    EntityEquipment equipment = living.getEquipment();
                    if (equipment == null) return;

                    ItemStack[] armorContent = equipment.getArmorContents();
                    for (ItemStack item : armorContent) {
                        if (item == null) continue;
                        ItemMeta itemMeta = item.getItemMeta();
                        if (itemMeta instanceof Damageable damageable) {
                            CobaltMagick.getInstance().getLogger().info("Damage: " + damageable.getDamage());
                            damageable.setDamage(damageable.getDamage()+1);
                        }
                        item.setItemMeta(itemMeta);
                    }
                    if (!(equipment instanceof PlayerInventory)) equipment.setArmorContents(armorContent);
                }

                // TODO: Armor Crunch particle effects
            })
            .extraLore(HexUtils.colorify("&7Armor Crunch I"))
            .modelData(11).category(MagickItemCategory.TOOL)
            .build());

}
