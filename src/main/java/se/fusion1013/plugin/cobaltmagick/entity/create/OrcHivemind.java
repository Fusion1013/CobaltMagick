package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.*;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;

public class OrcHivemind {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Orc\\\"}\"},SkullOwner:{Id:[I;1955677293,968966992,-1519714176,411475657],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGViMjgxZjZhMTg1ZWY3ZWQ3NzhjOTQyNmIyZWVmMzk5Y2VkNWEwZWU3YzViNzM2NWQ3MzRjMjE4MjU1NmIxYSJ9fX0=\"}]}}} 1");

        // Sword Item
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setCustomModelData(5);
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swordMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        swordMeta.setDisplayName(ChatColor.RESET + "Broadsword");
        sword.setItemMeta(swordMeta);

        // Equipment
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)chest.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(213, 235, 52));
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        ICustomEntity orc = new CustomEntity.CustomEntityBuilder("orc_hivemind", EntityType.ZOMBIFIED_PIGLIN)

                // Set stats
                .addExecuteOnSpawnModule(new EntityHealthModule(50))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, sword, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                // Loot
                .addExecuteOnDeathModule(new EntityDropModule(sword, 0.002f))

                // Set potion effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, true, false)))

                // Set aggro as default
                .addExecuteOnSpawnModule(new ISpawnExecutable() {
                    @Override
                    public ISpawnExecutable clone() {
                        return this;
                    }

                    @Override
                    public void execute(CustomEntity customEntity, ISpawnParameters iSpawnParameters) {
                        if (customEntity.getSummonedEntity() instanceof PigZombie pigZombie) {
                            pigZombie.setAnger(999999);
                            pigZombie.setTarget(PlayerUtil.getClosestPlayer(pigZombie.getLocation(), GameMode.SURVIVAL));
                        }
                    }
                })

                .build();

        return CustomEntityManager.register(orc);

    }

}
