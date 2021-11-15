package se.fusion1013.plugin.cobalt.commands;

import java.util.Map;

public class CommandFlags {

    private Map<String, String> flags;

    public CommandFlags(String[] args){
        // TODO
    }

    public Map<String, String> getFlags() { return flags; }

    public String getFlag(String... synonymousFlags){
        for (String flag : synonymousFlags){
            if (flags.containsKey(flag)){
                return flag;
            }
        }
        return null;
    }

    public static String[] stripFlags(String[] args){
        // TODO
        return null;
    }
}
