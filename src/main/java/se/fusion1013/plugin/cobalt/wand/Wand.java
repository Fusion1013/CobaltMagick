package se.fusion1013.plugin.cobalt.wand;

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
    int wandTier;

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

    public enum CastResult{
        SUCCESS,
        RECHARGE_TIME,
        CAST_DELAY,
        NO_MANA
    }
}
