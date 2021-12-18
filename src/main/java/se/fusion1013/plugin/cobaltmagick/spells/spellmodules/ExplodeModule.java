package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.util.BlockUtil;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class ExplodeModule extends AbstractSpellModule<ExplodeModule> implements SpellModule {

    boolean cancelsCast;

    // Optional Variables
    boolean fire = false;
    boolean destroyBlocks = false;
    double executeOnlyIfVelocityExceeds = 0;

    boolean executed = false;

    public ExplodeModule(boolean cancelsCast){
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
    public void executeOnCast(Wand wand, Player caster, ISpell spell) { explode(spell); }

    @Override
    public void executeOnTick(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;

        explode(spell);
    }

    @Override
    public void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        explode(spell);
    }

    @Override
    public void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        explode(spell);
    }

    @Override
    public void executeOnDeath(Wand wand, Player caster, ISpell spell) {
        explode(spell);
    }

    private void explode(ISpell spell) {
        executed = false;

        Location location = spell.getLocation();
        Vector velocityVector = null;
        if (spell instanceof MovableSpell movableSpell){
            velocityVector = movableSpell.getVelocityVector();
        }
        double explosionRadius = spell.getRadius();
        if (overrideRadius) explosionRadius = currentRadius;

        // Check if it only explodes if velocity exceeds value
        World world = location.getWorld();
        if (velocityVector != null) if (velocityVector.length() < executeOnlyIfVelocityExceeds) return;

        // Explode
        BlockUtil.setBlocksInSphere(location, Material.AIR, (int)explosionRadius, false, false, true, false, true);
        int iterations = (int)Math.max(1, explosionRadius * Math.floor(explosionRadius / 4));
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointOnSphere(explosionRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, explosionRadius), fire, destroyBlocks);
        }
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointInSphere(explosionRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, explosionRadius), fire, destroyBlocks);
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
