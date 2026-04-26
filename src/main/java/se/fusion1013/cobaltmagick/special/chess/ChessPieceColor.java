package se.fusion1013.cobaltmagick.special.chess;

public enum ChessPieceColor {
    White("white"), Black("black");

    private final String colorString;

    ChessPieceColor(String colorString) {
        this.colorString = colorString;
    }

    public String getColorString() {
        return colorString;
    }
}
