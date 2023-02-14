package se.fusion1013.plugin.cobaltmagick.item.components;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.components.AbstractItemComponent;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.Map;

public class ManaComponent extends AbstractItemComponent {

    // ----- VARIABLES -----

    private static final NamespacedKey MANA_MAX_KEY = new NamespacedKey(CobaltMagick.getInstance(), "mana_max");
    private static final NamespacedKey MANA_CHARGE_SPEED_KEY = new NamespacedKey(CobaltMagick.getInstance(), "mana_charge_speed");
    private static final NamespacedKey CURRENT_MANA_KEY = new NamespacedKey(CobaltMagick.getInstance(), "current_mana");

    private int defaultManaMax;
    private int defaultManaChargeSpeed;
    private int defaultCurrentMana;

    // ----- CONSTRUCTORS -----

    public ManaComponent(String owningItem) {
        super(owningItem);
    }

    // ----- ITEM CONSTRUCTION -----

    @Override
    public void onItemConstruction(ItemStack stack, ItemMeta meta, PersistentDataContainer persistentDataContainer) {
        super.onItemConstruction(stack, meta, persistentDataContainer);

        persistentDataContainer.set(MANA_MAX_KEY, PersistentDataType.INTEGER, defaultManaMax);
        persistentDataContainer.set(MANA_CHARGE_SPEED_KEY, PersistentDataType.INTEGER, defaultManaChargeSpeed);
        persistentDataContainer.set(CURRENT_MANA_KEY, PersistentDataType.INTEGER, defaultCurrentMana);
    }

    @Override
    public void loadValues(Map<?, ?> map) {

    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return "mana_component";
    }
}
