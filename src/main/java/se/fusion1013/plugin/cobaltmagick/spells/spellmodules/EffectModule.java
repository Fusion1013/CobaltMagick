package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs an operation on all entities in a spherical area
 */
public class EffectModule extends AbstractSpellModule<EffectModule> implements SpellModule {

    boolean cancelsCast;

    // Optional Variables
    PotionEffect effect;
    boolean freezing;
    int instantFreeze = 0;
    boolean fire;
    int fireTicks;

    boolean targetSelf = false;
    boolean inSphere = false;

    public EffectModule(double radius, boolean cancelsCast){
        overrideRadius(radius);
        this.cancelsCast = cancelsCast;
        this.inSphere = true;
    }

    public EffectModule(boolean cancelsCast){
        this.cancelsCast = cancelsCast;
    }

    public EffectModule(EffectModule target){
        super(target);
        this.cancelsCast = target.cancelsCast;

        this.effect = target.effect;
        this.freezing = target.freezing;
        this.instantFreeze = target.instantFreeze;
        this.fire = target.fire;
        this.fireTicks = target.fireTicks;

        this.targetSelf = target.targetSelf;
        this.inSphere = target.inSphere;
    }

    public EffectModule setFire(int fireTicks) {
        this.fire = true;
        this.fireTicks = fireTicks;
        return this;
    }

    public EffectModule setTargetSelf() {
        targetSelf = true;
        return this;
    }

    public EffectModule setInstantFreeze(int ticks){
        this.instantFreeze = ticks;
        return this;
    }

    public EffectModule setFreezing(){
        freezing = true;
        return this;
    }

    public EffectModule setPotionEffect(PotionEffect effect){
        this.effect = effect;
        return this;
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToLiving(caster);
    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToLiving(caster);
    }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToLiving(caster);
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (entityHit instanceof LivingEntity living) giveEffectsToLiving(living);
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToLiving(caster);
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToLiving(caster);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    private void giveEffectsToLiving(LivingEntity caster){
        if (effect != null) caster.addPotionEffect(effect);
        if (freezing) caster.setFreezeTicks(caster.getFreezeTicks() + 4);
        if (fire) caster.setFireTicks(fireTicks);
        if (caster.getFreezeTicks() < instantFreeze) caster.setFreezeTicks(instantFreeze);
    }

    public void giveEffectsInSphere(Location location){ // TODO: Move to util class
        World world = location.getWorld();
        if (world != null){
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, currentRadius, currentRadius, currentRadius));

            for (Entity e : nearbyEntities){
                if (e instanceof LivingEntity le && e.getLocation().distance(location) <= currentRadius){

                    if (effect != null) le.addPotionEffect(effect);
                    if (freezing) le.setFreezeTicks(le.getFreezeTicks() + 4);
                    if (fire) le.setFireTicks(fireTicks);
                    if (le.getFreezeTicks() < instantFreeze) le.setFreezeTicks(instantFreeze);
                }
            }
        }
    }

    @Override
    public EffectModule clone() {
        return new EffectModule(this);
    }

    protected EffectModule getThis() { return this; }
}
