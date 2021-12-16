package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

/**
 * The main command used for obtaining items from Cobalt Magick
 */
public class CGiveCommand {
    public static void register(){

        // Command for generating a wand with given stats
        CommandAPICommand withStatsCommand = new CommandAPICommand("withstats")
                .withPermission("cobaltmagick.commands.cgive")
                .withArguments(getWandArguments())
                .executesPlayer(CGiveCommand::getWandWithStats);

        // Command for generating a randomized wand with a given level
        CommandAPICommand randomWandCommand = new CommandAPICommand("random")
                .withPermission("cobaltmagick.commands.cgive")
                .withArguments(new IntegerArgument("level", 0, 20))
                .executesPlayer(CGiveCommand::getRandomWand);

        // Wand subcommand
        CommandAPICommand wandCommand = new CommandAPICommand("wand")
                .withPermission("cobaltmagick.commands.cgive")
                .withSubcommand(randomWandCommand)
                .withSubcommand(withStatsCommand);

        // Command for getting a specific spell
        CommandAPICommand spellCommand = new CommandAPICommand("spell")
                .withPermission("cobalt.commands.cgive")
                .withArguments(getSpellArguments())
                .executesPlayer(CGiveCommand::getSpell);

        // Main cgive command
        new CommandAPICommand("cgive")
                .withPermission("cobaltmagick.command.cgive")
                .withSubcommand(spellCommand)
                .withSubcommand(wandCommand)
                .register();
    }

    /**
     * Gives a random wand to the player with the specified level
     *
     * @param player player to give the wand to
     * @param args command arguments
     */
    private static void getRandomWand(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        int level = (int)args[0];
        int cost = 20 * level;
        if (cost == 20) cost+=10;

        Wand wand = new Wand(cost, level, false);
        int id = CobaltMagick.getInstance().getRDatabase().insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand);

        player.getInventory().addItem(wand.getWandItem());

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player_name", player.getName())
                .build();
        localeManager.sendMessage(player, "commands.cgive.wand.success", placeholders);
    }

    /**
     * Gets the arguments for the spell command
     *
     * @return list of arguments
     */
    private static List<Argument> getSpellArguments(){
        // Get arguments for spell command
        List<ISpell> spells = SpellManager.getAllSpells();
        String[] spellNames = new String[spells.size()];
        for (int i = 0; i < spells.size(); i++){
            ISpell s = spells.get(i);
            spellNames[i] = s.getInternalSpellName();
        }
        List<Argument> spellArguments = new ArrayList<>();
        spellArguments.add(new StringArgument("spell name").replaceSuggestions(info -> spellNames));

        return spellArguments;
    }

    /**
     * Gets the arguments for the wand command
     *
     * @return list of arguments
     */
    private static List<Argument> getWandArguments(){
        // Get arguments for wand with stats command
        List<Argument> wandArguments = new ArrayList<>();
        wandArguments.add(new BooleanArgument("shuffle"));
        wandArguments.add(new IntegerArgument("spells/cast", 0).replaceWithSafeSuggestions(info -> new Integer[]{}));
        wandArguments.add(new DoubleArgument("cast delay"));
        wandArguments.add(new DoubleArgument("recharge time"));
        wandArguments.add(new IntegerArgument("mana max", 0));
        wandArguments.add(new IntegerArgument("mana charge speed", 0));
        wandArguments.add(new IntegerArgument("capacity", 0, 54));
        wandArguments.add(new DoubleArgument("spread"));

        return wandArguments;
    }

    /**
     * Gives a wand with the specified stats to the player
     *
     * @param player player to give the wand to
     * @param args command arguments
     */
    private static void getWandWithStats(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        boolean shuffle = (Boolean)args[0];
        int spellsPerCast = (Integer)args[1];
        double castDelay = (Double)args[2];
        double rechargeTime = (Double)args[3];
        int manaMax = (Integer)args[4];
        int manaChargeSpeed = (Integer)args[5];
        int capacity = (Integer)args[6];
        double spread = (Double)args[7];

        Wand wand = new Wand(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, new ArrayList<>(), 0);
        int id = CobaltMagick.getInstance().getRDatabase().insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand);

        player.getInventory().addItem(wand.getWandItem());

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player_name", player.getName())
                .build();
        localeManager.sendMessage(player, "commands.cgive.wand.success", placeholders);
    }

    /**
     * Gives the specified spell to the player
     *
     * @param player player to give the spell to
     * @param args command arguments
     */
    private static void getSpell(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

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
    }
}
