package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.util.BlockUtil;

public class ReplaceBlocksModule implements SpellModule {

    Material replaceMaterial;
    double radius;
    boolean cancelsCast;

    boolean dropItems = false;
    boolean slowReplace = false;
    boolean replaceNonAir = false;
    boolean hollowReplace = false;
    boolean noSound = false;

    public ReplaceBlocksModule(Material replaceMaterial, double radius, boolean cancelsCast){
        this.replaceMaterial = replaceMaterial;
        this.radius = radius;
        this.cancelsCast = cancelsCast;
    }

    public ReplaceBlocksModule setNoSound(){
        this.noSound = true;
        return this;
    }

    public ReplaceBlocksModule setDropItems(){
        this.dropItems = true;
        return this;
    }

    public ReplaceBlocksModule setSlowReplace(){
        this.slowReplace = true;
        return this;
    }

    public ReplaceBlocksModule setReplaceNonAir(){
        this.replaceNonAir = true;
        return this;
    }

    public ReplaceBlocksModule setHollowReplace(){
        this.hollowReplace = true;
        return this;
    }

    @Override
    public void executeOnCast(Location location, Vector velocityVector) { replaceBlocksInSphere(location); }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        replaceBlocksInSphere(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        replaceBlocksInSphere(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        replaceBlocksInSphere(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        replaceBlocksInSphere(location);
    }

    private void replaceBlocksInSphere(Location location){
        BlockUtil.setBlocksInSphere(location, replaceMaterial, (int)Math.round(radius), dropItems, slowReplace, replaceNonAir, hollowReplace, noSound);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }
}
