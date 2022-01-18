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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractCustomEntity implements ICustomEntity, Cloneable {

    // Bossbar
    private BossBar bossbar;

    boolean hasBossbar = false;
    String bossbarText;
    double bossbarActivationRange;
    BarColor bossbarColor;
    BarStyle bossbarStyle;

    // World Information
    Location spawnLocation;

    // Lifespan
    boolean hasLifespan = false;
    int lifespan;
    int currentLifeSpan = 0;

    // Drops
    List<ItemStack> alwaysDropItems = new ArrayList<>(); // These items will always be dropped on death
    List<ItemStack> chooseOneDrop = new ArrayList<>(); // On death, one of these items will randomly chosen and dropped
    int xpAmountTotal = 0;
    int xpSplit = 1;

    // Entity Information
    EntityType baseEntityType;
    Entity summonedEntity;
    int maxHealth = 20;
    boolean scaleHealth = false;
    double scaleFactor = 1;

    // Internals
    String inbuiltName;

    public AbstractCustomEntity(EntityType baseEntity, String inbuiltName) {
        this.baseEntityType = baseEntity;
        this.inbuiltName = inbuiltName;
    }

    /**
     * Clone constructor
     *
     * @param target the target object to clone
     */
    public AbstractCustomEntity(AbstractCustomEntity target) {
        // Copy Bossbar
        this.bossbar = target.bossbar;
        this.hasBossbar = target.hasBossbar;
        this.bossbarText = target.bossbarText;
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

        // Copy Internals
        this.inbuiltName = target.inbuiltName;
    }

    @Override
    public void tick() {
        if (hasBossbar) handleBossbar();
        if (hasLifespan) currentLifeSpan++;

        // Death Check
        if (!isAlive()) kill();
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
    public void spawn(Location location) {
        this.spawnLocation = location;
        World spawnWorld = location.getWorld();
        if (spawnWorld == null) return;
        summonedEntity = spawnWorld.spawnEntity(location, baseEntityType);

        // Set Entity Stats
        summonedEntity.setPersistent(true);

        if (summonedEntity instanceof LivingEntity living) {
            // Set Health
            AttributeInstance maxHealthAttribute = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            int scaledHealth = (int)Math.round(Bukkit.getOnlinePlayers().size() * scaleFactor * maxHealth);

            if (maxHealthAttribute != null) maxHealthAttribute.setBaseValue(scaledHealth);
            living.setHealth(scaledHealth);

            // Lock Equipment Slots
            EntityEquipment eq = living.getEquipment();
            if (eq != null) {
                eq.setHelmetDropChance(0);
                eq.setChestplateDropChance(0);
                eq.setLeggingsDropChance(0);
                eq.setBootsDropChance(0);
                eq.setItemInMainHandDropChance(0);
                eq.setItemInOffHandDropChance(0);
            }
        }

        // Create Bossbar
        if (hasBossbar) bossbar = Bukkit.createBossBar(bossbarText, bossbarColor, bossbarStyle);
    }

    @Override
    public void kill() {
        createDrops();

        if (bossbar != null) bossbar.removeAll();
        if (summonedEntity != null) summonedEntity.remove();
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

    // ----- Settings -----

    public void scaleHealth(double scaleFactor) {
        this.scaleHealth = true;
        this.scaleFactor = scaleFactor;
    }

    public void addLifespan(int lifespan) {
        this.hasLifespan = true;

        this.lifespan = lifespan;
    }

    public void addBossbar(String text, double activationRange, BarColor color, BarStyle style) {
        this.hasBossbar = true;

        this.bossbarText = text;
        this.bossbarActivationRange = activationRange;
        this.bossbarColor = color;
        this.bossbarStyle = style;
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

    @Override
    public String getInbuiltName() {
        return inbuiltName;
    }

    @Override
    public boolean isAlive() {
        if (currentLifeSpan > lifespan) return false;
        return summonedEntity.isValid();
    }

    @Override
    public String getUniqueId() {
        return inbuiltName + "::" + getClass().hashCode();
    }

    @Override
    public abstract AbstractCustomEntity clone();


    // TODO: Create a builder and use that instead of separate classes
    protected static abstract class AbstractCustomEntityBuilder<T extends AbstractCustomEntity, B extends AbstractCustomEntityBuilder> {

        T obj;

        // ----- Custom Entity Attributes -----

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
        int maxHealth;
        boolean scalesHealth;
        double healthScaleFactor;

        // Potion effects

        // Entity Appearance

        public AbstractCustomEntityBuilder() {
            obj = createObj();
        }

        public T build() {
            obj.hasBossbar = hasBossbar;
            obj.bossbarText = bossbarText;
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
}
