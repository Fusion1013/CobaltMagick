package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class MethodSpellModule extends AbstractSpellModule<MethodSpellModule> implements SpellModule {

    // ----- VARIABLES -----

    List<IExecuteOnCast> onCast = new ArrayList<>();
    List<IExecuteOnTick> onTick = new ArrayList<>();
    List<IExecuteOnEntityHit> onEntityHit = new ArrayList<>();
    List<IExecuteOnBlockHit> onBlockHit = new ArrayList<>();
    List<IExecuteOnDeath> onDeath = new ArrayList<>();

    int runTaskForTicks = 1;

    boolean cancelsCast = false;

    // ----- CONSTRUCTORS -----

    public MethodSpellModule(boolean cancelsCast) {
        this.cancelsCast = cancelsCast;
    }

    // ----- BUILDER METHODS -----

    public MethodSpellModule setRunForTicks(int ticks) {
        this.runTaskForTicks = ticks;
        return getThis();
    }

    public MethodSpellModule addOnCast(IExecuteOnCast onCast) {
        this.onCast.add(onCast);
        return getThis();
    }

    public MethodSpellModule addOnTick(IExecuteOnTick onTick) {
        this.onTick.add(onTick);
        return getThis();
    }

    public MethodSpellModule addOnEntityHit(IExecuteOnEntityHit onEntityHit) {
        this.onEntityHit.add(onEntityHit);
        return getThis();
    }

    public MethodSpellModule addOnCast(IExecuteOnBlockHit onBlockHit) {
        this.onBlockHit.add(onBlockHit);
        return getThis();
    }

    public MethodSpellModule addOnDeath(IExecuteOnDeath onDeath) {
        this.onDeath.add(onDeath);
        return getThis();
    }

    // ----- EXECUTE -----

    BukkitTask castTask;
    int currentCastTick = 0;

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        castTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (currentCastTick > runTaskForTicks) castTask.cancel();
            for (IExecuteOnCast cast : onCast) cast.execute(wand, caster, spell);
            currentCastTick++;
        }, 0, 1);
    }

    BukkitTask tickTask;
    int currentTickTick = 0;

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        tickTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (currentTickTick > runTaskForTicks) tickTask.cancel();
            for (IExecuteOnTick cast : onTick) cast.execute(wand, caster, spell);
            currentTickTick++;
        }, 0, 1);
    }

    BukkitTask deathTask;
    int currentDeathTick = 0;

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        deathTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (currentDeathTick > runTaskForTicks) deathTask.cancel();
            for (IExecuteOnDeath cast : onDeath) cast.execute(wand, caster, spell);
            currentDeathTick++;
        }, 0, 1);
    }

    BukkitTask entityTask;
    int currentEntityTick = 0;

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);

        entityTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (currentEntityTick > runTaskForTicks) entityTask.cancel();
            for (IExecuteOnEntityHit cast : onEntityHit) cast.execute(wand, caster, spell, entityHit);
            currentEntityTick++;
        }, 0, 1);
    }

    BukkitTask blockTask;
    int currentBlockTick = 0;

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);

        blockTask = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            if (currentBlockTick > runTaskForTicks) blockTask.cancel();
            for (IExecuteOnBlockHit cast : onBlockHit) cast.execute(wand, caster, spell, blockHit, hitBlockFace);
            currentBlockTick++;
        }, 0, 1);
    }

    // ----- UTILITY METHODS -----

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    protected MethodSpellModule getThis() {
        return this;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MethodSpellModule(MethodSpellModule target) {
        this.onCast = target.onCast;
        this.onTick = target.onTick;
        this.onDeath = target.onDeath;
        this.onBlockHit = target.onBlockHit;
        this.onEntityHit = target.onEntityHit;
        this.cancelsCast = target.cancelsCast;

        this.runTaskForTicks = target.runTaskForTicks;
        this.currentCastTick = 0;
        this.castTask = null;
        this.currentTickTick = 0;
        this.tickTask = null;
        this.currentDeathTick = 0;
        this.deathTask = null;
        this.currentBlockTick = 0;
        this.blockTask = null;
        this.currentEntityTick = 0;
        this.entityTask = null;
    }

    @Override
    public MethodSpellModule clone() {
        return new MethodSpellModule(this);
    }

    // ----- INTERFACES -----

    public interface IExecuteOnCast { void execute(Wand wand, LivingEntity caster, ISpell spell); }
    public interface IExecuteOnTick { void execute(Wand wand, LivingEntity caster, ISpell spell); }
    public interface IExecuteOnDeath { void execute(Wand wand, LivingEntity caster, ISpell spell); }
    public interface IExecuteOnEntityHit { void execute(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit); }
    public interface IExecuteOnBlockHit { void execute(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace); }
}
