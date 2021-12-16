package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;

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
    public void executeOnTick(Player caster, Location location, Vector velocityVector) {
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnCast(Player caster, Location location, Vector velocityVector) {
        summon(location);
    }

    @Override
    public void executeOnBlockHit(Player caster, Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(caster, location, velocityVector, blockHit, hitBlockFace);
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnEntityHit(Player caster, Location location, Vector velocityVector, Entity entityHit) {
        super.executeOnEntityHit(caster, location, velocityVector, entityHit);
        if (!canRun) return;

        summon(location);
    }

    @Override
    public void executeOnDeath(Player caster, Location location, Vector velocityVector) {
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