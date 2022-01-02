package se.fusion1013.plugin.cobaltmagick.wand;

import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.ProjectileModifierSpell;

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
    List<ProjectileModifierSpell> modifierSpells = new ArrayList<>();

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

    public CastParser addModifiers(List<ProjectileModifierSpell> modifierSpells){
        this.modifierSpells = modifierSpells;
        return this;
    }

    public List<ISpell> prepareCast(){
        List<ISpell> spellsToCast = new ArrayList<>();
        List<ProjectileModifierSpell> modifiers = new ArrayList<>();

        int castSpells = 0;

        // We do not check for mana usage here, that will be done in the casting stage

        for (int i = startPos; i < clonedSpells.size(); i++){

            ISpell cs = clonedSpells.get(i);
            ISpell actualSpellInstance = spells.get(i);

            // Check if the spell is able to be cast
            if (!actualSpellInstance.getHasCast() && castSpells < casts && actualSpellInstance.getCount() > 0){

                if (cs instanceof ProjectileModifierSpell){ // Adds projectile modifiers
                    modifiers.add((ProjectileModifierSpell) cs);
                } else {
                    // Apply modifiers
                    for (ProjectileModifierSpell pms : modifiers){
                        cs = pms.modifySpell(cs);
                        spellsToCast.add(pms);
                    }
                    for (ProjectileModifierSpell pms : this.modifierSpells){
                        cs = pms.modifySpell(cs);
                        spellsToCast.add(pms);
                    }
                    modifiers.clear();

                    cs.performPreCast(spells, casts, i);
                    spellsToCast.add(cs);
                    castSpells++;
                }

                actualSpellInstance.setHasCast(true); // TODO: Make sure this works as expected
                if (actualSpellInstance.getConsumeOnUse()) actualSpellInstance.setCount(actualSpellInstance.getCount() - 1);
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
