package se.fusion1013.plugin.cobalt.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.manager.SpellManager;
import se.fusion1013.plugin.cobalt.spells.ISpell;
import se.fusion1013.plugin.cobalt.spells.Spell;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CGiveCommand {
    public static void register(){

        LocaleManager localeManager = LocaleManager.getInstance();

        List<Argument> wandArguments = new ArrayList<>();
        wandArguments.add(new BooleanArgument("shuffle"));
        wandArguments.add(new IntegerArgument("spells/cast", 0).replaceWithSafeSuggestions(info -> new Integer[]{}));
        wandArguments.add(new DoubleArgument("cast delay"));
        wandArguments.add(new DoubleArgument("recharge time"));
        wandArguments.add(new IntegerArgument("mana max", 0));
        wandArguments.add(new IntegerArgument("mana charge speed", 0));
        wandArguments.add(new IntegerArgument("capacity", 0, 54));
        wandArguments.add(new DoubleArgument("spread"));

        CommandAPICommand wandCommand = new CommandAPICommand("wand")
                .withPermission("cobalt.commands.cgive")
                .withArguments(wandArguments)
                .executesPlayer((player, args) -> {
                    boolean shuffle = (Boolean)args[0];
                    int spellsPerCast = (Integer)args[1];
                    double castDelay = (Double)args[2];
                    double rechargeTime = (Double)args[3];
                    int manaMax = (Integer)args[4];
                    int manaChargeSpeed = (Integer)args[5];
                    int capacity = (Integer)args[6];
                    double spread = (Double)args[7];

                    Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, new ArrayList<>(), 0);
                    int id = Cobalt.getInstance().getRDatabase().insertWand(wand);
                    wand.setId(id);
                    Wand.addWandToCache(wand);

                    player.getInventory().addItem(wand.getWandItem());

                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("player_name", player.getName())
                            .build();
                    localeManager.sendMessage(player, "commands.cgive.wand.success", placeholders);
                });
        wandCommand.register();

        List<ISpell> spells = SpellManager.getAllSpells();
        String[] spellNames = new String[spells.size()];
        for (int i = 0; i < spells.size(); i++){
            ISpell s = spells.get(i);
            spellNames[i] = s.getInternalSpellName();
        }
        List<Argument> spellArguments = new ArrayList<>();
        spellArguments.add(new StringArgument("spell name").replaceSuggestions(info -> spellNames));

        CommandAPICommand spellCommand = new CommandAPICommand("spell")
                .withPermission("cobalt.commands.cgive")
                .withArguments(spellArguments)
                .executesPlayer((player, args) -> {
                    String spellName = (String)args[0];
                    StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                            .addPlaceholder("spell_name", spellName)
                            .addPlaceholder("player_name", player.getName())
                            .build();
                    ISpell spell = SpellManager.getSpell(spellName);
                    if (spell == null) {
                        localeManager.sendMessage(player, "commands.cgive.spell.error.spell_not_found", namePlaceholder);
                        return;
                    }
                    ItemStack is = spell.getSpellItem();
                    if (is == null) {
                        localeManager.sendMessage(player, "commands.cgive.spell.error.spell_not_found", namePlaceholder);
                        return;
                    }
                    player.getInventory().addItem(is);
                    StringPlaceholders namePlaceholder2 = StringPlaceholders.builder()
                            .addPlaceholder("spell_name", spell.getSpellName())
                            .addPlaceholder("player_name", player.getName())
                            .build();
                    localeManager.sendMessage(player, "commands.cgive.spell.success", namePlaceholder2);
                });
        spellCommand.register();

        new CommandAPICommand("cgive")
                .withPermission("cobalt.command.cgive")
                .withSubcommand(spellCommand)
                .withSubcommand(wandCommand)
                .register();
    }
}
