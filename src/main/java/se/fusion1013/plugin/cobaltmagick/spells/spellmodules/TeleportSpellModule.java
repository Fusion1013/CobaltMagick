package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class TeleportSpellModule extends AbstractSpellModule<TeleportSpellModule> implements SpellModule {

    boolean cancelsCast;

    // Optional Variables
    int delay = 0;
    boolean teleportPlayer;
    boolean swapWithHit;

    public TeleportSpellModule(boolean cancelsCast){
        this.cancelsCast = cancelsCast;
    }

    public TeleportSpellModule(TeleportSpellModule target){
        this.cancelsCast = target.cancelsCast;
        this.delay = target.delay;
        this.teleportPlayer = target.teleportPlayer;
        this.swapWithHit = target.swapWithHit;
    }

    public TeleportSpellModule teleportPlayer(){
        this.teleportPlayer = true;
        return getThis();
    }

    public TeleportSpellModule swapWithHit(){
        this.swapWithHit = true;
        return getThis();
    }

    public TeleportSpellModule addDelay(int ticks){
        this.delay = ticks;
        return getThis();
    }

    @Override
    public void executeOnCast(Wand wand, Player caster, ISpell spell) {
        teleport(caster, spell.getLocation(), null);
    }

    @Override
    public void executeOnTick(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;
        teleport(caster, spell.getLocation(), null);
    }

    @Override
    public void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        if (!canRun) return;
        teleport(caster, spell.getLocation(), null);
    }

    @Override
    public void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit) {
        if (!canRun) return;
        teleport(caster, spell.getLocation(), entityHit);
    }

    @Override
    public void executeOnDeath(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;
        teleport(caster, spell.getLocation(), null);
    }

    private void teleport(Player caster, Location location, Entity entityHit){
        if (teleportPlayer) teleportPlayer(caster, location);
        if (swapWithHit && entityHit != null) swapTeleport(caster, entityHit);
    }

    private void swapTeleport(Player caster, Entity entityHit){
        Location e1 = caster.getLocation().clone();
        Location e2 = entityHit.getLocation().clone();

        caster.teleport(e2);
        entityHit.teleport(e1);
        caster.setFallDistance(0);
    }

    private void teleportPlayer(Player caster, Location location){
        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
            caster.teleport(location);
            caster.setFallDistance(0);
        }, delay);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public AbstractSpellModule<TeleportSpellModule> clone() {
        return new TeleportSpellModule(this);
    }

    @Override
    protected TeleportSpellModule getThis() {
        return this;
    }
}
