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

    // ----- MUSIC BOX HINTS -----

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

    public static ItemStack getDarkBook() {
        ItemStack bookStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) bookStack.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.TATTERED);
        meta.setTitle("Alchemist's Notebook");
        meta.addPages(Component.text("\"The one below listens to my songs...\n\nE C B G# F\n\nG D# G E A\""));
        meta.setCustomModelData(25);
        bookStack.setItemMeta(meta);
        return bookStack;
    }

    // ----- EMERALD TABLETS -----

    public static ItemStack getEmeraldTabletI() {
        ItemStack tabletStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) tabletStack.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.setTitle("Emerald Tablet - Volume I");
        meta.addPages(
                Component.text("\n\nTis true without lying, certain & most true. That which is below is like that which is above & that which is above is like that which is below to do with the miracles of one thing."),
                Component.text("\n\n\nAnd as all things have been & arose from one by the mediation of one: so all things have their birth from this one thing from adaptation.")
        );
        meta.setCustomModelData(19);
        tabletStack.setItemMeta(meta);
        return tabletStack;
    }

    public static ItemStack getEmeraldTabletII() {
        ItemStack tabletStack = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) tabletStack.getItemMeta();
        meta.setAuthor("???");
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.setTitle("Emerald Tablet - Volume II");
        meta.addPages(
                Component.text("\n\nThe definition of death is the disjunction of the composite. But there is no disjunction of that which is simple, for it is one. Death consists in the separation of the soul from the body."),
                Component.text("\n\n\nBecause anything formed out of two, three, or four components Must disintegrate, and this is death."),
                Component.text("\n\nUnderstand, further, That no complex substance which lacks fire eats, drinks, or sleeps. Because in all things which have a spirit fire is that which eats."),
                Component.text("\n\nThence, a promise of death, starts not in the disjunction, but in the conjunction of two. To look into the eye of death, hung around ones neck.")
        );
        meta.setCustomModelData(19);
        tabletStack.setItemMeta(meta);
        return tabletStack;
    }

}
