package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class ParticleModule extends AbstractSpellModule<ParticleModule> implements SpellModule {

    ParticleGroup group;
    boolean cancelsCast;

    public ParticleModule(ParticleGroup group, boolean cancelsCast){
        this.group = group;
        this.cancelsCast = cancelsCast;
    }

    public ParticleModule(ParticleModule target){
        super(target);
        this.group = target.group.clone();
        this.cancelsCast = target.cancelsCast;
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        display(spell.getLocation());
    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;

        display(spell.getLocation());
    }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        display(spell.getLocation());
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        display(spell.getLocation());
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        display(spell.getLocation());
    }

    public void display(Location location){
        group.display(location);
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public ParticleModule clone() {
        return new ParticleModule(this);
    }

    protected ParticleModule getThis() { return this; }
}
