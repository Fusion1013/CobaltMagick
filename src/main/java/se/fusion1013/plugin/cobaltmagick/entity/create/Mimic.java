package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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
import se.fusion1013.plugin.cobaltcore.entity.modules.*;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.ChargeAbility;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.Enderport;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.item.create.CreateKeyItems;

public class Mimic {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Mimic Pet (chest)\\\"}\"},SkullOwner:{Id:[I;2124186578,1952530906,-1212835149,-924549217],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjMjg5NWZkYmI4ZjY5OGQ3YWFjNDA4Y2E2NDdmN2I1YzE2ZjNjYTE5NjY5ZjQyNTRlM2E5MjIxYjZiMzAifX19\"}]}}} 1");

        // Sword Item
        ItemStack weapon = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        weaponMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        weapon.setItemMeta(weaponMeta);

        // Equipment
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)chest.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(67, 47, 18));
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        ICustomEntity mimic = new CustomEntity.CustomEntityBuilder("mimic", EntityType.HUSK)

                // Set stats
                .addExecuteOnSpawnModule(new EntityHealthModule(200))
                .addExecuteOnTickModule(new EntityBossBarModule("Mimic", 32, BarColor.YELLOW, BarStyle.SEGMENTED_6))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, weapon, 0.02f))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                .addExecuteOnTickModule(new EntityAmbientSoundModule(Sound.BLOCK_CHEST_LOCKED, 1, 1, 121))
                .addExecuteOnTickModule(new EntityAmbientSoundModule(Sound.BLOCK_CHEST_OPEN, 1, 1, 56))

                // Set potion effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 2, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, true, false)))

                // Abilities
                .addAbilityModule(
                        new ChargeAbility(6, 1, 8)
                                .damage(7)
                )
                .addAbilityModule(
                        new Enderport(8)
                )

                // Drops
                .addExecuteOnDeathModule(new EntityDropModule(
                        new LootPool(3,
                                new LootEntry(CustomItemManager.getCustomItemStack("rusty_key"), 1, 1),
                                new LootEntry(new ItemStack(Material.OAK_PLANKS), 1, 8),
                                new LootEntry(new ItemStack(Material.OAK_LOG), 1, 2)
                        )
                ))

                .build();

        return CustomEntityManager.register(mimic);

    }

}
