package se.fusion1013.cobaltmagick;

public record Response(ResponseType type, String message) {

    public boolean ok() {
        return type == ResponseType.OK;
    }

    public boolean error() {
        return type == ResponseType.FAIL;
    }

    public static Response ok(String message) {
        return new Response(ResponseType.OK, message);
    }

    public static Response error(String message) {
        return new Response(ResponseType.FAIL, message);
    }

}
