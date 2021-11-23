package se.fusion1013.plugin.cobalt.locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {
    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Fusion1013";
    }

    @Override
    public Map<String, String> getDefaultLocaleString() {
        return new LinkedHashMap<String, String>(){
            {
                this.put("#0", "Plugin Message Prefix");
                this.put("prefix", "&7[<g:#00aaaa:#0066aa>Cobalt&7] ");

                this.put("#1", "General Command Stuff");
                this.put("commands.error.incorrect_syntax", "&eIncorrect Syntax");

                this.put("#2", "Warp Command");
                this.put("commands.warp.error.warp_not_found", "&eWarp &b%name% &enot found");
                this.put("commands.warp.create.error.invalid_name", "&eInvalid Name: &b%name%");
                this.put("commands.warp.create.error.name_already_exists", "&eName Already Exists: &b%name%");
                this.put("commands.warp.create.info.created_warp", "&eCreated Warp &b%name%");

                this.put("commands.warp.info.header", "&eInformation for Warp &b%name%&e:");
                this.put("commands.warp.info.detail.owner", "&eOwner: &b%owner%");
                this.put("commands.warp.info.detail.world", "&eWorld: &b%world%");
                this.put("commands.warp.info.detail.location", "&eLocation: &b%x%&e, &b%y%&e, &b%z%");
                this.put("commands.warp.info.detail.distance", "&eDistance: &b%distance% &eblocks");
                this.put("commands.warp.info.detail.privacy", "&ePrivacy: &b%privacy%");

                this.put("commands.warp.list.header", "&eDisplaying All Available Warps:");
                this.put("commands.warp.list.entry", "&b%name% &e- &b%x%&e, &b%y%&e, &b%z%&e in &b%world%");

                this.put("commands.warp.teleport.success", "&eTeleported to &b%name%");

                this.put("commands.warp.delete.deleted_warps", "&eDeleted &b%count% &ewarp(s) with the name &b%name%");

                this.put("#3", "Wand");
                this.put("wand.spell.cast.no_mana", "&eWand is out of mana");
                this.put("wand.spell.cast.cast_delay", "&eCast delay");
                this.put("wand.spell.cast.recharge_time", "&eWand is still recharging");

                this.put("#10", "Misc");
                this.put("command-not-implemented", "&eCommand not yet implemented");
                this.put("command-unknown", "&eUnknown command");

                this.put("#11", "List Messages");
                this.put("list-header", "<g:#00aaaa:#0066aa>------ %header% ------");
                this.put("list-item-name", "&eName: &b%name%");
                this.put("list-item-location", "&eLocation: &b%x%&ex &b%y%&ey &b%z%&ez &eWorld: &b%world%");
                this.put("list-item-id-name-location", "&e[&b%id%&e] &eName: &b%name% &eLocation: &b%x%&ex &b%y%&ey &b%z%&ez");

                this.put("gradient", "<g:#ff1100:#00ff1e>GRADIENT");
            }
        };
    }
}
