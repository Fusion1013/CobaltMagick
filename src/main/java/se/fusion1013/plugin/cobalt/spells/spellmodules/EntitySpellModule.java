package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.util.GeometryUtil;

public class EntitySpellModule extends AbstractSpellModule<EntitySpellModule> implements SpellModule {

    EntityType entity;
    boolean cancelsCast; // TODO: Move cancelsCast to abstract class

    // Optional Variables
    boolean inSphere;

    public EntitySpellModule(EntityType entity, boolean cancelsCast){
        this.entity = entity;
        this.cancelsCast = cancelsCast;
    }

    public EntitySpellModule(EntitySpellModule target){
        super(target);
        this.entity = target.entity;
        this.cancelsCast = target.cancelsCast;

        this.inSphere = target.inSphere;
    }

    public EntitySpellModule setSummonInSphere(double radius){
        setRadius(radius);
        this.inSphere = true;
        return getThis();
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnCast(Location location, Vector velocityVector) {
        summon(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(location, velocityVector, blockHit, hitBlockFace);
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        super.executeOnEntityHit(location, velocityVector, entityHit);
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        summon(location);
    }

    private void summon(Location location){
        World w = location.getWorld();
        if (w == null) return;
        if (inSphere){
            Vector point = GeometryUtil.getPointInSphere(currentRadius);
            w.spawnEntity(location.clone().add(point), entity);
        }
        else w.spawnEntity(location, entity);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public EntitySpellModule clone() {
        return new EntitySpellModule(this);
    }

    @Override
    protected EntitySpellModule getThis() {
        return this;
    }
}
