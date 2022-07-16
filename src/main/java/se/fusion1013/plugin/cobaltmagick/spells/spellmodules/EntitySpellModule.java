package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class EntitySpellModule extends AbstractSpellModule<EntitySpellModule> implements SpellModule {

    // ----- VARIABLES -----

    EntityType entity;
    boolean cancelsCast; // TODO: Move cancelsCast to abstract class

    // Optional Variables
    boolean inSphere;
    boolean keepSpellVelocity = false;

    // ----- CONSTRUCTORS -----

    public EntitySpellModule(EntityType entity, boolean cancelsCast){
        this.entity = entity;
        this.cancelsCast = cancelsCast;
    }

    public EntitySpellModule(EntitySpellModule target){
        super(target);
        this.entity = target.entity;
        this.cancelsCast = target.cancelsCast;

        this.inSphere = target.inSphere;
        this.keepSpellVelocity = target.keepSpellVelocity;
    }

    // ----- BUILDER METHODS -----

    public EntitySpellModule setSummonInSphere(double radius){
        overrideRadius(radius);
        this.inSphere = true;
        return getThis();
    }

    public EntitySpellModule setKeepSpellVelocity() {
        this.keepSpellVelocity = true;
        return getThis();
    }

    // ----- EXECUTE METHODS -----

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;

        summon(spell);
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        summon(spell);
    }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        summon(spell);
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        summon(spell);
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        summon(spell);
    }

    // ----- SUMMON METHOD -----

    private void summon(ISpell spell){
        Location spawnLocation = spell.getLocation().clone();
        World w = spawnLocation.getWorld();
        if (w == null) return;

        Entity summonedEntity;

        // Modify the spawn location based on optional variables
        if (inSphere) spawnLocation.add(GeometryUtil.getPointInSphere(currentRadius));

        // Summon the entity
        summonedEntity = w.spawnEntity(spawnLocation, entity);

        // Velocity editing methods
        Vector velocity = new Vector();
        if (keepSpellVelocity && spell instanceof MovableSpell movableSpell) velocity.add(movableSpell.getVelocityVector()); // Only do this if the spell is movable
        summonedEntity.setVelocity(velocity);
    }

    // ----- UTILITY METHODS -----

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
