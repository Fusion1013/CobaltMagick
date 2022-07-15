package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;

public class OrcArbalist {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Orc\\\"}\"},SkullOwner:{Id:[I;1955677293,968966992,-1519714176,411475657],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGViMjgxZjZhMTg1ZWY3ZWQ3NzhjOTQyNmIyZWVmMzk5Y2VkNWEwZWU3YzViNzM2NWQ3MzRjMjE4MjU1NmIxYSJ9fX0=\"}]}}} 1");

        // Crossbow Item
        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemMeta bowMeta = crossbow.getItemMeta();
        bowMeta.addEnchant(Enchantment.PIERCING, 5, true);
        bowMeta.addEnchant(Enchantment.QUICK_CHARGE, 4, true);
        bowMeta.addEnchant(Enchantment.MULTISHOT, 2, true);
        crossbow.setItemMeta(bowMeta);

        // Equipment
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)chest.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(36, 49, 71));
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        ICustomEntity orcArcher = new CustomEntity.CustomEntityBuilder("orc_arbalist", EntityType.PILLAGER)
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, crossbow, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))
                .build();

        return CustomEntityManager.register(orcArcher);

    }

}
