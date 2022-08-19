package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class TrailSpellModule extends AbstractSpellModule<TrailSpellModule> implements SpellModule {

    // ----- VARIABLES -----

    ParticleGroup particle;
    boolean cancelsCast;

    // Optional
    boolean dripsBlock = false;
    Material dripBlock;
    double dripDistance;

    IBlockDataEditor blockDataEditor;

    // ----- CONSTRUCTORS -----

    public TrailSpellModule(ParticleGroup particle, boolean cancelsCast) {
        this.particle = particle;
        this.cancelsCast = cancelsCast;
    }

    // ----- BUILDER METHODS -----

    public TrailSpellModule addDripBlock(Material dripBlock, double dripDistance) {
        this.dripBlock = dripBlock;
        this.dripDistance = dripDistance;
        this.dripsBlock = true;
        return getThis();
    }

    public TrailSpellModule addBlockStateEditor(IBlockDataEditor editor) {
        this.blockDataEditor = editor;
        return getThis();
    }

    // ----- EXECUTE -----

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        drip(wand, caster, spell);
    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        drip(wand, caster, spell);
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        drip(wand, caster, spell);
    }

    private void drip(Wand wand, LivingEntity caster, ISpell spell) {
        Location currentSpellLocation = spell.getLocation();
        World world = currentSpellLocation.getWorld();

        // Display the particles
        particle.display(currentSpellLocation);

        // Drip block
        if (dripsBlock) {
            RayTraceResult result = world.rayTraceBlocks(currentSpellLocation, new Vector(0, -1, 0), dripDistance, FluidCollisionMode.NEVER, true);
            if (result != null) {
                if (result.getHitBlock() != null) {
                    Location dripLocation = result.getHitBlock().getLocation().clone().add(new Vector(0, 1, 0));
                    if (dripLocation.getBlock().isReplaceable()) {
                        dripLocation.getBlock().setType(dripBlock);

                        if (blockDataEditor != null) dripLocation.getBlock().setBlockData(blockDataEditor.editBlockData(dripLocation.getBlock().getBlockData()));
                    }
                }
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    @Override
    protected TrailSpellModule getThis() {
        return this;
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public TrailSpellModule(TrailSpellModule target) {
        this.cancelsCast = target.cancelsCast;
        this.particle = target.particle;

        this.dripsBlock = target.dripsBlock;
        this.dripBlock = target.dripBlock;
        this.dripDistance = target.dripDistance;

        this.blockDataEditor = target.blockDataEditor;
    }

    @Override
    public AbstractSpellModule<TrailSpellModule> clone() {
        return new TrailSpellModule(this);
    }

    // ----- BLOCK STATE INTERFACE -----

    public interface IBlockDataEditor {
        BlockData editBlockData(BlockData blockData);
    }
}
