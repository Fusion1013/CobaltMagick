package se.fusion1013.plugin.cobaltmagick.item.loot;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.util.RandomCollection;

import java.util.Random;

public class SpellLootEntry extends LootEntry {

    int spellTier;

    public SpellLootEntry(int spellTier) {
        super(new ItemStack(Material.STONE), 0, 0);

        this.spellTier = spellTier;
    }

    @Override
    public ItemStack getStack(Random r) {
        RandomCollection<ISpell> spellCollection = SpellManager.getWeightedSpellCollection(spellTier);
        return spellCollection.next().getSpellItem();
    }
}