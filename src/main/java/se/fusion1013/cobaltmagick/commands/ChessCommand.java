package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.special.chess.ChessManager;
import se.fusion1013.cobaltmagick.special.chess.ChessPieceColor;
import se.fusion1013.cobaltmagick.special.chess.ChessPieceType;

import java.util.Arrays;

public class ChessCommand {

    public static void register() {
        new CommandAPICommand("chess")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "chess"))
                .withSubcommand(ChessCommand.createSpawnCommand())
                .withSubcommand(ChessCommand.createBoardCommand())
                .register();
    }

    private static CommandAPICommand createBoardCommand() {
        return new CommandAPICommand("board")
                .withArguments(new LocationArgument("location"))
                .executes(ChessCommand::createBoard);
    }

    private static void createBoard(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        ChessManager.getInstance().setupBoard(location);
    }

    private static CommandAPICommand createSpawnCommand() {
        return new CommandAPICommand("spawn")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings(k -> Arrays.stream(ChessPieceType.values()).map(ChessPieceType::getTypeString).toArray(String[]::new))))
                .withArguments(new StringArgument("color").replaceSuggestions(ArgumentSuggestions.strings(k -> Arrays.stream(ChessPieceColor.values()).map(ChessPieceColor::getColorString).toArray(String[]::new))))
                .executes(ChessCommand::spawnChessPiece);
    }

    private static void spawnChessPiece(CommandSender sender, CommandArguments args) {
        Location location = (Location) args.get("location");
        String typeString = (String) args.get("type");
        String colorString = (String) args.get("color");

        if (location == null || typeString == null || colorString == null) return;

        ChessPieceType type = EnumUtils.findEnumInsensitiveCase(ChessPieceType.class, typeString);
        ChessPieceColor color = EnumUtils.findEnumInsensitiveCase(ChessPieceColor.class, colorString);

        if (type == null || color == null) return;

        ChessManager.getInstance().spawnPiece(location, type, color);
    }

}
