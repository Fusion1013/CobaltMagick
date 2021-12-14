package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpellModule<B extends AbstractSpellModule> implements SpellModule, Cloneable {

    double currentRadius = 1;
    double startRadius = 0;
    double targetRadius = 1;
    int expandTime = 1;
    boolean animateRadius = false;

    public AbstractSpellModule() { }

    public AbstractSpellModule(AbstractSpellModule target){
        this.currentRadius = target.currentRadius;
        this.startRadius = target.startRadius;
        this.targetRadius = target.targetRadius;
        this.expandTime = target.expandTime;
        this.animateRadius = target.animateRadius;
    }

    public B animateRadius(double startRadius, int expandTime){
        this.startRadius = startRadius;
        this.expandTime = expandTime;
        this.animateRadius = true;
        this.targetRadius = currentRadius;
        this.currentRadius = startRadius;
        return getThis();
    }

    public void setRadius(double radius){
        currentRadius = radius;
        startRadius = radius;
        targetRadius = radius;
    }

    public static List<SpellModule> cloneList(List<SpellModule> list) {
        List<SpellModule> clone = new ArrayList<SpellModule>(list.size());
        for (SpellModule item : list) clone.add(item.clone());
        return clone;
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        if (currentRadius < targetRadius){
            currentRadius += Math.min((targetRadius - startRadius) / (double)expandTime, targetRadius);
        }
    }

    @Override
    public abstract AbstractSpellModule<B> clone();

    protected abstract B getThis();
}
