package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class Apprentice {

    public static ICustomEntity register() {
        ItemStack wand = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setCustomModelData(1210);
        wand.setItemMeta(wandMeta);

        ICustomEntity apprentice = new CustomEntity.CustomEntityBuilder("apprentice", EntityType.ZOMBIE)
                .addAbilityModule(new CasterAbility(3, 10, SpellManager.SPARK_BOLT))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, wand, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, new ItemStack(Material.LEATHER_HELMET), 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE), 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS), 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, new ItemStack(Material.IRON_BOOTS), 0))
                .build();
        CustomEntityManager.register(apprentice);
        return apprentice;
    }

}
