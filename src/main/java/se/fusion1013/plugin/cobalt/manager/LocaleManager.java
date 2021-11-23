package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.locale.EnglishLocale;
import se.fusion1013.plugin.cobalt.util.HexUtils;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;

public class LocaleManager extends Manager {

    private static LocaleManager instance = null;
    EnglishLocale englishLocale;

    public LocaleManager(Cobalt cobalt) {
        super(cobalt);
        englishLocale = new EnglishLocale();
    }

    public static LocaleManager getInstance(){
        if (instance == null){
            instance = new LocaleManager(Cobalt.getInstance());
        }
        return instance;
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    public String getLocaleMessage(String messageKey){
        return this.getLocaleMessage(messageKey, StringPlaceholders.empty());
    }

    public String getLocaleMessage(String messageKey, StringPlaceholders stringPlaceholders){
        String message = this.englishLocale.getDefaultLocaleString().get(messageKey);
        if (message == null){
            return ChatColor.RED + "Missing message in locale file: " + messageKey;
        }
        return HexUtils.colorify(stringPlaceholders.apply(message));
    }

    public void sendMessage(CommandSender sender, String messageKey, StringPlaceholders stringPlaceholders){
        String prefix = this.getLocaleMessage("prefix");
        this.sendParsedMessage(sender, prefix + this.getLocaleMessage(messageKey, stringPlaceholders));
    }

    public void sendMessage(CommandSender sender, String messageKey){
        sender.sendMessage(getLocaleMessage("prefix") + getLocaleMessage(messageKey));
    }

    public void sendMessage(Player player, String messageKey, StringPlaceholders stringPlaceholders){
        String prefix = this.getLocaleMessage("prefix");
        this.sendParsedMessage(player, prefix + this.getLocaleMessage(messageKey, stringPlaceholders));
    }

    public void sendMessage(Player player, String messageKey){
        player.sendMessage(getLocaleMessage("prefix") + getLocaleMessage(messageKey));
    }

    private void sendParsedMessage(Player player, String message) {
        HexUtils.sendMessage(player, message);
    }

    private void sendParsedMessage(CommandSender sender, String message){
        sendParsedMessage((Player)sender, message);
    }
}
