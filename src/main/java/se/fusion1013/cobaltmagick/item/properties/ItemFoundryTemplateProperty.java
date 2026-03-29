package se.fusion1013.cobaltmagick.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.foundry.FoundryManager;

public class ItemFoundryTemplateProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final StringVariable template = new StringVariable("template");
    private boolean hasTemplate = false;

    @Override
    public String getId() {
        return "foundry_template";
    }

    @Override
    public void create(ItemCreationContext itemCreationContext) {
        if (!hasTemplate) return;
        itemCreationContext.persistent.set(FoundryManager.FOUNDRY_TEMPLATE_KEY, PersistentDataType.STRING, template.getValue());
    }

    @Override
    public void fromJson(JsonObject jsonObject, AbstractCobaltItem abstractCobaltItem) {

    }

    @Override
    public void saveJson(JsonObject jsonObject) {

    }

    @Override
    public void fromYaml(ConfigurationSection configurationSection, AbstractCobaltItem abstractCobaltItem) {
        if (!configurationSection.contains("template")) return;
        template.load(configurationSection);
        hasTemplate = true;
    }

    @Override
    public void saveYaml(ConfigurationSection configurationSection) {

    }
}
