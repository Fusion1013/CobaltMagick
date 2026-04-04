package se.fusion1013.cobaltmagick.spell;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

import java.util.Objects;

public final class SpellInstance implements ISpellInstance {
    private final ISpellTemplate spellTemplate;
    private String state;
    private final int slot;

    public SpellInstance(ISpellTemplate spellTemplate, String state, int slot) {
        this.spellTemplate = spellTemplate;
        this.state = state;
        this.slot = slot;
    }

    @Override
    public ItemStack getItemStack() {
        return spellTemplate.getItemStack();
    }

    @Override
    public String getInternalName() {
        return spellTemplate.getInternalName();
    }

    @Override
    public int getDraws() {
        return spellTemplate.getDraws();
    }

    @Override
    public int manaCost() {
        return spellTemplate.manaCost();
    }

    @Override
    public void modify(ShotState shotState, RuleLogger logger) {
        spellTemplate.modify(shotState, logger);
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    public ISpellTemplate spellTemplate() {
        return spellTemplate;
    }

    @Override
    public String state() {
        return state;
    }

    @Override
    public int slot() {
        return slot;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SpellInstance) obj;
        return Objects.equals(this.spellTemplate, that.spellTemplate) &&
                Objects.equals(this.state, that.state) &&
                this.slot == that.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spellTemplate, state, slot);
    }

    @Override
    public String toString() {
        return "SpellInstance[" +
                "spellTemplate=" + spellTemplate + ", " +
                "state=" + state + ", " +
                "slot=" + slot + ']';
    }

}
