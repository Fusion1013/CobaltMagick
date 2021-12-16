package se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers;

import se.fusion1013.plugin.cobaltmagick.spells.*;

public interface SpellModifier {

    void modifyProjectileSpell(ProjectileSpell spellToModify);
    void modifyStaticProjectileSpell(StaticProjectileSpell spellToModify);
    void modifyMovableSpell(MovableSpell spellToModify);

    SpellModifier clone();
}