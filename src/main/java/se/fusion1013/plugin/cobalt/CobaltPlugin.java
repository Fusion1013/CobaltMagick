package se.fusion1013.plugin.cobalt;

import org.bukkit.plugin.Plugin;

public interface CobaltPlugin extends Plugin {
    default String[] getAliases() { return new String[] {getName()}; }
    default void registerCommands() {};
    default void registerSettings() {};
    default void registerListeners() {};
    default void mapClasses() {};
    default void postLoad() {};
    default void onReload() {};
}
