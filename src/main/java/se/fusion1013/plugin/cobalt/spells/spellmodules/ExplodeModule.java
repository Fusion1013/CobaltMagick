package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.util.BlockUtil;
import se.fusion1013.plugin.cobalt.util.GeometryUtil;

public class ExplodeModule extends AbstractSpellModule<ExplodeModule> implements SpellModule {

    boolean cancelsCast;

    // Optional Variables
    boolean fire = false;
    boolean destroyBlocks = false;
    double executeOnlyIfVelocityExceeds = 0;

    boolean executed = false;

    public ExplodeModule(double radius, boolean cancelsCast){
        setRadius(radius);
        this.cancelsCast = cancelsCast;
    }

    public ExplodeModule(ExplodeModule target){
        super(target);
        this.cancelsCast = target.cancelsCast;

        this.fire = target.fire;
        this.destroyBlocks = target.destroyBlocks;
        this.executeOnlyIfVelocityExceeds = target.executeOnlyIfVelocityExceeds;
        this.executed = target.executed;
    }

    public ExplodeModule destroysBlocks(){
        this.destroyBlocks = true;
        return this;
    }

    public ExplodeModule setsFire(){
        this.fire = true;
        return this;
    }

    public ExplodeModule onlyIfVelocityExceeds(double value){
        this.executeOnlyIfVelocityExceeds = value;
        return this;
    }

    @Override
    public void executeOnCast(Player caster, Location location, Vector velocityVector) { explode(location, velocityVector); }

    @Override
    public void executeOnTick(Player caster, Location location, Vector velocityVector) {
        if (!canRun) return;

        explode(location, velocityVector);
    }

    @Override
    public void executeOnBlockHit(Player caster, Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(caster, location, velocityVector, blockHit, hitBlockFace);
        if (!canRun) return;

        explode(location, velocityVector);
    }

    @Override
    public void executeOnEntityHit(Player caster, Location location, Vector velocityVector, Entity entityHit) {
        super.executeOnEntityHit(caster, location, velocityVector, entityHit);
        if (!canRun) return;

        explode(location, velocityVector);
    }

    @Override
    public void executeOnDeath(Player caster, Location location, Vector velocityVector) {
        explode(location, velocityVector);
    }

    private void explode(Location location, Vector velocityVector) {
        executed = false;

        World world = location.getWorld();
        if (velocityVector.length() < executeOnlyIfVelocityExceeds) return;

        BlockUtil.setBlocksInSphere(location, Material.AIR, (int) currentRadius, false, false, true, false, true);
        int iterations = (int)Math.max(1, currentRadius * Math.floor(currentRadius / 4));
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointOnSphere(currentRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, currentRadius), fire, destroyBlocks);
        }
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointInSphere(currentRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, currentRadius), fire, destroyBlocks);
        }

        executed = true;
    }

    @Override
    public boolean cancelsCast() {
        return (executed && cancelsCast);
    }

    @Override
    public ExplodeModule clone() {
        return new ExplodeModule(this);
    }

    protected ExplodeModule getThis() { return this; }
}
