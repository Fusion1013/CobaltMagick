package se.fusion1013.plugin.cobaltmagick.wand;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.Database;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;

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
    List<ISpell> alwaysCast;

    double currentMana;
    double castCooldown;
    double rechargeCooldown;

    // Hidden Properties
    int id;
    int wandTier;
    int wandId; // This should be unique to all wands

    // Stored Spells
    List<ISpell> spells = new ArrayList<>();

    public AbstractWand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<ISpell> alwaysCast, int wandTier){
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




    // ----- WAND CACHE -----

    static List<Wand> wandCache = new ArrayList<>();
    static NamespacedKey wandKey = new NamespacedKey(CobaltMagick.getInstance(), "wand_id");

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
     * Gets all wands in the database and adds them to the cache for easy retrieval. Should be done on startup
     */
    public static void loadCacheFromDatabase() {
        List<Wand> wandsToCache = CobaltMagick.getInstance().getRDatabase().getWands();
        wandCache = new ArrayList<>(wandsToCache);
    }

    /**
     * Saves all wands that are currently in the wand cache to the database
     */
    public static void saveAllWandData() {
        Database db = CobaltMagick.getInstance().getRDatabase();
        db.updateWandSpells(wandCache);
    }

    /**
     * Returns the wand key
     *
     * @return wand key
     */
    public static NamespacedKey getWandKey() { return wandKey; }



    // ----- Getters / Setters -----

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
            return getWandFromCache(wandId);
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
        meta.setLore(getLore());

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
        int data = 111;

        if (shuffle) data += 100;

        if (capacity > 14) data += 20;
        else if (capacity > 4) data += 10;

        if (spellsPerCast >= 3) data += 2;
        else if (spellsPerCast >= 2) data += 1;

        return data;
    }

    /**
     * Gets the lore for the wand
     *
     * @return a list of strings representing the lore
     */
    public List<String> getLore(){
        List<String> lore = new ArrayList<>();

        if (shuffle) lore.add(ChatColor.WHITE + "Shuffle: " + ChatColor.BLUE + "Yes");
        else lore.add(ChatColor.WHITE + "Shuffle: " + ChatColor.BLUE + "No");
        lore.add(ChatColor.WHITE + "Spells/Cast: " + ChatColor.BLUE + spellsPerCast);
        lore.add(ChatColor.WHITE + "Cast Delay: " + ChatColor.BLUE + (double)Math.round(castDelay * 100) / 100 + ChatColor.WHITE + "s");
        lore.add(ChatColor.WHITE + "Recharge Time: " + ChatColor.BLUE + (double)Math.round(rechargeTime * 100) / 100 + ChatColor.WHITE + "s");
        lore.add(ChatColor.WHITE + "Mana Max: " + ChatColor.BLUE + manaMax);
        lore.add(ChatColor.WHITE + "Mana Charge Speed: " + ChatColor.BLUE + manaChargeSpeed);
        lore.add(ChatColor.WHITE + "Capacity: " + ChatColor.BLUE + capacity);
        lore.add(ChatColor.WHITE + "Spread: " + ChatColor.BLUE + (double)Math.round(spread * 10) / 10 + ChatColor.WHITE + " DEG");

        lore.add(ChatColor.GRAY + "id#" + id);
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
