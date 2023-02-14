package se.fusion1013.plugin.cobaltmagick.item.components.spell;

import se.fusion1013.plugin.cobaltmagick.item.components.spell.AbstractSpellComponent;
import se.fusion1013.plugin.cobaltmagick.spells.ProjectileSpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;

import java.util.Map;

public class ProjectileSpellComponent extends AbstractSpellComponent { // TODO: Create AbstractSpellComponent

    //region FIELDS


    //endregion

    //region SPELL CONSTRUCTION

    public ProjectileSpellComponent(String owningItem) {
        super(owningItem);
    }

    public ProjectileSpellComponent(String owningItem, Map<?, ?> data) {
        super(owningItem, data);
    }

    @Override
    protected Spell createSpell(Map<?, ?> data) {
        return new ProjectileSpell(id, internalName, data);
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getInternalName() {
        return "projectile_spell_component";
    }

    //endregion
}
