package se.fusion1013.plugin.cobaltmagick.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Holds most information about a Custom Entity
 */
public abstract class AbstractCustomEntity implements ICustomEntity, Cloneable {

    // ----- VARIABLES -----

    // Bossbar
    private BossBar bossbar; // Bossbar instance. Created on entity spawn if hasBossbar is set to true

    boolean hasBossbar = false; // Whether an entity has a bossbar or not
    String bossbarTitle; // The title of the bossbar
    double bossbarActivationRange; // Defines how close to the entity a player has to be to see the bossbar
    BarColor bossbarColor; // The color of the bossbar
    BarStyle bossbarStyle; // The style of the bossbar

    // World Information
    Location spawnLocation; // The location where the Custom Entity spawned

    // Lifespan
    boolean hasLifespan = false; // Determines whether the entity has a lifespan
    int lifespan; // The lifespan of the entity
    int currentLifeSpan = 0; // The current lifespan of the entity. If this exceeds the max lifetime, the entity dies

    // Drops
    List<ItemStack> alwaysDropItems = new ArrayList<>(); // These items will always be dropped on death
    List<ItemStack> chooseOneDrop = new ArrayList<>(); // On death, one of these items will randomly chosen and dropped
    int xpAmountTotal = 0; // The amount of xp that will be dropped on death
    int xpSplit = 1; // The number of orbs the xp will be split into

    // Entity Information
    EntityType baseEntityType;
    Entity summonedEntity;
    int maxHealth = 20;
    boolean scaleHealth = false;
    double scaleFactor = 1;
    int scaledHealth = 20;

    // Internals
    String inbuiltName;

    // ----- CONSTRUCTORS -----

    public AbstractCustomEntity(EntityType baseEntity, String inbuiltName) {
        this.baseEntityType = baseEntity;
        this.inbuiltName = inbuiltName;
    }

    // ----- EVENTS -----

    /**
     * Called when the entity is spawned.
     *
     * @param location the location where the entity spawned.
     */
    @Override
    public void spawn(Location location) {

        // Store Location and Summon Entity
        this.spawnLocation = location;
        World spawnWorld = location.getWorld();
        if (spawnWorld == null) return;
        summonedEntity = spawnWorld.spawnEntity(location, baseEntityType);

        // Set Entity Stats
        summonedEntity.setPersistent(true);

        if (summonedEntity instanceof LivingEntity living) {
            setEntityStats(living);
        }

        // Create Bossbar
        if (hasBossbar) bossbar = Bukkit.createBossBar(bossbarTitle, bossbarColor, bossbarStyle);
    }

    /**
     * Sets the statistics of the entity.
     *
     * @param entity entity to set the statistics of.
     */
    private void setEntityStats(LivingEntity entity) {
        // Set Health
        AttributeInstance maxHealthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        int scaledHealth = maxHealth;
        if (scaleHealth) {
            for (int i = 0; i < Bukkit.getOnlinePlayers().size()-1; i++) {
                scaledHealth = (int)Math.round((double)scaledHealth * scaleFactor);
            }
        }
        this.scaledHealth = scaledHealth;

        if (maxHealthAttribute != null) maxHealthAttribute.setBaseValue(scaledHealth);
        entity.setHealth(scaledHealth);

        // Lock Equipment Slots
        EntityEquipment eq = entity.getEquipment();
        if (eq != null) {
            eq.setHelmetDropChance(0);
            eq.setChestplateDropChance(0);
            eq.setLeggingsDropChance(0);
            eq.setBootsDropChance(0);
            eq.setItemInMainHandDropChance(0);
            eq.setItemInOffHandDropChance(0);
        }
    }

    /**
     * Called every tick that the entity is alive
     */
    @Override
    public void tick() {
        if (hasBossbar) handleBossbar();
        if (hasLifespan) currentLifeSpan++;

        // Death Check
        if (!isAlive()) onDeath();
    }

    /**
     * Called when the entity dies
     */
    @Override
    public void onDeath() {
        createDrops();

        if (bossbar != null) bossbar.removeAll();
        if (summonedEntity != null) summonedEntity.remove();
    }

