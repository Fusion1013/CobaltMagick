package se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpellModifier<B extends AbstractSpellModifier> implements SpellModifier, Cloneable {

    public AbstractSpellModifier() { }

    public AbstractSpellModifier(AbstractSpellModifier target){
    }

    public static List<SpellModifier> cloneList(List<SpellModifier> list) {
        List<SpellModifier> clone = new ArrayList<SpellModifier>(list.size());
        for (SpellModifier item : list) clone.add(item.clone());
        return clone;
    }

    @Override
    public abstract AbstractSpellModifier<B> clone();

    protected abstract B getThis();

    @Override
    public List<String> getExtraLore() {
        return new ArrayList<>();
    }
}
