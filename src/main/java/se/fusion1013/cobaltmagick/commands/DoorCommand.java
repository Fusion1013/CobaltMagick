package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class DoorCommand {

    public static void register() {
        new CommandAPICommand("door")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "door"))
                .withArguments(new LocationArgument("pos1", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("pos2", LocationType.BLOCK_POSITION))
                .withArguments(new BlockStateArgument("block"))
                .withArguments(new MultiLiteralArgument("action", "open", "close"))
                .executes(DoorCommand::triggerDoor)
                .register();
    }

    private static void triggerDoor(CommandSender commandSender, CommandArguments args) {
        Location location1 = (Location) args.get("pos1");
        Location location2 = (Location) args.get("pos2");
        BlockState blockState = (BlockState) args.get("block");
        String action = (String) args.get("action");

        Location center = getCenter(location1, location2);
        World world = center.getWorld();

        if (action.equalsIgnoreCase("open")) {
            fillVolume(location1, location2, Material.AIR.createBlockData());
            world.playSound(center, Sound.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS, 1, 1);
            displayParticlesAtBlocks(location1, location2, Particle.END_ROD);
        } else if (action.equalsIgnoreCase("close")) {
            fillVolume(location1, location2, blockState.getBlockData());
            world.playSound(center, Sound.BLOCK_VAULT_CLOSE_SHUTTER, SoundCategory.BLOCKS, 1, 1);
            displayParticlesAtBlocks(location1, location2, Particle.END_ROD);
        }
    }

    public static void fillVolume(Location pos1, Location pos2, BlockData blockData) {
        if (pos1 == null || pos2 == null || blockData == null) return;
        if (pos1.getWorld() == null || pos2.getWorld() == null) return;
        if (!pos1.getWorld().equals(pos2.getWorld())) return;

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Optional height safety check
                if (y < world.getMinHeight() || y > world.getMaxHeight()) continue;

                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setBlockData(blockData, false);
                }
            }
        }
    }

    public static void displayParticlesAtBlocks(Location pos1, Location pos2, Particle particle) {
        if (pos1 == null || pos2 == null || particle == null) return;
        if (pos1.getWorld() == null || pos2.getWorld() == null) return;
        if (!pos1.getWorld().equals(pos2.getWorld())) return;

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Optional height safety check
                if (y < world.getMinHeight() || y > world.getMaxHeight()) continue;

                for (int z = minZ; z <= maxZ; z++) {
                    world.spawnParticle(particle, new Location(world, x, y, z).toCenterLocation(), 3, .5, .5, .5, 0);
                }
            }
        }
    }

    public static Location getCenter(Location a, Location b) {
        if (a == null || b == null) return null;
        if (a.getWorld() == null || b.getWorld() == null) return null;
        if (!a.getWorld().equals(b.getWorld())) return null;

        World world = a.getWorld();

        double centerX = (a.getX() + b.getX()) / 2.0;
        double centerY = (a.getY() + b.getY()) / 2.0;
        double centerZ = (a.getZ() + b.getZ()) / 2.0;

        float yaw = (a.getYaw() + b.getYaw()) / 2.0f;
        float pitch = (a.getPitch() + b.getPitch()) / 2.0f;

        return new Location(world, centerX, centerY, centerZ, yaw, pitch);
    }

}
