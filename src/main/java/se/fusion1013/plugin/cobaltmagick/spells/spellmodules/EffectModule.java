package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

    boolean targetSelf = false;
    boolean inSphere = false;

    public EffectModule(double radius, boolean cancelsCast){
        overrideRadius(radius);
        this.cancelsCast = cancelsCast;
        this.inSphere = true;
    }

    public EffectModule(boolean cancelsCast){
        this.cancelsCast = cancelsCast;
        targetSelf = true;
    }

    public EffectModule(EffectModule target){
        super(target);
        this.cancelsCast = target.cancelsCast;

        this.effect = target.effect;
        this.freezing = target.freezing;
        this.instantFreeze = target.instantFreeze;

        this.targetSelf = target.targetSelf;
        this.inSphere = target.inSphere;
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
    public void executeOnCast(Wand wand, Player caster, ISpell spell) {
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToCaster(caster);
    }

    @Override
    public void executeOnTick(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToCaster(caster);
    }

    @Override
    public void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToCaster(caster);
    }

    @Override
    public void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToCaster(caster);
    }

    @Override
    public void executeOnDeath(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;
        if (inSphere) giveEffectsInSphere(spell.getLocation());
        if (targetSelf) giveEffectsToCaster(caster);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    private void giveEffectsToCaster(Player caster){
        if (effect != null) caster.addPotionEffect(effect);
        if (freezing) caster.setFreezeTicks(caster.getFreezeTicks() + 4);
        if (caster.getFreezeTicks() < instantFreeze) caster.setFreezeTicks(instantFreeze);
    }

    private void giveEffectsInSphere(Location location){
        World world = location.getWorld();
        if (world != null){
            List<Entity> nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, currentRadius, currentRadius, currentRadius));

            for (Entity e : nearbyEntities){
                if (e instanceof LivingEntity le && e.getLocation().distance(location) <= currentRadius){

                    if (effect != null) le.addPotionEffect(effect);
                    if (freezing) le.setFreezeTicks(le.getFreezeTicks() + 4);
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
