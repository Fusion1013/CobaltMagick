package se.fusion1013.plugin.cobalt.spells.spellmodifiers;

import se.fusion1013.plugin.cobalt.spells.*;

public interface SpellModifier {

    void modifyProjectileSpell(ProjectileSpell spellToModify);
    void modifyStaticProjectileSpell(StaticProjectileSpell spellToModify);
    void modifyMovableSpell(MovableSpell spellToModify);

    SpellModifier clone();
}
