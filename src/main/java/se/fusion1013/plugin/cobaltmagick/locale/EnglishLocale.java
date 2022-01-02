package se.fusion1013.plugin.cobaltmagick.locale;

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
                this.put("prefix", "&7[<g:#00aaaa:#0066aa>Magick&7] ");

                this.put("#1", "General Command Stuff");
                this.put("commands.error.incorrect_syntax", "&7Incorrect Syntax");

                this.put("#2", "Warp Command");
                this.put("commands.warp.error.warp_not_found", "&7Warp &3%name% &7not found");
                this.put("commands.warp.create.error.invalid_name", "&7Invalid Name: &3%name%");
                this.put("commands.warp.create.error.name_already_exists", "&7Name Already Exists: &3%name%");
                this.put("commands.warp.create.info.created_warp", "&7Created Warp &3%name%");

                this.put("commands.warp.info.header", "&7Information for Warp &3%name%&7:");
                this.put("commands.warp.info.detail.owner", "&7Owner: &3%owner%");
                this.put("commands.warp.info.detail.world", "&7World: &3%world%");
                this.put("commands.warp.info.detail.location", "&7Location: &3%x%&7, &3%y%&7, &3%z%");
                this.put("commands.warp.info.detail.distance", "&7Distance: &3%distance% &7blocks");
                this.put("commands.warp.info.detail.privacy", "&7Privacy: &3%privacy%");

                this.put("commands.warp.list.header", "&7Displaying All Available Warps:");
                this.put("commands.warp.list.entry", "&3%name% &7- &3%x%&7, &3%y%&7, &3%z%&7 in &3%world%");

                this.put("commands.warp.teleport.success", "&7Teleported to &3%name%");

                this.put("commands.warp.delete.deleted_warps", "&7Deleted &3%count% &7warp(s) with the name &3%name%");

                this.put("#3", "Wand");
                this.put("wand.spell.cast.no_mana", "&7Wand is out of mana");
                this.put("wand.spell.cast.cast_delay", "&7Cast delay");
                this.put("wand.spell.cast.recharge_time", "&7Wand is still recharging");

                this.put("#4", "CGive");
                this.put("commands.cgive.spell.error.spell_not_found", "&7Spell &3%spell_name% &7not found");
                this.put("commands.cgive.spell.success", "&7Gave Spell &3%spell_name% &7to &3%player_name%");
                this.put("commands.cgive.spell.all.success", "&7Gave &3%spell_count% &7Spells to &3%player_name%");
                this.put("commands.cgive.wand.success", "&7Gave new Wand to &3%player_name%");
                this.put("commands.cgive.spell.fromid.wand_not_found", "&7Could not find wand with id &3%wand_id%");
                this.put("commands.cgive.spell.fromid.success", "&7Gave wand with id &3%wand_id% &7to &3%player_name%");

                this.put("#5", "gamemode");
                this.put("commands.gamemode.change", "&7Set &3%player_name%&7's gamemode to &3%gamemode%");
                this.put("commands.gamemode.error.gamemode_not_found", "&7Gamemode &3%gamemode% &7not found");

                this.put("#6", "killspells");
                this.put("commands.killspells.killall.success", "&7Killed &3%killed_spells% &7spells");

                this.put("#10", "Misc");
                this.put("command-not-implemented", "&7Command not yet implemented");
                this.put("command-unknown", "&7Unknown command");

                this.put("#11", "List Messages");
                this.put("list-header", "<g:#00aaaa:#0066aa>------ %header% ------");
                this.put("list-item-name", "&7Name: &3%name%");
                this.put("list-item-location", "&7Location: &3%x%&7x &3%y%&7y &3%z%&7z &7World: &3%world%");
                this.put("list-item-id-name-location", "&7[&3%id%&7] &7Name: &3%name% &7Location: &3%x%&7x &3%y%&7y &3%z%&7z");

                this.put("gradient", "<g:#ff1100:#00ff1e>GRADIENT");
                this.put("rainbow", "&7[<r:1:1>ThisIsAReallyLongRainbowText&7]");
            }
        };
    }
}
