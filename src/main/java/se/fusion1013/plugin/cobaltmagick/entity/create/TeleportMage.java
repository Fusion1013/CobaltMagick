package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityHealthModule;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class TeleportMage {

    public static ICustomEntity register() {
        ItemStack wand = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta wandMeta = (LeatherArmorMeta) wand.getItemMeta();
        wandMeta.setCustomModelData(1212);
        wandMeta.setColor(Color.AQUA);
        wand.setItemMeta(wandMeta);

        // Armor
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta armorMeta = (LeatherArmorMeta) chest.getItemMeta();
        armorMeta.setColor(Color.AQUA);
        chest.setItemMeta(armorMeta);
        leg.setItemMeta(armorMeta);
        boot.setItemMeta(armorMeta);

        // Armor
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Ghost\\\"}\"},SkullOwner:{Id:[I;-670785813,327369386,-1100489743,38163057],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDg3ZmZjOWJlYjI1MTdmOWUyNzRiM2I1MGRlOGQyNmQ5NjVlNGVjMTVlZWYyMGZiNzAyNTkwNTJlMGY2ZDhkIn19fQ==\"}]}}} 1");

        ICustomEntity teleportMage = new CustomEntity.CustomEntityBuilder("teleport_mage", EntityType.ZOMBIE)
                .addAbilityModule(new CasterAbility(7, 10, SpellManager.SWAPPER))
                .addAbilityModule(new CasterAbility(10, 10, SpellManager.SPEED_UP, SpellManager.TELEPORT_BOLT))
                .setGeneralAbilityCooldown(2)
                .addExecuteOnSpawnModule(new EntityHealthModule(50))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, wand, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))
                .build();
        CustomEntityManager.register(teleportMage);
        return teleportMage;
    }

}
