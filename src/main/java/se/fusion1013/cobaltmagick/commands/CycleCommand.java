package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.SoundArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Objects;

public class CycleCommand {

    public static void register() {
        new CommandAPICommand("cycle")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "cycle"))
                .withArguments(new SoundArgument("sound"))
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new BlockStateArgument("block1"))
                .withArguments(new BlockStateArgument("block2"))
                .withOptionalArguments(new BlockStateArgument("block3"))
                .withOptionalArguments(new BlockStateArgument("block4"))
                .withOptionalArguments(new BlockStateArgument("block5"))
                .withOptionalArguments(new BlockStateArgument("block6"))
                .withOptionalArguments(new BlockStateArgument("block7"))
                .withOptionalArguments(new BlockStateArgument("block8"))
                .withOptionalArguments(new BlockStateArgument("block9"))
                .executes(CycleCommand::cycle)
                .register();
    }

    private static void cycle(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        if (location == null) return;

        Sound sound = (Sound) args.get("sound");

        BlockState blockState1 = (BlockState) args.get("block1");
        BlockState blockState2 = (BlockState) args.get("block2");
        BlockState blockState3 = getBlockState("block3", args);
        BlockState blockState4 = getBlockState("block4", args);
        BlockState blockState5 = getBlockState("block5", args);
        BlockState blockState6 = getBlockState("block6", args);
        BlockState blockState7 = getBlockState("block7", args);
        BlockState blockState8 = getBlockState("block8", args);
        BlockState blockState9 = getBlockState("block9", args);

        Material current = location.getBlock().getType();

        if (current == getType(blockState1)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState2, blockState1).getBlockData());
        } else if (current == getType(blockState2)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState3, blockState1).getBlockData());
        } else if (current == getType(blockState3)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState4, blockState1).getBlockData());
        } else if (current == getType(blockState4)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState5, blockState1).getBlockData());
        } else if (current == getType(blockState5)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState6, blockState1).getBlockData());
        } else if (current == getType(blockState6)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState7, blockState1).getBlockData());
        } else if (current == getType(blockState7)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState8, blockState1).getBlockData());
        } else if (current == getType(blockState8)) {
            location.getBlock().setBlockData(Objects.requireNonNullElse(blockState9, blockState1).getBlockData());
        } else {
            location.getBlock().setBlockData(blockState1.getBlockData());
        }

        location.getWorld().playSound(location.toCenterLocation(), sound, 1, 1);
    }

    private static @Nullable BlockState getBlockState(String param, CommandArguments args) {
        return args.get(param) != null ? (BlockState) args.get(param) : null;
    }

    private static @Nullable Material getType(BlockState state) {
        if (state == null) return null;
        return state.getType();
    }

}
