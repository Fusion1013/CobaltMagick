package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.state.CobaltState;
import se.fusion1013.plugin.cobaltmagick.state.StateEngine;
import se.fusion1013.plugin.cobaltmagick.manager.EntityManager;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.EffectModule;
import se.fusion1013.plugin.cobaltmagick.state.TimedState;
import se.fusion1013.plugin.cobaltmagick.util.AIUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HighAlchemistEntity extends StateEntity implements Cloneable {

    // ----- VARIABLES -----

    // Enabled
    boolean attacksEnabled = true;

    // Field
    double shieldRadius = 5;
    double damageFieldRadius = 2;

    // ----- CONSTRUCTORS -----

    public HighAlchemistEntity() {
        super(EntityType.ZOMBIE, "high_alchemist");

        createStateEngines();
    }

    // ----- STATE CONSTRUCTION -----

    private void createStateEngines() {

        // ----- SHIELDED / NORMAL STATES -----

        // Create Shielded/Normal States
        /*
        CobaltState<HighAlchemistEntity> initialState = new CobaltState<>((entity, state) -> true, 0);
        CobaltState<HighAlchemistEntity> lockedState = new CobaltState<HighAlchemistEntity>((entity, state) -> {
            CobaltMagick.getInstance().getLogger().info("Current Health: " + entity.getCurrentHealth() + " :: Max Health: " + entity.getMaxHealth());
            return entity.getCurrentHealth() < entity.getMaxHealth();
        }, 0)
                .setExitOperation((entity, state) -> entity.setAttacksEnabled(true));
         */
        TimedState<HighAlchemistEntity> shieldedState = new TimedState<>((entity, state) -> true, 0, 200);
        TimedState<HighAlchemistEntity> normalState = new TimedState<>((entity, state) -> true, 0, 400);

        // Add States Logic
        // lockedState.setTickOperation(this::lockedTick);
        shieldedState.setTickOperation(this::shieldedStateTick);
        normalState.setTickOperation(this::normalStateTick);

        shieldedState.setEnterOperation(this::enterShieldState);

        // Set Connected States
        // initialState.setConnectedStates(lockedState);
        // lockedState.setConnectedStates(shieldedState);
        shieldedState.setConnectedStates(normalState);
        normalState.setConnectedStates(shieldedState);

        // Create Engine
        addStateEngine(new StateEngine(shieldedState));

        // ----- HEALTH STATES -----

        // Create Health States

        CobaltState<HighAlchemistEntity> healthState0 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .9, 1), 9);
        CobaltState<HighAlchemistEntity> healthState1 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .8, .9), 8);
        CobaltState<HighAlchemistEntity> healthState2 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .7, .8), 7);
        CobaltState<HighAlchemistEntity> healthState3 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .6, .7), 6);
        CobaltState<HighAlchemistEntity> healthState4 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .5, .6), 5);
        CobaltState<HighAlchemistEntity> healthState5 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .4, .5), 4);
        CobaltState<HighAlchemistEntity> healthState6 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .3, .4), 3);
        CobaltState<HighAlchemistEntity> healthState7 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .2, .3), 2);
        CobaltState<HighAlchemistEntity> healthState8 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, .1, .2), 1);
        CobaltState<HighAlchemistEntity> healthState9 = new CobaltState<>((entity, state) -> isBetweenPercentHealth(entity, 0, .1), 0);

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

        addStateEngine(new StateEngine(healthState0));
    }

    // ----- STATE LOGIC METHODS -----

    /**
     * Executes the entry event for a health state.
     *
     * @param entity the entity that owns the health state.
     * @param state the state.
     * @param nSpellCasts the number of times to cast spells.
     */
    private void executeEnterHealthState(ICustomEntity entity, CobaltState<HighAlchemistEntity> state, int nSpellCasts) {

        // Switch color of the bossbar for a few seconds
        entity.switchBossbarColor(BarColor.RED);
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> entity.switchBossbarColor(BarColor.BLUE), 20);

        // Get entity info
        Location location = entity.getEntity().getLocation();
        World world = entity.getEntity().getWorld();

        // Play sound
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 3, 1);

        // Create Wand with Spells
        LivingEntity summonedEntity = entity.getEntity();

        Wand wand = new Wand(false, 1, 0.01, 0.01, 1000, 1000, 9, 0, new ArrayList<>(), 0);
        List<ISpell> spells = new ArrayList<>();
        spells.add(SpellManager.getSpell(710));
        for (int i = 0; i < 6; i++) spells.add(SpellManager.getSpell(92));
        wand.setSpells(spells);

        // Cast spells
        for (int i = 0; i < nSpellCasts; i++) Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> wand.castSpells(summonedEntity, new Vector(1, 0, 0), summonedEntity.getLocation().clone().add(new Vector(0, 1.2, 0))), 10L * i);
    }

    /**
     * Gets if the entity's health is below the given percent value.
     *
     * @param entity the entity to check.
     * @param lowerLimit lower percent value. Range between 0.0 - 1.0.
     * @param upperLimit higher percent value. Range between 0.0 - 1.0.
     * @return if the entity's health is below the given percent value.
     */
    private boolean isBetweenPercentHealth(ICustomEntity entity, double lowerLimit, double upperLimit) {
        double currentHealth = entity.getCurrentHealth();
        double maxHealth = entity.getMaxHealth();

        double upperPercent = upperLimit * maxHealth;
        double lowerPercent = lowerLimit * maxHealth;

        return currentHealth < upperPercent && currentHealth > lowerPercent;
    }

    private void enterShieldState(ICustomEntity entity, CobaltState<HighAlchemistEntity> state) {
        Entity summonedEntity = entity.getEntity();
        Location spawnLocation = entity.getSpawnLocation();

        ParticleStylePoint point = new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(30).setOffset(new Vector(.4, 1, .4)).build();
        ParticleGroup group = new ParticleGroup();
        List<ParticleStyle> styles = new ArrayList<>();
        styles.add(point);
        group.setParticleStyles(styles);
        group.display(summonedEntity.getLocation().clone().add(new Vector(0, 1, 0)));
        group.display(spawnLocation.clone().add(new Vector(0, 1, 0)));

        World world = spawnLocation.getWorld();
        if (world != null) {
            world.playSound(summonedEntity.getLocation(), "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
            world.playSound(spawnLocation, "minecraft:entity.illusioner.mirror_move", SoundCategory.HOSTILE, 1, 1);
        }
    }

    private void normalStateTick(ICustomEntity entity, CobaltState<HighAlchemistEntity> state) {
        Entity summonedEntity = entity.getEntity();

        // Create or Get DamageFieldGroup
        ParticleGroup damageFieldGroup = (ParticleGroup)state.getPersistentObject("damageGroup");
        if (damageFieldGroup == null) {
            damageFieldGroup = new ParticleGroup();
            List<ParticleStyle> damageFieldStyles = new ArrayList<>();
            damageFieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMALL_FLAME).setRadius(damageFieldRadius).setDensity(40).build());
            damageFieldGroup.setParticleStyles(damageFieldStyles);
        }

        summonedEntity.setGravity(true);
        damageFieldGroup.display(summonedEntity.getLocation().add(new Vector(0, 1, 0)));
        new EffectModule(damageFieldRadius, false).setFire(20).giveEffectsInSphere(summonedEntity.getLocation());
    }

    private void lockedTick(ICustomEntity entity, CobaltState<HighAlchemistEntity> state) {
        Entity summonedEntity = entity.getEntity();
        summonedEntity.setGravity(false);
        summonedEntity.teleport(entity.getSpawnLocation());
    }

    private void shieldedStateTick(ICustomEntity entity, CobaltState<HighAlchemistEntity> state) {
        Entity summonedEntity = entity.getEntity();

        // Create or Get ShieldGroup
        ParticleGroup shieldGroup = (ParticleGroup)state.getPersistentObject("shieldGroup");
        if (shieldGroup == null) {
            shieldGroup = new ParticleGroup();
            List<ParticleStyle> shieldStyles = new ArrayList<>();
            shieldStyles.add(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setRadius(shieldRadius).setDensity(100).build());
            shieldGroup.setParticleStyles(shieldStyles);
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

    // ----- EVENT -----

    @Override
    public void spawn(Location location) {
        super.spawn(location);

        summonedEntity.setSilent(true);
        LivingEntity living = (LivingEntity)summonedEntity;
        living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.37);
        living.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        living.getEquipment().setItemInOffHand(new ItemStack(Material.NETHERITE_SWORD));
        living.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1000000, 0, true, false));
        if (summonedEntity instanceof Zombie zombie) zombie.setAdult();

        // Set Armor
        EntityEquipment equipment = living.getEquipment();
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta)helmet.getItemMeta();

        if (meta != null) meta.setColor(Color.fromRGB(0, 255, 0));
        helmet.setItemMeta(meta);
        chest.setItemMeta(meta);
        leg.setItemMeta(meta);
        boot.setItemMeta(meta);

        equipment.setHelmet(helmet);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boot); // TODO: Set drop chance to 0
    }

    @Override
    public void tick() {
        super.tick();

        summonedEntity.setFallDistance(0);

        if (attacksEnabled) {
            // Random chance to summon an attacking wand
            Random r = new Random();
            int numPlayer = Bukkit.getOnlinePlayers().size();
            if (r.nextInt(0, Math.max(101 / numPlayer, 5)) == 0) summonWand();
            if (r.nextInt(0, 21) == 0) ambientSounds();
            if (r.nextInt(0, Math.max(41 / numPlayer, 5)) == 0) mainAttack();
        }
    }

    @Override
    public void onDeath() {
        summonedEntity.getWorld().playSound(summonedEntity.getLocation(), "minecraft:entity.wither.death", SoundCategory.HOSTILE, 3, 1);
        super.onDeath();
    }

    // ----- LOGIC METHODS -----

    private void mainAttack() {
        Wand wand = new Wand(false, 1, .2, 2, 1000, 1000, 9, 0, new ArrayList<>(), 0);
        List<ISpell> spells = new ArrayList<>();
        spells.add(SpellManager.getSpell(94));
        wand.setSpells(spells);

        // Find target
        LivingEntity targetEntity = AIUtil.findNearbyPlayerHealthWeighted(summonedEntity, 64);
        Location castLocation = summonedEntity.getLocation().clone().add(new Vector(0, 2.2, 0));

        if (targetEntity != null) {
            Location target = targetEntity.getLocation();
            Vector delta = new Vector(target.getX() - castLocation.getX(), target.getY() - castLocation.getY() + 1, target.getZ() - castLocation.getZ()).normalize();
            // Cast spell
            if (summonedEntity instanceof LivingEntity living) wand.castSpells(living, delta, castLocation);
        }
    }

    private void ambientSounds() {
        Location currentLocation = summonedEntity.getLocation();
        World currentWorld = currentLocation.getWorld();
        if (currentWorld == null) return;
        currentWorld.playSound(currentLocation, "minecraft:cobalt.brain", SoundCategory.HOSTILE, 1, 1);
    }

    /**
     * Summons a new wand, randomly chosen from four presets
     */
    private void summonWand() { // TODO: Wands (3) should be summoned when the alchemist does his attack
        Random r = new Random();
        int wandType = r.nextInt(0, 4); // Three different kinds of wands that can possibly be summoned by alchemy boi
        Wand wand = null;
        List<ISpell> wandSpells = new ArrayList<>();

        switch (wandType) {
            case 0 -> {
                wand = new Wand(false, 1, .2, 10, 1000, 1000, 9, 0, new ArrayList<>(), 0);
                for (int i = 0; i < 4; i++) wandSpells.add(SpellManager.getSpell(90));
            }
            case 1 -> {
                wand = new Wand(false, 1, .2, 1, 1000, 1000, 4, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(91));
            }
            case 2 -> {
                wand = new Wand(false, 1, .2, 100, 1000, 1000, 20, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(92));
            }
            case 3 -> {
                wand = new Wand(false, 1, .2, 100, 1000, 1000, 20, 0, new ArrayList<>(), 0);
                wandSpells.add(SpellManager.getSpell(93));
            }
        }
        if (wand != null) {
            wand.setSpells(wandSpells);

            SentientWand wandEntity = new SentientWand(wand);
            wandEntity.addLifespan(130);
            wandEntity.setXpDropAmount(0);
            EntityManager.getInstance().spawnCustomEntity(wandEntity, summonedEntity.getLocation()); // TODO: Change to a location near the entity
        }
    }

    // ----- GETTERS / SETTERS -----

    public boolean isAttacksEnabled() {
        return attacksEnabled;
    }

    public void setAttacksEnabled(boolean attacksEnabled) {
        this.attacksEnabled = attacksEnabled;
    }

    // ----- BUILDER -----

    public static class HighAlchemistBuilder extends AbstractCustomEntityBuilder<HighAlchemistEntity, HighAlchemistBuilder> {

        public HighAlchemistBuilder() {
            super(EntityType.ZOMBIE, "high_alchemist");
        }

        @Override
        protected HighAlchemistEntity createObj() {
            return new HighAlchemistEntity();
        }

        @Override
        protected HighAlchemistBuilder getThis() {
            return this;
        }
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public HighAlchemistEntity(HighAlchemistEntity target) {
        super(target);
        this.attacksEnabled = target.attacksEnabled;
        this.shieldRadius = target.shieldRadius;
        this.damageFieldRadius = target.damageFieldRadius;
    }

    @Override
    public HighAlchemistEntity clone() {
        return new HighAlchemistEntity(this);
    }
}
