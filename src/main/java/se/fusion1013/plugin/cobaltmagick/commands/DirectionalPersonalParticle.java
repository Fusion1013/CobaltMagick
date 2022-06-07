package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.List;

public class DirectionalPersonalParticle {

    public static void register() {

        new CommandAPICommand("dpp")
                .withPermission("dpp")
                .withArguments(new EntitySelectorArgument("players", EntitySelectorArgument.EntitySelector.MANY_PLAYERS))
                .withArguments(new ParticleArgument("particle"))
                .withArguments(new DoubleArgument("xOffset"))
                .withArguments(new DoubleArgument("yOffset"))
                .withArguments(new DoubleArgument("zOffset"))
                .withArguments(new IntegerArgument("count"))
                .withArguments(new IntegerArgument("speed"))
                .withArguments(new LocationArgument("location"))
                .withArguments(new IntegerArgument("distance"))
                .executes(((sender, args) -> {

                    Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> {
                        List<Player> players = (List<Player>) args[0];
                        ParticleData<?> particleData = (ParticleData<?>) args[1];
                        double xOffset = (double) args[2];
                        double yOffset = (double) args[3];
                        double zOffset = (double) args[4];
                        int count = (int) args[5];
                        int speed = (int) args[6];
                        Location location = (Location) args[7];
                        int particleDistance = (int) args[8];

                        for(Player p : players) {
                            Location playerLocation = p.getLocation();

                            if (playerLocation.distanceSquared(location) > particleDistance * particleDistance) {
                                Vector direction = VectorUtil.getDirection(playerLocation.toVector(), location.toVector()).normalize();

                                Location particleLocation = playerLocation.clone().add(direction.multiply(particleDistance));

                                if (particleData.data() != null) p.spawnParticle(particleData.particle(), particleLocation, count, xOffset, yOffset, zOffset, speed, particleData.data());
                                else p.spawnParticle(particleData.particle(), particleLocation, count, xOffset, yOffset, zOffset, speed);
                            } else {
                                if (particleData.data() != null) p.spawnParticle(particleData.particle(), location, count, xOffset, yOffset, zOffset, speed, particleData.data());
                                else p.spawnParticle(particleData.particle(), location, count, xOffset, yOffset, zOffset, speed);
                            }
                        }
                    });

                }))
                .register();

    }

}
