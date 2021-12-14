package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;

public class ParticleModule extends AbstractSpellModule<ParticleModule> implements SpellModule {

    ParticleGroup group;
    boolean cancelsCast;

    public ParticleModule(ParticleGroup group, boolean cancelsCast){
        this.group = group;
        this.cancelsCast = cancelsCast;
    }

    public ParticleModule(ParticleModule target){
        super(target);
        this.group = target.group.clone();
        this.cancelsCast = target.cancelsCast;
    }

    @Override
    public void executeOnCast(Location location, Vector velocityVector) {
        display(location);
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        super.executeOnTick(location, velocityVector);
        display(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        display(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        display(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        display(location);
    }

    public void display(Location location){
        group.display(location);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public ParticleModule clone() {
        return new ParticleModule(this);
    }

    protected ParticleModule getThis() { return this; }
}
