package se.fusion1013.plugin.nicobalt.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.nicobalt.Cobalt;
import se.fusion1013.plugin.nicobalt.locale.EnglishLocale;
import se.fusion1013.plugin.nicobalt.util.HexUtils;
import se.fusion1013.plugin.nicobalt.util.StringPlaceholders;

public class LocaleManager extends Manager {

    EnglishLocale englishLocale;

    public LocaleManager(Cobalt cobalt) {
        super(cobalt);
        englishLocale = new EnglishLocale();
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

    private String parsePlacehodlers(CommandSender sender, String message){
        return message;
    }

    private void sendParsedMessage(CommandSender sender, String message){
        HexUtils.sendMessage(sender, this.parsePlacehodlers(sender, message));
    }
}
