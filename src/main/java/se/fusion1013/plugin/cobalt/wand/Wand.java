package se.fusion1013.plugin.cobalt.wand;

import net.md_5.bungee.api.ChatColor;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.spells.ISpell;

import java.util.ArrayList;
import java.util.List;

public class Wand {

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

    // TODO: IMPLEMENT A WAND CACHE. CALLING THE DATABASE EVERY TIME A WAND IS USED IS EXTREMELY EXPENSIVE

    /**
     * Cast the next spells in the wand
     * @return The result of the casting
     */
    public CastResult cast(){

        // Check if the wand is on cooldown
        if (castCooldown > 0) return CastResult.CAST_DELAY;
        else if (rechargeCooldown > 0) return CastResult.RECHARGE_TIME;

        // Cast all the always cast spells. These spells get cast for free
        for (ISpell s : alwaysCast){
            s.castSpell(this);
        }

        int numSpellsToCast = spellsPerCast;
        int manaUsed = 0;
        for (int i = 0; i < numSpellsToCast; i++){
            ISpell spellToCast = spells.get(i);

            // If the spell has already been cast, don't cast it again and move on to the next spell
            if (spellToCast.getHasCast()){
                i--;
            } else {
                // Check Mana
                manaUsed += spellToCast.getManaDrain();
                if (manaUsed > currentMana) return CastResult.NO_MANA;

                // Cast Spell
                numSpellsToCast += spellToCast.getAddCasts();
                spellToCast.castSpell(this);
            }
        }

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

    public void setSpells(List<ISpell> spells){
        this.spells = spells;
    }

    public enum CastResult{
        SUCCESS,
        RECHARGE_TIME,
        CAST_DELAY,
        NO_MANA
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
