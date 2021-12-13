package se.fusion1013.plugin.cobalt.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockUtil {

    private static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow){
        if (centerBlock == null) return new ArrayList<>();

        List<Location> circleBlocks = new ArrayList<>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++){
            for (int y = by - radius; y <= by + radius; y++){
                for (int z = bz - radius; z <= bz + radius; z++){

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))){
                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius){
        return setBlocksInSphere(centerBlock, setBlock, radius, false, false, false, false);
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, false, false, false);
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, false, false);
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, replaceNonAir, false);
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir, boolean hollow){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, replaceNonAir, hollow, false);
    }

    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir, boolean hollow, boolean noSound){
        if (slowReplace) return setBlocksInSphereSlowly(centerBlock, setBlock, radius, dropItems, replaceNonAir, hollow, noSound);
        else {
            List<Location> circleBlocks = generateSphere(centerBlock, radius, hollow);
            for (Location l : circleBlocks){
                replaceBlock(l, setBlock, dropItems, replaceNonAir, noSound);
            }
            return circleBlocks.size();
        }
    }

    public static int setBlocksInSphereSlowly(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean replaceNonAir, boolean hollow, boolean noSound){
        List<Location> circleBlocks = generateSphere(centerBlock, radius, hollow);
        Random r = new Random();

        for (Location l : circleBlocks){
            Block block = l.getBlock();
            int breakTime = Math.min((int)block.getType().getHardness(), 20);

            Cobalt.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Cobalt.getInstance(), () ->
                    replaceBlock(l, setBlock, dropItems, replaceNonAir, noSound), r.nextInt(0, 10) + breakTime);
        }
        return circleBlocks.size();
    }

    private static void replaceBlock(Location location, Material setBlock, boolean dropItems, boolean replaceNonAir, boolean noSound){
        Block block = location.getBlock();

        if (!replaceNonAir && block.getType() != Material.AIR) return;
        if (block.getType().getHardness() <= 0) return;

        if (!noSound){
            for (Player p : Bukkit.getOnlinePlayers()){
                p.playSound(location, block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
            }
        }
        if (dropItems) block.breakNaturally();

        block.setType(setBlock);
    }
}
