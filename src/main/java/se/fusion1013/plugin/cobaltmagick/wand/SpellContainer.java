package se.fusion1013.plugin.cobaltmagick.wand;

import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

public class SpellContainer {

    public final int spellId;
    private int itemCount;

    public SpellContainer(int spellId, int itemCount) {
        this.spellId = spellId;
        this.itemCount = itemCount;
    }

    public ISpell getSpell() {
        ISpell spell = SpellManager.getSpell(spellId);
        if (spell == null) return null;

        spell.setCount(itemCount);
        return spell;
    }

    //region ITEM_COUNT

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int newCount) {
        itemCount = newCount;
    }

    public void decreaseItemCount() {
        itemCount--;
    }

    //endregion
}
