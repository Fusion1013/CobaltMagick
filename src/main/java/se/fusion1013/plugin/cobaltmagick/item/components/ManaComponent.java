package se.fusion1013.plugin.cobaltmagick.item.components;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.components.AbstractItemComponent;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

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
    public void onItemConstruction(ItemStack stack, ItemMeta meta) {
        super.onItemConstruction(stack, meta);

        meta.getPersistentDataContainer().set(MANA_MAX_KEY, PersistentDataType.INTEGER, defaultManaMax);
        meta.getPersistentDataContainer().set(MANA_CHARGE_SPEED_KEY, PersistentDataType.INTEGER, defaultManaChargeSpeed);
        meta.getPersistentDataContainer().set(CURRENT_MANA_KEY, PersistentDataType.INTEGER, defaultCurrentMana);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return "mana_component";
    }
}
