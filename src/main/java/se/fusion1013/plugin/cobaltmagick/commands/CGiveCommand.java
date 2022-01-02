package se.fusion1013.plugin.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;
import se.fusion1013.plugin.cobaltmagick.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

/**
 * The main command used for obtaining items from Cobalt Magick
 */
public class CGiveCommand {
    public static void register(){

        // Command for getting other Magick items
        CommandAPICommand itemCommand = new CommandAPICommand("item")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(new StringArgument("item name").replaceSuggestions(info -> CustomItemManager.getInstance().getItemNames()))
                .executesPlayer(CGiveCommand::giveItem);

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

        // Wand subcommand
        CommandAPICommand wandCommand = new CommandAPICommand("wand")
                .withPermission("cobalt.magick.commands.cgive")
                .withSubcommand(fromIdCommand)
                .withSubcommand(randomWandCommand)
                .withSubcommand(withStatsCommand);

        // Command for getting all spells of type
        CommandAPICommand ofTypeCommand = new CommandAPICommand("of_type")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(getTypeArguments())
                .executesPlayer(CGiveCommand::getAllSpells);

        // Command for getting all spells
        CommandAPICommand allSpellsCommand = new CommandAPICommand("all_spells")
                .withPermission("cobalt.magick.commands.cgive")
                .executesPlayer(CGiveCommand::getAllSpells);

        CommandAPICommand spellFromNameCommand = new CommandAPICommand("from_name")
                .withPermission("cobalt.magick.commands.cgive")
                .withArguments(getSpellArguments())
                .executesPlayer(CGiveCommand::getSpell);

        // Spell Subcommand
        CommandAPICommand spellCommand = new CommandAPICommand("spell")
                .withPermission("cobalt.magick.commands.cgive")
                .withSubcommand(allSpellsCommand)
                .withSubcommand(ofTypeCommand)
                .withSubcommand(spellFromNameCommand);

        // Main cgive command
        new CommandAPICommand("cgive")
                .withPermission("cobalt.magick.command.cgive")
                .withSubcommand(spellCommand)
                .withSubcommand(wandCommand)
                .withSubcommand(itemCommand)
                .register();
    }

    private static List<Argument> getGiveItemArgument(){
        List<Argument> arguments = new ArrayList<>();
        arguments.add(new StringArgument("item").replaceSuggestions(info -> CustomItemManager.getInstance().getItemNames()));
        return arguments;
    }

    private static void giveItem(Player player, Object[] args){
        String itemName = (String)args[0];
        ItemStack is = CustomItemManager.getInstance().getItem(itemName);
        if (is != null) player.getInventory().addItem(is);
    }

    /**
     * Gets the arguments for the type command
     *
     * @return list of arguments
     */
    private static List<Argument> getTypeArguments(){
        // Get arguments for spell command
        Spell.SpellType[] types = Spell.SpellType.values();

        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++){
            Spell.SpellType type = types[i];
            typeNames[i] = type.toString().toLowerCase();
        }
        List<Argument> typeArguments = new ArrayList<>();
        typeArguments.add(new StringArgument("type name").replaceSuggestions(info -> typeNames));

        return typeArguments;
    }

    /**
     * Gives a specific wand to the player
     *
     * @param player the player to give the spells to
     * @param args command arguments
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
            localeManager.sendMessage(player, "commands.cgive.spell.fromid.wand_not_found", spellPlaceholder);
            return;
        }

        ItemStack stack = wand.getWandItem();
        player.getInventory().addItem(stack);

        localeManager.sendMessage(player, "commands.cgive.spell.fromid.success", spellPlaceholder);
    }

    private static int getSpellOfType(Player player, Spell.SpellType type){
        List<ISpell> allSpells = SpellManager.getAllSpells();

        ItemStack shulk = new ItemStack(Material.CYAN_SHULKER_BOX, 1);
        BlockStateMeta bsm = (BlockStateMeta)shulk.getItemMeta();
        assert bsm != null;
        ShulkerBox box = (ShulkerBox)bsm.getBlockState();
        int counter = 0;
        int numSpells = 0;

        for (ISpell spell : allSpells){
            ItemStack is = spell.getSpellItem();
            if (is != null && spell.getSpellType() == type) {
                box.getInventory().addItem(is);
                counter++;
                numSpells++;
            }

            if (counter >= 27){
                giveShulker(player, box, type.name());

                shulk = new ItemStack(Material.CYAN_SHULKER_BOX, 1);
                bsm = (BlockStateMeta)shulk.getItemMeta();
                assert bsm != null;
                box = (ShulkerBox)bsm.getBlockState();
                counter = 0;
            }
        }
        if (!box.getInventory().isEmpty()){
            giveShulker(player, box, type.name());
        }

        return numSpells;
    }

    /**
     * Gives all spells to the player
     *
     * @param player player to give the spells to
     * @param args command arguments
     */
    private static void getAllSpells(Player player, Object[] args){
        LocaleManager localeManager = LocaleManager.getInstance();

        int spellCount = 0;
        Spell.SpellType type;

        if (args.length > 0){
            type = Spell.SpellType.valueOf(((String)args[0]).toUpperCase());
            spellCount = getSpellOfType(player, type);
        } else {
            for (Spell.SpellType s : Spell.SpellType.values()){
                spellCount += getSpellOfType(player, s);
            }
        }

        StringPlaceholders spellPlaceholder = StringPlaceholders.builder()
                .addPlaceholder("spell_count", spellCount)
                .addPlaceholder("player_name", player.getName())
                .build();

        localeManager.sendMessage(player, "commands.cgive.spell.all.success", spellPlaceholder);
    }

    private static void giveShulker(Player player, ShulkerBox box, String name){
        ItemStack shulk = new ItemStack(Material.CYAN_SHULKER_BOX, 1);
        BlockStateMeta meta = ((BlockStateMeta)shulk.getItemMeta());
        assert meta != null;
        meta.setDisplayName(name);
        meta.setBlockState(box);
        shulk.setItemMeta(meta);
        player.getInventory().addItem(shulk);
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
