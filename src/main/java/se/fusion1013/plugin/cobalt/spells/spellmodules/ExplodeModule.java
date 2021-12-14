package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
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
        this.currentRadius = radius;
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
    public void executeOnCast(Location location, Vector velocityVector) { explode(location, velocityVector); }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        super.executeOnTick(location, velocityVector);
        explode(location, velocityVector);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        explode(location, velocityVector);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        explode(location, velocityVector);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        explode(location, velocityVector);
    }

    private void explode(Location location, Vector velocityVector) {
        executed = false;

        World world = location.getWorld();
        if (velocityVector.length() < executeOnlyIfVelocityExceeds) return;

        BlockUtil.setBlocksInSphere(location, Material.AIR, (int) currentRadius, false, false, true, false, true);
        for (int i = 0; i < currentRadius * 10; i++){
            Vector pos = GeometryUtil.getPointOnSphere(currentRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(5, currentRadius), fire, destroyBlocks);
        }
        for (int i = 0; i < currentRadius * 10; i++){
            Vector pos = GeometryUtil.getPointInSphere(currentRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(5, currentRadius), fire, destroyBlocks);
        }
        // if (world != null) world.createExplosion(location, explosionPower, fire, destroyBlocks);

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
