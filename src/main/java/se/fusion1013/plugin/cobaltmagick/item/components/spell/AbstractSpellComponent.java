package se.fusion1013.plugin.cobaltmagick.item.components.spell;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.item.components.AbstractItemComponent;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.Spell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

import java.util.List;
import java.util.Map;

import static se.fusion1013.plugin.cobaltmagick.spells.Spell.SPELL_KEY;

public abstract class AbstractSpellComponent extends AbstractItemComponent {

    //region FIELDS

    private static final String DEFAULT_SPELL_NAME = "default_spell_name";

    // -- Internals
    protected int id = -1;
    protected String internalName = DEFAULT_SPELL_NAME;

    // -- Spell
    private Spell spell;

    //endregion

    //region CONSTRUCTORS

    public AbstractSpellComponent(String owningItem) {
        super(owningItem);
    }

    protected AbstractSpellComponent(String owningItem, Map<?, ?> data) {
        super(owningItem, data);

        // Internals
        if (data.containsKey("id")) id = (int) data.get("id");
        if (data.containsKey("identifier")) id = (int) data.get("identifier");
        internalName = owningItem;

        verifyFields();

        // Register the spell
        spell = SpellManager.register(createSpell(data));
    }

    protected abstract Spell createSpell(Map<?, ?> data);

    private void verifyFields() {
        if (id < 0) CobaltMagick.getInstance().getLogger().warning("Spell Component 'ID' invalid (" + id + "). Must be positive value.");
        if (internalName.equalsIgnoreCase(DEFAULT_SPELL_NAME)) CobaltMagick.getInstance().getLogger().warning("Spell Component 'Internal Name' not set (" + internalName + ")");
    }

    //endregion

    //region DISABLE

    @Override
    public void onDisable() {
        // TODO: Unregister the spell
    }

    //region

    //region ITEM CONSTRUCTION

    @Override
    public List<String> getLore() {
        List<String> lore = super.getLore();
        lore.addAll(spell.getLore());
        lore.add("");
        lore.add(spell.getSpellType().getSpellColor() + spell.getSpellType().name().replaceAll("_", " "));
        return lore;
    }

    @Override
    public void onItemConstruction(ItemStack stack, ItemMeta meta, PersistentDataContainer persistentDataContainer) {
        super.onItemConstruction(stack, meta, persistentDataContainer);

        meta.setDisplayName(spell.getFormattedName());
        persistentDataContainer.set(SPELL_KEY, PersistentDataType.INTEGER, id);
    }

    //endregion
}
