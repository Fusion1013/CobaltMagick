package se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers;

import se.fusion1013.plugin.cobaltmagick.spells.*;

import java.util.List;

public interface ISpellModifier {

    void modifyProjectileSpell(ProjectileSpell spellToModify);
    void modifyStaticProjectileSpell(StaticProjectileSpell spellToModify);
    void modifyMovableSpell(MovableSpell spellToModify);

    List<String> getExtraLore();

    ISpellModifier clone();
}
