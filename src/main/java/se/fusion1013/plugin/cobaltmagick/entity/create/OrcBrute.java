package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityHealthModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityPotionEffectModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.ChargeAbility;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;

import java.util.UUID;

public class OrcBrute {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Orc\\\"}\"},SkullOwner:{Id:[I;1031173686,1427721963,-1818151168,-828994218],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQyYWU3NmEzN2I1YmY3YzM3ODgwYjEwNjEwOTQ1NTFiYTE1YjQ2ZDUwNzhmMDYzOWY5ZGM0MjQ5NDRkOTAwOCJ9fX0=\"}]}}} 1");

        // Sword Item
        ItemStack sword = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setCustomModelData(6);
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swordMeta.setDisplayName(ChatColor.RESET + "Hillebard");
        sword.setItemMeta(swordMeta);

        // Equipment
        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        chest.setItemMeta(chestMeta);

        ICustomEntity orc_brute = new CustomEntity.CustomEntityBuilder("orc_brute", EntityType.ZOMBIE)

                // Set stats
                .addExecuteOnSpawnModule(new EntityHealthModule(100))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, sword, 0.02f))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS), 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, new ItemStack(Material.IRON_BOOTS), 0))

                // Set potion effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 3, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 1, true, false)))

                // Abilities
                .addAbilityModule(new ChargeAbility(12, 1, 5))

                .build();

        return CustomEntityManager.register(orc_brute);

    }

}
