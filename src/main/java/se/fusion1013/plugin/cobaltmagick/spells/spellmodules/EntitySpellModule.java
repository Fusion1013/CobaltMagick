package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

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
        overrideRadius(radius);
        this.inSphere = true;
        return getThis();
    }

    @Override
    public void executeOnTick(Wand wand, Player caster, ISpell spell) {
        if (!canRun) return;

        summon(spell.getLocation());
    }

    @Override
    public void executeOnCast(Wand wand, Player caster, ISpell spell) {
        summon(spell.getLocation());
    }

    @Override
    public void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        summon(spell.getLocation());
    }

    @Override
    public void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        summon(spell.getLocation());
    }

    @Override
    public void executeOnDeath(Wand wand, Player caster, ISpell spell) {
        summon(spell.getLocation());
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
