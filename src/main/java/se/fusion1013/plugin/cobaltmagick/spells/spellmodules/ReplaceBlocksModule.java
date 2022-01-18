package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.util.BlockUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class ReplaceBlocksModule extends AbstractSpellModule<ReplaceBlocksModule> implements SpellModule {

    Material replaceMaterial;
    boolean cancelsCast;

    boolean setTopBlocks = false;

    boolean dropItems = false;
    boolean slowReplace = false;
    boolean replaceNonAir = false;
    boolean hollowReplace = false;
    boolean noSound = false;

    int delay;

    public ReplaceBlocksModule(Material replaceMaterial, double radius, boolean cancelsCast){
        this.replaceMaterial = replaceMaterial;
        overrideRadius(radius);
        this.cancelsCast = cancelsCast;
    }

    public ReplaceBlocksModule(ReplaceBlocksModule target){
        super(target);
        this.replaceMaterial = target.replaceMaterial;
        this.cancelsCast = target.cancelsCast;

        this.setTopBlocks = target.setTopBlocks;

        this.dropItems = target.dropItems;
        this.slowReplace = target.slowReplace;
        this.replaceNonAir = target.replaceNonAir;
        this.hollowReplace = target.hollowReplace;
        this.noSound = target.noSound;
        this.delay = target.delay;
    }

    /**
     * Sets if only blocks on top of another blocks will be placed. Setting this will ignore any values for Drop Items, Replace Non Air, Hollow Replace and No Sound
     *
     * @return the module
     */
    public ReplaceBlocksModule onlySetTopBlocks(){
        this.setTopBlocks = true;
        return this;
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

    public ReplaceBlocksModule withDelay(int ticks){
        this.delay = ticks;
        return this;
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) { replaceBlocksInSphere(spell.getLocation()); }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;

        replaceBlocksInSphere(spell.getLocation());
    }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        replaceBlocksInSphere(spell.getLocation());
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        replaceBlocksInSphere(spell.getLocation());
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        replaceBlocksInSphere(spell.getLocation());
    }

    private void replaceBlocksInSphere(Location location){
        CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltMagick.getInstance(), () -> {
            if (setTopBlocks) BlockUtil.setTopBlocksInSphere(location, replaceMaterial, (int)Math.round(currentRadius), slowReplace);
            else BlockUtil.setBlocksInSphere(location, replaceMaterial, (int)Math.round(currentRadius), dropItems, slowReplace, replaceNonAir, hollowReplace, noSound);
        }, delay);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public ReplaceBlocksModule clone() {
        return new ReplaceBlocksModule(this);
    }

    protected ReplaceBlocksModule getThis() { return this; }
}
