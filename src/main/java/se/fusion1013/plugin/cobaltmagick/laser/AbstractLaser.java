package se.fusion1013.plugin.cobaltmagick.laser;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLaser {
    private List<Material> ignoreBlocks = new ArrayList<>();
    private List<Material> modifierBlocks = new ArrayList<>();

    public AbstractLaser(){
        setIgnoreBlocks();
        setModifierBlocks();
    }

    // TODO: Validate if it works
    boolean blocksLaser(Material material){
        return !ignoreBlocks.contains(material);
    }

    boolean modifiesLaser(Material material){
        return modifierBlocks.contains(material);
    }

    Vector calculateVector(Location location){
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Directional){
            BlockFace face = ((Directional)blockData).getFacing();
            return face.getDirection();
        } else {
            return null;
        }
    }


    private void setIgnoreBlocks(){
        ignoreBlocks.add(Material.IRON_BARS);
    }

    private void setModifierBlocks(){
        modifierBlocks.add(Material.DISPENSER);
    }
}
