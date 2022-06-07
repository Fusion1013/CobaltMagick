package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.DatabaseHook;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;
import se.fusion1013.plugin.cobaltmagick.util.ItemUtil;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

/**
 * The main command used for obtaining items from Cobalt Magick
 */
public class CGiveCommand {

    // ----- REGISTER COMMAND -----

    /**
     * Registers the cgive command
     */
    public static void register(){

        // Main cgive command
        new CommandAPICommand("cgive")
                .withPermission("cobalt.magick.command.cgive")
                .withSubcommand(createItemCommand())
                .withSubcommand(createSpellCommand())
                .withSubcommand(createWandCommand())
                .register();
    }

    // ----- ITEM COMMAND -----

    /**
     * Creates the command for getting magick items.
     *
     * @return the command.
     */
    private static CommandAPICommand createItemCommand() {
        return new CommandAPICommand("item")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new StringArgument("item name").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getCustomItemNames())))
                .executesPlayer(CGiveCommand::giveItem);
    }

    /**
     * Gives a specific magick item to the player.
     *
     * @param player the player to give the item to.
     * @param args the item to give the player.
     */
    private static void giveItem(Player player, Object[] args){
        String itemName = (String)args[0];
        ItemStack is = CustomItemManager.getItemStack(itemName);
        if (is != null) player.getInventory().addItem(is);
    }

    // ----- SPELL COMMAND -----

    /**
     * Creates the command for getting magick spells.
     *
     * @return the command.
     */
    private static CommandAPICommand createSpellCommand() {
        // Command for getting all spells of type
        CommandAPICommand ofTypeCommand = new CommandAPICommand("of_type")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings(info -> SpellManager.getTypeNames())))
                .executesPlayer(CGiveCommand::giveAllSpells);

        // Command for getting all spells
        CommandAPICommand allSpellsCommand = new CommandAPICommand("all_spells")
                .withPermission("cobalt.magick.commands.cgive")
                .executesPlayer(CGiveCommand::giveAllSpells);

        // Command for getting a spell from a name
        CommandAPICommand spellFromNameCommand = new CommandAPICommand("from_name")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> SpellManager.getSpellNames())))
                .executesPlayer(CGiveCommand::giveSpell);

        // Main Subcommand
        return new CommandAPICommand("spell")
                .withPermission("cobalt.magick.commands.cgive")
                .withSubcommand(allSpellsCommand)
                .withSubcommand(ofTypeCommand)
                .withSubcommand(spellFromNameCommand);
    }

    /**
     * Gives all spells of a certain type to a player.
     *
     * @param player player to give the spells to.
     * @param type the type of spell to give.
     * @return the number of spells given to the player.
     */
    private static int giveSpellOfType(Player player, Spell.SpellType type){

        ISpell[] spellsOfType = SpellManager.getSpellsOfType(type);
        ItemStack[] itemStacks = new ItemStack[spellsOfType.length];
        for (int i = 0; i < spellsOfType.length; i++) itemStacks[i] = spellsOfType[i].getSpellItem();

        ItemUtil.giveShulkerBox(player, itemStacks, Material.CYAN_SHULKER_BOX, type.name());

        return spellsOfType.length;
    }

    /**
     * Gives all spells of type to the player, or all spells if no type was specified.
     *
     * @param player player to give the spells to.
     * @param args the spell type. May be set to null.
     */
    private static void giveAllSpells(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        int spellCount = 0;
        Spell.SpellType type;

        if (args.length > 0){
            type = Spell.SpellType.valueOf(((String)args[0]).toUpperCase());
            spellCount = giveSpellOfType(player, type);
        } else {
            for (Spell.SpellType s : Spell.SpellType.values()){
                spellCount += giveSpellOfType(player, s);
            }
        }

        StringPlaceholders spellPlaceholder = StringPlaceholders.builder()
                .addPlaceholder("spell_count", spellCount)
                .addPlaceholder("player_name", player.getName())
                .build();

        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.all.success", spellPlaceholder);
    }

    /**
     * Gives the specified spell to the player.
     *
     * @param player player to give the spell to.
     * @param args the spell to give to the player.
     */
    private static void giveSpell(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        String spellName = (String)args[0];
        StringPlaceholders namePlaceholder = StringPlaceholders.builder()
                .addPlaceholder("spell_name", spellName)
                .addPlaceholder("player_name", player.getName())
                .build();
        ISpell spell = SpellManager.getSpell(spellName);
        if (spell == null) {
            localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.error.spell_not_found", namePlaceholder);
            return;
        }
        ItemStack is = spell.getSpellItem();
        if (is == null) {
            localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.error.spell_not_found", namePlaceholder);
            return;
        }
        player.getInventory().addItem(is);
        StringPlaceholders namePlaceholder2 = StringPlaceholders.builder()
                .addPlaceholder("spell_name", spell.getSpellName())
                .addPlaceholder("player_name", player.getName())
                .build();
        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.success", namePlaceholder2);
    }

    // ----- WAND COMMAND -----

    /**
     * Creates the command for getting magick wands.
     *
     * @return the command.
     */
    private static CommandAPICommand createWandCommand() {

        // Command for getting an existing wand from an id
        CommandAPICommand fromIdCommand = new CommandAPICommand("fromid")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new IntegerArgument("id"))
                .executesPlayer(CGiveCommand::getWandFromId);

        // Command for generating a wand with given stats
        CommandAPICommand withStatsCommand = new CommandAPICommand("withstats")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(getWandArguments())
                .executesPlayer(CGiveCommand::getWandWithStats);

        // Command for generating a randomized wand with a given level
        CommandAPICommand randomWandCommand = new CommandAPICommand("random")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new IntegerArgument("level", 0, 20))
                .executesPlayer(CGiveCommand::getRandomWand);

        // Main Subcommand
        return new CommandAPICommand("wand")
                .withPermission("cobalt.magick.commands.cgive")
                .withSubcommand(fromIdCommand)
                .withSubcommand(randomWandCommand)
                .withSubcommand(withStatsCommand);
    }

    // WAND

    /**
     * Gives a wand with the specified stats to the player.
     *
     * @param player player to give the wand to.
     * @param args the stats of the wand.
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
        int id = DatabaseHook.insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand);

        player.getInventory().addItem(wand.getWandItem());

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player_name", player.getName())
                .build();
        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.wand.success", placeholders);
    }

    /**
     * Gets the arguments for the wand command.
     *
     * @return list of arguments.
     */
    private static List<Argument> getWandArguments(){
        // Get arguments for wand with stats command
        List<Argument> wandArguments = new ArrayList<>();
        wandArguments.add(new BooleanArgument("shuffle"));
        wandArguments.add(new IntegerArgument("spells/cast", 0));
        wandArguments.add(new DoubleArgument("cast delay"));
        wandArguments.add(new DoubleArgument("recharge time"));
        wandArguments.add(new IntegerArgument("mana max", 0));
        wandArguments.add(new IntegerArgument("mana charge speed", 0));
        wandArguments.add(new IntegerArgument("capacity", 0, 54));
        wandArguments.add(new DoubleArgument("spread"));

        return wandArguments;
    }

    /**
     * Gives a random wand to the player with a specified level.
     *
     * @param player player to give the wand to.
     * @param args the level of the wand.
     */
    private static void getRandomWand(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        int level = (int)args[0];
        int cost = 20 * level;
        if (cost == 20) cost+=10;

        Wand wand = new Wand(cost, level, false);
        int id = DatabaseHook.insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand);

        player.getInventory().addItem(wand.getWandItem());

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("player_name", player.getName())
                .build();
        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.wand.success", placeholders);
    }

    /**
     * Gives a specific wand to the player.
     *
     * @param player the player to give the spells to.
     * @param args command arguments.
     */
    private static void getWandFromId(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();
        int id = (int)args[0];

        StringPlaceholders spellPlaceholder = StringPlaceholders.builder()
                .addPlaceholder("wand_id", id)
                .addPlaceholder("player_name", player.getName())
                .build();

        Wand wand = Wand.getWandFromCache(id);

        if (wand == null) {
            localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.fromid.wand_not_found", spellPlaceholder);
            return;
        }

        ItemStack stack = wand.getWandItem();
        player.getInventory().addItem(stack);

        localeManager.sendMessage(CobaltMagick.getInstance(), player, "commands.cgive.spell.fromid.success", spellPlaceholder);
    }
}
