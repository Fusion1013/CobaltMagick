package se.fusion1013.cobaltmagick.spell;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

import static se.fusion1013.cobaltmagick.spell.SpellManager.SPELL_ID_KEY;

public class SpellCreationContext {

    private final String internalName;
    public ItemStack itemStack;
    public ItemMeta itemMeta;
    public PersistentDataContainer persistent;
    public final List<String> lore = new ArrayList<>();
    public final NamespacedKey itemKey;
    public final NamespacedKey spellKey;

    public SpellCreationContext(String internalName, NamespacedKey itemKey, NamespacedKey spellKey) {
        this.internalName = internalName;
        itemStack = new ItemStack(Material.CLOCK);
        itemMeta = itemStack.getItemMeta();
        persistent = itemMeta.getPersistentDataContainer();
        this.itemKey = itemKey;
        this.spellKey = spellKey;
    }

    public ItemStack finalizeItem() {
        persistent.set(itemKey, PersistentDataType.INTEGER, 1);
        persistent.set(spellKey, PersistentDataType.INTEGER, 1);
        persistent.set(SPELL_ID_KEY, PersistentDataType.STRING, internalName);

        itemStack.setItemMeta(itemMeta);

        lore.replaceAll(HexUtils::colorify);

        if (itemStack.getLore() != null) {
            List<String> mergedLore = new ArrayList<>(itemStack.getLore());
            mergedLore.addAll(lore);
            itemStack.setLore(mergedLore);
        } else {
            itemStack.setLore(lore);
        }

        return itemStack;
    }

    // TODO: public Spell finalizeSpell()

}
