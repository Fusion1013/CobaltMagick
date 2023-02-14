package se.fusion1013.plugin.cobaltmagick.entity.create;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
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
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.state.CobaltState;
import se.fusion1013.plugin.cobaltcore.state.StateEngine;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.entity.EntityManager;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class GreenDragonlord {

    public static ICustomEntity register() {

        // Head Item
        ItemStack head = CustomItemManager.createPlayerHead("/give @p minecraft:player_head{display:{Name:\"{\\\"text\\\":\\\"Creeper Knight\\\"}\"},SkullOwner:{Id:[I;217357260,-579059199,-2139400198,-1986633351],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM0ZjQ4ODEwZGRjZGZjOGM3NmIxNDVjNTNiNzQ3MTAyYzc1NjU1NWFjNTVjNjQ4YjFmODIyNzgxNTA1NzNjMSJ9fX0=\"}]}}} 1");

        // Sword Item
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setCustomModelData(22);
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 7, true);
        swordMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swordMeta.setDisplayName(ChatColor.RESET + "Green Dragon Sword");
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

        // Create Entity
        ICustomEntity greenDragonlord = new CustomEntity.CustomEntityBuilder("green_dragonlord", EntityType.ZOMBIE)

                // Apply state engines
                .addExecuteOnTickModule(new EntityStateModule(createHealthStates()))

                // Set entity stats
                .addExecuteOnSpawnModule(new EntityHealthModule(1400).scaleHealth())
                .addEntityModification(entity -> {
                    if (entity instanceof Zombie zombie) {
                        zombie.setAdult();
                    }
                })

                // Potion Effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 0, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0, true, false)))

                // Add Equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, sword, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, head, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                // Bossbar
                .addExecuteOnTickModule(new EntityBossBarModule("Green Dragonlord", 100, BarColor.GREEN, BarStyle.SEGMENTED_10))

                // Drops
                .addExecuteOnDeathModule(new EntityDropModule(10000, 10,
                        new LootPool(1, new LootEntry(CustomItemManager.getCustomItem("green_dragon_sword").getItemStack(), 1, 1))
                ))

                // Sounds
                .addExecuteOnTickModule(new EntityAmbientSoundModule(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1, 41))

                // Attacks
                .addAbilityModule(new CasterAbility(2.2, 64, SpellManager.getSpell(91)))

                // Set general cooldown for abilities
                .setGeneralAbilityCooldown(0)

                .build();

        return CustomEntityManager.register(greenDragonlord);
    }

    // ----- HEALTH STATES -----

    private static StateEngine<CustomEntity> createHealthStates() {

        // Create Health States
        CobaltState<CustomEntity> healthState0 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .9, 1), 9);
        CobaltState<CustomEntity> healthState1 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .8, .9), 8);
        CobaltState<CustomEntity> healthState2 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .7, .8), 7);
        CobaltState<CustomEntity> healthState3 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .6, .7), 6);
        CobaltState<CustomEntity> healthState4 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .5, .6), 5);
        CobaltState<CustomEntity> healthState5 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .4, .5), 4);
        CobaltState<CustomEntity> healthState6 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .3, .4), 3);
        CobaltState<CustomEntity> healthState7 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .2, .3), 2);
        CobaltState<CustomEntity> healthState8 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, .1, .2), 1);
        CobaltState<CustomEntity> healthState9 = new CobaltState<>((entity, state) -> HighAlchemist.isBetweenPercentHealth(entity, 0, .1), 0);

        // Link health states
        healthState0.setConnectedStates(healthState1);
        healthState1.setConnectedStates(healthState2);
        healthState2.setConnectedStates(healthState3);
        healthState3.setConnectedStates(healthState4);
        healthState4.setConnectedStates(healthState5);
        healthState5.setConnectedStates(healthState6);
        healthState6.setConnectedStates(healthState7);
        healthState7.setConnectedStates(healthState8);
        healthState8.setConnectedStates(healthState9);

        // Set health executors
        healthState1.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 2, false));
        healthState2.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 4, false));
        healthState3.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 6, true));
        healthState4.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 8, false));
        healthState5.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 10, true));
        healthState6.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 10, false));
        healthState7.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 12, true));
        healthState8.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 14, true));
        healthState9.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 16, true));

        // Create Health Engine
        return new StateEngine<>(healthState0);

    }

    private static void executeEnterHealthState(CustomEntity entity, CobaltState<CustomEntity> state, int nSummons, boolean launch) {

        // Switch color of the bossbar for a few seconds
        EntityBossBarModule bossBarModule = entity.getTickExecutable(EntityBossBarModule.class);
        if (bossBarModule != null) {
            bossBarModule.getBossBar().getAdventureBossBar().color(BossBar.Color.RED);
            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> bossBarModule.getBossBar().getAdventureBossBar().color(BossBar.Color.GREEN), 20);
        }

        // Get entity info
        Location location = entity.getSummonedEntity().getLocation();
        World world = entity.getSummonedEntity().getWorld();

        // Play sound
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 3, 1);

        // Summon minions
        for (int i = 0; i < nSummons; i++) {
            CustomEntityManager.forceSummonEntity("green_dragon_minion", location);
        }

        // Give potion effects
        if (launch) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 10, false, false));
                    p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 10, 1, 1, 1, 0);
                }
            }
        }

    }

}
