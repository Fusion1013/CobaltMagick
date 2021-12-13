package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ExplodeModule implements SpellModule {

    float explosionPower;
    boolean cancelsCast;

    // Optional Variables
    boolean fire = false;
    boolean destroyBlocks = false;
    double executeOnlyIfVelocityExceeds = 0;

    boolean executed = false;

    public ExplodeModule(float explosionPower, boolean cancelsCast){
        this.explosionPower = explosionPower;
        this.cancelsCast = cancelsCast;
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
        if (world != null) world.createExplosion(location, explosionPower, fire, destroyBlocks);

        executed = true;
    }

    @Override
    public boolean cancelsCast() {
        return (executed && cancelsCast);
    }
}
