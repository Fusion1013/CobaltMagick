package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityEquipmentModule;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class Apprentice {

    public static ICustomEntity register() {
        ItemStack wand = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        LeatherArmorMeta wandMeta = (LeatherArmorMeta) wand.getItemMeta();
        wandMeta.setCustomModelData(1210);
        wandMeta.setColor(Color.PURPLE);
        wand.setItemMeta(wandMeta);

        // Head
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Goddess Helmet\\\"}\"},SkullOwner:{Id:[I;-530167655,1443512997,-1703327574,516093464],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNhYWY2OTUzZTc5Y2UxYTE5MGQ5YjUzZDFlYWQ4OGExMTBmMjQxMTY5YmI1OThhMTdhMzhjNmQ5NzJlZmEyOSJ9fX0=\"}]}}} 1");

        // Armor
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta armorMeta = (LeatherArmorMeta) chest.getItemMeta();
        armorMeta.setColor(Color.PURPLE);
        chest.setItemMeta(armorMeta);
        leg.setItemMeta(armorMeta);
        boot.setItemMeta(armorMeta);

        ICustomEntity apprentice = new CustomEntity.CustomEntityBuilder("apprentice", EntityType.ZOMBIE)
                .addAbilityModule(new CasterAbility(3, 10, "spark_bolt"))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, wand, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))
                .build();
        CustomEntityManager.register(apprentice);
        return apprentice;
    }

}
