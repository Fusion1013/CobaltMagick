package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityHealthModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityPotionEffectModule;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.BrutalBlood;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.PartingGift;

public class Orc {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Orc\\\"}\"},SkullOwner:{Id:[I;1955677293,968966992,-1519714176,411475657],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGViMjgxZjZhMTg1ZWY3ZWQ3NzhjOTQyNmIyZWVmMzk5Y2VkNWEwZWU3YzViNzM2NWQ3MzRjMjE4MjU1NmIxYSJ9fX0=\"}]}}} 1");

        // Sword Item
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setCustomModelData(4);
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swordMeta.setDisplayName(ChatColor.RESET + "Cleaver");
        sword.setItemMeta(swordMeta);

        // Equipment
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)chest.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(60, 112, 0));
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        ICustomEntity orc = new CustomEntity.CustomEntityBuilder("orc", EntityType.ZOMBIE)

                .addAbilityModule(new BrutalBlood(5, 3, 1, 40))

                // Set stats
                .addExecuteOnSpawnModule(new EntityHealthModule(40))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, sword, 0.02f))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                // Set potion effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, true, false)))

                .build();

        return CustomEntityManager.register(orc);

    }

}
