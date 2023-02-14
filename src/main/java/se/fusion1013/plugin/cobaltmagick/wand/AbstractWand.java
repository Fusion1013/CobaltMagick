package se.fusion1013.plugin.cobaltmagick.wand;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.RandomCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractWand {

    // Shown Properties
    boolean shuffle;
    int spellsPerCast;
    double castDelay;
    double rechargeTime;
    int manaMax;
    int manaChargeSpeed;
    int capacity;
    double spread;

    double currentMana;
    double castCooldown;
    double rechargeCooldown;

    double lastRechargeMax;

    // Hidden Properties
    int id;
    int wandTier;

    // Stored Spells
    List<SpellContainer> alwaysCast;
    List<SpellContainer> spells = new ArrayList<>();

    // Protection
    boolean regionAllowsManaRecharge = true;

    // Value override
    int modelDataOverride = 0;

    public AbstractWand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<SpellContainer> alwaysCast, int wandTier){
        this.shuffle = shuffle;
        this.spellsPerCast = spellsPerCast;
        this.castDelay = castDelay;
        this.rechargeTime = rechargeTime;
        this.manaMax = manaMax;
        this.manaChargeSpeed = manaChargeSpeed;
        this.capacity = capacity;
        this.spread = spread;
        this.alwaysCast = alwaysCast;
        this.wandTier = wandTier;
        this.currentMana = manaMax;

        this.lastRechargeMax = rechargeTime;
    }

    public AbstractWand(int cost, int level, boolean forceUnshuffle){
        generateRandomWand(cost, level, forceUnshuffle);
    }

    public AbstractWand(AbstractWand target) {
        this.shuffle = target.shuffle;
        this.spellsPerCast = target.spellsPerCast;
        this.castDelay = target.castDelay;
        this.rechargeTime = target.rechargeTime;
        this.manaMax = target.manaMax;
        this.manaChargeSpeed = target.manaChargeSpeed;
        this.capacity = target.capacity;
        this.spread = target.spread;
        this.alwaysCast = target.alwaysCast;
        this.wandTier = target.wandTier;
        this.currentMana = target.manaMax;

        this.lastRechargeMax = target.lastRechargeMax;

        this.spells = new ArrayList<>(target.getSpellContainers());
        this.alwaysCast = new ArrayList<>(target.getAlwaysCastContainers());

        this.id = target.id;
        this.modelDataOverride = target.modelDataOverride;
    }

    private void generateRandomWand(int cost, int level, boolean forceUnshuffle){
        Random r = new Random();

        if (level == 1 && r.nextInt(0, 101) < 50) cost = cost + 5;

        // Create WandStructure
        cost = cost + r.nextInt(-3, 4);
        int manaChargeSpeed = 50 * level + r.nextInt(-5, 5*level + 1);
        int manaMax = 50 + (150 * level) + (r.nextInt(-5, 6) * 10);

        WandStructure wandStructure = new WandStructure(cost, manaChargeSpeed, manaMax, forceUnshuffle);

        int p;

        // Slow mana charger
        p = r.nextInt(0, 101);
        if (p < 20){
            wandStructure.manaChargeSpeed = (50 * level + r.nextInt(-5, 5*level + 1)) / 5;
            wandStructure.manaMax = (50 + (150 * level) + (r.nextInt(-5, 6) * 10)) * 3;
        }

        // Really fast mana charger
        p = r.nextInt(0, 101);
        if (p < 15){
            wandStructure.manaChargeSpeed = (50 * level + r.nextInt(-5, 5*level + 1)) * 5;
            wandStructure.manaMax = (50 + (150 * level) + (r.nextInt(-5, 6) * 10)) / 3;
        }

        // Clamp manaMax and manaChargeSpeed to minimum values
        if (wandStructure.manaMax < 50) wandStructure.manaMax = 50;
        if (wandStructure.manaChargeSpeed < 10) wandStructure.manaChargeSpeed = 10;

        p = r.nextInt(0, 101);
        if (p < 15 + level*6){
            wandStructure.forceUnshuffle = true;
        }

        p = r.nextInt(0, 101);
        if (p < 5){
            wandStructure.isRare = true;
            wandStructure.cost = wandStructure.cost + 65;
        }

        String[] variables01 = {"reload_time", "fire_rate_wait", "spread_degrees", "speed_multiplier"};
        String[] variables02 = {"deck_capacity"};
        String[] variables03 = {"shuffle_deck_when_empty", "actions_per_round"};

        shuffleTable(variables01);
        if (!wandStructure.forceUnshuffle) shuffleTable(variables03);

        for (String item : variables01) {
            applyRandomVariable(wandStructure, item);
        }
        for (String value : variables02) {
            applyRandomVariable(wandStructure, value);
        }
        for (String s : variables03) {
            applyRandomVariable(wandStructure, s);
        }

        if (wandStructure.cost > 5 && r.nextInt(0, 1001) < 995){
            if (wandStructure.shuffleDeckWhenEmpty){
                wandStructure.deckCapacity = wandStructure.deckCapacity + (int)(wandStructure.cost/5);
                wandStructure.cost = 0;
            } else {
                wandStructure.deckCapacity = wandStructure.deckCapacity + (int)(wandStructure.cost / 10);
                wandStructure.cost = 0;
            }
        }

        wandStructure.deckCapacity = (int)clamp(2, wandStructure.deckCapacity, 27);

        if (wandStructure.deckCapacity <= 1) wandStructure.deckCapacity = 2;

        if (wandStructure.reloadTime >= 60) {
            randomAddActionsPerRound(wandStructure);

            if (r.nextInt(0, 101) < 50){
                int newActionsPerRound = wandStructure.deckCapacity;
                for (int i = 0; i < 6; i++){
                    if (wandStructure.actionsPerRound < wandStructure.deckCapacity+1){
                        int tempActionsPerRound = r.nextInt(wandStructure.actionsPerRound, wandStructure.deckCapacity+1);
                        if (tempActionsPerRound < newActionsPerRound){
                            newActionsPerRound = tempActionsPerRound;
                        }
                    }
                }
                wandStructure.actionsPerRound = newActionsPerRound;
            }
        }
        wandStructure.actionsPerRound = (int)clamp(1, wandStructure.actionsPerRound, wandStructure.deckCapacity);

        this.shuffle = wandStructure.shuffleDeckWhenEmpty;
        this.spellsPerCast = wandStructure.actionsPerRound;
        this.castDelay = wandStructure.fireRateWait;
        this.rechargeTime = wandStructure.reloadTime;
        this.manaMax = wandStructure.manaMax;
        this.manaChargeSpeed = wandStructure.manaChargeSpeed;
        this.capacity = wandStructure.deckCapacity;
        this.spread = wandStructure.spreadDegrees;
        this.alwaysCast = new ArrayList<>();
        this.wandTier = level;
        this.currentMana = wandStructure.manaMax;

        // Select random spells to add to the wand
        RandomCollection<ISpell> randomSpellCollection = SpellManager.getWeightedSpellCollection(level-1);

        int nSpellsToAdd = r.nextInt(0, capacity);
        List<SpellContainer> modifierSpells = new ArrayList<>();
        List<SpellContainer> projectileSpells = new ArrayList<>();
        for (int i = 0; i < nSpellsToAdd; i++) {
            ISpell spellToAdd = randomSpellCollection.next();
            if (spellToAdd == null) continue;

            SpellContainer spellContainer = new SpellContainer(spellToAdd.getId(), spellToAdd.getCount());

            if (spellToAdd instanceof MovableSpell) projectileSpells.add(spellContainer);
            else modifierSpells.add(spellContainer);
        }

        // Insert spells into the wand
        // Make sure modifier spells are put before projectile spells
        this.spells.addAll(modifierSpells);
        this.spells.addAll(projectileSpells);
    }

    private void randomAddActionsPerRound(WandStructure gun){
        Random r = new Random();
        gun.actionsPerRound = gun.actionsPerRound + 1;
        if (r.nextInt(0, 101) < 70){
            randomAddActionsPerRound(gun);
        }
    }

    private String[] shuffleTable(String[] t){
        int iterations = t.length;
        int j;
        Random r = new Random();

        for (int i = 1; i < iterations; i++){
            j = r.nextInt(0, i);
            String s1 = t[i];
            String s2 = t[j];
            t[i] = s2;
            t[j] = s1;
        }
        return t;
    }

    double randReloadTimeMin = .5;
    double randReloadTimeMax = 6.0;
    double randReloadTimeMean = 30;

    double randFireRateWaitMin = .1;
    double randFireRateWaitMax = 3.0;
    double randFireRateMean = 5;

    double randSpreadDegreesMin = -5;
    double randSpreadDegreesMax = 30;
    double randSpreadDegreesMean = 0;

    double randSpeedMultiplierMin = .8;
    double randSpeedMultiplierMax = 1.2;
    double randSpeedMultiplierMean = 1;

    double randDeckCapacityMin = 3;
    double randDeckCapacityMax = 10;
    double randDeckCapacityMean = 6;

    double randActionsPerRoundMin = 1;
    double randActionsPerRoundMax = 3;
    double randActionsPerRoundMean = 1;

    private void applyRandomVariable(WandStructure wandStructure, String variable){
        Random r = new Random();

        double cost = wandStructure.cost;

        switch (variable){
            case "reload_time":
                double min = clamp(1, 60-(cost*5), 240);
                double max = 1024;

                wandStructure.reloadTime = clamp(min, randDistr(randReloadTimeMin, randReloadTimeMax, randReloadTimeMean), max);
                wandStructure.cost = wandStructure.cost - ((60 - wandStructure.reloadTime) / 5);
                break;

            case "fire_rate_wait":
                min = clamp(-50, 16-cost, 50);
                max = 50;
                wandStructure.fireRateWait = clamp(min, randDistr(randFireRateWaitMin, randFireRateWaitMax, randFireRateMean), max);
                wandStructure.cost = wandStructure.cost - (16 - wandStructure.fireRateWait);
                break;

            case "spread_degrees":
                min = clamp(-35, cost / -1.5, 35);
                max = 35;
                wandStructure.spreadDegrees = clamp(min, randDistr(randSpreadDegreesMin, randSpreadDegreesMax, randSpreadDegreesMean), max);
                wandStructure.cost = wandStructure.cost - (16 - wandStructure.spreadDegrees);
                break;

            case "speed_multiplier":
                wandStructure.speedMultiplier = randDistr(randSpeedMultiplierMin, randSpeedMultiplierMax, randSpeedMultiplierMean);
                break;

            case "deck_capacity":
                min = 1;
                max = clamp(1, (cost/5)+6, 20);

                if (wandStructure.forceUnshuffle){
                    min = 1;
                    max = ((cost-15)/5);
                    if (max > 6) max = 6 + ((cost - (15+6*5))/10);
                }

                max = clamp(1, max, 20);
                wandStructure.deckCapacity = (int)clamp(min, randDistr(randDeckCapacityMin, randDeckCapacityMax, randDeckCapacityMean), max);
                wandStructure.cost = wandStructure.cost - ((wandStructure.deckCapacity-6)*5);
                break;

            case "shuffle_deck_when_empty":
                int random = r.nextInt(0, 2);
                if (wandStructure.forceUnshuffle){
                    random = 1;
                    if (cost < (15+wandStructure.deckCapacity*5)){
                        // This should not happen
                        CobaltMagick.getInstance().getLogger().info("Something went wrong when generating random wand");
                    }
                }

                if (random == 1 && cost >= (15 + wandStructure.deckCapacity * 5) && wandStructure.deckCapacity <= 9){
                    wandStructure.shuffleDeckWhenEmpty = false;
                    wandStructure.cost = wandStructure.cost - ((15-wandStructure.deckCapacity*5));
                }
                break;

            case "actions_per_round":
                int[] actionCosts = new int[]{0, 5+(wandStructure.deckCapacity*2), (int)(15+(wandStructure.deckCapacity*3.5)), 35+(wandStructure.deckCapacity*5), 45+(wandStructure.deckCapacity* wandStructure.deckCapacity)};

                min = 1;
                max = 1;

                for (int i = 0; i < actionCosts.length; i++){
                    int acost = actionCosts[i];
                    if (acost <= cost){
                        max = i;
                    }
                }
                max = clamp(1, wandStructure.deckCapacity, max);

                wandStructure.actionsPerRound = (int)Math.floor(clamp(min, randDistr(randActionsPerRoundMin, randActionsPerRoundMax, randActionsPerRoundMean), max));
                double tempCost = actionCosts[(int)clamp(1, wandStructure.actionsPerRound, actionCosts.length)];
                wandStructure.cost = wandStructure.cost - tempCost;
                break;
        }
    }

    private double randDistr(double min, double max, double mean){
        Random r = new Random();
        double gauss = r.nextGaussian();
        double diff = Math.max(max - mean, min - mean);
        double gs = clamp(min, (gauss * diff) + mean, max);
        return gs;
    }

    private double clamp(double min, double v, double max){
        return Math.max(min, Math.min(max, v));
    }

    private static class WandStructure {
        public double cost;
        public int deckCapacity = 0;
        public int actionsPerRound = 0;
        public double reloadTime = 0;
        public boolean shuffleDeckWhenEmpty = true;
        public double fireRateWait = 0;
        public double spreadDegrees = 0;
        public double speedMultiplier = 0;
        public double probUnshuffle = .1;
        public double probDrawMany = .15;
        public int manaChargeSpeed;
        public int manaMax;
        public boolean forceUnshuffle = false;
        public boolean isRare = false;

        public WandStructure(int cost, int manaChargeSpeed, int manaMax){
            this.cost = cost;
            this.manaChargeSpeed = manaChargeSpeed;
            this.manaMax = manaMax;
        }

        public WandStructure(int cost, int manaChargeSpeed, int manaMax, boolean forceUnshuffle) {
            this.cost = cost;
            this.manaChargeSpeed = manaChargeSpeed;
            this.manaMax = manaMax;
            if (forceUnshuffle) shuffleDeckWhenEmpty = false;
        }
    }

    // ----- WAND CACHE -----

    static NamespacedKey wandKey = new NamespacedKey(CobaltMagick.getInstance(), "wand_id");

    /**
     * Returns the wand key
     *
     * @return wand key
     */
    public static NamespacedKey getWandKey() { return wandKey; }

    // ----- Getters / Setters -----

    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
    }

    /**
     * Retrieves a wand from the cache from the given ItemStack
     * @param stack item to parse the wand from
     * @return a wand from the cache. Null if no wand was found
     */
    public static Wand getWand(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;
        Integer wandId = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.INTEGER);
        if (wandId != null) {
            return WandManager.getInstance().getWandFromCache(wandId);
        }
        return null;
    }

    public ItemStack getWandItem(){
        ItemStack is = new ItemStack(Material.LEATHER_HORSE_ARMOR, 1);
        ItemMeta meta = is.getItemMeta();
        if (meta == null) return is;

        NamespacedKey namespacedKey = new NamespacedKey(CobaltMagick.getInstance(), "wand_id");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, id);

        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Wand");
        meta.lore(getLore());

        meta.setCustomModelData(getWandModelData());

        if (meta instanceof LeatherArmorMeta leatherArmorMeta){
            Random r = new Random();
            leatherArmorMeta.setColor(Color.fromRGB(r.nextInt(0, 0xffffff)));
        }

        is.setItemMeta(meta);
        return is;
    }

    /**
     * Gets the wand model depending on the stats of the wand
     *
     * @return model id for this wand
     */
    public int getWandModelData(){
        if (modelDataOverride != 0) return modelDataOverride;

        int data = 1110;

        if (shuffle) data += 1000;

        if (capacity > 14) data += 200;
        else if (capacity > 4) data += 100;

        if (spellsPerCast >= 3) data += 20;
        else if (spellsPerCast >= 2) data += 10;

        Random r = new Random();
        data += r.nextInt(0, 10);
        return data;
    }

    /**
     * Gets the lore for the wand
     *
     * @return a list of strings representing the lore
     */
    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();

        lore.add(Component.text(""));

        lore.add(
                Component.text(getSpellString())
                        .color(NamedTextColor.WHITE)
                        .font(Key.key("minecraft:spells"))
                        .decoration(TextDecoration.ITALIC, false)
        );

        if (shuffle) lore.add(Component.text("Shuffle: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text("Yes").color(NamedTextColor.BLUE)));
        else lore.add(Component.text("Shuffle: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text("No").color(NamedTextColor.BLUE)));

        lore.add(Component.text("Spells/Cast: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text(spellsPerCast).color(NamedTextColor.BLUE)));
        lore.add(Component.text("Cast Delay: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text((double)Math.round(castDelay * 100) / 100).color(NamedTextColor.BLUE).append(Component.text("s").color(NamedTextColor.WHITE))));
        lore.add(Component.text("Recharge Time: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text((double)Math.round(rechargeTime * 100) / 100).color(NamedTextColor.BLUE).append(Component.text("s").color(NamedTextColor.WHITE))));
        lore.add(Component.text("Mana Max: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text(manaMax).color(NamedTextColor.BLUE)));
        lore.add(Component.text("Mana Charge Speed: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text(manaChargeSpeed).color(NamedTextColor.BLUE)));
        lore.add(Component.text("Capacity: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text(capacity).color(NamedTextColor.BLUE)));
        lore.add(Component.text("Spread: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text((double)Math.round(spread * 10) / 10).color(NamedTextColor.BLUE).append(Component.text(" DEG").color(NamedTextColor.WHITE))));
        lore.add(Component.text("id# ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.WHITE).append(Component.text(id).color(NamedTextColor.BLUE)));

        return lore;
    }

    private String getSpellString() {
        StringBuilder spellString = new StringBuilder();
        for (SpellContainer spellContainer : spells) {
            ISpell spell = SpellManager.getSpell(spellContainer.spellId);
            if (spell == null) continue;

            spellString.append(spell.getHexIcon()).append(" ");
        }
        return spellString.toString();
    }

    public void setSpells(List<ISpell> spells) {
        this.spells.clear();
        for (ISpell spell : spells) this.spells.add(new SpellContainer(spell.getId(), spell.getCount()));
    }

    public void setSpellContainers(List<SpellContainer> containers) {
        this.spells = containers;
    }

    public List<ISpell> getSpells() {
        List<ISpell> spellList = new ArrayList<>();
        for (SpellContainer spellContainer : spells) {
            spellList.add(spellContainer.getSpell());
        }
        return spellList;
    }

    public List<SpellContainer> getSpellContainers() {
        return spells;
    }

    public AbstractWand addSpell(ISpell spell) {
        this.spells.add(new SpellContainer(spell.getId(), spell.getCount()));
        return this;
    }

    public AbstractWand addSpells(ISpell... spells) {
        for (ISpell spell : spells) addSpell(spell);
        return this;
    }

    public AbstractWand overrideModelData(int data) {
        this.modelDataOverride = data;
        return this;
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public boolean isShuffle() {
        return shuffle;
    }

    public int getSpellsPerCast() {
        return spellsPerCast;
    }

    public double getCastDelay() {
        return castDelay;
    }

    public double getRechargeTime() {
        return rechargeTime;
    }

    /**
     * Gets the current recharge cooldown.
     *
     * @return the recharge cooldown.
     */
    public double getRechargeCooldown() {
        return rechargeCooldown;
    }

    public int getManaMax() {
        return manaMax;
    }

    public int getManaChargeSpeed() {
        return manaChargeSpeed;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getSpread() {
        return spread;
    }

    public void setAlwaysCast(List<ISpell> spells) {
        this.alwaysCast.clear();
        for (ISpell spell : spells) this.alwaysCast.add(new SpellContainer(spell.getId(), spell.getCount()));
    }

    public List<ISpell> getAlwaysCast() {
        List<ISpell> spellList = new ArrayList<>();
        for (SpellContainer spellContainer : alwaysCast) {
            spellList.add(spellContainer.getSpell());
        }
        return spellList;
    }

    public List<SpellContainer> getAlwaysCastContainers() {
        return alwaysCast;
    }

    public int getWandTier() {
        return wandTier;
    }

    public void setRegionAllowsManaRecharge(boolean isAllowed) {
        regionAllowsManaRecharge = isAllowed;
    }

    public void setRechargeCooldown(double rechargeCooldown) {
        this.rechargeCooldown = rechargeCooldown;
    }

    /**
     * Increases the mana of the wand by the given amount. Caps at 0 and <code>manaMax</code>.
     * Negative values will decrease the mana of the wand.
     *
     * @param manaIncrease the amount to increase the mana by
     */
    public void increaseMana(int manaIncrease){
        currentMana = Math.max(0, Math.min(currentMana+manaIncrease, manaMax));
    }



    // ----- ENUMERATORS -----

    /**
     * Result of a wand cast
     */
    public enum CastResult{
        SUCCESS,
        RECHARGE_TIME,
        CAST_DELAY,
        NO_MANA
    }
}
