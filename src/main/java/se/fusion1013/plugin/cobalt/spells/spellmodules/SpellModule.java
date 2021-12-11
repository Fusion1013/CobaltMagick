package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface SpellModule {
    void executeOnTick(Location location, Vector directionVector);
    void executeOnBlockHit(Location location, Vector directionVector, Block blockHit, BlockFace hitBlockFace);
    void executeOnEntityHit(Location location, Vector directionVector, Entity entityHit);
    void executeOnDeath(Location location, Vector directionVector);
    boolean cancelsCast();
}
