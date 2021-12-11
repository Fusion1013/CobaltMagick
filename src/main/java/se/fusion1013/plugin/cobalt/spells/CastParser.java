package se.fusion1013.plugin.cobalt.spells;

import se.fusion1013.plugin.cobalt.Cobalt;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a list of spells to cast. Does not actually cast the spells
 */
public class CastParser {

    List<ISpell> spells;
    List<ISpell> clonedSpells;
    int casts;
    int startPos;

    public CastParser(List<ISpell> spells, int casts){
        this.spells = spells;
        this.clonedSpells = cloneSpellList(spells);
        this.casts = casts;
        this.startPos = 0;
    }

    public CastParser(List<ISpell> spells, int casts, int startPos){
        this(spells, casts);
        this.startPos = startPos;
    }

    public List<ISpell> prepareCast(){
        List<ISpell> spellsToCast = new ArrayList<>();
        List<IModifier> modifiers = new ArrayList<>();

        int castSpells = 0;

        // We do not check for mana usage here, that will be done in the casting stage

        for (int i = startPos; i < clonedSpells.size(); i++){

            ISpell cs = clonedSpells.get(i);
            ISpell actualSpellInstance = spells.get(i);

            // Check if the spell is able to be cast
            if (!actualSpellInstance.getHasCast() && castSpells < casts){

                if (cs instanceof IModifier){ // Adds projectile modifiers
                    modifiers.add((IModifier) cs);
                } else {
                    // Adds projectile modifiers to the spell and clears the modifiers list
                    cs.addModifiers(modifiers);
                    modifiers.clear();

                    cs.performPreCast(spells, casts, i);
                    spellsToCast.add(cs);
                    castSpells++;
                }

                actualSpellInstance.setHasCast(true); // TODO: Make sure this works as expected
            }
        }
        return spellsToCast;
    }

    /**
     * Clones a list of spells
     *
     * @param spellsToClone the list of spells to clone
     * @return a clone of the list
     */
    private List<ISpell> cloneSpellList(List<ISpell> spellsToClone){
        List<ISpell> clone = new ArrayList<>();
        for (ISpell s : spellsToClone){
            clone.add(s.clone());
        }
        return clone;
    }
}
