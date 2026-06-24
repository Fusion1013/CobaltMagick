package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.particle.Wisp;
import se.fusion1013.cobaltmagick.particle.WispManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WispCommand {

    private static final Map<Location, List<Wisp>> SPAWNED_WISPS = new HashMap<>();

    public static void register() {
        new CommandAPICommand("wisp")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "wisp"))
                .withArguments(new LocationArgument("corner1", LocationType.BLOCK_POSITION))
                .withArguments(new LocationArgument("corner2", LocationType.BLOCK_POSITION))
                .withArguments(new IntegerArgument("amount"))
                .withArguments(new DoubleArgument("spawn_chance"))
                .withArguments(new DoubleArgument("horizontal_drift"))
                .withArguments(new DoubleArgument("vertical_drift"))
                .withArguments(new DoubleArgument("random_acceleration"))
                .withArguments(new IntegerArgument("min_lifetime_ticks"))
                .withArguments(new IntegerArgument("max_lifetime_ticks"))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new FloatArgument("particle_size"))
                .withArguments(new IntegerArgument("particles_per_wisp"))
                .withArguments(new DoubleArgument("trail_length"))
                .withArguments(new IntegerArgument("trail_particles"))
                .executes(WispCommand::tickWisps)
                .register();
    }

    private static void tickWisps(CommandSender sender, CommandArguments args) {
        Location corner1 = (Location) args.get("corner1");
        Location corner2 = (Location) args.get("corner2");
        int amount = (int) args.get("amount");
        double spawnChance = (double) args.get("spawn_chance");
        double horizontalDrift = (double) args.get("horizontal_drift");
        double verticalDrift = (double) args.get("vertical_drift");
        double randomAcceleration = (double) args.get("random_acceleration");
        int minLifetimeTicks = (int) args.get("min_lifetime_ticks");
        int maxLifetimeTicks = (int) args.get("max_lifetime_ticks");
        ParticleData particle = (ParticleData) args.get("particle");
        float particleSize = (float) args.get("particle_size");
        int particlesPerWisp = (int) args.get("particles_per_wisp");
        double trailLength = (double) args.get("trail_length");
        int trailParticles = (int) args.get("trail_particles");

        if (sender instanceof Player player) {
            tickWispsAtLocation(
                    player.getLocation(),
                    corner1,
                    corner2,
                    amount,
                    spawnChance,
                    horizontalDrift,
                    verticalDrift,
                    randomAcceleration,
                    minLifetimeTicks,
                    maxLifetimeTicks,
                    particle.particle(),
                    particleSize,
                    particlesPerWisp,
                    trailLength,
                    trailParticles
            );
        }

        if (sender instanceof BlockCommandSender block) {
            tickWispsAtLocation(
                    block.getBlock().getLocation(),
                    corner1,
                    corner2,
                    amount,
                    spawnChance,
                    horizontalDrift,
                    verticalDrift,
                    randomAcceleration,
                    minLifetimeTicks,
                    maxLifetimeTicks,
                    particle.particle(),
                    particleSize,
                    particlesPerWisp,
                    trailLength,
                    trailParticles
            );
        }
    }

    private static void tickWispsAtLocation(@NotNull Location location,
                                            Location corner1,
                                            Location corner2,
                                            int amount,
                                            double spawnChance,
                                            double horizontalDrift,
                                            double verticalDrift,
                                            double randomAcceleration,
                                            int minLifetimeTicks,
                                            int maxLifetimeTicks,
                                            Particle particle,
                                            float particleSize,
                                            int particlesPerWisp,
                                            double trailLength,
                                            int trailParticles) {

        List<Wisp> wisps = SPAWNED_WISPS.computeIfAbsent(location, k -> new ArrayList<>());
        WispManager.tickWisps(location.getWorld(),
                corner1,
                corner2,
                wisps,
                amount,
                spawnChance,
                horizontalDrift,
                verticalDrift,
                randomAcceleration,
                minLifetimeTicks,
                maxLifetimeTicks,
                particle,
                particleSize,
                particlesPerWisp,
                trailLength,
                trailParticles);

    }

}
