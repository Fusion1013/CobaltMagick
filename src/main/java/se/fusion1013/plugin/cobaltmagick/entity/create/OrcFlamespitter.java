package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityHealthModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityPotionEffectModule;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class OrcFlamespitter {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Orc\\\"}\"},SkullOwner:{Id:[I;-1030531819,776225961,-1964451803,1814425405],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTg4NmQxYWI4YzIwZGU3ZjM0M2E2MWQwOGZlNWVjN2E0ZmNiZDNkYWY4NDA1NTkyNGYwZWFmYjdlYWUzNzEwMiJ9fX0=\"}]}}} 1");

        // Equipment
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)chest.getItemMeta();

        if (meta != null) {
            meta.setColor(Color.fromRGB(194, 84, 25));
            meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
        }
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        // Lingering Potion Item
        ItemStack potion = new ItemStack(Material.LINGERING_POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setColor(Color.fromRGB(194, 84, 25));
        potion.setItemMeta(potionMeta);


        ICustomEntity orcPyromancer = new CustomEntity.CustomEntityBuilder("orc_flamespitter", EntityType.ZOMBIE)

                // Set entity stats
                .addExecuteOnSpawnModule(new EntityHealthModule(50))

                // Potion Effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 0, true, false)))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD), 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.OFF_HAND, potion, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                // Attacks
                .addAbilityModule(new CasterAbility(10.2, 16, SpellManager.FIRESPITTER))

                // Set general cooldown for abilities
                .setGeneralAbilityCooldown(10) // 20 seconds
                .build();
        return CustomEntityManager.register(orcPyromancer);
    }

}
