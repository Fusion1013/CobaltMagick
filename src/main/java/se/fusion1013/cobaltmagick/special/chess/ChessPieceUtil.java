package se.fusion1013.cobaltmagick.special.chess;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;

public class ChessPieceUtil {

    public static final NamespacedKey CHESS_PIECE_KEY = new NamespacedKey(CobaltMagick.getInstance(), "chess_piece");
    public static final NamespacedKey CHESS_PIECE_COLOR_KEY = new NamespacedKey(CobaltMagick.getInstance(), "chess_piece_color");
    public static final NamespacedKey CHESS_TARGET_KEY = new NamespacedKey(CobaltMagick.getInstance(), "chess_target");

    public static ItemDisplay getNearbyChessDisplay(Location location) {
        World world = location.getWorld();
        Collection<ItemDisplay> nearbyEntitiesByType = world.getNearbyEntitiesByType(ItemDisplay.class, location.toCenterLocation().subtract(0, 0.5f, 0), 0.5, 0.5, 0.5, e -> e.getPersistentDataContainer().has(CHESS_PIECE_KEY));
        return nearbyEntitiesByType.stream().findFirst().orElse(null);
    }

    public static Interaction getNearbyChessInteraction(Location location) {
        World world = location.getWorld();
        Collection<Interaction> nearbyEntitiesByType = world.getNearbyEntitiesByType(Interaction.class, location.toCenterLocation().subtract(0, 0.5f, 0), 0.5, 0.5, 0.5, e -> e.getPersistentDataContainer().has(CHESS_PIECE_KEY));
        return nearbyEntitiesByType.stream().findFirst().orElse(null);
    }

    public static ChessPieceType getType(PersistentDataContainer persistent) {
        String typeString = persistent.getOrDefault(CHESS_PIECE_KEY, PersistentDataType.STRING, "NONE");
        if (typeString.equalsIgnoreCase("NONE")) return null;
        return EnumUtils.findEnumInsensitiveCase(ChessPieceType.class, typeString);
    }

    public static ChessPieceColor getColor(PersistentDataContainer persistent) {
        String colorString = persistent.getOrDefault(CHESS_PIECE_COLOR_KEY, PersistentDataType.STRING, "NONE");
        if (colorString.equalsIgnoreCase("NONE")) return null;
        return EnumUtils.findEnumInsensitiveCase(ChessPieceColor.class, colorString);
    }


}
