package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.entity.LivingEntity;
import se.fusion1013.plugin.cobaltcore.util.shapegenerator.IShapeGenerator;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class BlockSpellModule extends AbstractSpellModule<BlockSpellModule> implements SpellModule {

    // ----- VARIABLES -----

    IShapeGenerator shapeGenerator;
    boolean cancelsCast;

    // ----- CONSTRUCTORS -----

    public BlockSpellModule(IShapeGenerator shapeGenerator, boolean cancelsCast) {
        this.shapeGenerator = shapeGenerator;
        this.cancelsCast = cancelsCast;
    }

    // ----- EXECUTE -----

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        shapeGenerator.place(spell.getLocation());
    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        shapeGenerator.place(spell.getLocation());
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        shapeGenerator.place(spell.getLocation());
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public BlockSpellModule(BlockSpellModule target) {
        this.cancelsCast = target.cancelsCast();
        this.shapeGenerator = target.shapeGenerator.clone();
    }

    @Override
    public BlockSpellModule clone() {
        return new BlockSpellModule(this);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    protected BlockSpellModule getThis() {
        return this;
    }
}
