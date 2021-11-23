package se.fusion1013.plugin.cobalt.wand;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.spells.ISpell;

import java.util.ArrayList;
import java.util.List;

public class Wand {

    // Cache
    static List<Wand> wandCache = new ArrayList<>();

    /**
     * Adds a wand to the cache. This should always be done on plugin load for all wands in the database
     *
     * @param wand the wand to add to the cache
     */
    public static void addWandToCache(Wand wand) { wandCache.add(wand); }

    /**
     * Gets a specific wand from the cache
     *
     * @param id the id of the wand to get
     * @return a wand or null
     */
    public static Wand getWandFromCache(int id) {
        for (Wand wand : wandCache) {
            if (wand.getId() == id) return wand;
        }
        return null;
    }

    /**
     * Gets all wands in the database and adds them to the cache for easy retrieval
     */
    public static void loadCacheFromDatabase() {
        List<Wand> wandsToCache = Cobalt.getInstance().getRDatabase().getWands();
        wandCache = new ArrayList<>(wandsToCache);
    }

    // Shown Properties
    boolean shuffle;
    int spellsPerCast;

    double castDelay;
    double rechargeTime;
    int manaMax;
    int manaChargeSpeed;
    int capacity;
    double spread;
    List<ISpell> alwaysCast;

    int currentMana;
    double castCooldown;
    double rechargeCooldown;

    // Hidden Properties
    int id;
    int wandTier;
    int wandId; // This should be unique to all wands

    // Wand Key

    static NamespacedKey wandKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");

    /**
     * Returns the wand key
     *
     * @return wand key
     */
    public static NamespacedKey getWandKey() { return wandKey; }

    // Stored Spells
    List<ISpell> spells = new ArrayList<>();

    public Wand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<ISpell> alwaysCast, int wandTier){
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
    }

    public void castSpells(Player p){
        CastResult result = castSpells(p, false);

        LocaleManager localeManager = LocaleManager.getInstance();
        switch (result){
            case CAST_DELAY:
                localeManager.sendMessage(p, "wand.spell.cast.cast_delay");
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case RECHARGE_TIME:
                localeManager.sendMessage(p, "wand.spell.cast.recharge_time");
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case NO_MANA:
                localeManager.sendMessage(p, "wand.spell.cast.no_mana");
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case SUCCESS:
                break;
        }
    }

    // TODO: Replace chat messages with something else
    /**
     * Cast the next spells in the wand
     * @return The result of the casting
     */
    private CastResult castSpells(Player p, boolean update){
        Cobalt.getInstance().getLogger().info("Casting spells in wand...");
        Cobalt.getInstance().getLogger().info("There are currently " + spells.size() + " spells in this wand");
        int spellsCast = 0;

        // Check if the wand is on cooldown
        if (castCooldown > 0) {
            return CastResult.CAST_DELAY;
        }
        else if (rechargeCooldown > 0) {
            return CastResult.RECHARGE_TIME;
        }

        // Cast all the always cast spells. These spells get cast for free
        for (ISpell s : alwaysCast){
            s.castSpell(this, p);
        }

        int numSpellsToCast = spellsPerCast;
        int manaUsed = 0;
        for (int i = 0; i < numSpellsToCast; i++){
            if (spells.size() <= i) break;
            ISpell spellToCast = spells.get(i);

            // If the spell has already been cast, don't cast it again and move on to the next spell
            if (spellToCast.getHasCast()){
                i--;
            } else {
                // Check Mana
                manaUsed += spellToCast.getManaDrain();
                if (manaUsed > currentMana) {
                    currentMana = 0;
                    return CastResult.NO_MANA;
                }

                // Cast Spell
                numSpellsToCast += spellToCast.getAddCasts();
                spellsCast++;
                spellToCast.castSpell(this, p);
            }
        }

        currentMana -= manaUsed;
        Cobalt.getInstance().getLogger().info(spellsCast + " spells cast");

        return CastResult.SUCCESS;
    }

    /**
     * Checks if all spells in the wand have been cast
     * @return If all spells have benn cast
     */
    private boolean allSpellsCast(){
        for (ISpell s : spells){
            if (!s.getHasCast()) return false;
        }
        return true;
    }

    public enum CastResult{
        SUCCESS,
        RECHARGE_TIME,
        CAST_DELAY,
        NO_MANA
    }

    // ----- Getters / Setters -----

    /**
     * Retrieves a wand from the cache from the given ItemStack
     * @param stack item to parse the wand from
     * @return a wand from the cache. Null if no wand was found
     */
    public static Wand getWand(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        Integer wandId = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.INTEGER);
        if (wandId != null) {
            return getWandFromCache(wandId);
        }
        return null;
    }

    public ItemStack getWandItem(){
        ItemStack is = new ItemStack(Material.STICK, 1);
        ItemMeta meta = is.getItemMeta();

        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, id);

        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Wand");
        meta.setLore(getLore());

        is.setItemMeta(meta);
        return is;
    }

    public List<String> getLore(){
        List<String> lore = new ArrayList<>();

        if (shuffle) lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Shuffle: " + ChatColor.BLUE + "Yes");
        else lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Shuffle: " + ChatColor.BLUE + "No");
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Spells/Cast: " + ChatColor.BLUE + spellsPerCast);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Cast Delay: " + ChatColor.BLUE + castDelay);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Recharge Time: " + ChatColor.BLUE + rechargeTime);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Mana Max: " + ChatColor.BLUE + manaMax);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Mana Charge Speed: " + ChatColor.BLUE + manaChargeSpeed);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Capacity: " + ChatColor.BLUE + capacity);
        lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Spread: " + ChatColor.BLUE + spread);
        // TODO: Add always casts

        // TODO: Find a way to display the spells that are currently in the wand (Could probably use bundles in the future)

        return lore;
    }

    public void setSpells(List<ISpell> spells){
        this.spells = spells;
    }
    public List<ISpell> getSpells() { return this.spells; }

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

    public void setAlwaysCast(List<ISpell> spells) { this.alwaysCast = spells; }
    public List<ISpell> getAlwaysCast() {
        return alwaysCast;
    }

    public int getWandTier() {
        return wandTier;
    }

    public int getWandId() {
        return wandId;
    }
}
