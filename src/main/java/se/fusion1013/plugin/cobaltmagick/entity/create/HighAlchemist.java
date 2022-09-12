package se.fusion1013.plugin.cobaltmagick.entity.create;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.*;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.SummonerAbility;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.state.CobaltState;
import se.fusion1013.plugin.cobaltcore.state.StateEngine;
import se.fusion1013.plugin.cobaltcore.state.TimedState;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.AdvancementGranter;
import se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand.SentientWandParameters;
import se.fusion1013.plugin.cobaltmagick.entity.modules.EntityAdvancementModule;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.item.loot.WandLootEntry;
import se.fusion1013.plugin.cobaltmagick.entity.EntityManager;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.EffectModule;
import se.fusion1013.plugin.cobaltmagick.util.constants.BookConstants;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class HighAlchemist {

    // ----- VARIABLES -----

    // Field
    static double shieldRadius = 5;
    static double damageFieldRadius = 2;

    // Wand
    static Wand wand = new Wand(false, 1, 0, 0, 1000, 1000, 9, 0, new ArrayList<>(), 0);

    // ----- REGISTER -----

    public static ICustomEntity register() {

        // Equipment
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)helmet.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(9, 25, 61));
        helmet.setItemMeta(meta);
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
        sword.setItemMeta(swordMeta);

        // Create Entity
        ICustomEntity highAlchemist = new CustomEntity.CustomEntityBuilder("high_alchemist", EntityType.ZOMBIE)

                // Apply state engines
                .addExecuteOnTickModule(new EntityStateModule(createShieldState())) // Shield state
                .addExecuteOnTickModule(new EntityStateModule(createHealthStates())) // Health state

                // Set entity stats
                .addExecuteOnSpawnModule(new EntityHealthModule(700).scaleHealth())
                .addEntityModification(entity -> {
                    if (entity instanceof Zombie zombie) {
                        zombie.setAdult();
                    }
                })

                // Potion effects
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 0, true, false)))
                .addExecuteOnSpawnModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, true, false)))

                // Add equipment
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HAND, sword, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.OFF_HAND, sword, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.HEAD, helmet, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.CHEST, chest, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.LEGS, leg, 0))
                .addExecuteOnSpawnModule(new EntityEquipmentModule(EquipmentSlot.FEET, boot, 0))

                // Bossbar
                .addExecuteOnTickModule(new EntityBossBarModule("High Alchemist", 50, BarColor.BLUE, BarStyle.SEGMENTED_10))

                // Drops
                .addExecuteOnDeathModule(new EntityDropModule(6000, 10, new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.CHEST, CustomLootTable.LootTarget.BARREL, CustomLootTable.LootTarget.SHULKER_BOX},
                        new LootPool(1, new LootEntry(SpellManager.ALPHA.getSpellItem(), 1, 1)),
                        new LootPool(1, new LootEntry(SpellManager.GAMMA.getSpellItem(), 1, 1)),
                        new LootPool(1, new LootEntry(ItemManager.CRYSTAL_KEY.getItemStack(), 1, 1)),
                        new LootPool(1, new LootEntry(BookConstants.getEmeraldTabletI(), 1, 1)),
                        new LootPool(1, new WandLootEntry(4, true))
                )))
                .addExecuteOnDeathModule(new EntityAdvancementModule(new AdvancementGranter("progression", "kill_high_alchemist", 50)))

                // Sounds
                .addExecuteOnTickModule(new EntityAmbientSoundModule("minecraft:cobalt.brain", 1, 1, 41))

                // Attacks
                .addAbilityModule(new CasterAbility(2.2, 64, SpellManager.ALCHEMIST_MAIN_SPELL))

                // Wand Summon
                .addAbilityModule(new SummonerAbility(EntityManager.SENTIENT_WAND, 1, 1, 3)
                        .setSpawnChance(.25)
                        .setCustomSpawnParameters(new SentientWandParameters(
                                new CasterAbility(1.1, 30, SpellManager.getSpell(90)).setOffset(new Vector())
                        ))
                )
                .addAbilityModule(new SummonerAbility(EntityManager.SENTIENT_WAND, 1, 1, 3)
                        .setSpawnChance(.25)
                        .setCustomSpawnParameters(new SentientWandParameters(
                                new CasterAbility(1.1, 30, SpellManager.getSpell(91)).setOffset(new Vector())
                        ))
                )
                .addAbilityModule(new SummonerAbility(EntityManager.SENTIENT_WAND, 1, 1, 3)
                        .setSpawnChance(.25)
                        .setCustomSpawnParameters(new SentientWandParameters(
                                new CasterAbility(3.2, 30, SpellManager.getSpell(92)).setOffset(new Vector())
                        ))
                )
                .addAbilityModule(new SummonerAbility(EntityManager.SENTIENT_WAND, 1, 1, 3)
                        .setSpawnChance(.25)
                        .setCustomSpawnParameters(new SentientWandParameters(
                                new CasterAbility(3.2, 30, SpellManager.getSpell(93))
                        ))
                )

                // Set general cooldown for abilities
                .setGeneralAbilityCooldown(0) // 0 seconds
                .build();

        return CustomEntityManager.register(highAlchemist);
    }

    // ----- HEALTH STATES -----

    private static StateEngine<CustomEntity> createHealthStates() {
        // Create Health States

        CobaltState<CustomEntity> healthState0 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .9, 1), 9);
        CobaltState<CustomEntity> healthState1 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .8, .9), 8);
        CobaltState<CustomEntity> healthState2 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .7, .8), 7);
        CobaltState<CustomEntity> healthState3 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .6, .7), 6);
        CobaltState<CustomEntity> healthState4 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .5, .6), 5);
        CobaltState<CustomEntity> healthState5 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .4, .5), 4);
        CobaltState<CustomEntity> healthState6 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .3, .4), 3);
        CobaltState<CustomEntity> healthState7 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .2, .3), 2);
        CobaltState<CustomEntity> healthState8 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .1, .2), 1);
        CobaltState<CustomEntity> healthState9 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, 0, .1), 0);

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

        healthState1.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 1));
        healthState2.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 1));
        healthState3.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 1));
        healthState4.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 2));
        healthState5.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 2));
        healthState6.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 2));
        healthState7.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 3));
        healthState8.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 3));
        healthState9.setEnterOperation((entity, state) -> executeEnterHealthState(entity, state, 4));

        // Create Health Engine
        return new StateEngine<>(healthState0);
    }

    /**
     * Executes the entry event for a health state.
     *
     * @param entity the entity that owns the health state.
     * @param state the state.
     * @param nSpellCasts the number of times to cast spells.
     */
    private static void executeEnterHealthState(CustomEntity entity, CobaltState<CustomEntity> state, int nSpellCasts) {

        // Switch color of the bossbar for a few seconds
        EntityBossBarModule bossBarModule = entity.getTickExecutable(EntityBossBarModule.class);
        if (bossBarModule != null) {
            bossBarModule.getBossBar().setColor(BarColor.RED);
            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> bossBarModule.getBossBar().setColor(BarColor.BLUE), 20);
        }

        // Get entity info
        Location location = entity.getSummonedEntity().getLocation();
        World world = entity.getSummonedEntity().getWorld();

        // Play sound
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 3, 1);

        // Create Wand with Spells
        if (entity.getSummonedEntity() instanceof LivingEntity living) {
            List<ISpell> spells = new ArrayList<>();
            spells.add(SpellManager.getSpell(710));
            for (int i = 0; i < 6; i++) spells.add(SpellManager.getSpell(92));
            wand.setSpells(spells);
            wand.setCurrentMana(1000); // TODO: Make a dev wand that does not consume mana

            // Cast spells
            for (int i = 0; i < nSpellCasts; i++) Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> wand.castSpells(living, new Vector(1, 0, 0), living.getLocation().clone().add(new Vector(0, 1.2, 0))), 10L * i);
        }
    }

    /**
     * Gets if the entity's health is below the given percent value.
     *
     * @param entity the entity to check.
     * @param lowerLimit lower percent value. Range between 0.0 - 1.0.
     * @param upperLimit higher percent value. Range between 0.0 - 1.0.
     * @return if the entity's health is below the given percent value.
     */
    private static boolean isBetweenPercentHealth(CustomEntity entity, double lowerLimit, double upperLimit) {
        if (entity.getSummonedEntity() instanceof LivingEntity living) {
            double currentHealth = living.getHealth();
            double maxHealth = living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            double upperPercent = upperLimit * maxHealth;
            double lowerPercent = lowerLimit * maxHealth;

            return currentHealth < upperPercent && currentHealth > lowerPercent;
        }
        return false;
    }

    // ----- SHIELD STATES -----

    private static StateEngine<CustomEntity> createShieldState() {
        TimedState<CustomEntity> shieldedState = new TimedState<>((entity, state) -> true, 0, 200);
        TimedState<CustomEntity> normalState = new TimedState<>((entity, state) -> true, 0, 400);

        // Add States Logic
        // lockedState.setTickOperation(this::lockedTick);
        shieldedState.setTickOperation(HighAlchemist::shieldedStateTick);
        normalState.setTickOperation(HighAlchemist::normalStateTick);

        shieldedState.setEnterOperation(HighAlchemist::enterShieldState);

        // Set Connected States
        // initialState.setConnectedStates(lockedState);
        // lockedState.setConnectedStates(shieldedState);
        shieldedState.setConnectedStates(normalState);
        normalState.setConnectedStates(shieldedState);

        // Create Engine
        return new StateEngine<>(shieldedState);
    }

    private static void shieldedStateTick(CustomEntity entity, CobaltState<CustomEntity> state) {
        Entity summonedEntity = entity.getSummonedEntity();

        // Create or Get ShieldGroup
        ParticleGroup shieldGroup = (ParticleGroup)state.getPersistentObject("shieldGroup");
        if (shieldGroup == null) {
            shieldGroup = new ParticleGroup();
            List<ParticleStyle> shieldStyles = new ArrayList<>();
            shieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setRadius(shieldRadius).setDensity(100).build());
            for (ParticleStyle style : shieldStyles) shieldGroup.addParticleStyle(style);
        }

        summonedEntity.setGravity(false);
        summonedEntity.teleport(entity.getSpawnLocation());
        shieldGroup.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));

        // Get all arrow entities and multiplies velocity by -1
        List<Entity> nearbyEntities = summonedEntity.getNearbyEntities(shieldRadius, shieldRadius, shieldRadius);
        for (Entity e : nearbyEntities) {
            if (e instanceof Arrow a && e.getLocation().distanceSquared(summonedEntity.getLocation()) < shieldRadius * shieldRadius) {
                Vector arrowVelocity = a.getVelocity();
                arrowVelocity.multiply(-1);
                a.setVelocity(arrowVelocity);
            }
        }
    }

    private static void normalStateTick(CustomEntity entity, CobaltState<CustomEntity> state) {
        Entity summonedEntity = entity.getSummonedEntity();

        // Create or Get DamageFieldGroup
        ParticleGroup damageFieldGroup = (ParticleGroup)state.getPersistentObject("damageGroup");
        if (damageFieldGroup == null) {
            damageFieldGroup = new ParticleGroup();
            List<ParticleStyle> damageFieldStyles = new ArrayList<>();
            damageFieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMALL_FLAME).setRadius(damageFieldRadius).setDensity(40).build());
            for (ParticleStyle style : damageFieldStyles) damageFieldGroup.addParticleStyle(style);
        }

        summonedEntity.setGravity(true);
        damageFieldGroup.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));
        new EffectModule(damageFieldRadius, false).setFire(20).giveEffectsInSphere(summonedEntity.getLocation());
    }

    /**
     * Teleports the entity to the spawn location.
     *
     * @param entity the entity to teleport
     * @param state the state.
     */
    private static void enterShieldState(CustomEntity entity, CobaltState<CustomEntity> state) {
        Entity summonedEntity = entity.getSummonedEntity();
        Location spawnLocation = entity.getSpawnLocation();

        ParticleStylePoint point = new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(30).setOffset(new Vector(.4, 1, .4)).build();
        ParticleGroup group = new ParticleGroup();
        List<ParticleStyle> styles = new ArrayList<>();
        styles.add(point);
        for (ParticleStyle style : styles) group.addParticleStyle(style);
        group.display(summonedEntity.getLocation().clone().add(new Vector(0, 1, 0)));
        group.display(spawnLocation.clone().add(new Vector(0, 1, 0)));

        World world = spawnLocation.getWorld();
        if (world != null) {
            world.playSound(summonedEntity.getLocation(), "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
            world.playSound(spawnLocation, "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
        }
    }

}
