package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public interface SpellModule {
    void executeOnCast(Wand wand, Player caster, ISpell spell);
    void executeOnTick(Wand wand, Player caster, ISpell spell);
    void executeOnBlockHit(Wand wand, Player caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace);
    void executeOnEntityHit(Wand wand, Player caster, MovableSpell spell, Entity entityHit);
    void executeOnDeath(Wand wand, Player caster, ISpell spell);

    void update();
    boolean cancelsCast();
    SpellModule clone();
}
