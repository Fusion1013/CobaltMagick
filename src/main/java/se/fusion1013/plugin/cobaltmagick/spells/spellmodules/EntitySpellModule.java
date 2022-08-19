package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;
import se.fusion1013.plugin.cobaltmagick.util.VectorUtils;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.Random;

public class EntitySpellModule<T extends Entity> extends AbstractSpellModule<EntitySpellModule<T>> implements SpellModule {

    // ----- VARIABLES -----

    EntityType entity = null;

    Class<T> entityClass = null;
    Consumer<T> function = null;

    boolean cancelsCast; // TODO: Move cancelsCast to abstract class

    // Optional Variables
    boolean inSphere;
    boolean keepSpellVelocity = false;

    double randomVelocitySpreadX = 0;
    double randomVelocitySpreadY = 0;

    // ----- CONSTRUCTORS -----

    public EntitySpellModule(EntityType entity, boolean cancelsCast){
        this.entity = entity;
        this.cancelsCast = cancelsCast;
    }

    public EntitySpellModule(Class<T> clazz, Consumer<T> function, boolean cancelsCast) {
        this.entityClass = clazz;
        this.function = function;
        this.cancelsCast = cancelsCast;
    }

    public EntitySpellModule(EntitySpellModule<T> target){
        super(target);
        this.entity = target.entity;

        this.entityClass = target.entityClass;
        this.function = target.function;

        this.cancelsCast = target.cancelsCast;

        this.inSphere = target.inSphere;
        this.keepSpellVelocity = target.keepSpellVelocity;

        this.randomVelocitySpreadX = target.randomVelocitySpreadX;
        this.randomVelocitySpreadY = target.randomVelocitySpreadY;
    }

    // ----- BUILDER METHODS -----

    public EntitySpellModule<T> setSummonInSphere(double radius){
        overrideRadius(radius);
        this.inSphere = true;
        return getThis();
    }

    public EntitySpellModule<T> setKeepSpellVelocity() {
        this.keepSpellVelocity = true;
        return getThis();
    }

    public EntitySpellModule<T> setRandomVelocitySpread(double x, double y) {
        this.randomVelocitySpreadX = x;
        this.randomVelocitySpreadY = y;
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

        Random r = new Random();

        // Modify the spawn location based on optional variables
        if (inSphere) spawnLocation.add(GeometryUtil.getPointInSphere(currentRadius));

        // Summon the entity
        if (entity == null) summonedEntity = w.spawn(spawnLocation, entityClass, function);
        else summonedEntity = w.spawnEntity(spawnLocation, entity);

        // Velocity editing methods
        Vector velocity = new Vector();
        if (keepSpellVelocity && spell instanceof MovableSpell movableSpell) velocity.add(movableSpell.getVelocityVector()); // Only do this if the spell is movable
        Vector rightVector = VectorUtils.getRightVector(velocity);
        Vector upVector = velocity.clone().rotateAroundAxis(rightVector, Math.PI / 2);
        if (randomVelocitySpreadX != 0) velocity.rotateAroundAxis(rightVector, Math.toRadians(2 * (r.nextDouble() - .5) * Math.max(0, randomVelocitySpreadX)));
        if (randomVelocitySpreadY != 0) velocity.rotateAroundAxis(upVector, Math.toRadians(2 * (r.nextDouble() - .5) * Math.max(0, randomVelocitySpreadY)));
        summonedEntity.setVelocity(velocity);
    }

    // ----- UTILITY METHODS -----

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public EntitySpellModule<T> clone() {
        return new EntitySpellModule<T>(this);
    }

    @Override
    protected EntitySpellModule<T> getThis() {
        return this;
    }
}
