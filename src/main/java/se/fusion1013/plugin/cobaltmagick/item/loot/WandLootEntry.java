package se.fusion1013.plugin.cobaltmagick.item.loot;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltmagick.database.DatabaseHook;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.Random;

public class WandLootEntry extends LootEntry {

    int cost;
    int level;
    boolean forceUnshuffle;

    public WandLootEntry(int level, boolean forceUnshuffle) {
        super(new ItemStack(Material.STONE), 0, 0);

        this.cost = 20 * level;
        if (cost == 20) this.cost+=10;
        this.level = level;
        this.forceUnshuffle = forceUnshuffle;
    }

    @Override
    public ItemStack getStack(Random r) {
        Wand wand = new Wand(cost, level, forceUnshuffle);
        int id = DatabaseHook.insertWand(wand);
        wand.setId(id);
        Wand.addWandToCache(wand); // TODO: Add this to wand manager

        return wand.getWandItem();
    }
}
