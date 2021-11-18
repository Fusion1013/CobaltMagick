package se.fusion1013.plugin.cobalt.util;

public class StringUtil {
    public static boolean isWord(String toCheck){
        return toCheck.matches("\\w+$");
    }
}
