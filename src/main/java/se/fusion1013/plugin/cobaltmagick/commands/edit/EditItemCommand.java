package se.fusion1013.plugin.cobaltmagick.commands.edit;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.item.enchantment.CobaltEnchantment;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

public class EditItemCommand {

    public static CommandAPICommand register() {
        return new CommandAPICommand("item")
                .withPermission("cobalt.magick.commands.cedit.item")
                .withSubcommand(createCustomModelDataCommand())
                .withSubcommand(createCustomNameCommand())
                .withSubcommand(createBookToggleWritableCommand())
                .withSubcommand(createBookTextCommand())
                .withSubcommand(createBookGenerationCommand())
                .withSubcommand(createBookAuthorCommand())
                .withSubcommand(createPotionEffectCommand())
                .withSubcommand(createPotionColorCommand())
                .withSubcommand(createWeightedEnchantmentCommand())
                .withSubcommand(createCobaltEnchantmentCommand());
    }

    // ----- ENCHANTMENTS -----

    private static CommandAPICommand createCobaltEnchantmentCommand() {
        return new CommandAPICommand("cobalt_enchantment")
                .withPermission("cobalt.magick.commands.cedit.item.cobalt_enchantment")
                .withArguments(new StringArgument("enchantment").replaceSuggestions(ArgumentSuggestions.strings(EnchantmentManager.getEnchantmentNames())))
                .withArguments(new IntegerArgument("level"))
                .executesPlayer(EditItemCommand::addCobaltEnchantment);
    }

