package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CustomItem extends AbstractCustomItem implements MagickItem {

    public CustomItem(String internalName) {
        super(internalName);
    }

    public static class CustomItemBuilder extends AbstractCustomItemBuilder<CustomItem, CustomItemBuilder>{

        public CustomItemBuilder(String internalName, Material material, int count) {
            super(internalName, material, count);
        }

        @Override
        protected CustomItem createObj() {
            return new CustomItem(internalName);
        }

        @Override
        protected CustomItemBuilder getThis() {
            return this;
        }
    }
}
