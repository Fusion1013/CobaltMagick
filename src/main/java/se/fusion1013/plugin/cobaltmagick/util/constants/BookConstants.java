package se.fusion1013.plugin.cobaltmagick.util.constants;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class BookConstants { // TODO: Load from json instead

    public static ItemStack getLightBook() {
        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) bookStack.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.TATTERED);
        meta.setTitle("A Cunning Contraption");
        meta.addPage("The secret lies in music!\n\nThe key to the heavens' lock is borne from music all over the world.\n\nAnd in a way, as above, so below...");
        meta.setCustomModelData(25);
        bookStack.setItemMeta(meta);
        return bookStack;
    }

}
