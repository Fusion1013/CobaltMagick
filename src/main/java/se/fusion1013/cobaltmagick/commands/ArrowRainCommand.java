package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.enchantments.arrow_rain.ArrowRainUtil;

public class ArrowRainCommand {

    public static void register() {
        new CommandAPICommand("arrowrain")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "arrowrain"))
                .withArguments(new LocationArgument("location"))
                .withArguments(new DoubleArgument("radius"))
                .withArguments(new IntegerArgument("arrowsPerTick"))
                .withArguments(new IntegerArgument("durationInTicks"))
                .withArguments(new DoubleArgument("height"))
                .executes(ArrowRainCommand::arrowRain)
                .register();
    }

    private static void arrowRain(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        double radius = (double) args.get("radius");
        int arrowsPerTick = (int) args.get("arrowsPerTick");
        int durationInTicks = (int) args.get("durationInTicks");
        double height = (double) args.get("height");
        Projectile projectile = location.getWorld().spawn(location, Arrow.class);
        ArrowRainUtil.start(projectile, location, radius, arrowsPerTick, durationInTicks, height);
    }

}
