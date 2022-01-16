package se.fusion1013.plugin.cobaltmagick.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomItem implements MagickItem {

    String internalName;
    NamespacedKey namespacedKey;

    Material material;
    int count;

    String customName;
    List<String> lore;

    int customModel;

    public AbstractCustomItem(String internalName){
        this.internalName = internalName;
        this.namespacedKey = new NamespacedKey(CobaltMagick.getInstance(), internalName);
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack is = new ItemStack(material, count);
        ItemMeta meta = is.getItemMeta();

        if (meta != null) {

            // Metadata
            meta.setDisplayName(customName);
            meta.setLore(lore);
            meta.setCustomModelData(customModel);

            // Persistent Data
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(namespacedKey, PersistentDataType.INTEGER, 1);

            is.setItemMeta(meta);
        }

        return is;
    }

    protected static abstract class AbstractCustomItemBuilder<T extends AbstractCustomItem, B extends AbstractCustomItemBuilder>{

        T obj;

        String internalName;
        Material material;
        int count;

        String customName;
        List<String> lore;

        int customModel;

        public AbstractCustomItemBuilder(String internalName, Material material, int count){
            this.internalName = internalName;
            this.material = material;
            this.count = count;

            this.lore = new ArrayList<>();

            obj = createObj();
        }

        public T build(){
            obj.material = material;
            obj.count = count;

            obj.customName = customName;
            obj.lore = lore;

            obj.customModel = customModel;

            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        public B setCustomModel(int customModel){
            this.customModel = customModel;
            return getThis();
        }

        public B addLoreLine(String loreLine){
            lore.add(loreLine);
            return getThis();
        }

        public B setLore(List<String> lore){
            this.lore = lore;
            return getThis();
        }

        public B setCustomName(String customName){
            this.customName = customName;
            return getThis();
        }
    }
}
