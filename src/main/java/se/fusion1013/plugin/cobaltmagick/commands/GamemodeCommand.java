package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;

public class GamemodeCommand {

    public static void register(){
        CommandAPI.unregister("gamemode", true);
        LocaleManager localeManager = LocaleManager.getInstance();

        new CommandAPICommand("gamemode")
                .withArguments(new StringArgument("gamemode"))
                .withAliases("gm")
                .withHelp("Changes your gamemode", "Changes your gamemode to the specified value")
                .executes(((sender, args) -> {
                    if (sender instanceof Player){
                        Player p = (Player)sender;

                        GameMode gameMode = getGamemode((String)args[0]);
                        StringPlaceholders placeholders = StringPlaceholders.builder()
                                .addPlaceholder("player_name", sender.getName())
                                .addPlaceholder("gamemode", args[0])
                                .build();

                        if (gameMode != null) {
                            p.setGameMode(gameMode);
                            localeManager.sendMessage(sender, "commands.gamemode.change", placeholders);
                        } else {
                            localeManager.sendMessage(sender, "commands.gamemode.error.gamemode_not_found", placeholders);
                        }
                    }
                })).register();
    }

    private static GameMode getGamemode(String s){
        if (GameMode.SURVIVAL.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("0")) return GameMode.SURVIVAL;
        if (GameMode.CREATIVE.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("1")) return GameMode.CREATIVE;
        if (GameMode.SPECTATOR.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("3")) return GameMode.SPECTATOR;
        if (GameMode.ADVENTURE.toString().startsWith(s.toUpperCase()) || s.equalsIgnoreCase("2")) return GameMode.ADVENTURE;
        return null;
    }
}
