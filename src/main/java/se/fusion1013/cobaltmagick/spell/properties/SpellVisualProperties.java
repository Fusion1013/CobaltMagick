package se.fusion1013.cobaltmagick.spell.properties;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.spell.AbstractSpellTemplate;
import se.fusion1013.cobaltmagick.spell.SpellCreationContext;

public class SpellVisualProperties extends AbstractObjectProperties<SpellCreationContext, AbstractSpellTemplate> {

    private final StringVariable displayName = new StringVariable("display_name", "DEFAULT");
    private final StringVariable itemModel = new StringVariable("item_model", "");

    @Override
    public void create(SpellCreationContext obj) {
        setItemModel(obj);
        if (displayName.getValue() != null) obj.itemMeta.setDisplayName(HexUtils.colorify(displayName.getValue()));
    }

    private void setItemModel(SpellCreationContext obj) {
        if (itemModel.getValue() == null) return;
        if (itemModel.getValue().isEmpty()) return;

        String[] itemModelNamespaceSplit = itemModel.getValue().split(":");
        if (itemModelNamespaceSplit.length > 1)
            obj.itemMeta.setItemModel(new NamespacedKey(itemModelNamespaceSplit[0], itemModelNamespaceSplit[1]));
        else obj.itemMeta.setItemModel(new NamespacedKey("minecraft", itemModelNamespaceSplit[0]));
    }

    @Override
    public void fromJson(JsonObject jsonObject, AbstractSpellTemplate abstractSpellTemplate) {

    }

    @Override
    public void saveJson(JsonObject jsonObject) {

    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractSpellTemplate abstractSpellTemplate) {
        displayName.load(yaml);
        itemModel.load(yaml);
    }

    @Override
    public void saveYaml(ConfigurationSection configurationSection) {

    }

    @Override
    public String getId() {
        return "spell_visual";
    }
}
