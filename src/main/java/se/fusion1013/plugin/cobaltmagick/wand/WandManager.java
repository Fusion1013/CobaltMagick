package se.fusion1013.plugin.cobaltmagick.wand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.wand.IWandDao;
import se.fusion1013.plugin.cobaltmagick.manager.WorldGuardManager;
import se.fusion1013.plugin.cobaltmagick.protection.CustomWorldGuardFlags;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandManager extends Manager implements Runnable {

    // ----- VARIABLES -----

    private static final Map<Integer, Wand> WAND_CACHE = new HashMap<>(); // Stores cached wands. <id, wand>

    // ----- CONSTRUCTORS -----

    public WandManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- WAND CREATION -----

    /**
     * Creates a new <code>Wand</code>. Handles cache & database insertion. Do not use this method for <code>Wand</code>'s that are supposed to be temporary.
     *
     * @param shuffle weather the <code>Wand</code> should shuffle spells or not.
     * @param spellsPerCast the number of spells the <code>Wand</code> casts on every spell cast.
     * @param castDelay the delay between individual spells in the <code>Wand</code> being cast.
     * @param rechargeTime the time it takes for the <code>Wand</code> to recharge.
     * @param manaMax the maximum mana of the <code>Wand</code>.
     * @param manaChargeSpeed the amount of mana that the <code>Wand</code> recharges each tick.
     * @param capacity the max number of spells the <code>Wand</code> can hold.
     * @param spread the spread of the <code>Wand</code>.
     * @param alwaysCast the spells that are always cast on each spell cast.
     * @param wandTier the tier of the <code>Wand</code>.
     * @return the created <code>Wand</code>.
     */
    public Wand createWand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<ISpell> alwaysCast, int wandTier) {
        // Create the wand
        Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);

        // Insert wand into the database & wand cache
        insertWand(wand);

        return wand;
    }

    /**
     * Creates a new <code>Wand</code> with semi-randomized statistics based on the input. Handles cache & database insertion. Do not use this method for <code>Wand</code>'s that are supposed to be temporary.
     *
     * @param cost the cost of the <code>Wand</code>.
     * @param level the level of the <code>Wand</code>.
     * @param forceUnshuffle weather the <code>Wand</code> should be forced to not be shuffled.
     * @return the created <code>Wand</code>.
     */
    public Wand createWand(int cost, int level, boolean forceUnshuffle) {
        // Create the wand
        Wand wand = new Wand(cost, level, forceUnshuffle);

        // Insert wand into the database & wand cache
        insertWand(wand);

        return wand;
    }

    /**
     * Inserts a <code>Wand</code> into the database & wand cache. Sets the id of the wand.
     *
     * @param wand the <code>Wand</code> to insert into the database & cache.
     */
    private void insertWand(Wand wand) {
        // Set id of wand
        wand.setId(WAND_CACHE.size());

        // Insert wand into cache
        WAND_CACHE.put(wand.getId(), wand);

        // Insert wand into database
        DataManager.getInstance().getDao(IWandDao.class).insertWandAsync(wand);
    }

    // ----- WAND RECHARGING -----

    // TODO: Replace with more performant system

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            List<Wand> wandList = findWandsInPlayerInventory(p);
            boolean rechargeAllowed = true;
            if (WorldGuardManager.isEnabled()) rechargeAllowed = CustomWorldGuardFlags.isManaRechargeAllowed(p, p.getLocation());

            for (Wand wand : wandList) {
                wand.setRegionAllowsManaRecharge(rechargeAllowed);
            }
        }
    }

    private List<Wand> findWandsInPlayerInventory(Player p) {
        Inventory inventory = p.getInventory();
        List<Wand> wandList = new ArrayList<>();

        for (ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                Wand wand = Wand.getWand(stack);
                if (wand != null) wandList.add(wand);
            }
        }

        return wandList;
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets a <code>Wand</code> from the wand cache.
     *
     * @param id the id of the <code>Wand</code>.
     * @return a <code>Wand</code> with the given id, or null if a <code>Wand</code> with that id is not in the cache.
     */
    public Wand getWandFromCache(int id) {
        return WAND_CACHE.get(id);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Load wands from database
        List<Wand> wands = DataManager.getInstance().getDao(IWandDao.class).getWands();
        for (Wand wand : wands) {
            WAND_CACHE.put(wand.id, wand);
        }

        // Schedule wand task
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 20); // TODO: Track using events instead
    }

    @Override
    public void disable() {
        CobaltMagick.getInstance().getLogger().info("Saving wands...");
        DataManager.getInstance().getDao(IWandDao.class).updateWandSpellsSync(WAND_CACHE.values().toArray(new Wand[0]));
        // Wand.saveAllWandData();
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static WandManager INSTANCE = null;
    /**
     * Returns the object representing this <code>WandManager</code>.
     *
     * @return The object of this class
     */
    public static WandManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new WandManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
