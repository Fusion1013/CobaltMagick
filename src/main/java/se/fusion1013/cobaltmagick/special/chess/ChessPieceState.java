package se.fusion1013.cobaltmagick.special.chess;

import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;

public record ChessPieceState(Location location, ItemDisplay display, Interaction interaction, ChessPieceType type,
                              ChessPieceColor color) {

    public void teleport(Location targetLocation) {
        Location normalizedTargetLocation = targetLocation.toCenterLocation().subtract(0, 0.5, 0);
        display.setTeleportDuration((int) (location.distance(targetLocation) * 5));
        display.teleport(normalizedTargetLocation);
        interaction.teleport(normalizedTargetLocation);
    }

    public void remove() {
        display.remove();
        interaction.remove();
    }

}
