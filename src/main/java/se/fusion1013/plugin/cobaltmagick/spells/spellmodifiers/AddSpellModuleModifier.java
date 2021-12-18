package se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers;

import se.fusion1013.plugin.cobaltmagick.spells.*;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.AbstractSpellModule;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.SpellModule;

import java.util.ArrayList;
import java.util.List;

public class AddSpellModuleModifier extends AbstractSpellModifier<AddSpellModuleModifier> {

    List<SpellModule> onCast = new ArrayList<>();
    List<SpellModule> onTick = new ArrayList<>();
    List<SpellModule> onEntityCollision = new ArrayList<>();
    List<SpellModule> onBlockCollision = new ArrayList<>();
    List<SpellModule> onDeath = new ArrayList<>();

    public AddSpellModuleModifier(){ }

    public AddSpellModuleModifier(AddSpellModuleModifier target) {
        super(target);

        this.onCast = AbstractSpellModule.cloneList(target.onCast);
        this.onTick = AbstractSpellModule.cloneList(target.onTick);
        this.onEntityCollision = AbstractSpellModule.cloneList(target.onEntityCollision);
        this.onBlockCollision = AbstractSpellModule.cloneList(target.onBlockCollision);
        this.onDeath = AbstractSpellModule.cloneList(target.onDeath);
    }

    public AddSpellModuleModifier addOnCollisionDeath(SpellModule onCollisionDeath){
        this.onBlockCollision.add(onCollisionDeath);
        this.onEntityCollision.add(onCollisionDeath);
        this.onDeath.add(onCollisionDeath);
        return getThis();
    }

    public AddSpellModuleModifier addOnCast(SpellModule onCast) {
        this.onCast.add(onCast);
        return this;
    }
    public AddSpellModuleModifier addOnTick(SpellModule onTick) {
        this.onTick.add(onTick);
        return this;
    }
    public AddSpellModuleModifier addOnEntityCollision(SpellModule onEntityCollision) {
        this.onEntityCollision.add(onEntityCollision);
        return this;
    }
    public AddSpellModuleModifier addOnBlockCollision(SpellModule onBlockCollision) {
        this.onBlockCollision.add(onBlockCollision);
        return this;
    }
    public AddSpellModuleModifier addOnCollision(SpellModule onCollision){
        this.onBlockCollision.add(onCollision);
        this.onEntityCollision.add(onCollision);
        return this;
    }
    public AddSpellModuleModifier addOnDeath(SpellModule onDeath) {
        this.onDeath.add(onDeath);
        return this;
    }

    @Override
    public void modifyProjectileSpell(ProjectileSpell spellToModify) {
        onCast.forEach(spellToModify::addExecuteOnCast);
        onTick.forEach(spellToModify::addExecuteOnTick);
        onEntityCollision.forEach(spellToModify::addExecuteOnEntityCollision);
        onBlockCollision.forEach(spellToModify::addExecuteOnBlockCollision);
        onDeath.forEach(spellToModify::addExecuteOnDeath);
    }

    @Override
    public void modifyStaticProjectileSpell(StaticProjectileSpell spellToModify) {
        onCast.forEach(spellToModify::addExecuteOnCast);
        onTick.forEach(spellToModify::addExecuteOnTick);
        onDeath.forEach(spellToModify::addExecuteOnDeath);
    }

    @Override
    public void modifyMovableSpell(MovableSpell spellToModify) { }

    @Override
    public AddSpellModuleModifier clone() {
        return new AddSpellModuleModifier(this);
    }

    @Override
    protected AddSpellModuleModifier getThis() {
        return this;
    }
}