    /**
     * Sets the correct value for the bossbar and then displays the bossbar to all players within activation range
     */
    private void handleBossbar() {
        // Sets the correct value for the bar
        if (summonedEntity instanceof LivingEntity living) {
            bossbar.setProgress(living.getHealth() / living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }

        // Sets the players that can see the bossbar
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(summonedEntity.getLocation()) < bossbarActivationRange * bossbarActivationRange) {
                bossbar.addPlayer(p);
            } else {
                bossbar.removePlayer(p);
            }
        }
    }

    @Override
    public void switchBossbarColor(BarColor color) {
        bossbar.removeAll();
        if (hasBossbar) bossbar = Bukkit.createBossBar(bossbarTitle, color, bossbarStyle);
    }

    /**
     * Creates the drops for the entities.
     * Includes XP and Item Drops.
     */
    private void createDrops() {
        Location dropLocation = summonedEntity.getLocation();
        World dropWorld = dropLocation.getWorld();

        if (dropWorld == null) return;

        // Create Always Drop Item Drops
        for (ItemStack item : alwaysDropItems) {
            Item droppedItem = (Item)dropWorld.spawnEntity(dropLocation, EntityType.DROPPED_ITEM);
            droppedItem.setItemStack(item);
        }

        // Choose one item to drop
        if (chooseOneDrop.size() > 0) {
            Random r = new Random();
            int slot = r.nextInt(0, chooseOneDrop.size());
            Item droppedRandomItem = (Item)dropWorld.spawnEntity(dropLocation, EntityType.DROPPED_ITEM);
            droppedRandomItem.setItemStack(chooseOneDrop.get(slot));
        }

        // Create XP Drops
        if (xpAmountTotal > 0) {
            int amountPerOrb = xpAmountTotal / xpSplit;
            for (int i = 0; i < xpSplit; i++) {
                ExperienceOrb xpOrb = (ExperienceOrb) dropWorld.spawnEntity(dropLocation, EntityType.EXPERIENCE_ORB);
                xpOrb.setExperience(amountPerOrb);
            }
        }
    }

    @Override
    public boolean isAlive() {
        if (currentLifeSpan > lifespan && hasLifespan) return false;
        return summonedEntity.isValid();
    }

    // ----- GETTERS / SETTERS -----

    public void scaleHealth(double scaleFactor) {
        this.scaleHealth = true;
        this.scaleFactor = scaleFactor;
    }

    public void addLifespan(int lifespan) {
        this.hasLifespan = true;

        this.lifespan = lifespan;
    }

    /**
     * Adds an item that will always be dropped on death
     *
     * @param item item to add
     */
    public void addAlwaysDropItem(ItemStack item) {
        alwaysDropItems.add(item);
    }

    /**
     * Adds an item that has the potential to be dropped on death.
     * On death, one of the items in the list will be chosen and dropped.
     *
     * @param item item to add
     */
    public void addChooseOneDrop(ItemStack item) {
        chooseOneDrop.add(item);
    }

    /**
     * Sets the amount of xp that will be dropped on death.
     *
     * @param xp the amount of xp to drop
     */
    public void setXpDropAmount(int xp) {
        this.xpAmountTotal = xp;
    }

    /**
     * Sets the amount of xp that will be dropped on death.
     * The split represents the number of xp orbs the total will be split between.
     *
     * @param xp the amount of xp to drop
     * @param split the number of orbs to split the xp between
     */
    public void setXpDropAmount(int xp, int split) {
        this.xpAmountTotal = xp;
        this.xpSplit = split;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public String getInbuiltName() {
        return inbuiltName;
    }

    @Override
    public String getUniqueId() {
        return inbuiltName + "::" + getClass().hashCode();
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity)summonedEntity;
    }

    @Override
    public double getMaxHealth() {
        return scaledHealth;
    }

    @Override
    public double getCurrentHealth() {
        return ((LivingEntity)summonedEntity).getHealth();
    }

    // ----- BUILDER -----

    protected static abstract class AbstractCustomEntityBuilder<T extends AbstractCustomEntity, B extends AbstractCustomEntityBuilder> {

        T obj;

        // ----- Custom Entity Attributes -----

        // Entity Base
        EntityType baseEntity;
        String inbuiltName;

        // Bossbar
        boolean hasBossbar = false;
        String bossbarText;
        double bossbarActivationRange;
        BarColor bossbarColor;
        BarStyle bossbarStyle;

        // Lifespan
        boolean hasLifespan = false;
        int lifespan;

        // Drops
        List<ItemStack> alwaysDropItems = new ArrayList<>(); // These items will always be dropped on death
        List<ItemStack> chooseOneDrop = new ArrayList<>(); // On death, one of these items will randomly chosen and dropped
        int xpAmountTotal = 0;
        int xpSplit = 1;

        // Entity Stats
        int maxHealth = 20;
        boolean scalesHealth = false;
        double healthScaleFactor = 1;

        public AbstractCustomEntityBuilder(EntityType baseEntity, String inbuiltName) {
            this.baseEntity = baseEntity;
            this.inbuiltName = inbuiltName;

            obj = createObj();
        }

        public T build() {
            obj.hasBossbar = hasBossbar;
            obj.bossbarTitle = bossbarText;
            obj.bossbarActivationRange = bossbarActivationRange;
            obj.bossbarColor = bossbarColor;
            obj.bossbarStyle = bossbarStyle;

            obj.hasLifespan = hasLifespan;
            obj.lifespan = lifespan;

            obj.alwaysDropItems = alwaysDropItems;
            obj.chooseOneDrop = chooseOneDrop;
            obj.xpAmountTotal = xpAmountTotal;
            obj.xpSplit = xpSplit;

            obj.maxHealth = maxHealth;
            obj.scaleHealth = scalesHealth;
            obj.scaleFactor = healthScaleFactor;

            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        public B scaleHealth(double healthScaleFactor) {
            this.scalesHealth = true;
            this.healthScaleFactor = healthScaleFactor;
            return getThis();
        }

        public B setMaxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            return getThis();
        }

        public B addXpDrop(int xp, int split) {
            this.xpAmountTotal = xp;
            this.xpSplit = split;
            return getThis();
        }

        public B addXpDrop(int xp) {
            this.xpAmountTotal = xp;
            return getThis();
        }

        public B addChooseOneDropItem(ItemStack item) {
            chooseOneDrop.add(item);
            return getThis();
        }

        public B addAlwaysDropItem(ItemStack item) {
            alwaysDropItems.add(item);
            return getThis();
        }

        public B addLifetime(int lifespan) {
            this.hasLifespan = true;
            this.lifespan = lifespan;
            return getThis();
        }

        public B addBossbar(String text, double activationRange, BarColor color, BarStyle style) {
            this.hasBossbar = true;
            this.bossbarText = text;
            this.bossbarActivationRange = activationRange;
            this.bossbarColor = color;
            this.bossbarStyle = style;
            return getThis();
        }
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    /**
     * Clone constructor
     *
     * @param target the target object to clone
     */
    public AbstractCustomEntity(AbstractCustomEntity target) {
        // Copy Bossbar
        this.bossbar = target.bossbar;
        this.hasBossbar = target.hasBossbar;
        this.bossbarTitle = target.bossbarTitle;
        this.bossbarActivationRange = target.bossbarActivationRange;
        this.bossbarColor = target.bossbarColor;
        this.bossbarStyle = target.bossbarStyle;

        // Copy World Information
        this.spawnLocation = target.spawnLocation;

        // Copy Lifespan
        this.hasLifespan = target.hasLifespan;
        this.lifespan = target.lifespan;
        this.currentLifeSpan = target.currentLifeSpan;

        // Copy Drops
        this.alwaysDropItems = target.alwaysDropItems;
        this.chooseOneDrop = target.chooseOneDrop;
        this.xpAmountTotal = target.xpAmountTotal;
        this.xpSplit = target.xpSplit;

        // Copy Entity Information
        this.baseEntityType = target.baseEntityType;
        this.summonedEntity = target.summonedEntity;
        this.maxHealth = target.maxHealth;
        this.scaleHealth = target.scaleHealth;
        this.scaleFactor = target.scaleFactor;
        this.scaledHealth = target.scaledHealth;

        // Copy Internals
        this.inbuiltName = target.inbuiltName;
    }

    @Override
    public abstract AbstractCustomEntity clone();
}