    private static void addCobaltEnchantment(Player player, Object[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();

        String enchant = (String) args[0];
        int level = (int) args[1];

        EnchantmentWrapper wrapper = EnchantmentManager.getEnchantment(enchant, level, true);
        stack = wrapper.add(stack);
        player.getInventory().setItemInMainHand(stack);
    }

    private static CommandAPICommand createWeightedEnchantmentCommand() {
        return new CommandAPICommand("weighted_enchantment")
                .withPermission("cobalt.magick.commands.cedit.item.weighted_enchantment")
                .withArguments(new IntegerArgument("tier"))
                .executesPlayer(EditItemCommand::addWeightedEnchantment);
    }

    private static void addWeightedEnchantment(Player player, Object[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        int tier = (int) args[0];
        ItemUtil.addWeightedEnchantment(stack, tier);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", stack.getType().toString())
                .addPlaceholder("tier", tier)
                .build();

        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.enchantment.weighted", placeholders);
    }

    // ----- POTION COLOR -----

    private static CommandAPICommand createPotionColorCommand() {
        return new CommandAPICommand("potion_color")
                .withPermission("cobalt.magick.commands.cedit.item.potion_color")
                .withArguments(new IntegerArgument("r"))
                .withArguments(new IntegerArgument("g"))
                .withArguments(new IntegerArgument("b"))
                .executesPlayer(EditItemCommand::setPotionColor);
    }

    private static void setPotionColor(Player player, Object[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        int r = (Integer) args[0];
        int g = (Integer) args[1];
        int b = (Integer) args[2];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("r", r)
                .addPlaceholder("g", g)
                .addPlaceholder("b", b)
                .build();

        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(Color.fromRGB(r, g, b));
            stack.setItemMeta(potionMeta);
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.potion.color", placeholders);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.potion.failed");
        }
    }

    // ----- POTION EFFECT -----

    private static CommandAPICommand createPotionEffectCommand() {
        return new CommandAPICommand("potion_effect")
                .withPermission("cobalt.magick.commands.cedit.potion_effect")
                .withArguments(new PotionEffectArgument("effect"))
                .withArguments(new IntegerArgument("duration"))
                .withArguments(new IntegerArgument("amplifier"))
                .executesPlayer(EditItemCommand::addPotionEffect);
    }

    private static void addPotionEffect(Player player, Object[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        PotionEffectType type = (PotionEffectType) args[0];
        int duration = (Integer) args[1] * 20;
        int amplifier = (Integer) args[2];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("effect", type.getName())
                .addPlaceholder("duration", duration)
                .addPlaceholder("amplifier", amplifier)
                .build();

        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
            stack.setItemMeta(potionMeta);
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.potion.effect", placeholders);
        } else {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.potion.failed");
        }
    }

    // ----- BOOK -----

    private static CommandAPICommand createBookToggleWritableCommand() {
        return new CommandAPICommand("toggle-writable-book")
                .withPermission("cobalt.magick.commands.cedit.toggle_writable_book")
                .executesPlayer(EditItemCommand::convertBook);
    }

    /**
     * Converts a book between a Book and Quill and a written book, depending on what the player is currently holding
     *
     * @param player player
     * @param args arguments
     */
    private static void convertBook(Player player, Object[] args) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType() == Material.WRITABLE_BOOK) {
            stack.setType(Material.WRITTEN_BOOK);
            StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("book_type", "Written Book").build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.toggle_writable.success", placeholders);
        }
        else if (stack.getType() == Material.WRITTEN_BOOK) {
            stack.setType(Material.WRITABLE_BOOK);
            StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("book_type", "Writable Book").build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.toggle_writable.success", placeholders);
        } else {
            StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("item", stack.getType().toString()).build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.toggle_writable.failed", placeholders);
        }
    }

    private static CommandAPICommand createBookTextCommand() {
        return new CommandAPICommand("book-text")
                .withPermission("cobalt.magick.commands.cedit.book_text")
                .withArguments(new IntegerArgument("page-index"))
                .withArguments(new GreedyStringArgument("text"))
                .executesPlayer(((sender, args) -> {
                    editBook(sender, null, null, (String) args[1], (Integer) args[0]);
                }));
    }

    private static CommandAPICommand createBookGenerationCommand() {
        return new CommandAPICommand("book-generation")
                .withPermission("cobalt.magick.commands.cedit.book_generation")
                .withArguments(new StringArgument("generation").replaceSuggestions(ArgumentSuggestions.strings(info -> getBookGenerationTypes())))
                .executesPlayer((sender, args) -> {
                    editBook(sender, null, (String)args[0], null, 0);
                });
    }

    private static CommandAPICommand createBookAuthorCommand() {
        return new CommandAPICommand("book-author")
                .withPermission("cobalt.magick.commands.cedit.book_author")
                .withArguments(new TextArgument("author"))
                .executesPlayer((sender, args) -> {
                    editBook(sender, (String) args[0], null, null, 0);
                });
    }

    /**
     * Edits a book in the players hand
     *
     * @param player player that is holding the item
     * @param author new author of the book
     * @param generation new generation of the book
     * @param text new text of the book. Uses HexUtils to color the text
     * @param page the page to insert the text into
     */
    private static void editBook(Player player, String author, String generation, String text, int page) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", stack.getType().toString())
                .build();

        if (meta == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.failed", placeholders);
            return;
        }

        if (meta instanceof BookMeta bookMeta) {

            // Set author of the book
            if (author != null) {
                bookMeta.setAuthor(author);
                placeholders.addPlaceholder("author", author);
                LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.author", placeholders);
            }

            // Set generation of book
            if (generation != null) {

                BookMeta.Generation gen = EnumUtils.findEnumInsensitiveCase(BookMeta.Generation.class, generation);
                if (gen != null) {
                    bookMeta.setGeneration(BookMeta.Generation.valueOf(generation.toUpperCase()));
                    placeholders.addPlaceholder("generation", generation);
                    LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.generation", placeholders);
                } else {
                    placeholders.addPlaceholder("generation", generation);
                    LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.generation.not_valid", placeholders);
                }
            }

            // Add pages to the book
            if (text != null) {
                int currentPageCount = bookMeta.getPageCount();
                if (currentPageCount < page) {
                    for (int i = currentPageCount; i < page; i++) {
                        bookMeta.addPage("");
                    }
                }

                // Add new text to the book
                if (page > 0) {
                    bookMeta.setPage(page, HexUtils.colorify(text));
                    LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.text.success");
                } else {
                    LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.text.failed");
                }
            }

            stack.setItemMeta(bookMeta);

        } else {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.book.failed", placeholders);
        }


    }

    private static String[] getBookGenerationTypes() {
        BookMeta.Generation[] generations = BookMeta.Generation.values();
        String[] genStrings = new String[generations.length];
        for (int i = 0; i < generations.length; i++) genStrings[i] = generations[i].toString().toLowerCase();
        return genStrings;
    }

    // ----- CUSTOM NAME -----

    private static CommandAPICommand createCustomNameCommand() {
        return new CommandAPICommand("custom-name")
                .withPermission("cobalt.magick.commands.cedit.custom_name")
                .withArguments(new GreedyStringArgument("name"))
                .executesPlayer(EditItemCommand::editItemCustomName);
    }

    /**
     * Edits the custom name of the item a <code>Player</code> is holding.
     *
     * @param player the <code>Player</code>.
     * @param args command arguments.
     */
    private static void editItemCustomName(Player player, Object[] args){
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        String name = (String)args[0];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", stack.getType().toString())
                .addPlaceholder("name", name)
                .build();

        if (meta == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.custom_name.failed", placeholders);
            return;
        }

        meta.setDisplayName(HexUtils.colorify(name));
        stack.setItemMeta(meta);

        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.custom_name.changed", placeholders);
    }

    // ----- CUSTOM MODEL DATA -----

    private static CommandAPICommand createCustomModelDataCommand() {
        return new CommandAPICommand("custom-model-data")
                .withPermission("cobalt.magick.commands.cedit.custom_model_data")
                .withArguments(new IntegerArgument("data"))
                .executesPlayer(EditItemCommand::editItemCustomModelData);
    }

    /**
     * Sets the model data of the item the <code>Player</code> is holding.
     *
     * @param player the <code>Player</code> to set the held item data for.
     * @param args command arguments.
     */
    private static void editItemCustomModelData(Player player, Object[] args){
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        int data = (Integer)args[0];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("item", stack.getType().toString())
                .addPlaceholder("data", data)
                .build();

        // Player is not holding an item
        if (meta == null) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.custom_model_data.failed", placeholders);
            return;
        }

        meta.setCustomModelData(data);
        stack.setItemMeta(meta);

        // Send command feedback
        LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.edit.item.custom_model_data.changed", placeholders);
    }

}
