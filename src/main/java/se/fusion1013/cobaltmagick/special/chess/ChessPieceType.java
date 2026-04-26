package se.fusion1013.cobaltmagick.special.chess;

import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;

public enum ChessPieceType {

    Pawn("pawn", key("pawn"), List.of(new Vector(1, 0, 0)), 0.75f),
    Knight("knight", key("knight"), List.of(new Vector(2, 0, 1), new Vector(2, 0, -1), new Vector(1, 0, 2), new Vector(1, 0, -2), new Vector(-2, 0, 1), new Vector(-2, 0, -1), new Vector(-1, 0, 2), new Vector(-1, 0, -2)), 1.6f),
    Bishop("bishop", key("bishop"), List.of(), 1.7f),
    King("king", key("king"), List.of(new Vector(1, 0, 0), new Vector(-1, 0, 0), new Vector(1, 0, 1), new Vector(-1, 0, -1), new Vector(1, 0, -1), new Vector(-1, 0, 1), new Vector(0, 0, 1), new Vector(0, 0, -1)), 2),
    Queen("queen", key("queen"), getQueenMoves(), 1.75f),
    Rook("rook", key("rook"), List.of(), 1);

    private final String typeString;
    private final NamespacedKey key;
    private final List<Vector> offsets;
    private final float height;

    ChessPieceType(String typeString, NamespacedKey key, List<Vector> offsets, float height) {
        this.typeString = typeString;
        this.key = key;
        this.offsets = offsets;
        this.height = height;
    }

    public String getTypeString() {
        return typeString;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public List<Vector> getOffsets() {
        return offsets;
    }

    public float getHeight() {
        return height;
    }

    private static NamespacedKey key(String key) {
        return new NamespacedKey(CobaltMagick.getInstance(), key);
    }

    private static List<Vector> getQueenMoves() {
        List<Vector> list = new ArrayList<>();
        for (int x = -8; x <= 8; x++) {
            list.add(new Vector(x, 0, 0));
            list.add(new Vector(0, 0, x));
            list.add(new Vector(x, 0, x));
            list.add(new Vector(-x, 0, x));
        }
        return list;
    }
}
