package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpellModule<B extends AbstractSpellModule> implements SpellModule, Cloneable {

    // Radius Options
    boolean overrideRadius = false;
    double currentRadius = 1;
    double startRadius = 0;
    double targetRadius = 1;
    int expandTime = 1;
    boolean animateRadius = false;

    // Max Iterations Per Tick
    int maxIterationsPerTick = 1;
    int currentIterations = 0;
    boolean canRun = true;

    // Cooldown
    int currentCooldown;
    int cooldown;

    public AbstractSpellModule() { }

    public AbstractSpellModule(AbstractSpellModule target){
        this.overrideRadius = target.overrideRadius;
        this.currentRadius = target.currentRadius;
        this.startRadius = target.startRadius;
        this.targetRadius = target.targetRadius;
        this.expandTime = target.expandTime;
        this.animateRadius = target.animateRadius;

        this.maxIterationsPerTick = target.maxIterationsPerTick;
        this.currentIterations = target.currentIterations;
        this.canRun = target.canRun;

        this.currentCooldown = target.currentCooldown;
        this.cooldown = target.cooldown;
    }

    public B setCooldown(int ticks){
        setCooldown(ticks, 0);
        return getThis();
    }

    public B setCooldown(int ticks, int initialCooldown){
        this.cooldown = ticks;
        this.currentCooldown = initialCooldown;
        return getThis();
    }

    public B setMaxIterationsPerTick(int maxIterationsPerTick){
        this.maxIterationsPerTick = maxIterationsPerTick;
        return getThis();
    }

    /**
     * Animates the radius over the given time. This will override the radius
     *
     * @param startRadius
     * @param expandTime
     * @return
     */
    public B animateRadius(double startRadius, int expandTime, double endRadius){
        this.overrideRadius = true;
        this.startRadius = startRadius;
        this.expandTime = expandTime;
        this.animateRadius = true;
        this.targetRadius = endRadius;
        this.currentRadius = startRadius;
        return getThis();
    }

    /**
     * Overrides the radius. If this value is not set, the module will use the radius of the spell
     *
     * @param radius the new radius
     */
    public B overrideRadius(double radius){
        this.overrideRadius = true;
        this.currentRadius = radius;
        this.startRadius = radius;
        this.targetRadius = radius;
        return getThis();
    }

    public static List<SpellModule> cloneList(List<SpellModule> list) {
        List<SpellModule> clone = new ArrayList<SpellModule>(list.size());
        for (SpellModule item : list) clone.add(item.clone());
        return clone;
    }

    @Override
    public void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit) {
        currentIterations++;
        canRun = currentIterations <= maxIterationsPerTick && canRun;
    }

    @Override
    public void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        currentIterations++;
        canRun = currentIterations <= maxIterationsPerTick && canRun;
    }

    @Override
    public void update() {
        currentIterations = 0;

        // Radius Animation
        if (currentRadius < targetRadius){
            currentRadius += Math.min((targetRadius - startRadius) / (double)expandTime, targetRadius);
        }

        // Cooldown
        if (currentCooldown > 0) currentCooldown--;
        else currentCooldown = cooldown;

        canRun = currentCooldown <= 0;
    }

    @Override
    public abstract AbstractSpellModule<B> clone();

    protected abstract B getThis();
}
