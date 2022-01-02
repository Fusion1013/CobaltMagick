package se.fusion1013.plugin.cobaltmagick.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;

public class SpellCastEvent extends Event implements Cancellable {

    private final ISpell spell;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public SpellCastEvent(ISpell spellCast){
        this.spell = spellCast;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    public ISpell getSpell(){
        return spell;
    }
}
