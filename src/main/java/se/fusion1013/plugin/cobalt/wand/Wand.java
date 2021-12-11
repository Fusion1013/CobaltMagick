package se.fusion1013.plugin.cobalt.wand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.database.Database;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.spells.CastParser;
import se.fusion1013.plugin.cobalt.spells.IModifier;
import se.fusion1013.plugin.cobalt.spells.ISpell;
import se.fusion1013.plugin.cobalt.spells.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wand implements Runnable { // TODO: Move things to abstract class and extend it

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
     * Gets all wands in the database and adds them to the cache for easy retrieval. Should be done on startup
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

    double currentMana;
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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cobalt.getInstance(), this, 0, 1);
    }

    public void castSpells(Player p){
        CastResult result = performSpellCast(p);

        LocaleManager localeManager = LocaleManager.getInstance();
        switch (result){
            case CAST_DELAY:
                // localeManager.sendMessage(p, "wand.spell.cast.cast_delay");
                // p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case RECHARGE_TIME:
                // localeManager.sendMessage(p, "wand.spell.cast.recharge_time");
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case NO_MANA:
                // localeManager.sendMessage(p, "wand.spell.cast.no_mana");
                p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);
                break;
            case SUCCESS:
                break;
        }
    }

    /**
     * Cast the next spells in the wand
     * @return The result of the casting
     */
    private CastResult performSpellCast(Player p){
        int spellsCast = 0;

        // Check if the wand is on cooldown
        if (castCooldown > 0) return CastResult.CAST_DELAY;
        else if (rechargeCooldown > 0) return CastResult.RECHARGE_TIME;

        // Cast all the always cast spells. These spells get cast for free
        for (ISpell s : alwaysCast){
            s.castSpell(this, p);
        }

        int manaUsed = 0;
        double castDelayInduced = castDelay;
        int startPos = 0;
        // if (shuffle)  startPos = new Random().nextInt(0, spells.size()); // TODO: Low chance of early spells in wand to be cast

        CastParser parser = new CastParser(spells, spellsPerCast, startPos);
        List<ISpell> spellsToCast = parser.prepareCast();

        for (ISpell s : spellsToCast){
            // Check mana
            manaUsed += s.getTrueManaDrain();

            if (manaUsed > currentMana) {
                currentMana = 0;
                if (allSpellsCast()) recharge(); // Recharge if all spells have been cast
                return CastResult.NO_MANA;
            }

            s.castSpell(this, p);
            castDelayInduced += s.getTrueCastDelay();
        }

        currentMana = Math.max(0, currentMana - manaUsed);
        castCooldown = Math.max(0, castDelayInduced);

        // Check if all spells in the wand has been cast. If they have, start recharge cooldown
        if (allSpellsCast()) recharge();

        return CastResult.SUCCESS;
    }

    private void recharge(){
        for (ISpell spell : spells){
            spell.setHasCast(false);
            rechargeCooldown += spell.getRechargeTime();
        }
        rechargeCooldown += rechargeTime;
    }

    /**
     * Checks if all spells in the wand have been cast
     *
     * @return If all spells have been cast
     */
    private boolean allSpellsCast(){
        for (ISpell s : spells){
            if (!s.getHasCast()) return false; // TODO: Check if the spell is a modifier
        }
        return true;
    }

    @Override
    public void run() {
        // Increase mana
        if (currentMana < manaMax) currentMana = Math.min(currentMana + ((double)manaChargeSpeed / 20.0), manaMax);

        // Decrease Cast Delay
        if (castCooldown > 0) castCooldown = Math.max(0, castCooldown - 0.05);

        // Decrease Recharge Time
        if (rechargeCooldown > 0) rechargeCooldown = Math.max(0, rechargeCooldown - 0.05);

        // Display data to player holding item
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack is = p.getInventory().getItemInMainHand();
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                Integer wandId = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.INTEGER);
                if (wandId != null){
                    if (wandId.equals(id)) {
                        displayData(p);
                    }
                }
            }
        }

        // Passive spells
    }

    /**
     * Displays wand data (Recharge & Mana) to the player
     *
     * @param p player to display the data to
     */
    private void displayData(Player p){
        double recharge = Math.max(rechargeCooldown, castCooldown);

        String message = ChatColor.RED + "Recharge" + ChatColor.GRAY + ": " + ChatColor.RED + Math.round(recharge*10)/10.0 + ChatColor.GRAY + "s" + "          " + ChatColor.AQUA + "Mana" + ChatColor.GRAY + ": " + ChatColor.AQUA + Math.round(currentMana);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    /**
     * Result of a wand cast
     */
    public enum CastResult{
        SUCCESS,
        RECHARGE_TIME,
        CAST_DELAY,
        NO_MANA
    }

    /**
     * Saves all wands that are currently in the wand cache to the database
     */
    public static void saveAllWandData() {
        Database db = Cobalt.getInstance().getRDatabase();
        db.updateWandSpells(wandCache);
    }

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

        NamespacedKey namespacedKey = new NamespacedKey(Cobalt.getInstance(), "wand_id");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, id);

        meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Wand");
        meta.setLore(getLore());

        meta.setCustomModelData(1);
        if (meta instanceof  LeatherArmorMeta){
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
            Random r = new Random();
            leatherArmorMeta.setColor(Color.fromRGB(r.nextInt(0, 0xffffff)));
        }

        is.setItemMeta(meta);
        return is;
    }

    public List<String> getLore(){
        List<String> lore = new ArrayList<>();

        if (shuffle) lore.add(ChatColor.WHITE + "Shuffle: " + ChatColor.BLUE + "Yes");
        else lore.add(ChatColor.WHITE + "Shuffle: " + ChatColor.BLUE + "No");
        lore.add(ChatColor.WHITE + "Spells/Cast: " + ChatColor.BLUE + spellsPerCast);
        lore.add(ChatColor.WHITE + "Cast Delay: " + ChatColor.BLUE + castDelay);
        lore.add(ChatColor.WHITE + "Recharge Time: " + ChatColor.BLUE + rechargeTime);
        lore.add(ChatColor.WHITE + "Mana Max: " + ChatColor.BLUE + manaMax);
        lore.add(ChatColor.WHITE + "Mana Charge Speed: " + ChatColor.BLUE + manaChargeSpeed);
        lore.add(ChatColor.WHITE + "Capacity: " + ChatColor.BLUE + capacity);
        lore.add(ChatColor.WHITE + "Spread: " + ChatColor.BLUE + spread);

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
}
