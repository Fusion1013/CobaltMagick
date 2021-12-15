package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface SpellModule {
    void executeOnCast(Player caster, Location location, Vector velocityVector);
    void executeOnTick(Player caster, Location location, Vector velocityVector);
    void executeOnBlockHit(Player caster, Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace);
    void executeOnEntityHit(Player caster, Location location, Vector velocityVector, Entity entityHit);
    void executeOnDeath(Player caster, Location location, Vector velocityVector);

    void update();
    boolean cancelsCast();
    SpellModule clone();
}
