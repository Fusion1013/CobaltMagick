package se.fusion1013.plugin.cobalt.laser;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SimpleLaser extends AbstractLaser {
    private Location startLocation;
    private final int maxIterations = 200;
    private final int maxSegments = 200;

    private List<LaserPath> laserPaths;

    public SimpleLaser(Location startLocation){
        this.startLocation = startLocation;
        this.laserPaths = new ArrayList<>();
    }

    public void tick(){
        recalculatePath(startLocation, 0);
        displayAllLaserSegments();
    }

    private void displayAllLaserSegments(){
        for (LaserPath laserPath : laserPaths){
            laserPath.display();
        }
    }

    private void recalculatePath(Location location, int iterations){
        Vector direction = calculateVector(location);
        Location endLocation = findNextBlock(location, direction, 0);

        laserPaths.add(new LaserPath(location, endLocation));

        if (iterations < maxSegments){
            recalculatePath(endLocation, iterations);
        }
    }

    private Location findNextBlock(Location location, Vector direction, int iterations){
        Location newLocation = location.add(direction);
        Block newBlock = newLocation.getBlock();
        if (modifiesLaser(newBlock.getType())){
            return newLocation;
        }
        else if (blocksLaser(newBlock.getType())){
            return newLocation;
        } else if (iterations >= maxIterations) {
            return newLocation;
        }
        else {
            return findNextBlock(newLocation, direction, iterations++);
        }
    }
}
