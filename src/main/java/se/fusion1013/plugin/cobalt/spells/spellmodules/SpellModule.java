package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface SpellModule {
    void executeOnCast(Location location, Vector velocityVector);
    void executeOnTick(Location location, Vector velocityVector);
    void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace);
    void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit);
    void executeOnDeath(Location location, Vector velocityVector);
    void reset();
    boolean cancelsCast();
    SpellModule clone();
}
